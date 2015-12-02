package app.wingman.ui.activities;


import android.content.Intent;


import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;

import com.quickblox.core.request.QBPagedRequestBuilder;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import app.wingman.R;



import app.wingman.ui.adapters.ContactsAdapter;

import app.wingman.interfaces.ContactsQuery;
import app.wingman.utils.GetMyLocation;
import app.wingman.utils.PreferencesUtils;

public class PickContact  extends app.wingman.ui.activities.BaseActivity implements QBEntityCallback<ArrayList<QBUser>>,
       AdapterView.OnItemClickListener {

    ContactsAdapter mAdapter;
    ListView   contactlist;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private ArrayList<String> alphabetarray = new ArrayList<String>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();



    int pageCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
                startActivity(i);
                finish();
            }
        });


         mAdapter = new ContactsAdapter(PickContact.this);
           contactlist = (ListView) findViewById(R.id.lstcontacts);



    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * add location
     */
public void addLocation(){

    if(isSessionActive()){




        GetMyLocation obj=new GetMyLocation(getApplicationContext());
        obj.getMyLocation();

        //updateUserProfile();


    }


   
}

    @Override
    public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

        System.out.println("size"+qbUsers.size());
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> list) {

    }
    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {

                    addLocation();
                }
            }
        });
    }




}
