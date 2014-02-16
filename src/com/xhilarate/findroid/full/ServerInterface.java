package com.xhilarate.findroid.full;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.net.URLEncoder;


import android.content.Context;
import android.util.Log;

public class ServerInterface {
	static Context localcont;
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String PASSWORD_PREF_KEY = "passwd";
    public static String SERVER_URL = "http://mess.byethost31.com/findroid/";
    
    public ServerInterface(Context C)
    {localcont=C;}
    
    public static String getRegister(String phn, String fname, String lname, String email) {
    	SERVER_URL += "mreg.php";	
    	SharedPreferences passwdfile =localcont.getSharedPreferences(FINDROID_PREFS, 0);
		String passwdMD5=passwdfile.getString(PASSWORD_PREF_KEY,"");
        String data = "phn=" + phn;
        data += "&fname=" + fname;
        data += "&lname=" + lname;
        data += "&email=" + email;
        data += "&passwd=" + passwdMD5;
        return executeHttpRequest(data);   
    }
    
    public static String setLocation(String data) {
    	SERVER_URL += "mcon.php";
    	return executeHttpRequest(data);
    }
    
    //http://mess.byethost31.com/findroid/mreg.php?phn=12122111&fname=swap&lname=dink&email=abcaaza

    private static String executeHttpRequest(String data) {
            String result = "";
            try {
                    URL url = new URL(SERVER_URL);
                    URLConnection connection = url.openConnection();
                    
                    /*
                     * We need to make sure we specify that we want to provide input and
                     * get output from this connection. We also want to disable caching,
                     * so that we get the most up-to-date result. And, we need to 
                     * specify the correct content type for our data.
                     */
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send the POST data
                    DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                    dataOut.writeBytes(data);
                    dataOut.flush();
                    dataOut.close();

                    // get the response from the server and store it in result
                    DataInputStream dataIn = new DataInputStream(connection.getInputStream()); 
                    String inputLine;
                    while ((inputLine = dataIn.readLine()) != null) {
                            result += inputLine;
                    }
                    dataIn.close();
            } catch (IOException e) {
                    /*
                     * In case of an error, we're going to return a null String. This
                     * can be changed to a specific error message format if the client
                     * wants to do some error handling. For our simple app, we're just
                     * going to use the null to communicate a general error in
                     * retrieving the data.
                     */
                    e.printStackTrace();
                    result = null;
            }
            SERVER_URL = "http://mess.byethost31.com/findroid/";
            return result;
    }
    

}
