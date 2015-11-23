package app.wingman.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;
import app.wingman.ui.activities.ChatActivity;


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


    void buildListView(List<QBDialog> dialogs){
        final app.wingman.ui.adapters.DialogsAdapter adapter = new app.wingman.ui.adapters.DialogsAdapter(dialogs, getActivity());
        groupsListView.setAdapter(adapter);

        // choose dialog
        //
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(app.wingman.ui.activities.ChatActivity.EXTRA_DIALOG, selectedDialog);

                // Open chat activity
                //
//                ChatActivity.start(getActivity(), bundle);
//
//                getActivity(). finish();


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("Exit Group");

                builder.setMessage("Do you really want to leave this group...");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
