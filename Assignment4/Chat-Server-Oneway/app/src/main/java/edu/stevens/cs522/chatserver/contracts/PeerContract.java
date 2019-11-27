package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

import static edu.stevens.cs522.chatserver.contracts.BaseContract.withExtendedPath;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public final static String ID = ChatProvider.ID;
    public final static String NAME = ChatProvider.Name;
    public final static String TIMESTAMP = ChatProvider.Timestamp;
    public final static String ADDRESS = ChatProvider.Address;

    private static int idColumn = -1;
    private static int nameColumn = -1;
    private static int timestampColumn = -1;
    private static int ipColumn = -1;

    public static long getID(Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putID(ContentValues out, long id){
        out.put(ID, id);
    }

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues out, String name) {
        out.put(NAME, name);
    }

    public static Date getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor, timestampColumn);
    }

    public static void putTimestamp(ContentValues out, String timestamp) {
        out.put(TIMESTAMP, timestamp);
    }

    public static InetAddress getAddress(Cursor cursor) {
        if (ipColumn < 0) {
            ipColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return InetAddressUtils.getAddress(cursor, ipColumn);
    }

    public static void putAddress(ContentValues out, InetAddress ip) {
        InetAddressUtils.putAddress(out, ADDRESS, ip);
    }
}
