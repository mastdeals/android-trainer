package com.glm.utils.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.glm.bean.ConfigTrainer;

public class HttpClientHelper {
	private HttpClient httpclient = null;
	private HttpPost httppost = null;
	/**costruttore*/
	public HttpClientHelper(){
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost("http://www.yoursite.com/script.php");
	}
	
	public void registerToAndroidTrainerServer(String sGCMId, ConfigTrainer oConfigTrainer) {	   
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("gcmid", sGCMId));
	        //Add Others value
	        
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        Log.v(this.getClass().getCanonicalName(),"Register to Android Trainer Server");
	    } catch (ClientProtocolException e) {
	    	Log.e(this.getClass().getCanonicalName(),"Error ClientProtocolException Register to Android Trainer Server");
	    } catch (IOException e) {
	        Log.e(this.getClass().getCanonicalName(),"Error IOException Register to Android Trainer Server");
	    }
	} 
}
