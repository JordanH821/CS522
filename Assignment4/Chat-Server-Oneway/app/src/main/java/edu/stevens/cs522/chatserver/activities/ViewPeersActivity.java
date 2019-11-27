package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.providers.ChatProvider;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    /*
     */

    private SimpleCursorAdapter peerAdapter;
    Cursor peers;
    ListView peerList;
    final static int PEER_LOADER_ID = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        String from[] = {ChatProvider.Name};
        int to[] = {android.R.id.text1};
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, peers, from, to);
        peerAdapter.swapCursor(null);
        peerList = (ListView)findViewById(R.id.peer_list);
        peerList.setAdapter(peerAdapter);
        peerList.setOnItemClickListener(this);

        getLoaderManager().initLoader(PEER_LOADER_ID, null, this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch(id){
            case PEER_LOADER_ID:
                String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Timestamp, ChatProvider.Address};
                return new CursorLoader(this, PeerContract.CONTENT_URI, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        peerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        peerAdapter.swapCursor(null);
    }

}
