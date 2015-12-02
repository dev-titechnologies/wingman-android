package app.wingman.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.models.modelclass;
import app.wingman.ui.fragment.MyRequests;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link modelclass} and makes a call to the
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestsRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestsRecyclerViewAdapter.ViewHolder> {

    private final List<modelclass> mValues;
    MyRequests.OnListFragmentInteractionListener mListener;
    Context contxt;

    public MyRequestsRecyclerViewAdapter(Context contextt,List<modelclass> items, MyRequests.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener=listener;
        contxt= contextt;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mContentView.setText(mValues.get(position).getUserName()+" added you to the group "+
                mValues.get(position).getGroupName());

        holder.mAccept.setTag(R.string.connections_userid,mValues.get(position).getConnectionsUserId());
        holder.mAccept.setTag(R.string.connections_POSITIONS,position);
        holder.mDecline.setTag(R.string.connections_POSITIONS,position);
        holder.mAccept.setTag(R.string.connections_groupid,mValues.get(position).getConnectionsGRoupId());
        holder.mDecline.setTag(R.string.connections_groupid,mValues.get(position).getConnectionsGRoupId());
        holder.mDecline.setTag(R.string.connections_userid,mValues.get(position).getConnectionsUserId());

        holder.mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplicationSingleton.GroupTag=view.getTag(R.string.connections_groupid).toString();
                ApplicationSingleton.RESPONSETAG="1";

                ApplicationSingleton.UserTag=view.getTag(R.string.connections_userid).toString();

                mValues.remove(Integer.parseInt(view.getTag(R.string.connections_POSITIONS).toString()));
                notifyDataSetChanged();
                mListener.onListFragmentInteraction(1);
            }
        });
        holder.mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationSingleton.GroupTag=view.getTag(R.string.connections_groupid).toString();
                ApplicationSingleton.UserTag=view.getTag(R.string.connections_userid).toString();
                ApplicationSingleton.RESPONSETAG="0";
                System.out.println("sizee"+mValues.size());
                mValues.remove(Integer.parseInt(view.getTag(R.string.connections_POSITIONS).toString()));
                notifyDataSetChanged();
                mListener.onListFragmentInteraction(2);
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
        public final Button mAccept;
        public final Button mDecline;
        public modelclass mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mContentView = (TextView) view.findViewById(R.id.requestcontent);
            mAccept=(Button)view.findViewById(R.id.btnaccept);
            mDecline=(Button)view.findViewById(R.id.btnreject);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
