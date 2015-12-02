package app.wingman.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
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

import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.ui.adapters.CreateGrpuserAdapter;
import app.wingman.utils.PreferencesUtils;
import app.wingman.utils.tagview.OnTagClickListener;
import app.wingman.utils.tagview.Tag;
import app.wingman.utils.tagview.TagView;

public class CreateGroup extends AppCompatActivity  {
    CreateGrpuserAdapter mAdapter;
    ListView contactlist;
    EditText groupname;
    ImageButton tags;
    Button creategrpButton;
    ArrayList <String> mcontacts= new ArrayList<String>();
    String phoneNumber;
    private List<String> selected = new ArrayList<String>();

    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private ArrayList<String> alphabetarray = new ArrayList<String>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();

    public static ArrayList<String> phones = new ArrayList<String>();
    public static ArrayList<QBUser> userslist=new ArrayList<QBUser>();
    int pageCount=0;
    JSONObject groupapi;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupname =(EditText)findViewById(R.id.groupname);
        creategrpButton = (Button)findViewById(R.id.creategrpButton);
        tags=(ImageButton)findViewById(R.id.tags);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
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
                if (CreateGrpuserAdapter.selected.size() > 1 && groupname.getText().toString().length() > 1)

                {
                    app.wingman.core.ChatService.getInstance().addDialogsUsers(CreateGrpuserAdapter.selected);

                    // Create new group dialog
                    progressBar.setVisibility(View.VISIBLE);
                    QBDialog dialogToCreate = new QBDialog();
                    dialogToCreate.setName(groupname.getText().toString());
//                    if (CreateGrpuserAdapter.selected.size() == 1) {
//                        dialogToCreate.setType(QBDialogType.PRIVATE);
//                    } else

                    {
                        dialogToCreate.setType(QBDialogType.GROUP);
                    }

                    //for creating a empty group

                  //  dialogToCreate.setOccupantsIds(getUserIds(mAdapter.getSelected()));

                    QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                        @Override
                        public void onSuccess(QBDialog dialog, Bundle args) {
                            groupapi = new JSONObject();

try{



                                groupapi.put("group_qb_id",dialog.getDialogId().toString());
                                groupapi.put("group_name",dialog.getName().toString());
                                groupapi.put("admin_id",PreferencesUtils.getData("user_id", getApplicationContext()));

                                groupapi.put("tag_list", selected.toString());

                                groupapi.put("occupantsIds",getUserIds(CreateGrpuserAdapter.selected).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

new createGroupApi().execute();
//                            if (mAdapter.getSelected().size() == 1) {
//                                startSingleChat(dialog);
//                            } else
//
                            {
//                                startGroupChat(dialog);
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

        getNumber(this.getContentResolver());

        retrieveAllUsersFromPage(1);


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
            tag.id=Integer.parseInt(c.getString("id"));
            tag.layoutColor = (R.color.colorAccent);

            tags.add(tag);


            view.addTags(tags);
            view.setTag(c.getString("id"));
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }



    view.setOnTagClickListener(new OnTagClickListener() {
        @Override
        public void onTagClick(Tag tag, int position) {
            if(selected.contains(tag.text)){
                selected.remove(   tag.id);
            }
                else{
            selected.add(tag.id+"");

            }


            Log.e("selected tags",  selected.toString());
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

    public static ArrayList<Integer> getUserIds(List<QBUser> users){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(QBUser user : users){
            ids.add(user.getId());
        }
        return ids;
    }
    public void getNumber(ContentResolver cr)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = (phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).replaceAll("\\p{P}","").replaceAll(" ","");
            System.out.println(".................."+phoneNumber);
            mcontacts.add(phoneNumber);
        }
        phones.close();// close cursor

    }
    private void retrieveAllUsersFromPage(int page){
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(100);

        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {



            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                ArrayList<QBUser>name = new ArrayList<QBUser>();
                for(int i =0;i<users.size();i++){
                    Log.e("mobile from qb",users.get(i).getPhone().toString());
                    if(containsEqualsIgnoreCase(mcontacts,users.get(i).getPhone())){



                            name.add(users.get(i));

                    }
                }



                mAdapter = new CreateGrpuserAdapter(getApplicationContext(),name );
                contactlist.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {

            }
        });

    }




    public  class createGroupApi extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
Log.e("request create grp",groupapi.toString());
            String res = Connecttoget.callJsonWithparams(Urls.CREATEGROUP,groupapi.toString());
            Log.e("response ",res);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject r = new JSONObject(s);
                progressBar.setVisibility(View.GONE);
            if(r.getInt("status")==1){

                Toast.makeText(getApplicationContext(),"Group Created..!",2000).show();
               Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
                startActivity(i);
                finish();

            }else{
                Toast.makeText(getApplicationContext(),"Error..!",2000).show();
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    } //code for comparing string with array
    boolean containsEqualsIgnoreCase(ArrayList<String> c, String s) {

        for (String str : c) {
            if (s.equalsIgnoreCase(str)) {
                Log.e("compare numbers",str+"=="+s);
                return true;
            }else{
                Log.e("compare fails",str+"=="+s);
            }
        }
        return false;
    }

}
