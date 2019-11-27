package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, IQueryListener<Peer> {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private PeerManager peerManager;

    private SimpleCursorAdapter peerAdapter;

    private ListView peersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

        String[] from = new String[] { PeerContract.NAME};
        int[] to = new int[] { android.R.id.text1};
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        peersList = (ListView)findViewById(R.id.peer_list);
        peersList.setAdapter(peerAdapter);
        
        peersList.setOnItemClickListener(this);
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
            throw new IllegalStateException("Unable to move to position in cursor: "+ position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        // TODO
        peerAdapter.changeCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null);
    }

}
