package app.wingman.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.wingman.R;

import app.wingman.interfaces.ContactsQuery;
import app.wingman.ui.adapters.CreateGrpuserAdapter;
import app.wingman.utils.PreferencesUtils;
import app.wingman.utils.tagview.OnTagClickListener;
import app.wingman.utils.tagview.Tag;
import app.wingman.utils.tagview.TagView;

public class CreateGroup extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    CreateGrpuserAdapter mAdapter;
    ListView contactlist;
    EditText groupname;
    ImageButton tags;
    Button creategrpButton;
    private List<String> selected = new ArrayList<String>();

    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private ArrayList<String> alphabetarray = new ArrayList<String>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();

    public static ArrayList<String> phones = new ArrayList<String>();
    public static ArrayList<QBUser> userslist=new ArrayList<QBUser>();
    int pageCount=0;
    JSONObject groupapi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupname =(EditText)findViewById(R.id.groupname);
        creategrpButton = (Button)findViewById(R.id.creategrpButton);
        tags=(ImageButton)findViewById(R.id.tags);
        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showtags();
            }
        });

        setSupportActionBar(toolbar);
        creategrpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelected().size() > 1 && groupname.getText().toString().length() > 1) {
                    app.wingman.core.ChatService.getInstance().addDialogsUsers(mAdapter.getSelected());

                    // Create new group dialog
                    //
                    QBDialog dialogToCreate = new QBDialog();
                    dialogToCreate.setName(groupname.getText().toString());
                    if (mAdapter.getSelected().size() == 1) {
                        dialogToCreate.setType(QBDialogType.PRIVATE);
                    } else {
                        dialogToCreate.setType(QBDialogType.GROUP);
                    }
                    dialogToCreate.setOccupantsIds(getUserIds(mAdapter.getSelected()));

                    QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                        @Override
                        public void onSuccess(QBDialog dialog, Bundle args) {
                            groupapi = new JSONObject();

                            try {
                                groupapi.put("group_qb_id",dialog.getDialogId().toString());
                                groupapi.put("group_name",dialog.getName().toString());
                                groupapi.put("admin_id","");
                                groupapi.put("tag_list",selected);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (mAdapter.getSelected().size() == 1) {
                                startSingleChat(dialog);
                            } else {
                                startGroupChat(dialog);
                            }




                        }

                        @Override
                        public void onError(List<String> errors) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(CreateGroup.this);
                            dialog.setMessage("dialog creation errors: " + errors).create().show();
                        }
                    });
                } else {
                    if (groupname.getText().toString().length() < 1)
                        Toast.makeText(getApplicationContext(), "Group Name Should Not Be Empty!", 2000).show();
                    else
                        Toast.makeText(getApplicationContext(), "Select one more!", 2000).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DialogsActivity.class);
                startActivity(i);
                finish();
            }
        });
        retrieveAllUsersFromPage(1);

        mAdapter = new CreateGrpuserAdapter(CreateGroup.this);
        contactlist = (ListView) findViewById(R.id.lstcontacts);

    }

public void Showtags(){
    // tag dialog
    final Dialog dialog = new Dialog(CreateGroup.this);
    dialog.setContentView(R.layout.dialog_tags);
    dialog.setTitle("Tags...");
    ArrayList<Tag> tags = new ArrayList<>();

    final TagView view = (TagView)dialog.findViewById(R.id.tag_group);

    JSONArray tagData = null;
    try {
        tagData = new JSONArray(PreferencesUtils.getData("ALLTAGS", getApplicationContext()));

        for(int i =0;i<tagData.length();i++){
            JSONObject c = tagData.getJSONObject(i);
            Tag tag = new Tag(c.getString("tag_name"));
            tag.radius = 10f;
            tag.layoutColor = (R.color.colorAccent);

            tags.add(tag);


            view.addTags(tags);
            view.setTag(c.getString("tag_name"));
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

//    for(int i=0; i<10;i++){
////        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
////        View inflatedLayout= inflater.inflate(R.layout.tag_item, null, false);
//        Tag tag = new Tag("tag"+i);
//        tag.radius = 10f;
//        tag.layoutColor = (R.color.colorAccent);
//
//        tags.add(tag);
//
//
//        view.addTags(tags);
//        view.setTag("test"+i);
//
//    }

    view.setOnTagClickListener(new OnTagClickListener() {
        @Override
        public void onTagClick(Tag tag, int position) {
            if(selected.contains(tag.text)){
                selected.remove(tag.text);}
                else{
            selected.add(tag.text);}
            Log.e("selected tags",selected.toString());
            Toast.makeText(getApplicationContext(),tag.text,2000).show();

        }
    });



    Button dialogButton = (Button) dialog.findViewById(R.id.ok);
    // if button is clicked, close the custom dialog
    dialogButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });

    dialog.show();
}
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContactsQuery.QUERY_ID)
        {
            Uri contentUri;



            contentUri = ContactsQuery.CONTENT_URI;


            return new CursorLoader(CreateGroup.this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }



        return null;
    }
    public static ArrayList<Integer> getUserIds(List<QBUser> users){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(QBUser user : users){
            ids.add(user.getId());
        }
        return ids;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {



        if (loader.getId() == ContactsQuery.QUERY_ID) {

            mAdapter.swapCursor(data);

            data.moveToFirst();

//            do{
//                try{
//                    names.add(data.getString(2));
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//                Constantss.selectedcontactsarray.add("0");
//
//            } while(data.moveToNext());









            //////////////////////////////////////////////



            contactlist.setAdapter(mAdapter);
//            contactlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Log.e("item data",userslist.get(position).getPhone().indexOf());
//                }
//            });



//	     	       contactlist.setOnScrollListener(new AbsListView.OnScrollListener() {
//	     	            @Override
//	     	            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//	     	                // Pause image loader to ensure smoother scrolling when flinging
//	     	                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//
//	     	                	//System.out.println("scroll fling");
//	     	                } else {
//
//	     	                	//System.out.println("scroll not fling");
//	     	                }
//	     	            }
//
//	     	            @Override
//	     	            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
//	     	        });


        }


        ///////////////////////////////////////////////////////////////////

    }
    private void retrieveAllUsersFromPage(int page){
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(100);

        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

            int userNumber = 1;

            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {


                int size = users.size();
                for (int i = 0; i < size; i++) {

                    phones.add(users.get(i).getPhone());
                    userslist.addAll(users);
                }

                userNumber = users.size() + 1;
                int currentPage = params.getInt(Consts.CURR_PAGE);
                int totalEntries = params.getInt(Consts.TOTAL_ENTRIES);

                if (userNumber < totalEntries) {
                    retrieveAllUsersFromPage(currentPage + 1);
                } else {

                    PreferencesUtils.SaveUsers("users", userslist, phones, CreateGroup.this);
                    getSupportLoaderManager().initLoader(1, null, CreateGroup.this);
                }
            }

            @Override
            public void onError(List<String> errors) {

            }
        });

    }


    // Start

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public void startSingleChat(QBDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(app.wingman.ui.activities.ChatActivity.EXTRA_DIALOG, dialog);

        app.wingman.ui.activities.ChatActivity.start(this, bundle);
    }

    private void startGroupChat(QBDialog dialog){
        Bundle bundle = new Bundle();
        bundle.putSerializable(app.wingman.ui.activities.ChatActivity.EXTRA_DIALOG, dialog);

        app.wingman.ui.activities.ChatActivity.start(this, bundle);
    }
}
