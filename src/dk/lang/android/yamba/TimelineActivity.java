package dk.lang.android.yamba;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends Activity {
	DbHelper dbHelper; // this was deprecated in chapter 9 .. we are now in chapter 10
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
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
		
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		// Connect to database
		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();
		// DbHelper.C_CREATED_AT + " Desc" == the private StatusData.GET_ALL_ORDER_BY
		// besides, didn't we put all the functionality of the DbHelper into StatusData 
		// to get rid of it?
		// in fact, the cursor below is returned by:
		//cursor = StatusData.getStatusUpdates();
		
		// Get the data from the database
		cursor = db.query(DbHelper.TABLE,  null,  null,  null,  null,  null, DbHelper.C_CREATED_AT + " Desc");
		startManagingCursor(cursor);
		
		adapter = new TimelineAdapter(this, cursor);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
