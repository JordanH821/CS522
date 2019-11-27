package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chat.providers.ChatProvider;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String SEQUENCE_NUMBER = ChatProvider.SequenceNumber;

    public static final String MESSAGE_TEXT = ChatProvider.Text;

    public static final String CHAT_ROOM = ChatProvider.ChatRoom;

    public static final String TIMESTAMP = ChatProvider.Timestamp;

    public static final String LATITUDE = ChatProvider.Latitude;

    public static final String LONGITUDE = ChatProvider.Longitude;

    public static final String SENDER = ChatProvider.Name;

    public static final String SENDER_ID = ChatProvider.SenderID;

    public static final String[] COLUMNS = {ID, SEQUENCE_NUMBER, MESSAGE_TEXT, CHAT_ROOM, TIMESTAMP, LATITUDE, LONGITUDE, SENDER, SENDER_ID};

    private static int idColumn = -1;
    private static int sequenceNumberColumn = -1;
    private static int messageTextColumn = -1;
    private static int chatRoomColumn = -1;
    private static int senderIDColumn = -1;
    private static int senderColumn = -1;
    private static int timeStampColumn = -1;
    private static int latitudeColumn = -1;
    private static int longitudeColumn = -1;

    public static long getID(Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putID(ContentValues out, long id){
        out.put(ID, id);
    }


    public static long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(SEQUENCE_NUMBER);
        }
        return cursor.getLong(sequenceNumberColumn);
    }

    public void putSequenceNumberColumn(ContentValues out, String messageText) {
        out.put(SEQUENCE_NUMBER, messageText);
    }


    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }


    public static String getChatRoom(Cursor cursor){
        if(chatRoomColumn < 0){
            chatRoomColumn = cursor.getColumnIndexOrThrow(CHAT_ROOM);
        }
        return cursor.getString(chatRoomColumn);
    }

    public static void putChatRoom(ContentValues out, String chatRoom){
        out.put(CHAT_ROOM, chatRoom);
    }


    public static long getSenderID(Cursor cursor){
        if(senderIDColumn < 0){
            senderIDColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return cursor.getLong(senderIDColumn);
    }

    public static void putSenderID(ContentValues out, long senderID){
        out.put(SENDER_ID, senderID);
    }


    public static String getSender(Cursor cursor){
        if(senderColumn < 0){
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender){
        out.put(SENDER, sender);
    }


    public static Date getTimeStamp(Cursor cursor){
        if(timeStampColumn < 0){
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor, timeStampColumn);
    }

    public static void putTimeStamp(ContentValues out, Date timeStamp){
        DateUtils.putDate(out, TIMESTAMP, timeStamp);
    }

    public static Double getLatitude(Cursor cursor){
        if(latitudeColumn < 0){
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }
        return cursor.getDouble(latitudeColumn);
    }

    public static void putLatitude(ContentValues out, String lat){
        out.put(LATITUDE, lat);
    }

    public static Double getLongitude(Cursor cursor){
        if(longitudeColumn < 0){
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudeColumn);
    }

    public static void putLongitude(ContentValues out, String lon) {
        out.put(LONGITUDE, lon);
    }
}
