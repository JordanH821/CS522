package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chat.contracts.MessageContract;

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

    public Double latitude;

    public Double longitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("ID: " + id);
        sb.append(", Seq#: " + seqNum);
        sb.append(", Message: " + messageText);
        sb.append(", Chatroom: " + chatRoom);
        sb.append(", Sender: " + sender);
        sb.append(", SenderID: " + senderId);
        sb.append(", Time: " + timestamp);
        sb.append(", Long: " + longitude);
        sb.append(", Lat:" + longitude);
        sb.append("}");
        return sb.toString();
    }

    public ChatMessage() {
    }

    public ChatMessage(Cursor cursor) {
        id = MessageContract.getID(cursor);
        seqNum = MessageContract.getSequenceNumber(cursor);
        messageText = MessageContract.getMessageText(cursor);
        chatRoom = MessageContract.getChatRoom(cursor);
        timestamp = MessageContract.getTimeStamp(cursor);
        latitude = MessageContract.getLatitude(cursor);
        longitude = MessageContract.getLongitude(cursor);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderID(cursor);
    }

    public ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        messageText = in.readString();
        chatRoom = in.readString();
        timestamp = DateUtils.readDate(in);
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public void writeToProvider(ContentValues out) {
//        out.put(MessageContract.ID, id);
        out.put(MessageContract.SEQUENCE_NUMBER, seqNum);
        out.put(MessageContract.MESSAGE_TEXT, messageText);
        out.put(MessageContract.CHAT_ROOM, chatRoom);
        DateUtils.putDate(out, MessageContract.TIMESTAMP, timestamp);
        out.put(MessageContract.LATITUDE, latitude);
        out.put(MessageContract.LONGITUDE, longitude);
        out.put(MessageContract.SENDER, sender);
        out.put(MessageContract.SENDER_ID, senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(seqNum);
        dest.writeString(messageText);
        dest.writeString(chatRoom);
        DateUtils.writeDate(dest, timestamp);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }

    };


}
