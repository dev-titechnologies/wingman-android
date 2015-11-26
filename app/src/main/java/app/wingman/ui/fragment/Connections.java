package app.wingman.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.listeners.QBParticipantListener;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestUpdateBuilder;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.wingman.R;

import app.wingman.core.Chat;
import app.wingman.ui.activities.ChatActivity;
import app.wingman.ui.activities.GroupsActivity;
import app.wingman.utils.PreferencesUtils;


public class Connections extends Fragment {

    public Connections() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private ListView groupsListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connections, container, false);

        groupsListView = (ListView)view. findViewById(R.id.roomsList);
            getDialogs();

        return  view;
    }
    public void getDialogs(){


        // Get dialogs
        //
        app.wingman.core.ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {

                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>) object;

                // build list view
                //
                buildListView(dialogs);
            }

            @Override
            public void onError(List errors) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("No recent chats found").create().show();

            }
        });
    }

    private QBGroupChatManager groupChatManager;
    private QBGroupChat groupChat;
    void buildListView(List<QBDialog> dialogs){
        final app.wingman.ui.adapters.DialogsAdapter adapter = new app.wingman.ui.adapters.DialogsAdapter(dialogs, getActivity());
        groupsListView.setAdapter(adapter);
adapter.notifyDataSetChanged();
        // choose dialog


        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(app.wingman.ui.activities.ChatActivity.EXTRA_DIALOG, selectedDialog);

                // Open chat activity
                //
//                ChatActivity.start(getActivity(), bundle);
//
//                getActivity(). finish();


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Exit Group");

                builder.setMessage("Do you really want to leave this group...");
                AlertDialog.Builder ok = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//leaving a group
                        QBDialog dialog1 = new QBDialog();
                        dialog1.setDialogId(selectedDialog.getDialogId());//group id for leav


                        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();



                        requestBuilder.pullAll("occupants_ids", PreferencesUtils.getData("user_id",getActivity())); // Remove yourself from group (user with ID 22)

                        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                        groupChatManager.updateDialog(dialog1, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
                            @Override
                            public void onSuccess(QBDialog dialog, Bundle args) {
                                Log.e("group exit  ","success"+dialog.toString());
                                getDialogs();
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(List<String> errors) {
                                Log.e("group exit","error"+errors.toString());

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

}
