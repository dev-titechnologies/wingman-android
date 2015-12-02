package app.wingman.ui.activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import app.wingman.R;

import app.wingman.networks.Connecttoget;
import app.wingman.pushnotifications.Consts;


import app.wingman.settings.Urls;
import app.wingman.utils.PreferencesUtils;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


public class DialogsActivity extends BaseActivity {

    private static final String TAG = DialogsActivity.class.getSimpleName();

    private ListView dialogsListView;
    private ProgressBar progressBar;

    private app.wingman.pushnotifications.PlayServicesHelper playServicesHelper;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    SearchView searchview=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);

        PreferencesUtils.saveData("callfromgroup", "false", getApplicationContext());

        playServicesHelper = new app.wingman.pushnotifications.PlayServicesHelper(this);
Log.e("regId",playServicesHelper.regId);
        dialogsListView = (ListView) findViewById(R.id.roomsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(Color.parseColor("#3F51B5"));
        mWaveSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.common_action_bar_splitter);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                if(isSessionActive()){
                    getDialogs();
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        // Register to receive push notifications events
        //
        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(app.wingman.pushnotifications.Consts.NEW_PUSH_EVENT));

        // Get dialogs if the session is active
        //
        if(isSessionActive()){
            getDialogs();
        }

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.Connect:
                        Toast.makeText(getApplicationContext(),"Connect Selected", Toast.LENGTH_SHORT).show();

                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.Request:
                        Toast.makeText(getApplicationContext(),"Request Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Groups:

                        PreferencesUtils.saveData("callfromgroup","true",getApplicationContext());
                         Intent groups = new Intent(getApplicationContext(),GroupsActivity.class);
                        startActivity(groups);
                        return true;
                    case R.id.Messages:
                        Toast.makeText(getApplicationContext(),"Messages Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.CreateGroup:
                        Toast.makeText(getApplicationContext(),"CreateGroup Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



    }

    public void getDialogs(){


        // Get dialogs
        //
        app.wingman.core.ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {

                System.out.println("load next session response");
                progressBar.setVisibility(View.GONE);
                mWaveSwipeRefreshLayout.setRefreshing(false);
                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>)object;

                // build list view
                //
                buildListView(dialogs);
            }

            @Override
            public void onError(List errors) {
                progressBar.setVisibility(View.GONE);
                mWaveSwipeRefreshLayout.setRefreshing(false);
                AlertDialog.Builder dialog = new AlertDialog.Builder(app.wingman.ui.activities.DialogsActivity.this);
                dialog.setMessage("No recent chats found").create().show();

            }
        });
    }


    void buildListView(List<QBDialog> dialogs){
        final app.wingman.ui.adapters.DialogsAdapter adapter = new app.wingman.ui.adapters.DialogsAdapter(dialogs, app.wingman.ui.activities.DialogsActivity.this);
        dialogsListView.setAdapter(adapter);


        // choose dialog
        //
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(app.wingman.ui.activities.ChatActivity.EXTRA_DIALOG, selectedDialog);

                // Open chat activity
                //
                ChatActivity.start(app.wingman.ui.activities.DialogsActivity.this, bundle);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playServicesHelper.checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rooms, menu);

        MenuItem item=(MenuItem)menu.findItem(R.id.action_search);
        SearchManager manager=(SearchManager)DialogsActivity.this.getSystemService(Context.SEARCH_SERVICE);


        if(item!=null){

            searchview=(SearchView)item.getActionView();
        }if(searchview!=null){
            ComponentName cn = new ComponentName(this, SearchActivity.class);
            searchview.setSearchableInfo(manager.getSearchableInfo(DialogsActivity.this.getComponentName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {

            // go to New Dialog activity
            //
//            Intent intent = new Intent(app.wingman.ui.activities.DialogsActivity.this, NewDialogActivity.class);
//            startActivity(intent);
//            finish();
            Intent intent = new Intent(app.wingman.ui.activities.DialogsActivity.this, CreateGroup.class);
            startActivity(intent);
            finish();
            return true;
        } if (id == R.id.action_contacts) {

            // go to New Dialog activity
            //
            Intent intent = new Intent(app.wingman.ui.activities.DialogsActivity.this, PickContact.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Our handler for received Intents.
    //
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String message = intent.getStringExtra(Consts.EXTRA_MESSAGE);

            Log.i(TAG, "Receiving event " + Consts.NEW_PUSH_EVENT + " with data: " + message);
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
                    System.out.println("load next session recreate");
                    getDialogs();
                }
            }
        });
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
//    public   void showNotification(){
//
//        // define sound URI, the sound to be played when there's a notification
//
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        // intent triggered, you can add other intent for other actions
//
//        Intent intent = new Intent(DialogsActivity.this, DialogsActivity.class);
//
//        PendingIntent pIntent = PendingIntent.getActivity(DialogsActivity.this, 0, intent, 0);
//
//        // this is it, we'll build the notification!
//
//        // in the addAction method, if you don't want any icon, just set the first param to 0
//
//        Notification mNotification = new Notification.Builder(this)
//
//                .setContentTitle("New Post!")
//
//                .setContentText("Here's an awesome update for you!")
//
//                .setSmallIcon(R.drawable.ic_user)
//
//                .setContentIntent(pIntent)
//
//                .setSound(soundUri)
//
//
//
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // If you want to hide the notification after it was selected, do the code below
//
//        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, mNotification);
//
//    }


//    public  class getallData extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//
//
//            String gettag= Connecttoget.callJsonWithparams(Urls.GETNAMES);
//            try {
//                JSONObject alltagarray = new JSONObject(gettag);
//                if(alltagarray.getInt("status")==1){
//                    JSONArray tags = alltagarray.getJSONArray("data");
//                    Log.e("get tag ",tags.toString());
//                    PreferencesUtils.saveData("ALLTAGS", tags.toString(), getApplicationContext());}
//                else{
//                    Toast.makeText(getApplicationContext(),"ERROR FROM SERVER.!!",2000).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//
//            return null;
//        }
//
//
//    }

}
