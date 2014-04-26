package com.example.elevation;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.example.elevation.LongSessionActivity.PlaceholderFragment;
import com.example.firstapp.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class ShortSessionActivity extends ActionBarActivity implements SensorEventListener {

	public final static String EXTRA_MESSAGE1 = "com.example.elevation.MESSAGE";
	public ArrayList<DataObject> ZValues = new ArrayList<DataObject>();
	public SessionData sessD = new SessionData(0.00,0.00,0.00, 0.00);
	public double distanceUp = 0, distanceDown = 0, velocity = 0;
	public long prevTime = 0;
	public long count = 0;
	public DecimalFormat df = new DecimalFormat("####0.0000");

    
    /** indicates whether or not Accelerometer Sensor is supported */
    private static Boolean supported;
    /** indicates whether or not Accelerometer Sensor is running */
    private static boolean running = false;
    
	Thread background = new Thread(new Runnable() {
	    public void run() {
	        try {
	        	
	        	while (!Thread.interrupted()) {
	        		
	        		background.sleep(1000);
	        	}
	        } catch (InterruptedException e) {
	        	;
	        }
	    }
	});
	
	private SensorManager sensorManager;
	 double ax,ay,az;   // these are the acceleration in x,y and z axis
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_short_session);
	        if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new PlaceholderFragment()).commit();
			}
	        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
	        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 10000);
	        
	        prevTime = System.currentTimeMillis();             
	        
	        
	   }
	   @Override
	   public void onAccuracyChanged(Sensor arg0, int arg1) {
	   }

	   @Override
	   public void onSensorChanged(SensorEvent event) {
	        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
	        	// In this example, alpha is calculated as t / (t + dT),
	        	  // where t is the low-pass filter's time-constant and
	        	  // dT is the event delivery rate.

	        	  final float alpha = (float) 0.937;
	        	  Double gravity = 9.81;

	        	  // Isolate the force of gravity with the low-pass filter.
	        	  gravity = alpha * gravity + (1 - alpha) * event.values[2];

	                    az= event.values[2] - gravity;
	                    /*
	                    Toast.makeText(getBaseContext(), "Motion detected\n" + "az = " + event.values[2] +  "\nz = " + (az+9.8), 
	                            Toast.LENGTH_SHORT).show();
	                    */
	                    System.out.println("VAL: az = " + event.values[2] +  " z = " + (az+9.81));                   
	                    double z = az+9.8; 	                    
	                    
	                    System.out.println("INFO: Z is " + z + " Velocity is " + velocity);
	                    
	                    // The Main Algorithm
	                    double acc = z*0.01;
	                    if (z > 0.1 || z < -0.1) {
	                    	velocity = velocity + acc;
		                    if (velocity > 0.15) {
		                    	distanceUp = distanceUp + (velocity * (System.currentTimeMillis() - prevTime) * 0.001);
		                    } else if (velocity < -0.1){
		                    	distanceDown = distanceDown + (velocity * (System.currentTimeMillis() - prevTime) * 0.001);
		                    }
	                    } else if (velocity > 0.15) {
	                    	distanceUp = distanceUp + (velocity * (System.currentTimeMillis() - prevTime) * 0.001);
	                    } else if (velocity < -0.1){
	                    	distanceDown = distanceDown + (velocity * (System.currentTimeMillis() - prevTime) * 0.001);
	                    }
	                    
	                    prevTime = System.currentTimeMillis();
	                    count++;
	            }
	   }
	    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.short_session, menu);
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
	
	public double computePauseElevation(long size){
		velocity = 0;
		sessD.ascent = distanceUp;
		sessD.descent = (-1)*distanceDown;
		sessD.distance = (distanceUp - distanceDown);
		sessD.netElevation = (distanceUp + distanceDown);
		return (distanceUp - distanceDown);
	}
	
	public double computeStopElevation(long size){
		velocity = 0;
		double up = distanceUp;
		double down = distanceDown;
		sessD.ascent = distanceUp;
		sessD.descent = (-1)*distanceDown;
		sessD.distance = (distanceUp - distanceDown);
		sessD.netElevation = (distanceUp + distanceDown);
		distanceUp = distanceDown = 0;
		return (up - down);
	}
	
	
	public void stopped (View view) {
		background.interrupt();
		// Compute the Ascent, Descent, Distance and Duration
		
		Intent intent = new Intent(this, SummaryActivity.class);
		
		double distance = computeStopElevation(count);
				
		System.out.println("Value: " + df.format(sessD.ascent));
		
		String output = "\n\n Ascent: " + df.format(sessD.ascent) + " m \n\n Descent: " + df.format(sessD.descent) + " m \n\n Distance: " + df.format(sessD.distance) +" m\n\n" + "Net Elevation: " + df.format(sessD.netElevation) +" m\n\n";
		
		intent.putExtra(EXTRA_MESSAGE1, output);
		
		// Save the Session Info
		SharedPreferences sharedPref = this.getSharedPreferences("prevSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.previous_session), output);
        editor.commit();
        
		startActivity(intent);
	}
	
	public void paused (View view) {
		background.interrupt();
		// Compute the Ascent, Descent, Distance and Duration
		Intent intent = new Intent(this, PausedActivity.class);
		
		double distance = computePauseElevation(count);
		
		intent.putExtra(EXTRA_MESSAGE1, "\n\n Ascent: " + df.format(sessD.ascent) + " m \n\n Descent: " + df.format(sessD.descent) + " m \n\n Distance: " + df.format(sessD.distance) +" m\n\n" + "Net Elevation: " + df.format(sessD.netElevation) +" m\n\n");
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
			View rootView = inflater.inflate(R.layout.fragment_short_session,
					container, false);
			return rootView;
		}
	}
	public final class RandomInteger {
		  
		  public  final int rand(){
		    
		    //note a single Random object is reused here
		    Random randomGenerator = new Random();
		         return randomGenerator.nextInt(100);
		  }
	}

}
