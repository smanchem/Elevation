package com.example.elevation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import org.json.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.firstapp.R;

public class LongSessionActivity extends ActionBarActivity {

	private LocationHelper locationHelper = LocationHelper.getLocationHelper(this);
	public ArrayList<Double> altitude = new ArrayList<Double>();
	
	
	Thread background = new Thread(new Runnable() {
	    public void run() {
	    	int i = 0;
	    	Log.v("INFO", "Background thread running");
	    	System.out.println("INFO: Background Thread running");
	        try {
	        	while (!Thread.interrupted()) {
	        		// Gather GPS Data here
	        		double lat = locationHelper.getLatitude();
	        		double lon = locationHelper.getLongitude();	 
	        		System.out.println("Latitude: " + lat + "Longitude: " + lon);
	        		
	        		System.out.println("INFO: URL constructed");
	        		String req = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + lat + "," + lon + "&sensor=false&key=AIzaSyBP-T6Zcrh_N2SRRAhT-kKRmhgo2pHqUdQ";
	        		
	        		HttpResponse response = null;
	        		try {
	        			HttpClient client = new DefaultHttpClient();	        		
		                HttpGet request = new HttpGet();
		                System.out.println("INFO: Setting up request");
		                request.setURI(new URI(req));
		                System.out.println("INFO: Executing");
		                response = client.execute(request);
		                System.out.println("INFO: Executed");
	        		} catch (URISyntaxException e) {
	        			e.printStackTrace();
	        			System.out.println("Exception: URL");
	        		} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        		HttpEntity entity = response.getEntity();
	        		String responseString = "";
	        		try {
						responseString = EntityUtils.toString(entity);
						System.out.print("INFO: Content is " + responseString);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Exception: Caught Parse");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Exception: Caught IO");
					}	
	        	    
    	            
	        		JSONObject obj;
					try {
						obj = new JSONObject (responseString);
						JSONArray array = obj.getJSONArray("results");
		        		altitude.add(Double.parseDouble(array.getJSONObject(i).getString("elevation")));
		        		Toast.makeText(getBaseContext(), "Motion detected\n" + "Altitude = " + altitude.get(0), 
	                            Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        		
	        		// Sleep for 30 seconds
	        		background.sleep(30000);
	        	}	        	
	        	
	        } catch (InterruptedException e) {
	        	;
	        } catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_long_session);
		
		Log.v("INFO:", "Created layout");
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}		
		
		locationHelper.setupLocationDetectors();
		locationHelper.setupLocationDetection(new LocationProcessor() {

			@Override
			public void process(double latitude, double longitude) {
			}
			
		});
		
		background.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.long_session, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public final static String EXTRA_MESSAGE = "com.example.elevation.MESSAGE";
	
	public void stopped (View view) {
		background.interrupt();
		// Compute the Ascent, Descent, Distance and Duration
		Intent intent = new Intent(this, SummaryActivity.class);
		intent.putExtra(EXTRA_MESSAGE, "\n\n Ascent: 0 m \n\n Descent: 0 m \n\n Distance: 0 m\n\n");
		startActivity(intent);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_long_session,
					container, false);
			return rootView;
		}
	}

}
