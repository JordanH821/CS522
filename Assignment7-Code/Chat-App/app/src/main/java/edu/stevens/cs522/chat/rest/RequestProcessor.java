package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.util.Log;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.activities.ChatActivity;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.providers.ChatProvider;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
    }

    public Response process(Request request) {
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
            Log.println(Log.DEBUG, "MESSAGE INSERTED", cm.toString());
        }
        return response;
    }

}
