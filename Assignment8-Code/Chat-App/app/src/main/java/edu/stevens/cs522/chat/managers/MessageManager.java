package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.providers.ChatProvider;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };
    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<ChatMessage> listener) {
        // TODO use QueryBuilder to complete this
        Log.i("MESSAGE MANAGER", "INSIDE getAllMessagesAsync");
        String[] projection = {ChatProvider.ID, ChatProvider.Name, ChatProvider.Text};
        QueryBuilder.executeQuery(tag, (Activity)context, MessageContract.CONTENT_URI, projection, null, null, null, loaderID, creator, listener);
    }

    public void persistAsync(final ChatMessage message) {
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

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<ChatMessage> listener) {
        // TODO use QueryBuilder to complete this
        // Remember to reset the loader!
        String[] projection = {ChatProvider.ID, ChatProvider.SenderID, ChatProvider.Name, ChatProvider.Text};
        String select = ChatProvider.SenderID + "=?";
        String[] selectArgs = {Long.toString(peer.id)};
        QueryBuilder.executeQuery(tag, (Activity)context, MessageContract.CONTENT_URI, projection, select, selectArgs, null, loaderID, creator, listener);
    }

    public long persist(ChatMessage message) {
        // Synchronous version, executed on background thread
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        Uri insertUri = getSyncResolver().insert(MessageContract.CONTENT_URI, values);
        message.id = MessageContract.getId(insertUri);
        return message.id;
    }


}

