package app.wingman.ui.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;
import app.wingman.ui.adapters.ContactListadapter;
import app.wingman.utils.PreferencesUtils;

public class AddtogroupActivity extends AppCompatActivity {
    Toolbar tb;
    ListView contacts;
    ContactListadapter adapter;
    ArrayList <String> mcontacts= new ArrayList<String>();
    String phoneNumber;
    ArrayList <QBUser> qbuser= new ArrayList<QBUser>();
Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtogroup);
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        contacts = (ListView)findViewById(R.id.contacts);
        add =(Button)findViewById(R.id.add);
        getNumber(this.getContentResolver());
        retrieveAllUsersFromPage();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmembers();
            }
        });
    }
    public void addmembers(){

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
//        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(100);

        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

            int userNumber = 1;

            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {

               // Log.e("getqbusers",users.size()+"");


                qbuser = users;
ArrayList<QBUser>name = new ArrayList<QBUser>();
                for(int i =1;i<=mcontacts.size();i++) {

                    if (containsEqualsIgnoreCase(mcontacts, qbuser.get(i).getPhone())) {


                        name.add(qbuser.get(i));


                    }


                }

//                for(int k =0;k<GroupDetail.usersList.size();k++){
//
//                    if(name.contains(GroupDetail.usersList.get(k))){
//
//                        name.add(GroupDetail.usersList.get(k));
//
//                    }else{
//                        name.remove(GroupDetail.usersList.get(k));
//                    }
//
//                }


                    Log.e("name1",name.toString());
                Log.e("grp data",GroupDetail.usersList.toString());

                adapter = new ContactListadapter(getApplicationContext(),name );
                contacts.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onError(List<String> errors) {

            }
        });

    }
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
