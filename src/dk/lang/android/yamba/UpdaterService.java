package dk.lang.android.yamba;

import java.util.List;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";

	static final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
	DbHelper dbHelper;
	SQLiteDatabase db;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.yamba = (YambaApplication)getApplication();
		this.updater = new Updater();
		dbHelper = new DbHelper(this);
		Log.d(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.runFlag=true;
		this.updater.start();
		this.yamba.setServiceRunning(true);
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag=false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRunning(false);
		Log.d(TAG, "onDestroyed");
	}
	
	/**
	 * Threat that performs the actual update from the online service
	 */
	private class Updater extends Thread {
		List<Twitter.Status> timeline;
		
		public Updater(){
			super("UpdaterService-Updater");
		}
	
	
		@Override
		public void run(){
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					// Get the timeline from the cloud
					try {
						timeline = yamba.getTwitter().getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					// Open the databse for writing
					db = dbHelper.getWritableDatabase();
					
					// Loop over the timeline and print it out
					ContentValues values = new ContentValues();
					for (Twitter.Status status : timeline) {
						// Insert into databse
						values.clear(); 
						values.put(DbHelper.C_ID, status.id);
						values.put(DbHelper.C_CREATED_AT,  status.createdAt.getTime());
						values.put(DbHelper.C_SOURCE, status.source);
						values.put(DbHelper.C_TEXT, status.text);
						values.put(DbHelper.C_USER, status.user.name);
						try {
							db.insertOrThrow(DbHelper.TABLE, null, values);
							Log.d(TAG, String.format("%s-%s-%s:%s", status.id, status.source, status.user.name, status.text));
						} catch (SQLException e) {
							// ignore the exception
						}
					}
					// Close the database
					db.close();
					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
	} // Updater
}








