package app.wingman.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    ArrayList<modelclass> Connectionslist;
    Context CONTEXT;

   public ConnectionsAdapter (ArrayList<modelclass> list,Context contxt){

       Connectionslist=list;
       CONTEXT=contxt;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View views= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result,parent,false);
       ViewHolder obj=new ViewHolder(views);
        return obj;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       holder.mConnectionName.setText(Connectionslist.get(position).getUserName());
        try {
            System.out.println("url for "+(Connectionslist.get(position).getUserCustomData()));
            holder.mConnectionStatus.setText(new JSONObject(Connectionslist.get(position).getUserCustomData()).getString("user_info").toString());
            System.out.println("url for "+new JSONObject(Connectionslist.get(position).getUserCustomData()).getString("profile_pic"));
            Picasso.with(CONTEXT).load(new JSONObject(Connectionslist.get(position).getUserCustomData()).getString("profile_pic"))
                    .placeholder(R.drawable.ic_user_black)

                    .transform(new CircleTransform()).into(holder.mConnectionImage)
            ;
        }catch(Exception e){
            e.printStackTrace();

            holder.mConnectionStatus.setText("I am using Wingman");
        }


    }

    @Override
    public int getItemCount() {
        return Connectionslist.size();
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
