package dk.lang.android.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineActivity extends Activity {
	DbHelper dbHelper; // this was deprecated in chapter 9 .. we are now in chapter 10
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
	
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
		listTimeline.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
