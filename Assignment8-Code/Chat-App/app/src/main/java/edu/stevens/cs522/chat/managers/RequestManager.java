package edu.stevens.cs522.chat.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.providers.ChatProvider;

/**
 * Created by dduggan.
 *
 * The API used by the Web service for synchronizing messages with server.
 * It is assumed that all operations are invoked on the background thread for Web services.
 */

public class RequestManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final String MAX_SEQNO_COLUMN = "max_seq_num";

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    public RequestManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void persist(ChatMessage message) {
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        Uri uri = getSyncResolver().insert(MessageContract.CONTENT_URI, values);
        message.id = MessageContract.getId(uri);
    }

    /**
     * Get the last sequence number in the messages database.
     * @return
     */
    public long getLastSequenceNumber() {
        String selection = MessageContract.SEQUENCE_NUMBER + "<>0";
        String[] selectionArgs = { };
        String[] columns = { String.format("MAX(%s) as %s", MessageContract.SEQUENCE_NUMBER, MAX_SEQNO_COLUMN) };
        Cursor cursor = getSyncResolver().query(MessageContract.CONTENT_URI, columns, selection, selectionArgs, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                // Empty database
                return 0;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Get all unsent messages, identified by sequence number = 0.
     * @return
     */
    public TypedCursor<ChatMessage> getUnsentMessages() {
        String selection = MessageContract.SEQUENCE_NUMBER + "=0";
        String[] selectionArgs = { };
        String[] columns = MessageContract.COLUMNS;
        Cursor cursor = getSyncResolver().query(MessageContract.CONTENT_URI, columns, selection, selectionArgs, MessageContract.TIMESTAMP);
        return new TypedCursor(cursor, creator);
    }

    /**
     * After syncing with server, update the sequence numbers of uploaded messages
     * @param id
     * @param seqNum
     */
    public void updateSeqNum(long id, long seqNum) {
        Uri uri = MessageContract.CONTENT_URI(id);
        ContentValues values = new ContentValues();
        values.put(MessageContract.SEQUENCE_NUMBER, seqNum);
        if (getSyncResolver().update(uri, values, null, null) != 1) {
            throw new IllegalStateException("Unexpected result from update of message seq num!");
        }
    }

    public void updateAllPeers(ArrayList<Peer> peers){
        ContentResolver cr = getSyncResolver();
        cr.delete(PeerContract.CONTENT_URI, null, null);
        for(int i = 0; i < peers.size(); i++){
            Peer p = peers.get(i);
            ContentValues values = new ContentValues();
            p.writeToProvider(values);
            cr.insert(PeerContract.CONTENT_URI, values);
        }
    }

    public void updateAllMessages(ArrayList<ChatMessage> messages){
        ContentResolver cr = getSyncResolver();
        String selection = ChatProvider.Name + "=?";
//        String[] selectionArgs = {values.getAsString(ChatProvider.Name)};
//        Cursor upsertCheck = cr.query(ChatProvider., projection, selection, selectionArgs, null, null, null);
        for(int i = 0; i < messages.size(); i++){
            ChatMessage cm = messages.get(i);
//            cr.query(MessageContract.CONTENT_URI, MessageContract.COLUMNS, );
            Log.i("SYNC MESSAGE", cm.toString());
        }
    }
}
