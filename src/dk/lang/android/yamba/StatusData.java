package dk.lang.android.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


public class StatusData {
	private static final String TAG = StatusData.class.getSimpleName();

	static final String DATABASE = "timeline.db";
	static final int VERSION = 1;
	static final String TABLE = "timeline";

	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_SOURCE = "source";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	private static final String[] MAX_CREATED_AT_COLUMNS = { "max(" + StatusData.C_CREATED_AT + ")" };
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating database: " + DATABASE);
			db.execSQL("create table " 
						+ TABLE + " (" 
						+ C_ID + " int primary key, "
						+ C_CREATED_AT + " int, "
						+ C_SOURCE + " text, "
						+ C_USER + " text, " 
						+ C_TEXT + " text)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TABLE);
			onCreate(db);
		}
	}
	
	private final DbHelper dbHelper;
	
	public StatusData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized data");
	}
	public void close(){
		this.dbHelper.close();
	}

	// return value has been changed from void to long to accomodate the StatusProvider.
	public long insertOrIgnore(ContentValues values) {
		Log.d(TAG, "insertOrIgnore on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
		    return db.insertWithOnConflict(TABLE,  null,  values,  SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close();
		}		
	}

	public int update(ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, "update on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
		    return db.update(TABLE, values, selection, selectionArgs);
		} finally {
			db.close();
		}		
	}
	public int delete(String selection, String[] selectionArgs) {
		Log.d(TAG, "delete on " + selection);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
		    return db.delete(TABLE, selection, selectionArgs);
		} finally {
			db.close();
		}		
	}	
	
	public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, projection, selection, selectionArgs, null, null, GET_ALL_ORDER_BY);
	}
	
	public Cursor getStatusUpdates(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	/**
	 * @return Timestamp of the latest status we have in the database
	 */
	public long getLatestStatusCreatedAtTime(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	/**
	 * @param id of the status we are looking for
	 * @return Text of the status
	 */
	public String getStatusTextById(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}

	}
}
