/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.providers.ChatProvider;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.ServiceManager;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
    private Handler receiverHandler;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver cr = getContentResolver();
        ChatProvider cp = new ChatProvider();
        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from = new String[] { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);
        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        peerManager = new PeerManager(this);
        messageManager.getAllMessagesAsync(this);

        // TODO instantiate helper for service
        helper = new ChatHelper(this);

        // TODO initialize sendResultReceiver
        receiverHandler = new Handler();
        sendResultReceiver = new ResultReceiverWrapper(receiverHandler);

        // TODO (SYNC) initialize serviceManager
        chatRoomName = (EditText)findViewById(R.id.chat_room);
        messageText = (EditText)findViewById(R.id.message_text);
        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        serviceManager = new ServiceManager(this);

        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // Launch registration activity
            Settings.getAppId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
        if (Settings.SYNC) {
            serviceManager.scheduleSyncRepeating(10);
//            serviceManager.scheduleBackgroundOperations();
        }
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
        if (Settings.SYNC) {
            serviceManager.cancelBackgroundOperations();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent pIntent = new Intent(this, ViewPeersActivity.class);
                startActivity(pIntent);
                break;

            // TODO PEERS provide the UI for registering
            case R.id.register:
                if(!Settings.isRegistered(this)) {
                    Intent rIntent = new Intent(this, RegisterActivity.class);
                    startActivity(rIntent);
                } else {
                    Toast toastReg = Toast.makeText(getApplicationContext(), "ALREADY REGISTERED", Toast.LENGTH_SHORT);
                    toastReg.show();
                }

                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent sIntent = new Intent(this, SettingsActivity.class);
                startActivity(sIntent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom = chatRoomName.getText().toString();

            String message = messageText.getText().toString();

            // TODO use helper to post a message
            if(helper != null) {
                helper.postMessage(chatRoom, Settings.getChatName(this),message, sendResultReceiver);
            } else {
                Log.e("CHAT_HELPER", "CHAT_HELPER == null", new Exception("ChatHelper null"));
            }

            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        /*
         * RequestService calls back with result of Web service request
         */
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast toastSuccess = Toast.makeText(getApplicationContext(), "SUCCESSFULLY SENT MESSAGE", Toast.LENGTH_SHORT);
                toastSuccess.show();
                break;
            default:
                // TODO show a failure toast message
                Toast toastFail = Toast.makeText(getApplicationContext(), "FAILED TO SEND MESSAGE", Toast.LENGTH_SHORT);
                toastFail.show();
                break;
        }
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
        Log.i("HANDLE RESULTS", "PeerManager");
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        messagesAdapter.swapCursor(null);
    }
}