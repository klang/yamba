package dk.lang.android.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class YamdaWidget extends AppWidgetProvider {
	public static final String TAG = YamdaWidget.class.getSimpleName();
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Cursor c = context.getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null, StatusData.C_CREATED_AT+" DESC");
		try {
			if(c.moveToFirst()) {
				// for the love of all that is Holy, why do we have to decode the data in this convoluted way?
				CharSequence user = c.getString(c.getColumnIndex(StatusData.C_USER));
				CharSequence createdAt = DateUtils.getRelativeTimeSpanString(context, c.getLong(c.getColumnIndex(StatusData.C_CREATED_AT)));
				CharSequence message = c.getString(c.getColumnIndex(StatusData.C_TEXT));
				
				// Loop through all instances of the widget
				for (int appWidgetId : appWidgetIds) {
					Log.d(TAG, "Update widget " + appWidgetId);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yamda_widget);
					views.setTextViewText(R.id.textUser, user);
					views.setTextViewText(R.id.textCreatedAt, createdAt);
					views.setTextViewText(R.id.textText, message);
					views.setOnClickPendingIntent(R.id.yamda_icon, PendingIntent.getActivity(context,  0, new Intent(context, TimelineActivity.class),	0));
					
				}
			} else {
				Log.d(TAG, "No data to update");
			}
		} finally {
			c.close();
		}
		Log.d(TAG, "onUpdated");
	}
}
