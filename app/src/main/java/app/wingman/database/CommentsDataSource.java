/**
 * 
 */
package app.wingman.database;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONException;
import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;


import app.wingman.models.modelclass;

/**
 * @author titech
 *
 */
public class CommentsDataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumnsCategory = { MySQLiteHelper.CHAT_ID,
	      MySQLiteHelper.USER_NAME,MySQLiteHelper.USER_CUSTOMDATA,MySQLiteHelper.USER_EMAIL,MySQLiteHelper.USER_PHONE};
	private String[] ColumnsCategory = { MySQLiteHelper.GROUP_ID,MySQLiteHelper.GROUP_NAME,MySQLiteHelper.USERS,MySQLiteHelper.ADMIN_ID,MySQLiteHelper.TAGS
			};

		        

	  public CommentsDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();  
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  
	  public void bulkInsertUserData(ArrayList<modelclass> lst) {
          String sql = "INSERT INTO "+ MySQLiteHelper.TABLE_USER +" VALUES (?,?,?,?,?,?);";
          SQLiteStatement statement = database.compileStatement(sql);
          database.beginTransaction();


		  for(int i =0;i<lst.size();i++){

			try {
				Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, new String[] { MySQLiteHelper.CHAT_ID,MySQLiteHelper.USER_NAME,
						}, MySQLiteHelper.CHAT_ID + "=?",
						new String[] { (lst.get(i).getUserId()) }, null, null, null, null);


				if (cursor == null || cursor.getCount()==0) {


			System.out.println("adding user " + lst.get(i).getUserName());
	  			statement.clearBindings();

                statement.bindString(2, lst.get(i).getUserId());
              statement.bindString(3, lst.get(i).getUserName());
               statement.bindString(4, lst.get(i).getUserCustomData());
				statement.bindString(5, lst.get(i).getUserEmail());
				statement.bindString(6, lst.get(i).getUserPhone());
                statement.execute();






		  }else{

			  updateUsers(lst.get(i));
		  }

		  } catch (IllegalArgumentException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();

		  }catch(Exception e){

				e.printStackTrace();
			}
		  }database.setTransactionSuccessful();
		  database.endTransaction();
        
       // Log.e("bulk complete","bulk complete");    
      	
	  }


	public void bulkInsertGroupData(ArrayList<modelclass> lst) {
		String sql = "INSERT INTO "+ MySQLiteHelper.TABLE_GROUP +" VALUES (?,?,?,?,?,?);";
		SQLiteStatement statement = database.compileStatement(sql);
		database.beginTransaction();

		for(int i =0;i<lst.size();i++){




			try {

				Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, new String[] { MySQLiteHelper.CHAT_ID,MySQLiteHelper.USER_NAME,
						}, MySQLiteHelper.CHAT_ID + "=?",
						new String[] { (lst.get(i).getGroupid()) }, null, null, null, null);
				System.out.println("cursor size"+cursor.getCount());
				if (cursor == null) {


					statement.bindString(3, lst.get(i).getGroupName());
					statement.bindString(2, lst.get(i).getGroupid());
					//statement.bindString(4, url);
					//statement.bindString(5, tag);
					statement.bindString(4, lst.get(i).getGroupUsers());
					statement.bindString(5, lst.get(i).getAdminId());
					statement.bindString(6, lst.get(i).getGroupTags());
					statement.execute();
				}else{

					updateUsers(lst.get(i));
				}



			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		database.setTransactionSuccessful();
		database.endTransaction();



		// Log.e("bulk complete","bulk complete");

	}


	  
	  public int updateUsers(modelclass comment) {


		  System.out.println("adding user update " + comment.getUserName());
	        ContentValues values = new ContentValues();
	        values.put(MySQLiteHelper.USER_NAME, comment.getUserName());
		    values.put(MySQLiteHelper.CHAT_ID, comment.getUserId());
		    values.put(MySQLiteHelper.USER_CUSTOMDATA,comment.getUserCustomData());
		    values.put(MySQLiteHelper.USER_PHONE, comment.getUserPhone());
		  values.put(MySQLiteHelper.USER_EMAIL, comment.getUserEmail());


	        // updating row
	        return database.update(MySQLiteHelper.TABLE_USER, values, MySQLiteHelper.CHAT_ID + " = ?",
	                new String[] { String.valueOf(comment.getUserId()) });
	    }
	  
	 public  modelclass getUser(String id) {
	        
	   
	        Cursor cursor = database.query(MySQLiteHelper.TABLE_USER, new String[] { MySQLiteHelper.CHAT_ID,MySQLiteHelper.USER_NAME,
	               }, MySQLiteHelper.CHAT_ID + "=?",
	                new String[] { (id) }, null, null, null, null);  
	        if (cursor != null)  
	            cursor.moveToFirst();

		 modelclass obj = new modelclass();

		 obj.setUserId(cursor.getString(0));
		 obj.setUserName(cursor.getString(1));
	        
	        return obj;
	    }
	 public List<modelclass> getAllUsers() {
		    List<modelclass> comments = new ArrayList<modelclass>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_USER,
		        ColumnsCategory, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
				modelclass comment = cursorToComment(cursor);
		      comments.add(comment);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return comments;
		  }
	public List<modelclass> getAllGroups() {
		List<modelclass> comments = new ArrayList<modelclass>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_GROUP,
				ColumnsCategory, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			modelclass comment = cursorToGroup(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

/**
used for searching names
 @param searchKey- key passed from search function of toolbar
 */
	public ArrayList<modelclass> getSearchResult(String searchKey){

		ArrayList<modelclass> comments = new ArrayList<modelclass>();
		String id="0";
		String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_USER+ " WHERE "+MySQLiteHelper.USER_NAME+" LIKE ?";
		Cursor cursor = database.rawQuery(selectQuery, new String[]{"%"+searchKey.toLowerCase()+"%"});

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			modelclass comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		// make sure to close the cursor


		return comments;
	}

	 private modelclass cursorToComment(Cursor cursor) {

		 modelclass comment = new modelclass();
		    //System.out.println("savingid"+cursor.getString(0));
		    comment.setUserId(cursor.getString(1));
		    comment.setUserName(cursor.getString(2));
		 comment.setUserCustomData(cursor.getString(3));
		 comment.setUserEmail(cursor.getString(4));
		 comment.setUserPhone(cursor.getString(5));

		    return comment;
		  }
	private modelclass cursorToGroup(Cursor cursor) {

		modelclass comment = new modelclass();
		//System.out.println("savingid"+cursor.getString(0));
		comment.setGroupid(cursor.getString(0));

		comment.setGroupName(cursor.getString(1));
		comment.setGroupUsers(cursor.getString(2));
		comment.setAdminId(cursor.getString(3));
		comment.setGroupTags(cursor.getString(4));

		return comment;
	}
	 ///////////////////////for LOCATION fetching/////////////////////////////////



//
//	public void bulkInsertcache(ArrayList<modelclass> lst) {
//
//
//
//
//
//		String sql = "INSERT INTO "+ MySQLiteHelper.TABLE_CACHE +" VALUES (?,?,?,?,?);";
//		SQLiteStatement statement = database.compileStatement(sql);
//		database.beginTransaction();
//
//		for(int i =0;i<lst.size();i++){
//
//
//
//
//
//			try {
//
//Log.e("list inside time",lst.get(i).getTime());
//
//				statement.clearBindings();
//
//				statement.bindString(2, lst.get(i).getUrl());
//				statement.bindString(3, lst.get(i).getUrl_params());
////				statement.bindString(4, url);
//				//statement.bindString(5, tag);
//				statement.bindString(4, lst.get(i).getResponse());
//				statement.bindString(5, lst.get(i).getTime());
//				statement.execute();
//
//
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		database.setTransactionSuccessful();
//		database.endTransaction();
//
//
//
//		 Log.e("bulk complete", "bulk complete");
//
//	}



	public JSONObject getcacheResponse(String url, String url_params) {
		JSONObject comments = null;

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CACHE,
				new String[] { MySQLiteHelper.URL,MySQLiteHelper.RESPONSE,MySQLiteHelper.TIME
				},MySQLiteHelper.URL +" = ? AND "+MySQLiteHelper.REQ_PARAMS +" = ?",

				new String[] { url,url_params }, null, null, null, null);


		cursor.moveToFirst();
		Log.e("cursor", cursor.getCount() + "");


		while (!cursor.isAfterLast()) {

			comments	= new JSONObject();
			try {
				comments.put("url",cursor.getString(0));
				comments.put("response",cursor.getString(1));
				comments.put("time",cursor.getString(2));
			} catch (JSONException e) {
				e.printStackTrace();
			}


			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}


	public Boolean checkCache(String url, String url_params) {
		Boolean comments = false;

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CACHE,
				new String[] { MySQLiteHelper.REQ_PARAMS,MySQLiteHelper.RESPONSE,MySQLiteHelper.TIME
				},MySQLiteHelper.URL +" = ? AND "+MySQLiteHelper.REQ_PARAMS +" = ?",

				new String[] { url,url_params }, null, null, null, null);


		cursor.moveToFirst();



if(cursor.getCount()!=0)

			comments= true;



		// make sure to close the cursor
		cursor.close();
		return comments;
	}


	/*
	drop tables of category
	 */
	public void DropCategoryTables(){




		database.delete(MySQLiteHelper.TABLE_GROUP, null, null);
		database.delete(MySQLiteHelper.TABLE_USER,null,null);





	}





/*
to clear whole db while logout
 */

	public  void cleardb(){
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_STATE);
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_CATEGORY_1);
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_CATEGORY_2);
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_CATEGORY_3);
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_MAINCATEGORY);
//		database.execSQL("delete from " + MySQLiteHelper.TABLE_COUNTRY);


	}
}