package dk.lang.android.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class TimelineActivity extends Activity {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	TextView textTimeline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_basic);
	}

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
		
		// Iterate over all the data and print it out
		String user, text, output;
		while (cursor.moveToNext()) {
			user = cursor.getString(cursor.getColumnIndex(DbHelper.C_USER));
			text = cursor.getString(cursor.getColumnIndex(DbHelper.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			textTimeline.append(output);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
