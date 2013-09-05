package com.example.espnathletes;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.espnathletes.AthleteReceiver.Receiver;

public class MainActivity extends ListActivity implements Receiver {

	private Intent service;
	//private ListView athleteList;
	private AthleteReceiver mReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //athleteList = (ListView) findViewById(R.id.athleteList);
        
        mReceiver = new AthleteReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        service = new Intent(this, AthleteService.class);
        service.putExtra("Receiver", mReceiver);
        
        startService(service);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopService(service);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		parseAthletesJson(resultData.getString("Athletes"));
	}

	private void parseAthletesJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			
			JSONArray athletesArray = jsonObject.getJSONArray("sports")
												.getJSONObject(0)
												.getJSONArray("leagues")
												.getJSONObject(0)
												.getJSONArray("athletes");
			
			List<String> names = new ArrayList<String>();
			
			for(int i = 0; i < athletesArray.length(); i++) {
				JSONObject athlete = athletesArray.getJSONObject(i);
				String name = athlete.getString("displayName");
				names.add(name);
				
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
			//athleteList.setAdapter(adapter);
			setListAdapter(adapter);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
