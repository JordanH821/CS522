package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    public static final String ID = "_id";
    public static final String Peer_FK = "peer_fk";
    public static final String Name = "name";
    public static final String Timestamp = "timestamp";
    public static final String Address = "address";
    public static final String Text = "message_text";
    public static final String MessagesPeerIndex = "MessagesPeerIndex";
    public static final String PeerNameIndex = "PeerNameIndex";
    public static final String FOREIGN_KEYS_ON = "PRAGMA foreign_keys=ON;";

    // Create the constants used to differentiate between the different URI requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static final String mimeTypeBase = "vnd.android.cursor/vnd.edu.stevens.cs522.chatserver.";

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE_PEERS =
                "CREATE TABLE " + PEERS_TABLE + " ("
                        + ID + " integer primary key, "
                        + Name + " text not null, "
                        + Timestamp + " long not null, "
                        + Address + " text not null);";
//                    + Port + " text not null);";

        private static final String DATABASE_CREATE_MESSAGES =
                "CREATE TABLE " + MESSAGES_TABLE + " ("
                        + ID + " integer primary key, "
                        + Peer_FK + " integer not null, "
                        + Name + " text not null, "
                        + Timestamp + " long not null, "
                        + Text + " text, "
                        + "FOREIGN KEY(" + Peer_FK + ") references " + PEERS_TABLE + "(" + ID + "));";

        private static final String DATABASE_CREATE_MESSAGES_PEER_INDEX =
                "CREATE INDEX " + MessagesPeerIndex + " ON " + MESSAGES_TABLE + "(" + Peer_FK + ");";

        private static final String DATABASE_CREATE_PEER_NAME_INDEX =
                "CREATE INDEX " + PeerNameIndex + " ON " + PEERS_TABLE + "(" + Name + ");";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FOREIGN_KEYS_ON);
            db.execSQL(DATABASE_CREATE_PEERS);
            db.execSQL(DATABASE_CREATE_MESSAGES);
            db.execSQL(DATABASE_CREATE_PEER_NAME_INDEX);
            db.execSQL(DATABASE_CREATE_MESSAGES_PEER_INDEX);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            onCreate(db);
        }

    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), DATABASE_VERSION, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        switch(uriMatcher.match(uri)){
            case MESSAGES_ALL_ROWS:
                return mimeTypeBase + MESSAGES_TABLE;
            case PEERS_ALL_ROWS:
                return mimeTypeBase + PEERS_TABLE;
            case MESSAGES_SINGLE_ROW:
                return mimeTypeBase + "message";
            case PEERS_SINGLE_ROW:
                return mimeTypeBase + "peer";
            default:
                throw new UnsupportedOperationException("bad uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // Make sure to notify any observers
                row = db.insert(MESSAGES_TABLE, null, values);
                if(row > 0){
                    Uri instanceUri = MessageContract.CONTENT_URI(row);
                    Uri peerUri = PeerContract.CONTENT_URI(values.getAsLong(ChatProvider.Peer_FK));
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                    cr.notifyChange(peerUri, null);
                    return instanceUri;
                }
                throw new UnsupportedOperationException("failed inserting into Messages: " + row);
            case PEERS_ALL_ROWS:
                // Make sure to notify any observers
                String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Timestamp, ChatProvider.Address};
                String selection = ChatProvider.Name + "=?";
                String[] selectionArgs = {values.getAsString(ChatProvider.Name)};
                Cursor upsertCheck = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, null);
                if(upsertCheck.moveToFirst()) {
//                    Peer updatePeer = new Peer(upsertCheck);
                    long id = PeerContract.getID(upsertCheck);
                    values.remove(ChatProvider.ID);
                    values.put(ChatProvider.ID, id);
                    Uri instanceUri = PeerContract.CONTENT_URI(id);
                    selection = ChatProvider.ID + "=";
                    String[] updateArgs = {Long.toString(id)};
                    db.update(PEERS_TABLE, values, selection + Long.toString(id), null);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                    return instanceUri;
                } else {
                    Cursor allPeers = db.query(PEERS_TABLE, projection, null, null, null, null, null);
                    if(allPeers.getCount() > 0){
                        values.put(ChatProvider.ID, allPeers.getCount() + 1);
                    } else {
                        values.put(ChatProvider.ID, 1);
                    }
                    row = db.insert(PEERS_TABLE, null, values);
                    if (row > 0) {
                        Uri instanceUri = PeerContract.CONTENT_URI(row);
                        ContentResolver cr = getContext().getContentResolver();
                        cr.notifyChange(instanceUri, null);
                        return instanceUri;
                    }
                }
                throw new UnsupportedOperationException("failed inserting into Peers: ");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor query;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                query = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PEERS_ALL_ROWS:
                query = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_SINGLE_ROW:
                selection = MessageContract.ID + "=?";
                String[] messArgs = {Long.toString(MessageContract.getId(uri))};
                query = db.query(MESSAGES_TABLE, projection, selection, messArgs, null, null, sortOrder);
                break;
            case PEERS_SINGLE_ROW:
                selection = PeerContract.ID + "=?";
                String[] peerArgs = {Long.toString(PeerContract.getId(uri))};
                query = db.query(PEERS_TABLE, projection, selection, peerArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalStateException("insert: bad case");
        }
        ContentResolver cr = getContext().getContentResolver();
        query.setNotificationUri(cr, uri);
        return query;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.update(MESSAGES_TABLE, values, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                return db.update(PEERS_TABLE, values, selection, selectionArgs);
            case MESSAGES_SINGLE_ROW:
                selection = MessageContract.ID + "=?";
                String[] messArgs = {Long.toString(MessageContract.getId(uri))};
                return db.update(MESSAGES_TABLE, values, selection, selectionArgs);
            case PEERS_SINGLE_ROW:
                selection = PeerContract.ID + "=?";
                String[] peerArgs = {Long.toString(PeerContract.getId(uri))};
                return db.update(PEERS_TABLE, values, selection, selectionArgs);
            default:
                throw new IllegalStateException("update: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                return db.delete(PEERS_TABLE, selection, selectionArgs);
            case MESSAGES_SINGLE_ROW:
                selection = MessageContract.ID + "=?";
                String[] messArgs = {Long.toString(MessageContract.getId(uri))};
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            case PEERS_SINGLE_ROW:
                selection = PeerContract.ID + "=?";
                String[] peerArgs = {Long.toString(PeerContract.getId(uri))};
                return db.delete(PEERS_TABLE, selection, selectionArgs);
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }
}
