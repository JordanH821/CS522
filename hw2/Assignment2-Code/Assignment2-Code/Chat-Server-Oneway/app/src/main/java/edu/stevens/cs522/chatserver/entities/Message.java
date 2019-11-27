package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message (Parcel in) {
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

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return null;
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return null;
        }

    };

}

