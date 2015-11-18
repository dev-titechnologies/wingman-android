/**
 * 
 */
package app.wingman.adapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.interfaces.ContactsQuery;
import app.wingman.ui.activities.PickContact;
import app.wingman.utils.Roundedimageview;


/**
 * @author titech
 *
 */
public class ContactsAdapter extends CursorAdapter implements SectionIndexer{
    private LayoutInflater mInflater; // Stores the layout inflater
  //  private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
    private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
    public static String displayName,phone;
    //String  email="";


    
    public static abstract class Row {}
	
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Instantiates a new Contacts Adapter.
     * @param context A context that has access to the app's layout.
     */
    public ContactsAdapter(Context context) {
        super(context, null, 0);

        // Stores inflater for use later
        mInflater = LayoutInflater.from(context);

       
      

    
    }
    
    public static final class Items extends Row {
        public final String text;

        public Items(String text) {
            this.text = text;
        }
    }
	 public static final class Section extends Row {
	        public final String text;

	        public Section(String text) {
	            this.text = text;
	        }
	    }

    /**
     * Identifies the start of the search string in the display name column of a Cursor row.
     * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
     * return 1.
     *
     * @param displayName The contact display name.
     * @return The starting position of the search string in the display name, 0-based. The
     * method returns -1 if the string is not found in the display name, or if the search
     * string is empty or null.
     */
//    private int indexOfSearchQuery(String displayName) {
//        if (!TextUtils.isEmpty(mSearchTerm)) {
//            return displayName.toLowerCase(Locale.getDefault()).indexOf(
//                    mSearchTerm.toLowerCase(Locale.getDefault()));
//        }
//        return -1;
//    }

    /**
     * Overrides newView() to inflate the list item views.
     */
    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        // Inflates the list item layout.
        final View itemLayout =
                mInflater.inflate(R.layout.contact_row_item, viewGroup, false);
        

        // Creates a new ViewHolder in which to store handles to each view resource. This
        // allows bindView() to retrieve stored references instead of calling findViewById for
        // each instance of the layout.
        final ViewHolder holder = new ViewHolder();
        holder.text1 = (TextView) itemLayout.findViewById(R.id.cntctperson_name);
        holder.text2 = (TextView) itemLayout.findViewById(R.id.cntctemail);
        holder.invite = (Button) itemLayout.findViewById(R.id.invite);
        holder.icon = (ImageView) itemLayout.findViewById(R.id.person_photo);
        
        

        // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
        // available to bindView and other methods that receive a handle to the item view.
        itemLayout.setTag(holder);
        itemLayout.setTag(R.id.cntctperson_name, holder.text1);
        itemLayout.setTag(R.id.cntctemail, holder.text2);
        itemLayout.setTag(R.id.invite, holder.invite);
        itemLayout.setTag(R.id.person_photo, holder.icon);
    	
        final int position=cursor.getPosition();

//        // Returns the item layout view
//        itemLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Phonebook phd=new Phonebook();
//				if(holder.selection.isChecked()){
//					
//					holder.selection.setChecked(false);
//				}
//				else{
//			
//					holder.selection.setChecked(false);
//				
//					 
//				}
//				
//			}
//		});
        return itemLayout;
    }

    /**
     * Binds data from the Cursor to the provided view.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Gets handles to individual view resources
        final ViewHolder holder = (ViewHolder) view.getTag();

        // For Android 3.0 and later, gets the thumbnail image Uri from the current Cursor row.
        // For platforms earlier than 3.0, this isn't necessary, because the thumbnail is
        // generated from the other fields in the row.
        holder.text1.setTag(cursor.getPosition());
        holder.text2.setTag(cursor.getPosition());

        holder.icon.setTag(cursor.getPosition());

        displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
        phone = (cursor.getString(ContactsQuery.PHONE)).trim().replaceAll("\\s+", "").replaceAll("-", "");


            holder.text1.setText(displayName);
            if (phone.trim().length() > 0)
                holder.text2.setText(phone);
            else
                holder.text2.setVisibility(View.INVISIBLE);


            String email = "";

            try {

                String contactId = cursor.getString(ContactsQuery.EMAIL);
                Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{contactId}, null);
                email = "";

                while (emails.moveToNext()) {


                    email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));




                }
                emails.close();


            } catch (Exception e) {

                //System.out.println("error"+e);
                email = "";

            }


            try {
                Bitmap bmp = ApplicationSingleton.fetchThumbnail(context, ApplicationSingleton.fetchThumbnailId(context, phone));
                //  //System.out.println("bmp"+bmp);
                holder.icon.setImageBitmap(Roundedimageview.getCroppedBitmap(bmp, bmp.getWidth() - 20));
            } catch (Exception e) {
                //System.out.println(e);
                Bitmap bm = createTHumbnail(context.getResources().openRawResource(R.drawable.ic_user_black));

                holder.icon.setImageBitmap(Roundedimageview.getCroppedBitmap(bm, bm.getWidth() - 20));
            }

        if (PickContact.phones.contains(phone)) {

            System.out.println("phone is there"+" "+PickContact.phones+" real "+phone);
            holder.invite.setTag((R.string.emailtag), phone);
            holder.invite.setText(context.getResources().getString(R.string.action_add));

        } else {
            holder.invite.setText(context.getResources().getString(R.string.action_invite));
        }

//            try{
//            if(Constantss.selectedcontactsarray.size()>0){
//
//
//            	   if(Constantss.selectedcontactsarray.get(Integer.parseInt(holder.selection.getTag().toString())).equalsIgnoreCase("0")) //checking whether contact already selected or not
//
//                 	  holder.selection.setChecked(false);
//
//                   else
//
//                 	  holder.selection.setChecked(true);
//            }
//           else
//            	System.out.println("noselection");
//            }
//            catch(Exception e){
//            	//System.out.println("exception"+e);
//            }


            // Processes the QuickContactBadge. A QuickContactBadge first appears as a contact's
            // thumbnail image with styling that indicates it can be touched for additional
            // information. When the user clicks the image, the badge expands into a dialog box
            // containing the contact's details and icons for the built-in apps that can handle
            // each detail type.

            // Generates the contact lookup Uri
            final Uri contactUri = Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));

            // Binds the contact's lookup Uri to the QuickContactBadge
            //   holder.icon.assignContactUri(contactUri);

            // Loads the thumbnail image pointed to by photoUri into the QuickContactBadge in a
            // background worker thread


    }

    /**
     * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
     * CursorAdapter.
     */
    @Override
    public Cursor swapCursor(Cursor newCursor) {
        // Update the AlphabetIndexer with new cursor as well
       // mAlphabetIndexer.setCursor(newCursor);
        return super.swapCursor(newCursor);
    }

    /**
     * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
     * getCount returns zero. As a result, no test for Cursor == null is needed.
     */
    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    /**
     * Defines the SectionIndexer.getSections() interface.
     */
    @Override
    public Object[] getSections() {
    	String[] sections = new String[mSections.length()];
    	for (int i = 0; i < mSections.length(); i++)
    		sections[i] = String.valueOf(mSections.charAt(i));
    	return sections;
    }

    /**
     * Defines the SectionIndexer.getPositionForSection() interface.
     */
    @Override
   public int getPositionForSection(int section) {
//    	for (int i = section; i >= 0; i--) {
//    		for (int j = 0; j < getCount(); j++) {
//    			if (i == 0) {
//    				// For numeric section
//    				for (int k = 0; k <= 9; k++) {
//    					if (StringMatcher.match(String.valueOf(Phonebook.names.get(j).charAt(0)), String.valueOf(k)))
//    						return j;
//    				}
//    			} else {
//    				if (StringMatcher.match(String.valueOf(Phonebook.names.get(j).charAt(0)), String.valueOf(mSections.charAt(i))))
//    					return j;
//    			}
//    		}
//    	}
   	return 0;
    }

    /**
     * Defines the SectionIndexer.getSectionForPosition() interface.
     */
    @Override
    public int getSectionForPosition(int i) {
    	return 0;
    }

    /**
     * A class that defines fields for each resource ID in the list item layout. This allows
     * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
     * calling findViewById in each iteration of bindView.
     */
    public  class ViewHolder {
        TextView text1;
        TextView text2;
        ImageView icon;

        Button invite;
    }
    
 
    
    // for section
    
private List<Row> rows;
    
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
    
    /*
     * for creating thumbnail
     */
    
    public Bitmap createTHumbnail(InputStream fis){
    	
    	byte[] imageData = null;
    	 try 
    	    {

    	    final int THUMBNAIL_SIZE = 96;
    	    //InputStream is=getAssets().open("apple-android-battle.jpg");
    	    //FileInputStream fis = new FileInputStream("/sdcard/apple.jpg");
    	    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

    	    Float width = new Float(imageBitmap.getWidth());
    	    Float height = new Float(imageBitmap.getHeight());
    	    Float ratio = width/height;
    	    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int)(THUMBNAIL_SIZE * ratio), THUMBNAIL_SIZE, false);

    	    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
    	    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    	    imageData = baos.toByteArray();
    	    return imageBitmap;
    	   // im.setImageBitmap(imageBitmap);
    	    }
    	    catch(Exception ex) {
    	    	return null;
    	    }
    }



    
   
}
