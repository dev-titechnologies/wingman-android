/**
 * 
 */
package app.wingman.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author titech
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


	private static final String DATABASE_NAME = "WINGMAN.db";

	private static final int DATABASE_VERSION = 1;


	 public static final String COLUMN_ID = "_id";
	  public static final String TABLE_USER = "user";
	  public static final String TABLE_GROUP = "group";

	public static final String TABLE_CACHE = "cache";
	public static final String PARENT_ID = "parentid";
	  public static final String CHAT_ID = "chatid";
	  public static final String USER_NAME = "name";
	  public static final String USER_EMAIL = "email";
	  public static final String USER_CUSTOMDATA = "userdata";
	public static final String USER_PHONE = "phone";


	// FOR TABLE GROUP
	public static final String GROUP_ID = "group_id";
	public static final String GROUP_NAME = "name";
	public static final String USERS = "name";
	public static final String ADMIN_ID = "admin_id";
	public static final String TAGS="tags";


// FOR CACHE
	public static final String REQ_PARAMS = "req_params";
	public static final String RESPONSE = "response";
	public static final String URL = "url";
	public static final String TIME = "time";




	  // Database creation sql statement
	  private static final String CREATE_USER = "create table "
	      + TABLE_USER + "("+ COLUMN_ID
	      + " integer primary key autoincrement, " + CHAT_ID
	      + " text not null, " + USER_NAME
			  + " text not null, " + USER_CUSTOMDATA
			  + " text not null, " + USER_EMAIL

			  + " text not null, " +USER_PHONE
	      + " text not null);";

	private static final String CREATE_GROUP = "create table "
			+TABLE_GROUP + "("+ COLUMN_ID
			+ " integer primary key autoincrement, " + GROUP_ID
			+ " text not null, " + GROUP_NAME
			+ " text not null, " + USERS
			+ " text not null, " + ADMIN_ID
			+ " text not null, " + TAGS
			+ " text not null);";

	private static final String STATE_CACHE = "create table "
			+ TABLE_CACHE + "("+ COLUMN_ID
			+ " integer primary key autoincrement, "
		 + URL
			+ " text not null, " + REQ_PARAMS
			+ " text not null, " + RESPONSE
			+ " text not null, " + TIME
			+ " text not null);" ;

	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  
	    database.execSQL(CREATE_USER);
		

		  database.execSQL(CREATE_GROUP);
	  }



	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
//	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINCATEGORY);
//	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_1);
//		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_2);
//		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_3);
//		  db.execSQL(STATE_CACHE);
//	    onCreate(db);
	  }



	} 
