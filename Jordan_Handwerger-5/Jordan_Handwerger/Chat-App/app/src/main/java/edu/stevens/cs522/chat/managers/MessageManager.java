package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.providers.ChatProvider;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };


    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Text};
        QueryBuilder.executeQuery(tag, (Activity)context, MessageContract.CONTENT_URI, projection, null, null, null, loaderID, creator, listener);
    }

    public void persistAsync(final Message message) {
        // TODO use AsyncContentResolver to complete this
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        getAsyncResolver().insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                message.id = MessageContract.getId(value);
                getSyncResolver().notifyChange(value, null);
            }
        });
    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        // Remember to reset the loader!
        String[] projection = {ChatProvider.ID, ChatProvider.Peer_FK, ChatProvider.Name, ChatProvider.Text};
        String select = ChatProvider.Peer_FK + "=?";
        String[] selectArgs = {Long.toString(peer.id)};
        QueryBuilder.executeQuery(tag, (Activity)context, MessageContract.CONTENT_URI, projection, select, selectArgs, null, loaderID, creator, listener);
    }

    public long persist(Message message) {
        // Synchronous version, executed on background thread
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        Uri insertUri = getSyncResolver().insert(MessageContract.CONTENT_URI, values);
        message.id = MessageContract.getId(insertUri);
        return message.id;
    }


}
