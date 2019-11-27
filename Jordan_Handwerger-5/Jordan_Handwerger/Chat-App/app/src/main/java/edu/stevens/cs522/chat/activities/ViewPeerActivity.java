package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.providers.ChatProvider;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements IQueryListener<Message> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter peerAdapter;

    private MessageManager messageManager;

    private ListView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI and initiate query of message database

        ((TextView)findViewById(R.id.view_user_name)).setText(peer.name);
        ((TextView)findViewById(R.id.view_address)).setText(peer.address.toString());
        ((TextView)findViewById(R.id.view_timestamp)).setText(peer.timestamp.toString());

        String[] from = {ChatProvider.Text};
        int[] to = {android.R.id.text1};
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        messageList = (ListView) findViewById(R.id.view_messages);
        messageList.setAdapter(peerAdapter);

        messageManager = new MessageManager(this);
        messageManager.getMessagesByPeerAsync(peer, this);
    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        // TODO
        peerAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null);
    }


}
