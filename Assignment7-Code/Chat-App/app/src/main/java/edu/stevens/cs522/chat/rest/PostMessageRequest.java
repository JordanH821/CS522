package edu.stevens.cs522.chat.rest;

import android.nfc.Tag;
import android.os.Parcel;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.base.DateUtils;

/**
 * Created by dduggan.
 */

public class PostMessageRequest extends Request {

    public String chatRoom;

    public String sender;

    public String message;

    public PostMessageRequest(long senderId, UUID clientID, String chatRoom, String sender, String message) {
        super(senderId, clientID);
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        // TODO
        headers.put(Request.APP_ID_HEADER, appID.toString());
        headers.put(Request.TIMESTAMP_HEADER, Long.toString(DateUtils.now().getTime()));
        headers.put(Request.LATITUDE_HEADER, Request.DefaultLatitude.toString());
        headers.put(Request.LONGITUDE_HEADER, Request.DefaultLongitude.toString());
        return headers;
    }

    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // TODO write a JSON message of the form:
        // { "room" : <chat-room-name>, "message" : <message-text>
        jw.beginObject();
        jw.name("chatroom");
        jw.value(chatRoom);
        jw.name("text");
        jw.value(message);
        jw.endObject();
        Log.i("JSON", wr.toString());
        return wr.toString();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new PostMessageResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest, flags);
        dest.writeString(chatRoom);
        dest.writeString(sender);
        dest.writeString(message);
    }

    public PostMessageRequest(Parcel in) {
        super(in);
        // TODO
        chatRoom = in.readString();
        sender = in.readString();
        message = in.readString();
    }

    public static Creator<PostMessageRequest> CREATOR = new Creator<PostMessageRequest>() {
        @Override
        public PostMessageRequest createFromParcel(Parcel source) {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size) {
            return new PostMessageRequest[size];
        }
    };

}
