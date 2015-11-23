package app.wingman.networks;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connecttoget {

	public static String callJsonWithparams(String urlstring,String params){
		
		String data=null;



		try {
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);

            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            // Starts the query

            conn.setRequestProperty("Content-Type", "application/json");



            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();

         InputStream stream = conn.getInputStream();

      data = convertStreamToString(stream);

     
         stream.close();

         } catch (Exception e) {

          System.out.println("error "+e);
            data = data+"error";

         }
		
		return data;
		
	}



    public static String callJson(String urlstring){

        String data=null;

        try {
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();






            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            // Starts the query

            conn.connect();

            InputStream stream = conn.getInputStream();

            data = convertStreamToString(stream);


            stream.close();

        } catch (Exception e) {

            System.out.println("error "+e);

        }

        return data;

    }
	
	static String convertStreamToString(InputStream is) {
	      java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	      return s.hasNext() ? s.next() : "";
	   }

}
