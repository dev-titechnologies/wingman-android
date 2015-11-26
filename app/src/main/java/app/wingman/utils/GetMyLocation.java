package app.wingman.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;




import app.wingman.ApplicationSingleton;
import app.wingman.ui.activities.DialogsActivity;
import app.wingman.ui.activities.LoginActivity;


public class GetMyLocation {

	static double loc[] = new double[3];

	LocationManager locationManager;
	Context context;
	Location location;
	String place = "";

	public GetMyLocation(Context context) {
		this.context = context;


	}
	public void getMyLocation(){

		new GetPlace(getMyLocationn()).execute();
	}

	//to find the current latitude and longitude of user
	public double[]  getMyLocationn() {

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		Log.d("STATUS n", "else" + location);

		if (location != null) {
			loc[0] = location.getLatitude();
			loc[1] = location.getLongitude();
			ApplicationSingleton.LOCATION_ARRAY = loc;
			return loc;
		} else {
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			boolean is_gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Log.d("STATUS n", "If " + location);

			if (location != null) {
				loc[0] = location.getLatitude();
				loc[1] = location.getLongitude();
				ApplicationSingleton.LOCATION_ARRAY = loc;
				double distance;
				return loc;
			} else {

				//SAME NETWORK PROVIDER
				locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

					ApplicationSingleton.ShowAlert(context,"please add location permission in settings page of the app");

				}else {
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					Log.d("STATUS n", "else" + location);

					if (location != null) {

						loc[0] = location.getLatitude();
						loc[1] = location.getLongitude();
						ApplicationSingleton.LOCATION_ARRAY = loc;
						return loc;
					}
				}
            }

        }

//System.out.println("gps my location 81 location class"+loc[0]);

		return null;
    }

	public class GetPlace extends AsyncTask<String,String,String>{


		double[] loc;

		public GetPlace(double[] locs){

			loc=locs;
		}

		@Override
		protected String doInBackground(String... strings) {


				String result;
				try {
					StringBuilder sb = new StringBuilder();
					InputStream is = (InputStream) new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+loc[0]+","+loc[1]+"&sensor=true").getContent();
					System.out.println("locationn"+"http://maps.googleapis.com/maps/api/geocode/json?latlng="+loc[0]+","+loc[1]+"&sensor=true");
					BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					reader.close();
					result = sb.toString();
					JSONObject json=new JSONObject(result);
					JSONArray jsonarray=json.getJSONArray("results");
					JSONObject jsonobject=jsonarray.getJSONObject(0);

					String country="";

					JSONArray js=new JSONArray(jsonobject.getString("address_components"));

					for(int s=0;s<js.length();s++){
						if(js.getJSONObject(s).getString("types").contains("country")){
							if(js.getJSONObject(s).has("long_name")){
								System.out.println("placeeeee"+js.getJSONObject(s).getString("long_name"));
								country=js.getJSONObject(s).getString("long_name");

							}
						}
					}

					//place=jsonobject.getString("formatted_address");items
					place=jsonobject.getString("formatted_address")+"/"+loc[0]+"/"+loc[1]+"/"+js.getJSONObject(2).getString("long_name"); //FOR GETTING LATITUDE AND LONGITUDE I APPEND LOC[] VALUES
					//System.out.println("gps 106"+place);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					place="";
					//System.out.println("gps url error"+e);
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//System.out.println("gps io error"+e);
					place="";
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					place="";
					//System.out.println("gps json error"+e);
					e.printStackTrace();
				}
				catch(Exception e){
					place="";
					//System.out.println("gps error"+e);
					e.printStackTrace();
				}

                   ApplicationSingleton.USER_STREET=place;

                 return place;

			}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
Log.e("ApplicationSingleton",ApplicationSingleton.LOCATION_ARRAY.toString());
			if (PreferencesUtils.getData("user logged", context).equals("0")){

				Intent in = new Intent(context, LoginActivity.class);
				context.startActivity(in);

			}else {
				Intent intent = new Intent(context, DialogsActivity.class);
				context.startActivity(intent);
			}

//			QBLocation location = new QBLocation(ApplicationSingleton.LOCATION_ARRAY[0], ApplicationSingleton.LOCATION_ARRAY[1], s);
//			QBLocations.createLocation(location, new QBEntityCallbackImpl<QBLocation>() {
//				@Override
//				public void onSuccess(QBLocation qbLocation, Bundle args) {
//
//
//
//
//				}
//
//				@Override
//				public void onError(List<String> errors) {
//					ApplicationSingleton.ShowAlert(context, "chat location adding errors: " + errors.toString());
//
//				}
//			});
		}
	}



}
