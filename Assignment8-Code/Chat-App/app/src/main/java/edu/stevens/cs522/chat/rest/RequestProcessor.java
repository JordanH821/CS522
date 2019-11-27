package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.base.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        // Used for managing messages in the database
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
//        if(request == null){
//            Log.w("Process", "Process Null Request");
//            return null;
//        }
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Log.println(Log.DEBUG, "RequestProcessor", "Before perform");
        Response response = restMethod.perform(request);
        Log.println(Log.DEBUG, "RequestProcessor", "After perform");
        if (response instanceof RegisterResponse) {
            // TODO update the user name and sender id in settings, updated peer record PK
            Settings.saveChatName(context, request.chatname);
            Long senderID = ((RegisterResponse) response).getSenderId();
            Settings.saveSenderId(context, senderID);
            PeerManager pm = new PeerManager(context);
            Peer peer = new Peer();
            peer.name = request.chatname;
            peer.id = senderID;
            peer.longitude = Request.DefaultLongitude;
            peer.latitude = Request.DefaultLatitude;
            peer.timestamp = DateUtils.now();
            pm.persist(peer);
            Log.println(Log.DEBUG, "PEER INSERTED", peer.toString());
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        // TODO insert the message into the local database
        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            ChatMessage cm = new ChatMessage();
            MessageManager mm = new MessageManager(context);
//            cm.id = ((PostMessageResponse) response).getMessageId();
            cm.sender = request.sender;
            cm.senderId = Settings.getSenderId(context);
            cm.seqNum = ((PostMessageResponse) response).getMessageId();
            cm.messageText = request.message;
            cm.chatRoom = request.chatRoom;
            cm.timestamp = request.timestamp;
            cm.latitude = request.latitude;
            cm.longitude = request.longitude;
            cm.id = mm.persist(cm);
            Log.i("REQUEST PROCESSOR", "inserted:\t" + cm.toString());
        }
        return response;
    }

    /**
     * For SYNC: perform a sync using a request manager
     * @param request
     * @return
     */
    public Response perform(SynchronizeRequest request) {
        Log.i("REQUEST PROCESSOR", "Sending SyncRequest");
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        try {
            /*
             * This is the callback from streaming new local messages to the server.
             */
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        while(messages.moveToNext()){
                            ChatMessage cm = new ChatMessage(messages.getCursor());
                            wr.beginObject();
                            wr.name("chatroom");
                            wr.value(cm.chatRoom);
                            wr.name("timestamp");
                            wr.value(cm.timestamp.toString());
                            wr.name("latitude");
                            wr.value(cm.latitude);
                            wr.name("longitude");
                            wr.value(cm.longitude);
                            wr.endObject();
                        }

                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            /*
             * Connect to the server and upload messages not yet shared.
             */
            response = restMethod.perform(request, out);

            /*
             * Stream downloaded peer and message information, and update the database.
             * The connection is closed in the finally block below.
             */
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.
            ArrayList<Peer> newPeers = new ArrayList<Peer>();
            ArrayList<ChatMessage> newMessages = new ArrayList<ChatMessage>();
            rd.beginObject();

            while(rd.peek() != JsonToken.END_OBJECT){
                String label = rd.nextName(); //clients or messages
                if("clients".equals(label)){
                    rd.beginArray(); //get array of clients
                    while(rd.hasNext()){
                        newPeers.add(readPeer(rd));
                    }
                    rd.endArray();
                } else if("messages".equals(label)){
                    rd.beginArray();
                    while(rd.hasNext()){
                        newMessages.add(readMessage(rd));
                    }
                    rd.endArray();
                }
            }
            rd.endObject();

            // Update DB
            requestManager.updateAllPeers(newPeers);
            requestManager.updateAllMessages(newMessages);

            /*
             *
             */
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(0, ErrorResponse.Status.SERVER_ERROR, e.getMessage());

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

    private Peer readPeer(JsonReader reader){

//        public long id;
//        public String name;
//        public Date timestamp;
//        public Double longitude;
//        public Double latitude;
        try{
            Peer newPeer = new Peer();
            reader.beginObject();
            while(reader.hasNext()){
                String p = reader.nextName();
                if(p.equals("id")){
                    newPeer.id = reader.nextLong();
                } else if(p.equals("name")){
                    newPeer.name = reader.nextString();
                } else if(p.equals("timestamp")){
                    newPeer.timestamp = new Date(reader.nextLong());
                } else if(p.equals("longitude")){
                    newPeer.longitude = reader.nextDouble();
                } else if(p.equals("latitude")){
                    newPeer.latitude = reader.nextDouble();
                }
            }
            reader.endObject();
            Log.i("SYNC", newPeer.toString());
            return newPeer;
        } catch (Exception e){
            Log.e("SYNC", "Problem syncing new peers", e);
            return null;
        }
    }

    private ChatMessage readMessage(JsonReader reader){
//        public long id;
//        public long seqNum;
//        public String messageText;
//        public String chatRoom;
//        public Date timestamp;
//        public Double latitude;
//        public Double longitude;
//        public String sender;
//        public long senderId;
        try{
            ChatMessage newMess = new ChatMessage();
            reader.beginObject();
            while(reader.hasNext()){
                String p = reader.nextName();
                if(p.equals("chatroom")){
                    newMess.chatRoom = reader.nextString();
                } else if(p.equals("timestamp")){
                    newMess.timestamp = new Date(reader.nextLong());
                } else if(p.equals("longitude")){
                    newMess.longitude = reader.nextDouble();
                } else if(p.equals("latitude")){
                    newMess.latitude = reader.nextDouble();
                } else if(p.equals("seqnum")) {
                    newMess.seqNum = reader.nextLong();
                } else if(p.equals("sender")) {
                    newMess.sender = reader.nextString();
                } else if(p.equals("text")){
                    newMess.messageText = reader.nextString();
                }
            }
            reader.endObject();
            Log.i("SYNC", newMess.toString());
            return newMess;
        } catch (Exception e){
            Log.e("SYNC", "Problem syncing new messages", e);
            return null;
        }
    }
}
