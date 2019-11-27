package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String PEER_KEY = "peer";
    public static long PEER_ID;
    ListView messagesList;
    SimpleCursorAdapter messagesAdapter;
    Cursor messageCursor;
    static final int MESSAGE_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        ((TextView)(findViewById(R.id.view_user_name))).setText(peer.name);
        ((TextView)(findViewById(R.id.view_address))).setText(InetAddressUtils.toIpAddress(peer.address));
        ((TextView)(findViewById(R.id.view_timestamp))).setText(peer.timestamp.toString());

        String[] from = {ChatProvider.Text};
        int[] to = {android.R.id.text1};
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, messageCursor, from, to);
        messagesList = (ListView)findViewById(R.id.view_messages);
        messagesList.setAdapter(messagesAdapter);
        PEER_ID = peer.id;
        getLoaderManager().initLoader(MESSAGE_LOADER_ID, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Filter messages with the sender id
        switch(id){
            case MESSAGE_LOADER_ID:
                String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Timestamp, ChatProvider.Text};
                String selection = ChatProvider.Peer_FK + "=?";
                String selectionArgs[] = {Long.toString(PEER_ID)};
//                String selectionArgs[] = {Long.toString(PEER_ID)};
                return new CursorLoader(this, MessageContract.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        messagesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        messagesAdapter.swapCursor(null);
    }

}
