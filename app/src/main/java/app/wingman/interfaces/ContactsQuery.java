/**
 * 
 */
package app.wingman.interfaces;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * @author titech
 *
 */
public interface ContactsQuery {
	
	final static Uri CONTENT_URI = Phone.CONTENT_URI;
	//String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"+ ("1") + "'";
//    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
//            + " COLLATE LOCALIZED ASC";
//	return new CursorLoader(this, CONTENT_URI, null, selection
//            + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER
//            + "=1", null, sortOrder); 
    // An identifier for the loader
    final static int QUERY_ID = 1;

    // A content URI for the Contacts table
   // final static Uri CONTENT_URI = Contacts.CONTENT_URI;

    // The search/filter query Uri
    final static Uri FILTER_URI = Phone.CONTENT_URI;

    // The selection clause for the CursorLoader query. The search criteria defined here
    // restrict results to contacts that have a display name and are linked to visible groups.
    // Notice that the search on the string provided by the user is implemented by appending
    // the search string to CONTENT_FILTER_URI.
    @SuppressLint("InlinedApi")
    final static String SELECTION =
            (Contacts.DISPLAY_NAME) +
            "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1"+ " AND " + Contacts.HAS_PHONE_NUMBER + "=1";
   

    // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
    // sort key allows for localization. In earlier versions. use the display name as the sort
    // key.
    @SuppressLint("InlinedApi")
    final static String SORT_ORDER =
         Contacts.DISPLAY_NAME;

    // The projection for the CursorLoader query. This is a list of columns that the Contacts
    // Provider should return in the Cursor.
    @SuppressLint("InlinedApi")
    final static String[] PROJECTION = {

            // The contact's row id
            Contacts._ID,

            // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
            // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
            // a "permanent" contact URI.
            Contacts.LOOKUP_KEY,

            // In platform version 3.0 and later, the Contacts table contains
            // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
            // some other useful identifier such as an email address. This column isn't
            // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
            // instead.
       Contacts.DISPLAY_NAME,

            // In Android 3.0 and later, the thumbnail image is pointed to by
            // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
            // you generate the pointer from the contact's ID value and constants defined in
            // android.provider.ContactsContract.Contacts.
            		Phone.CONTACT_ID,
          
            		Phone.NUMBER,
            				 
            				 
            // The sort order column for the returned Cursor, used by the AlphabetIndexer
          
    };
     
    // The query column numbers which map to each value in the projection
    final static int ID = 0;
    final static int LOOKUP_KEY = 1;
    final static int DISPLAY_NAME = 2;
    final static int PHONE =4;
    final static int EMAIL =3;
   
}