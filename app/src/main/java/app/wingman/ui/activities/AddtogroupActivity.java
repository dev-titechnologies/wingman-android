package app.wingman.ui.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;
import app.wingman.database.CommentsDataSource;

import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.ui.adapters.ContactListadapter;
import app.wingman.ui.adapters.ContactsAdapter;
import app.wingman.utils.PreferencesUtils;

public class AddtogroupActivity extends AppCompatActivity {
    Toolbar tb;
    ListView contacts;
    ContactListadapter adapter;
    ArrayList <String> mcontacts= new ArrayList<String>();
    String phoneNumber;
    ArrayList <QBUser> qbuser= new ArrayList<QBUser>();
Button add;
    ProgressBar progressBar;
    CommentsDataSource obj;
//    List<QBUser> comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtogroup);
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        obj=new CommentsDataSource(AddtogroupActivity.this);
        obj.open();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        contacts = (ListView)findViewById(R.id.contacts);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        add =(Button)findViewById(R.id.add);
        getNumber(this.getContentResolver());

//        comments = new ArrayList<QBUser>();
//        comments = obj.getAllUsers();




        retrieveAllUsersFromPage();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmembers();
            }
        });
    }
    public void addmembers(){


        AlertDialog.Builder builder = new AlertDialog.Builder(AddtogroupActivity.this);
        builder.setTitle("Exit Group");

        builder.setMessage("Do you want to add him...");
        AlertDialog.Builder ok = builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();



new adduserGrp().execute();









            //this code is to add the user directly without any invitation


//
//                QBDialog dialog1 = new QBDialog();
//                String chatid = ChatActivity.dialog.getDialogId();
//                dialog1.setDialogId(chatid);
//
//
//                QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
//String usertoadd = getUserIds(ContactListadapter.selected).get(0).toString();
//
//Log.e("adding user ",chatid+"="+usertoadd);
//                requestBuilder.push("occupants_ids", usertoadd); // add user
//
//                QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
//                groupChatManager.updateDialog(dialog1, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
//                    @Override
//                    public void onSuccess(QBDialog dialog, Bundle args) {
//                        Toast.makeText(getApplicationContext(),"adding new  user...",2000).show();
//                        Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(List<String> errors) {
//
//                        Log.e("error on adding","error"+errors.toString());
//
//
//
//
//                    }
//                });

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
    private void retrieveAllUsersFromPage(){
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(100);

        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

            int userNumber = 1;

            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {


                ArrayList<QBUser>name = new ArrayList<QBUser>();
        for(int i =0;i<users.size();i++){
            Log.e("mobile from qb",users.get(i).getPhone().toString());
            if(containsEqualsIgnoreCase(mcontacts,users.get(i).getPhone())){


                if(!containsEqualsIgnoreCase2(GroupDetail.usersList,users.get(i).getPhone()))
                name.add(users.get(i));

            }
        }


                progressBar.setVisibility(View.GONE);
                adapter = new ContactListadapter(getApplicationContext(),name );
                contacts.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(List<String> errors) {

            }
        });

    }





//    private void retrieveAllUsersFromPage(){
//        ArrayList<modelclass>name = new ArrayList<modelclass>();
//        for(int i =0;i<comments.size();i++){
//            Log.e("user from db",comments.get(i).getUserPhone().toString());
//            if(!containsEqualsIgnoreCase(GroupDetail.usersList,comments.get(i).getUserPhone())){
//                name.add(comments.get(i));
//            }
//        }
//
//
//        adapter = new ContactListadapter(getApplicationContext(),name );
//        contacts.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }


    //code for comparing string with array
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

//comparing phone with group members
    boolean containsEqualsIgnoreCase2(ArrayList<QBUser> c, String s) {

        for (QBUser str : c) {
            if (s.equalsIgnoreCase(str.getPhone())) {
                Log.e("compare numbers",str+"=="+s);
                return true;
            }else{
                Log.e("compare fails",str+"=="+s);
            }
        }
        return false;
    }

    public static ArrayList<Integer> getUserIds(List<QBUser> users){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(QBUser user : users){
            ids.add((user.getId()));
        }
        return ids;
    }
    public  class adduserGrp extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONObject updarray = new JSONObject();
            try {
                updarray.put("sender_id",PreferencesUtils.getData("user_id",getApplicationContext()));

                updarray.put("receiver_id",getUserIds(ContactListadapter.selected));
                updarray.put("group_id",ChatActivity.dialog.getDialogId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("request add user",updarray.toString());
            String gettag= Connecttoget.callJsonWithparams(Urls.ADDUSERGRP,updarray.toString());

            return gettag;
        }

        @Override
        protected void onPostExecute(String gettag) {
            super.onPostExecute(gettag);
            try {
                JSONObject updarray_res = new JSONObject(gettag);
                Log.e("response",updarray_res.toString());
                if(updarray_res.getInt("status")==1){

                    Log.e("response  ",updarray_res.toString());
                    Toast.makeText(getApplicationContext(),"adding new  user...",2000).show();
                    Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
                    startActivity(i);
                    finish();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Adding User Failed.!!",2000).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    }
