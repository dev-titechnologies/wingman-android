package app.wingman.ui.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;


/**
 * Created by root on 26/11/15.
 */

public class ContactListadapter extends BaseAdapter implements Filterable {
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
    private List<QBUser> selected = new ArrayList<QBUser>();
    public ContactListadapter(Context context,ArrayList <QBUser> qbuser ) {


        this.context = context;
this.qbuser = qbuser;
        getFilter();

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
            convertView = inflater.inflate(R.layout.list_item_user, viewGroup, false);
            holder = new ViewHolder();
            holder.userLogin = (TextView)convertView.findViewById(R.id.userLogin);
            holder.userlogo = (ImageView) convertView.findViewById(R.id.userlogo);
            holder.addCheckBox = (CheckBox) convertView.findViewById(R.id.addCheckBox);
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


//                if(containsEqualsIgnoreCase(contactItems,qbuser.get(position).getPhone())) {
//                    holder.userLogin.setText(qbuser.get(position).getFullName().toString());
//                }
            holder.userLogin.setText(qbuser.get(position).getFullName());

        }
        return convertView;
    }



    @Override
    public Filter getFilter() {
        return null;
    }
}
