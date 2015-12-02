package app.wingman.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.HexagonImageView;
import com.qb.gson.JsonObject;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.wingman.R;
import app.wingman.utils.PreferencesUtils;

public class GroupDetail extends AppCompatActivity {
    private List<String> qboppids = new ArrayList<String>();
    Toolbar tb;
    HexagonImageView user1, user2, user3, user4, user5, user6;
    public static ArrayList<QBUser> usersList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        tb = (Toolbar) findViewById(R.id.toolbar);


        user1 = (HexagonImageView) findViewById(R.id.user1);
        user2 = (HexagonImageView) findViewById(R.id.user2);
        user3 = (HexagonImageView) findViewById(R.id.user3);
        user4 = (HexagonImageView) findViewById(R.id.user4);
        user5 = (HexagonImageView) findViewById(R.id.user5);
        user6 = (HexagonImageView) findViewById(R.id.user6);


        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((ChatActivity.dialog.getName()).toUpperCase());

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.e("dialog", ChatActivity.dialog.getOccupants().toArray().toString());

        for (int i = 0; i < ChatActivity.dialog.getOccupants().toArray().length; i++) {

            qboppids.add(ChatActivity.dialog.getOccupants().toArray()[i].toString());


        }

        Log.e("user id", ChatActivity.dialog.getUserId().toString());

        Log.e("all oop ids", qboppids.toString());


        new getusers().execute();

    }


    class getusers extends AsyncTask<String, String, ArrayList<QBUser>> {


        @Override
        protected ArrayList<QBUser> doInBackground(String... params) {



            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
            requestBuilder.setPage(1);
            requestBuilder.setPerPage(10);

            //group user occupant ids
            List<Integer> participantIdsList = ChatActivity.dialog.getOccupants();

            try {

                //getting group user list by passying the occupant ids
                usersList = QBUsers.getUsersByIDs(participantIdsList, requestBuilder, new Bundle());


                Log.e("userlist", usersList.toString());

            } catch (QBResponseException e) {
                e.printStackTrace();
            }

            return usersList;
        }

        @Override
        protected void onPostExecute(ArrayList<QBUser> s) {
            super.onPostExecute(s);

            for (int i = 0; i < s.size(); i++) {

                if (i == 0) {

                    try {
                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (i == 1) {

                    try {

                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (i == 2) {

                    try {
                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (i == 3) {

                    try {
                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user4);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (i == 4) {

                    try {
                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (i == 5) {

                    try {
                        JSONObject cc;
                        if (s.get(i).getCustomData().equals("{}")) {
                            cc = new JSONObject("{\"profile_pic\":\"https://cdn3.iconfinder.com/data/icons/rcons-user-action/32/boy-512.png\"}");
                        } else {
                            cc = new JSONObject(s.get(i).getCustomData().toString());
                        }

                        Picasso.with(getApplicationContext())
                                .load(cc.getString("profile_pic"))

                                .error(R.drawable.ic_user_pink)         // optional
                                .into(user6);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();




        if (id == R.id.exit) {

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetail.this);
            builder.setTitle("Exit Group");

            builder.setMessage("Do you really want to leave this group...");
            AlertDialog.Builder ok = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//leaving a group

                    QBDialog dialog1 = new QBDialog();
                    dialog1.setDialogId(ChatActivity.dialog.getDialogId());


                    QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();



                    requestBuilder.pullAll("occupants_ids", PreferencesUtils.getData("user_id",getApplicationContext())); // Remove yourself (user with ID 22)

                    QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    groupChatManager.updateDialog(dialog1, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
                        @Override
                        public void onSuccess(QBDialog dialog, Bundle args) {
                            Toast.makeText(getApplicationContext(),"Deleteing Group",2000).show();
                            Intent i = new Intent(getApplicationContext(),DialogsActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onError(List<String> errors) {

                            Log.e("error on leaving","error"+errors.toString());




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




            return true;
        }else if(id==R.id.add_user){
Intent k = new Intent(getApplicationContext(),AddtogroupActivity.class);
            startActivity(k);
        }




        return super.onOptionsItemSelected(item);
    }


}
