/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
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

import java.net.InetAddress;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;
import edu.stevens.cs522.chat.settings.Settings;

public class ChatActivity extends Activity implements OnClickListener, IQueryListener<Message>, ServiceConnection, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;


    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

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

        /**
         * Initialize settings to default values.
         */
        Settings.init(this);
        ContentResolver cr = getContentResolver();
        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from = new String[] { MessageContract.NAME, MessageContract.MESSAGE_TEXT };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);
        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        peerManager = new PeerManager(this);
        messageManager.getAllMessagesAsync(this);
        // TODO initiate binding to the service
        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, this, Context.BIND_AUTO_CREATE);

        // TODO initialize sendResultReceiver (for receiving notification of message sent)
        receiverHandler = new Handler();
        sendResultReceiver = new ResultReceiverWrapper(receiverHandler);
        
        // initialize UI
        destinationHost = (EditText)findViewById(R.id.destination_host);
        destinationPort = (EditText)findViewById(R.id.destination_port);
        messageText = (EditText)findViewById(R.id.message_text);
        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

	public void onResume() {
        super.onResume();
        // TODO register result receiver
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        // TODO unregister result receiver
        sendResultReceiver.setReceiver(null);
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
        if (chatService != null) {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            InetAddress destAddr;

            int destPort;

            String username;

            String message;

            // Get destination and message from UI, and username from preferences.
            destAddr = InetAddressUtils.fromString(destinationHost.getText().toString());
            destPort = Integer.parseInt(destinationPort.getText().toString());
            message = messageText.getText().toString();

            // Get username from default shared preferences (see PreferenceManager, SettingsActivity)
            username = Settings.getChatName(this);

            // TODO use chatService to send the message
            if(chatService != null){
                chatService.send(destAddr, destPort, username, message, sendResultReceiver);
            } else {
                Log.e("CHAT_SERVICE", "ChatService == null", new Exception("ChatService null"));
            }

            Log.i(TAG, "Sent message: " + message);
            messageText.setText("");
        }
    }

    @Override
    /**
     * Show a text message when notified that sending a message succeeded or failed
     */
    public void onReceiveResult(int resultCode, Bundle data) {
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
    public void handleResults(TypedCursor<Message> results) {
        // TODO
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        messagesAdapter.swapCursor(null);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODO initialize chatService
        chatService = ((ChatService.ChatBinder)service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatService = null;
    }
}