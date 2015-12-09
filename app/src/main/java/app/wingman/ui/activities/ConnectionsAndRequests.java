package app.wingman.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestUpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.ui.adapters.MyRequestsRecyclerViewAdapter;
import app.wingman.ui.fragment.MyConnections;
import app.wingman.ui.fragment.MyRequests;
import app.wingman.utils.PreferencesUtils;

public class ConnectionsAndRequests extends AppCompatActivity implements MyConnections.OnListFragmentInteractionListener,MyRequests.OnListFragmentInteractionListener {

    boolean fromRequest=false;  // TO CHECK WHETHER READSTATUS URL OF CONNECTIONS OR INVITATIONS NEED TO BE CALLED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_and_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if (findViewById(R.id.content_frame) != null) {


            if (savedInstanceState != null) {
                return;
            }

          if( ApplicationSingleton.CALLINGREQUESTS){

              getSupportActionBar().setTitle("My Requests");

              MyRequests firstFragment = new MyRequests();


              firstFragment.setArguments(getIntent().getExtras());


              getSupportFragmentManager().beginTransaction()
                      .add(R.id.content_frame, firstFragment).commit();

          }else {

              getSupportActionBar().setTitle("My Connections");
              MyConnections firstFragment = new MyConnections();


              firstFragment.setArguments(getIntent().getExtras());


              getSupportFragmentManager().beginTransaction()
                      .add(R.id.content_frame, firstFragment).commit();
          }
        }
    }

    @Override
    public void onListFragmentInteraction(int position) {



        if(position==0){   // click from connections list
            fromRequest=false;

            new setReadStatus().execute();

        }else if(position==1 || position==2){  // click from requests page accept button or reject button

            fromRequest=true;
            new RespondToRequests().execute();

        }

    }


    /**
     * RESPOND TO ACCEPT/REJECT response

     */

    public class RespondToRequests extends AsyncTask<String,String,Boolean> {

        String apiresultMessage,apiresult;


        @Override
        protected Boolean doInBackground(String... strings) {

            try{
                ApplicationSingleton.objList.clear();
                JSONObject obj=new JSONObject();
                obj.put("user_qb_id", PreferencesUtils.getData("userid", ConnectionsAndRequests.this));
                obj.put("group_qb_id",ApplicationSingleton.GroupTag );
                obj.put("accept",ApplicationSingleton.RESPONSETAG);

                System.out.println("poassing"+obj.toString()+" " +ApplicationSingleton.apiresult);
                apiresult= (Connecttoget.callJsonWithparams(Urls.RESPOND_TO_INVITATION,obj.toString()));

                if ( new JSONObject(apiresult).getInt("status")==1)  {



                    return true;
                }else
                    apiresultMessage=new JSONObject(apiresult).getString("message");
            }catch(JSONException E){

                E.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){



                new setReadStatus().execute();
            }else
                ApplicationSingleton.ShowFailedAlert(ConnectionsAndRequests.this,apiresultMessage);
        }
    }

    /**
     * set read status
     */
    public class setReadStatus extends AsyncTask<String,String,Boolean>{


        String apiresultMessage,apiresult;
        @Override
        protected Boolean doInBackground(String... strings) {


            try{

                JSONObject params=new JSONObject();
                params.put("user_qb_id",ApplicationSingleton.UserTag);
                params.put("group_qb_id",ApplicationSingleton.GroupTag);
                if(fromRequest)
               apiresult=Connecttoget.callJsonWithparams(Urls.SETREADSTATUSINVITATION,params.toString());
                else
                   apiresult=Connecttoget.callJsonWithparams(Urls.SETREADSTATUS,params.toString());

                if (new JSONObject(apiresult).getInt("status")==1)  {

                    return true;
                }

            }catch(JSONException E){

                E.printStackTrace();
            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(ApplicationSingleton.RESPONSETAG.equals("1") && fromRequest)
                ApplicationSingleton.ShowSuccessAlert(ConnectionsAndRequests.this,"Successfully added to group");
            Intent in=new Intent(ConnectionsAndRequests.this, ConnectionsAndRequests.class);
           startActivity(in);

            finish();

        }
    }
    /**
     * add myself to chat group
     */
    public void AddToGroup(){


                QBDialog dialog1 = new QBDialog();
                String chatid = ApplicationSingleton.GroupTag;
                dialog1.setDialogId(chatid);


                QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
             final String usertoadd = ApplicationSingleton.UserTag;


                requestBuilder.push("occupants_ids","6967861"); // add user

                QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                groupChatManager.updateDialog(dialog1, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {

                        ApplicationSingleton.ShowSuccessAlert(ConnectionsAndRequests.this,"Successfully added to group");
                    }

                    @Override
                    public void onError(List<String> errors) {

                        ApplicationSingleton.ShowFailedAlert(ConnectionsAndRequests.this,"Adding to group failed "+errors.toString()+usertoadd);




                    }
                });
    }

}
