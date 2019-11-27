package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.providers.ChatProvider;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // TODO get a list of all peers in the database
        // use QueryBuilder to complete this
        String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Address, ChatProvider.Timestamp};
        QueryBuilder.executeQuery(tag, (Activity)context, PeerContract.CONTENT_URI, projection, null, null, null, loaderID, creator, listener);
    }

    public void getPeerAsync(long id, IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
    }

    public void persistAsync(final Peer peer, final IContinue<Long> callback) {
        // TODO upsert the peer into the database
        // use AsyncContentResolver to complete this
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        AsyncContentResolver acr = getAsyncResolver();
        acr.insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                getSyncResolver().notifyChange(value, null);
                callback.kontinue(PeerContract.getId(value));
            }
        });
    }

    public long persist(Peer peer) {
        // Synchronous version, executed on background thread
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        Uri insertUri = getSyncResolver().insert(PeerContract.CONTENT_URI, values);
        peer.id = MessageContract.getId(insertUri);
        return peer.id;
    }

}
