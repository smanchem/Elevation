package com.example.elevation;

import com.example.elevation.ChooseSessionActivity.PlaceholderFragment;
import com.example.firstapp.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class DisplayLogActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Display Previous Logs
		// Read saved data if available and fill the fields
		
		// Create the text view
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);		
	
        SharedPreferences sharedPref = this.getSharedPreferences("prevSession", Context.MODE_PRIVATE);
        String prevSessionInfo = sharedPref.getString(getString(R.string.previous_session), message);
        System.out.println("INFO: Previous Session Info is " + prevSessionInfo);
        
        String prefix = "\nPrevious Session:\n";
                
	   	TextView ascent = new TextView(this);
		ascent.setTextSize(30);
		ascent.setText(prefix + prevSessionInfo);
		setContentView(ascent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_log, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_log,
					container, false);
			return rootView;
		}
	}

}
