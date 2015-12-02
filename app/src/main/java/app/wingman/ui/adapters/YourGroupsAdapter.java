package app.wingman.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import app.wingman.R;
import app.wingman.models.modelclass;
import app.wingman.utils.CircleTransform;

/**
 * Class used as the recyclerviewadapter of connections list populated from search option of toolbar
 * Created by karthika on 24/11/15.
 */
public class YourGroupsAdapter extends BaseAdapter {

    ArrayList<String> groupslist;
    Context CONTEXT;

   public YourGroupsAdapter(ArrayList<String> list, Context contxt){

       groupslist=list;
       CONTEXT=contxt;

    }






    @Override
    public int getCount() {
        return groupslist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
TextView name;
    ImageView groupic;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View views= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room,parent,false);
        name =(TextView)views.findViewById(R.id.roomName);
        groupic =(ImageView)views.findViewById(R.id.roomImage);
        name.setText(groupslist.get(position).toString());
        Picasso.with(CONTEXT)
                .load(R.drawable.yourgroups)

                .into(groupic);
        return views;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mConnectionName;
        TextView mConnectionStatus;
        ImageView mConnectionImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mConnectionImage=(ImageView)itemView.findViewById(R.id.imgreslt);
            mConnectionName=(TextView) itemView.findViewById(R.id.txtnamereslt);
            mConnectionStatus=(TextView) itemView.findViewById(R.id.txtstatusreslt);
        }
    }
}
