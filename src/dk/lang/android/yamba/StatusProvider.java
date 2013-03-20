package dk.lang.android.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class StatusProvider extends ContentProvider {
	private static final String TAG = StatusProvider.class.getSimpleName();
	
	public static final Uri CONTENT_URI = Uri.parse("content://dk.lang.yamba.statusprovider");
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.dk.lang.yamba.status";
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.dk.lang.yamba.mstatus";
	StatusData statusData;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		if (id < 0) {
			return statusData.delete(selection, selectionArgs);
		} else {
			return statusData.delete(StatusData.C_ID+"="+id, null);
		}
	}
	@Override
	public String getType(Uri uri) {
		return this.getId(uri) > 0 ? MULTIPLE_RECORDS_MIME_TYPE : SINGLE_RECORD_MIME_TYPE;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = statusData.insertOrIgnore(values);

		if (id == -1) {
			throw new RuntimeException(String.format("%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG, values, uri));
		} else {
			return ContentUris.withAppendedId(uri, id);
		}
		
		// why in the Hell are we trying to access a private variable on StatusData?
		// Don't touch my private parts!!
		/*
		SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
		try {
			long id = db.insertOrThrow(StatusData.Table, null, values);
			if (id == -1) {
				throw new RuntimeException(String.format("%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG, values, uri));
			} else {
				return ContentUris.withAppendedId(uri, id);
			}
		} finally {
			db.close();
		}
		*/
		
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		long id = this.getId(uri);
		if (id < 0) {
			return statusData.query(projection, selection, selectionArgs, sortOrder);
		} else {
			return statusData.query(projection, StatusData.C_ID+"="+id, null, null);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		if (id < 0) {
			return statusData.update(values, selection, selectionArgs);
		} else {
			return statusData.update(values, StatusData.C_ID+"="+id, null);
		}
	}

	
	public long getId(Uri uri) {
		String lastPathSegment = uri.getLastPathSegment();
		if (lastPathSegment != null) {
			try {
				return Long.parseLong(lastPathSegment);
			} catch (NumberFormatException e) {
				// at least we tried
			}
		}
		return -1;
	}

}
