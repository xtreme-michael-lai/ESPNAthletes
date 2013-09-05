package com.example.espnathletes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class AthleteService extends IntentService {

	private ResultReceiver receiver;
	
	public AthleteService() {
		super("AthleteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		receiver = intent.getParcelableExtra("Receiver");
		
		String jsonAthletes = pullAthletes();
		if(jsonAthletes != null) {
			Bundle bundle = new Bundle();
			bundle.putString("Athletes", jsonAthletes);
			receiver.send(200, bundle);
		}
		
	}
	
	private String pullAthletes() {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://api.espn.com/v1/sports/football/nfl/athletes/?limit=15&_accept=application%2Fjson&apikey=rcre8d6w8fkyaaskprwugv4d");
		HttpResponse response;
		
		try {
			response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				
				InputStream in = entity.getContent();
				BufferedReader bd = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = bd.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				in.close();
				
				return sb.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  
		}
		
		return null;
	}

}
