package app.wingman.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.qb.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.wingman.R;
import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.ui.adapters.ConnectionsAdapter;
import app.wingman.ui.adapters.YourGroupsAdapter;
import app.wingman.utils.PreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link YourGroups#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourGroups extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public YourGroups() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourGroups.
     */
    // TODO: Rename and change types and number of parameters
    public static YourGroups newInstance(String param1, String param2) {
        YourGroups fragment = new YourGroups();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
ListView groups;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      new   getyourgroups().execute();
        View v;
        v = inflater.inflate(R.layout.fragment_your_groups, container, false);
        groups = (ListView)v.findViewById(R.id.groups);



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    public  class getyourgroups extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            JSONObject temp = new JSONObject();
            try {
                temp.put("user_id",PreferencesUtils.getData("user_id",getActivity()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String gettag= Connecttoget.callJsonWithparams(Urls.GETYOURGROUPS,temp.toString());




            return gettag;
        }

        @Override
        protected void onPostExecute(String getdata) {
            super.onPostExecute(getdata);
            try {
                Log.e("res yourgroups",getdata);
                JSONObject alltagarray = new JSONObject(getdata);
                if(alltagarray.getInt("status")==1) {
JSONArray cc = alltagarray.getJSONArray("groupList");
                    ArrayList<String> groupnames = new ArrayList<String>();
                    for(int i =0;i<cc.length();i++){

                        JSONObject c = cc.getJSONObject(i);

                        groupnames.add(c.getString("group_name"));

                    }
Log.e("groupnames",groupnames.toString());
                    YourGroupsAdapter adapter = new YourGroupsAdapter(groupnames,getActivity());
                    groups.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"ERROR FROM SERVER.!!",2000).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
