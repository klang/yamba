package dk.lang.android.yamba;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	DbHelper dbHelper; // this was deprecated in chapter 9 .. we are now in chapter 10
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	IntentFilter filter;
	TimelineReceiver receiver;
	
	// wtf? disregarding the StatusData work again?!? (p.145)
	static final String[] FROM = {DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT};
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };		

	SimpleCursorAdapter adapter;
	// View binder constant to inject business logic that converts a timestamp to relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.textCreatedAt)
				return false;			
			// Update the created at text to relative time
			Long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText(relTime);
			return true;
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		// Check if preferences have been set (this is not the right place to ask that question)
		// .. yamba.getPrefs isn't implemented, of course .. 
		/* TODO: resolve this cluster-fuck
		if (yamba.getPrefs().getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		*/
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		// Connect to database TODO: maybe use the StatusData class to provide this?
		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
		filter = new IntentFilter("dk.lang.yamba.NEW_STATUS");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();
		
		cursor = db.query(DbHelper.TABLE,  null,  null,  null,  null,  null, DbHelper.C_CREATED_AT + " Desc");
		startManagingCursor(cursor);
		registerReceiver(receiver,filter);
		
		adapter = new TimelineAdapter(this, cursor);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);	
		//this.setupList();		
	}
	@Override
	protected void onPause(){
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Deprecated
	private void setupList() {
		// DbHelper.C_CREATED_AT + " Desc" == the private StatusData.GET_ALL_ORDER_BY
		// besides, didn't we put all the functionality of the DbHelper into StatusData 
		// to get rid of it?
		// in fact, the cursor below is returned by:
		// cursor = StatusData.getStatusUpdates();
		// Get the data from the database TODO: get the database in the right way, via StatusData
		cursor = db.query(DbHelper.TABLE,  null,  null,  null,  null,  null, DbHelper.C_CREATED_AT + " Desc");
		startManagingCursor(cursor);
		
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		yamba.getStatusData().close();
	}
	
	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
	
}
