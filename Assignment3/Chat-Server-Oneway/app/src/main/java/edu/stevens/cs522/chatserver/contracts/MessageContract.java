package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

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


    public static final String ID = ChatProvider.ID;

    public static final String MESSAGE_TEXT = ChatProvider.Text;

    public static final String TIMESTAMP = ChatProvider.Timestamp;

    public static final String NAME = ChatProvider.Name;

    public static final String PEER_FK = ChatProvider.Peer_FK;

    private static int idColumn = -1;
    private static int peerFKColumn = -1;
    private static int nameColumn = -1;
    private static int messageTextColumn = -1;
    private static int timeStampColumn = -1;

    public static long getID(Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putID(ContentValues out, long id){
        out.put(ID, id);
    }

    public static long getPeerFK(Cursor cursor){
        if(peerFKColumn < 0){
            peerFKColumn = cursor.getColumnIndexOrThrow(PEER_FK);
        }
        return cursor.getLong(peerFKColumn);
    }

    public static void putPeerFK(ContentValues out, long peerFK){
        out.put(PEER_FK, peerFK);
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


    public static void putTimeStamp(ContentValues out, Date timeStamp){
//        out.put(TIMESTAMP, timeStamp);
        DateUtils.putDate(out, TIMESTAMP, timeStamp);
    }

    public static Date getTimeStamp(Cursor cursor){
        if(timeStampColumn < 0){
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor, timeStampColumn);
    }

    public static void putName(ContentValues out, String name){
        out.put(NAME, name);
    }

    public static String getName(Cursor cursor){
        if(nameColumn < 0){
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }
}