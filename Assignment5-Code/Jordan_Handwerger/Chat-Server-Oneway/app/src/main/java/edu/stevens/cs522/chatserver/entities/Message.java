package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatserver.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor cursor) {
        id = MessageContract.getID(cursor);
        messageText = MessageContract.getMessageText(cursor);
        timestamp = MessageContract.getTimeStamp(cursor);
        sender = MessageContract.getName(cursor);
        senderId = MessageContract.getPeerFK(cursor);
    }

    public Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        timestamp = DateUtils.readDate(in);
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public void writeToProvider(ContentValues out) {
//        out.put(MessageContract.ID, id);
        out.put(MessageContract.MESSAGE_TEXT, messageText);
        DateUtils.putDate(out, MessageContract.TIMESTAMP, timestamp);
        out.put(MessageContract.NAME, sender);
        out.put(MessageContract.PEER_FK, senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(messageText);
        DateUtils.writeDate(dest, timestamp);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}