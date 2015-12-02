package app.wingman.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;


import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.wingman.R;
import app.wingman.core.Chat;
import app.wingman.core.ChatService;
import app.wingman.core.GroupChatImpl;
import app.wingman.core.PrivateChatImpl;
import app.wingman.ui.adapters.ChatAdapter;
import app.wingman.utils.PreferencesUtils;
import app.wingman.utils.RealPathUtil;


public class ChatActivity extends app.wingman.ui.activities.BaseActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_DIALOG = "dialog";
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private static final int REQUEST_MEDIA = 100;
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    private EditText messageEditText;
    private ListView messagesContainer;
    private Button sendButton;

    private ProgressBar progressBar;
    private ChatAdapter adapter;
    private File photoFile;
    private Chat chat;
    public static QBDialog dialog;

    private View stickersFrame;
    private boolean isStickersFrameVisible;
    private ImageView stickerButton;
    private RelativeLayout container;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    private Toolbar toolbar;
    private TextView grp_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




        initViews();
        // Init chat if the session is active
        //
        if (isSessionActive()) {
            initChat();
        }

        ChatService.getInstance().addConnectionListener(chatConnectionListener); }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {


        {
            try {
                chat.release();
            } catch (XMPPException e) {
                Log.e(TAG, "failed to release chat", e);
            }
            super.onBackPressed();

            Intent i = new Intent(ChatActivity.this, DialogsActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        grp_title= (TextView)findViewById(R.id.grp_title);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //on back press in back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Setup opponents info
        //
        Intent intent = getIntent();
        dialog = (QBDialog) intent.getSerializableExtra(EXTRA_DIALOG);

        //saving the username of id for displaying the names in chatadapter



        container = (RelativeLayout) findViewById(R.id.container);
        if (dialog.getType() == QBDialogType.GROUP) {

            grp_title.setText(dialog.getName().toString());

        }

//        else if (dialog.getType() == QBDialogType.PRIVATE) {
//            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
//            companionLabel.setText(ChatService.getInstance().getDialogsUsers().get(opponentID).getLogin());
//        }

        // Send button
        //
        sendButton = (Button) findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
//                if (TextUtils.isEmpty(messageText)) {
//                    return;
//                }

                sendChatMessage(messageText);


            }
        });
        grp_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Intent gd = new Intent(getApplicationContext(),GroupDetail.class);
                gd.putExtra("dialog",dialog);
                startActivity(gd);
            }
        });



    }
    private void getImages() {

       if(checkPermissionForExternalStorage())
       {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);



       }
    }
    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                String realPath;
                // SDK < API11
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());



                try {
             File f = new File(  realPath);
                    uploadSelectedImage(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private final boolean PUBLIC_ACCESS_TRUE = true;
    private void uploadSelectedImage(File imageFile) {

        // Upload new file
        //
        QBContent.uploadFileTask(imageFile, PUBLIC_ACCESS_TRUE, null, new QBEntityCallbackImpl<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
//                DataHolder.getDataHolder().addQbFile(qbFile);


                // create a message
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setProperty("save_to_history", "1"); // Save a message to history

                // attach a photo

                QBAttachment attachment = getAttachment(qbFile);

                chatMessage.addAttachment(attachment);
                try {
                    chat.sendMessage(chatMessage);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                if (dialog.getType() == QBDialogType.PRIVATE) {
                    showMessage(chatMessage);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> strings) {
                Log.e("error uploading", "error uploading");
            }
        }, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.e(" uploading", progress + "");
            }
        });
    }



    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }
    private void sendChatMessage(String messageText ) {
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);

        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime() / 1000);



// attach a photo




//        QBFile someFile = new QBFile(selectedImagePath);
//        String fileId = "" + someFile.getId();
//        QBAttachment attachment = new QBAttachment("photo");
//        attachment.setId(fileId);
//        chatMessage.addAttachment(attachment);
        try {
            chat.sendMessage(chatMessage);
        } catch (XMPPException e) {
            Log.e(TAG, "failed to send a message", e);
        } catch (SmackException sme) {
            Log.e(TAG, "failed to send a message", sme);
        }

        messageEditText.setText("");

        if (dialog.getType() == QBDialogType.PRIVATE) {
            showMessage(chatMessage);
        }
    }


    private QBAttachment getAttachment(QBFile file) {
        // TODO temp value
        String contentType = "image/jpeg";

        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
        attachment.setId(file.getUid());
        attachment.setName(file.getName());
        attachment.setContentType(contentType);
        attachment.setUrl(file.getPublicUrl());
        attachment.setSize(file.getSize());

        return attachment;
    }




    public void setContentBottomPadding(int padding) {
        container.setPadding(0, 0, 0, padding);
    }

    private void initChat() {

        if (dialog.getType() == QBDialogType.GROUP) {
            chat = new GroupChatImpl(this);

            // Join group chat
            //
            progressBar.setVisibility(View.VISIBLE);
            //
            joinGroupChat();

        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);

            chat = new PrivateChatImpl(this, opponentID);

            // Load CHat history
            //
            loadChatHistory();
        }
    }

    private void joinGroupChat() {
        ((GroupChatImpl) chat).joinGroupChat(dialog, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {

                // Load Chat history
                //
                loadChatHistory();
            }

            @Override
            public void onError(List list) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("error when join group chat: " + list.toString()).create().show();
            }
        });
    }

    private void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

//saving user names from id
                for (int i = 0; i < dialog.getOccupants().size(); i++) {
                    int participantId = Integer.valueOf((dialog.getOccupants().get(i)));

                    QBUsers.getUser(participantId, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {

                            PreferencesUtils.saveData(qbUser.getId().toString(),qbUser.getFullName(),getApplicationContext());
                        }

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(List<String> list) {

                        }
                    });

                }




                adapter = new ChatAdapter(ChatActivity.this, new ArrayList<QBChatMessage>());
                messagesContainer.setAdapter(adapter);

                for (int i = messages.size() - 1; i >= 0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {
                if (!ChatActivity.this.isFinishing()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                    dialog.setMessage("load chat history errors: " + errors).create().show();
                }
            }
        });
    }

    public void showMessage(QBChatMessage message) {
        adapter.add(message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }

    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection connection) {
            Log.i(TAG, "authenticated");
        }

        @Override
        public void connectionClosed() {
            Log.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

            // leave active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupChatImpl) chat).leave();
                    }
                });
            }
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if (seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful");

            // Join active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat();
                    }
                });
            }
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };


    //
    // ApplicationSessionStateCallback
    //

    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    initChat();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.attach) {
//            getImages();
//        }

//        if (id == R.id.add_user) {
//
//
//
            QBDialog dialog1 = new QBDialog();
            dialog1.setDialogId("565d802aa0eb47dca20006fd");


            QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
            requestBuilder.push("occupants_ids", "6967861"); // add another users


// requestBuilder.pullAll("occupants_ids", 6967861); // Remove yourself (user with ID 22)

            QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
            groupChatManager.updateDialog(dialog1, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
                @Override
                public void onSuccess(QBDialog dialog, Bundle args) {
Log.e("member added","added"+dialog.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    Log.e("member added","error"+errors.toString());

                }
            });

            return true;
//        }
        }
        return super.onOptionsItemSelected(item);}

}
