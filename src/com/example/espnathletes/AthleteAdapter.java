package com.example.espnathletes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AthleteAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final List<String> values;
	private HeadshotManager headshotManager;
	private String headshotURL = "http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/<id>.png&w=350&h=254";
	
	public AthleteAdapter(Context context, int resource, List<String> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
		
		headshotManager = new HeadshotManager();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(convertView == null) 
			convertView = inflater.inflate(R.layout.row, parent, false);
		
		TextView label = (TextView) convertView.findViewById(R.id.label);
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		String value = values.get(position);
		try {
			JSONObject athleteInfo = new JSONObject(value);
			String name = athleteInfo.getString("displayName");
			String id = athleteInfo.getString("id");
			String url = headshotURL.replace("<id>", id);
		
			
			headshotManager.run(url, icon);
			
			label.setText(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	public class HeadshotManager {
	    private final Map<String, Drawable> headshotMap;

	    public HeadshotManager() {
	        headshotMap = new HashMap<String, Drawable>();
	    }

	    public Drawable fetchHeadshot(String url) {
	        if (headshotMap.containsKey(url)) {
	            return headshotMap.get(url);
	        }

	        try {
	            InputStream is = fetch(url);
	            Drawable drawable = Drawable.createFromStream(is, "src");

	            if (drawable != null) {
	                headshotMap.put(url, drawable);
	            }

	            return drawable;
	            
	        } catch (MalformedURLException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        }
	    }
	    
	    public class GetHeadshot extends AsyncTask<String, Void, Drawable> {
	    	private ImageView imageView;
	    	
	    	public GetHeadshot(ImageView imageView) {
	    		this.imageView = imageView;
	    	}
	    	
			@Override
			protected Drawable doInBackground(String... urls) {
				Drawable drawable = fetchHeadshot(urls[0]);
				return drawable;
			}
			
			@Override
			protected void onPostExecute(Drawable result) {
				imageView.setImageDrawable(result);
			}
	    }
	    
	    public void run(final String url, final ImageView imageView) {
	    	GetHeadshot getHeadshot = new GetHeadshot(imageView);
	    	getHeadshot.execute(new String[] { url });
	    }

	    private InputStream fetch(String url) throws MalformedURLException, IOException {
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpGet request = new HttpGet(url);
	        HttpResponse response = httpClient.execute(request);
	        return response.getEntity().getContent();
	    }
	}
}
