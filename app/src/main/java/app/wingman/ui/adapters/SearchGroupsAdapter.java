package app.wingman.ui.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import app.wingman.R;
import app.wingman.models.modelclass;
import app.wingman.ui.activities.LoginActivity;

/**
 * Created by karthika on 2/12/15.
 * this will show the result of group serach from toolbar
 */
public class SearchGroupsAdapter extends RecyclerView.Adapter<SearchGroupsAdapter.GroupViewHolder> {


    ArrayList<modelclass> Connectionslist;


    public SearchGroupsAdapter(ArrayList<modelclass> list){

        Connectionslist=list;


    }
    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room,parent,false);
        GroupViewHolder obj=new GroupViewHolder(layoutView);
        return obj;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

        try {
            holder.mGroupIcon.setImageResource(R.drawable.yourgroups);
            holder.mGroupName.setText(Connectionslist.get(position).getGroupName());
            JSONArray grouparray = new JSONArray(Connectionslist.get(position).getGroupUsers());
            int size=grouparray.length();
            String users="";
            for (int i = 0; i <size;i++){

                users=users+grouparray.getJSONObject(i).getString("name");

                if(i!=size-1)
                    users=users+" , ";
            }
                holder.mGroupMembers.setText(users);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return Connectionslist.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder{


        ImageView mGroupIcon;
        TextView mGroupName,mGroupMembers;
        public GroupViewHolder(View itemView) {
            super(itemView);

            mGroupIcon=(ImageView)itemView.findViewById(R.id.roomImage);
            mGroupName=(TextView) itemView.findViewById(R.id.roomName);
            mGroupMembers=(TextView)itemView.findViewById(R.id.lastMessage);

        }
    }
}
