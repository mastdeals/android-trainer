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
	private final String sURI_Register="http://androidtrainer.no-ip.org:8080/GCMTrainerWeb/register";
	/**costruttore*/
	public HttpClientHelper(){
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(sURI_Register);
	}
	
	public void registerToAndroidTrainerServer(String sGCMId, ConfigTrainer oConfigTrainer) {	   
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("gcmid", sGCMId));
	        //Add Others value
	        nameValuePairs.add(new BasicNameValuePair("age", String.valueOf(oConfigTrainer.getiAge())));
	        nameValuePairs.add(new BasicNameValuePair("weight", String.valueOf(oConfigTrainer.getiWeight())));
	        nameValuePairs.add(new BasicNameValuePair("gender", oConfigTrainer.getsGender()));
	        nameValuePairs.add(new BasicNameValuePair("name",  oConfigTrainer.getsName()));
	        nameValuePairs.add(new BasicNameValuePair("nick", oConfigTrainer.getsNick()));
	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        Log.v(this.getClass().getCanonicalName(),"Register to Android Trainer Server");
	        Log.v(this.getClass().getCanonicalName(),"response: "+response.getStatusLine().getStatusCode());
	    } catch (ClientProtocolException e) {
	    	Log.e(this.getClass().getCanonicalName(),"Error ClientProtocolException Register to Android Trainer Server");
	    } catch (IOException e) {
	        Log.e(this.getClass().getCanonicalName(),"Error IOException Register to Android Trainer Server");
	    }
	} 
}
