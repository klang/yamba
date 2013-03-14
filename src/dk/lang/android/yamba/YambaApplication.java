package dk.lang.android.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	public Twitter twitter;
	private SharedPreferences prefs;
	
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
	
	// implements OnSharedPreferenceChangeListener
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// invalidate twitter object
		twitter = null; // we are using the object as immutable?!?
	}
	
}
