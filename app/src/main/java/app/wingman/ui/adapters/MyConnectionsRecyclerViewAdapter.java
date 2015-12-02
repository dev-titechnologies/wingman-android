package app.wingman.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;



import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.models.modelclass;
import app.wingman.ui.fragment.MyConnections;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} 
 * specified
 * TODO: Replace the implementation with code for your data type.
 */
public class MyConnectionsRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionsRecyclerViewAdapter.ViewHolder> {

    private final List<modelclass> mValues;
    MyConnections.OnListFragmentInteractionListener mListener;
    Context contxt;


    public MyConnectionsRecyclerViewAdapter(Context contextt, ArrayList<modelclass> items, MyConnections.OnListFragmentInteractionListener mListenerr) {
        mValues = items;
        mListener=mListenerr;
        contxt= contextt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_myconnections_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mContentView.setText(mValues.get(position).getConnectionsString());
        holder.mView.setTag(R.string.connections_userid,mValues.get(position).getConnectionsUserId());
        holder.mView.setTag(R.string.connections_groupid,mValues.get(position).getConnectionsGRoupId());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApplicationSingleton.UserTag=v.getTag(R.string.connections_userid).toString();
                ApplicationSingleton.GroupTag=v.getTag(R.string.connections_groupid).toString();
                mListener.onListFragmentInteraction(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mContentView;
        public modelclass mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
