package app.wingman.ui.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import app.wingman.R;
import app.wingman.adapter.ContactsAdapter;
import app.wingman.interfaces.ContactsQuery;
import app.wingman.utils.PreferencesUtils;

public class PickContact extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {

    ContactsAdapter mAdapter;
    ListView   contactlist;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private ArrayList<String> alphabetarray = new ArrayList<String>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();

    public static ArrayList<String> phones = new ArrayList<String>();
    ArrayList<QBUser> userslist=new ArrayList<QBUser>();
    int pageCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        retrieveAllUsersFromPage(1);

         mAdapter = new ContactsAdapter(PickContact.this);
           contactlist = (ListView) findViewById(R.id.lstcontacts);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;



            contentUri = ContactsQuery.CONTENT_URI;


            return new CursorLoader(PickContact.this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }



        return null;
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
            contactlist.setOnItemClickListener(PickContact.this);



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


                int size=users.size();
                for(int i=0;i<size;i++){

                    phones.add(users.get(i).getPhone());
                    userslist.addAll(users);
                }

              userNumber=users.size()+1;
                int currentPage = params.getInt(Consts.CURR_PAGE);
                int totalEntries = params.getInt(Consts.TOTAL_ENTRIES);

                if (userNumber < totalEntries) {
                    retrieveAllUsersFromPage(currentPage + 1);
                }else{

                    PreferencesUtils.SaveUsers("users",userslist,phones,PickContact.this);
                    getSupportLoaderManager().initLoader(1, null, PickContact.this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
