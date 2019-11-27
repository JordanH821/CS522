package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable, Persistable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage(Cursor cursor) {
        // TODO
    }

    public ChatMessage(Parcel in) {
        // TODO
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            // TODO
            return null;
        }

        @Override
        public ChatMessage[] newArray(int size) {
            // TODO
            return null;
        }

    };

}
