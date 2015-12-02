package app.wingman.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;
import app.wingman.models.modelclass;


/**
 * Created by root on 26/11/15.
 */

public class CreateGrpuserAdapter extends BaseAdapter implements Filterable {
    @Override
    public int getCount() {
        return qbuser.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    Context context;
    int count=0;
    ArrayList <QBUser> qbuser= new ArrayList<QBUser>();
    public static List<QBUser> selected = new ArrayList<QBUser>();
    public CreateGrpuserAdapter(Context context, ArrayList <QBUser> qbuser ) {


        this.context = context;
        this.qbuser = qbuser;

    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    class ViewHolder {
        ImageView userlogo;
        TextView userLogin;
        CheckBox addCheckBox;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        LayoutInflater inflater;
        if (convertView == null) {

            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_row_item, viewGroup, false);
            holder = new ViewHolder();
            holder.userLogin = (TextView)convertView.findViewById(R.id.cntctperson_name);
            holder.userlogo = (ImageView) convertView.findViewById(R.id.person_photo);
            holder.addCheckBox = (CheckBox) convertView.findViewById(R.id.invite);
            holder.addCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((((CheckBox) v).isChecked())) {
                        selected.add(qbuser.get(position));
                    } else {
                        selected.remove(qbuser.get(position));
                    }
                }
            });
            holder.addCheckBox.setText("Add");

            try{
if(!qbuser.get(position).getCustomData().equals("{}")){
    try {
        Picasso.with(context)
                            .load(new JSONObject(qbuser.get(position).getCustomData()).getString("profile_pic"))
                            .into(holder.userlogo);

    } catch (JSONException e) {
        e.printStackTrace();
    }
}else{
    Picasso.with(context)
            .load(R.drawable.ic_user)
            .into(holder.userlogo);
}}
            catch (Exception e){
return null;
//Log.e("erro in",qbuser.get(position).getFullName());

            }

//                if(containsEqualsIgnoreCase(contactItems,qbuser.get(position).getPhone())) {
//                    holder.userLogin.setText(qbuser.get(position).getFullName().toString());
//                }
            holder.userLogin.setText(qbuser.get(position).getFullName());

            final int version = Build.VERSION.SDK_INT;
            if (version >= 23) {
                holder.addCheckBox.setTextColor(context.getColor(R.color.colorAccent));
                holder.userLogin.setTextColor(context.getColor(R.color.black));
            } else {
                holder.addCheckBox.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.userLogin.setTextColor(context.getResources().getColor(R.color.black));
            }


//            try {
//                JSONObject cdata = new JSONObject(qbuser.get(position).getCustomData());
//                Picasso.with(context)
//                        .load(cdata.getString("profile_pic"))
//                        .into(holder.userlogo);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }
        return convertView;
    }



    @Override
    public Filter getFilter() {
        return null;
    }
}
