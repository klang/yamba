package dk.lang.android.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	public Twitter twitter;
	private SharedPreferences prefs;
	private boolean serviceRunning;
	private StatusData statusData;
	
	// extends Application
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}
	
	// other stuff
	public synchronized Twitter getTwitter(){
		if (twitter == null) {
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String apiRoot = prefs.getString("apiRoot","http://yamba.marakana.com/api");
			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(apiRoot)) {
				// Connect to twitter.com
				twitter = new Twitter(username, password);
				twitter.setAPIRootUrl(apiRoot);
			}
		}
		return this.twitter;
	}
	public boolean isServiceRunning(){
		return serviceRunning;
	}
	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}
	public StatusData getStatusData(){
		if (statusData == null) {
			statusData = new StatusData(this);
		}
		return statusData;
	}
	
	// Connects to the online service and puts the latest statuses into DB
	// Returns the count of new statuses
	public synchronized int fetchStatusUpdates() {
		Log.d(TAG, "Fetching status updates");
		Twitter twitter = this.getTwitter();
		if (twitter == null) {
			Log.d(TAG, "Twitter connection info not initialied");
			return 0;
		}
		try{
			List<Status> statusUpdates = twitter.getFriendsTimeline();
			long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
			int count = 0;
			
			ContentValues values = new ContentValues();
			for (Status status : statusUpdates) {
				// Insert into databse
				//values.clear(); 
				values.put(StatusData.C_ID, status.id);
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT,	 createdAt);
				values.put(StatusData.C_SOURCE, status.source);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_USER, status.getUser().getName());
				
				Log.d(TAG, "Got update wih id " + status.getId() + ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if (latestStatusCreatedAtTime < createdAt) { count++; }
			}
			Log.d(TAG, count > 0 ? "Got " + count + " status updates" : "No new updates");
			return count;
		} catch (RuntimeException e) {
			Log.e(TAG, "Failed to fetch status updates", e);
			return 0;
		}
		
	}
	
	
	// implements OnSharedPreferenceChangeListener
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// invalidate twitter object
		twitter = null; // we are using the object as immutable?!?
	}
	
}
