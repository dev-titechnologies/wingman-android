package app.wingman.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import app.wingman.ApplicationSingleton;
import app.wingman.R;

import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;

import app.wingman.ui.adapters.MyConnectionsRecyclerViewAdapter;
import app.wingman.utils.PreferencesUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MyConnections extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyConnections() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyConnections newInstance(int columnCount) {
        MyConnections fragment = new MyConnections();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myconnections, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

           new GetConnections().execute();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * call get connections api
     */

    public class GetConnections extends AsyncTask<String,String,Boolean>{


        @Override
        protected Boolean doInBackground(String... strings) {

            try{
                ApplicationSingleton.objList.clear();
                JSONObject obj=new JSONObject();
                obj.put("user_id", PreferencesUtils.getData("userid", getActivity()));
                System.out.println("poassing"+obj.toString());
                ApplicationSingleton.apiresult= (Connecttoget.callJsonWithparams(Urls.GET_CONNECTIONS,obj.toString()));
                if ( new JSONObject(ApplicationSingleton.apiresult).getInt("status")==1 &&
                        new JSONObject(ApplicationSingleton.apiresult).has("connections")) {
                    ApplicationSingleton.apiresultJSON = new JSONObject(ApplicationSingleton.apiresult).getJSONArray("connections");
                    int length = ApplicationSingleton.apiresultJSON.length();

                        for (int i = 0; i < length; i++) {

                            ApplicationSingleton.apiresultJSONOBJECT=ApplicationSingleton.apiresultJSON.getJSONObject(i);
                            ApplicationSingleton.responseobj.setConnectionsUserId(ApplicationSingleton.apiresultJSONOBJECT.getString("user_qb_id"));
                            ApplicationSingleton.responseobj.setConnectionsGRoupId(ApplicationSingleton.apiresultJSONOBJECT.getString("group_qb_id"));
                            ApplicationSingleton.objList.add( ApplicationSingleton.responseobj);
                        }

                    return true;
                }else
                    ApplicationSingleton.apiresultMessage=new JSONObject(ApplicationSingleton.apiresult).getString("message");
            }catch(JSONException E){

                E.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){

                recyclerView.setAdapter(new MyConnectionsRecyclerViewAdapter(getActivity(),ApplicationSingleton.objList,mListener));
            }else
                ApplicationSingleton.ShowFailedAlert(getActivity(),ApplicationSingleton.apiresultMessage);
        }
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(int position);
    }


}
