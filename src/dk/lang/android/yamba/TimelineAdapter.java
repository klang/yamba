package dk.lang.android.yamba;

import dk.lang.android.yamba.R;
import android.content.Context;
import android.text.format.DateUtils;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TimelineAdapter extends SimpleCursorAdapter {
	// wtf? disregarding the StatusData work again?!? (p.145)
	static final String[] FROM = {DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT};
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };		

	@SuppressWarnings("deprecation")
	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, FROM, TO);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		
		// Manually bind created at trimestamp to its view
		Long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper.C_CREATED_AT));
		TextView textCreatedAt = (TextView) row. findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}

}
