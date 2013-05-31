package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IAlarmDb {

	public static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_TAG = "tag";
	public static final String KEY_ONOFF = "onoff";
	public static final String KEY_SOUND_PATH = "soundpath";
	public static final String KEY_DAYS = "days";
	public static final String KEY_SLEEP = "sleep";
	
	private static final String DB_NAME = "ialarms.db";
	private static final String DB_TABLE = "table_alarm";
	
	private static final int DB_VERSION = 1;
	private Context mContext = null;
	
	private static SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;

	public IAlarmDb(Context context) {
		mContext = context;
	}

	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		
	}

	public void close() {
//		mSQLiteDatabase.close();
		mDatabaseHelper.close();
	}

	/**
	 * insert a data
	 * @param style
	 * @param down
	 * @param up
	 * @return
	 */
	public long insertData(String time, String tag, String onoff, String soundpath, String days, String sleep) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_TAG, tag);
		initialValues.put(KEY_ONOFF, onoff);
		initialValues.put(KEY_SOUND_PATH, soundpath);
		initialValues.put(KEY_DAYS, days);
		initialValues.put(KEY_SLEEP, sleep);

		return mSQLiteDatabase.insert(DB_TABLE, KEY_ID, initialValues);
	}

	/**
	 * get the data of one column to a array list
	 * 
	 * @param column
	 * @return
	 */
	public static ArrayList<String> getColumnInf(String column) {
		ArrayList<String> getlist = new ArrayList<String>();
		Cursor findColumDate = mSQLiteDatabase.query(DB_TABLE, new String[] { column },
				null, null, null, null, null);
		findColumDate.moveToFirst();
		final int Index = findColumDate.getColumnIndexOrThrow(column);
		for (findColumDate.moveToFirst(); !findColumDate.isAfterLast(); findColumDate.moveToNext()) {
			String getOneItem = findColumDate.getString(Index);
			getlist.add(getOneItem);
		}
		return getlist;
	}
	
	/**
	 * drop the table
	 * @param db
	 */
	public void clearTable() {
		String DB_DELETE = "delete from " + DB_TABLE +";";
		mSQLiteDatabase.execSQL(DB_DELETE);
	}

	/**
	 * judge the table is null or not
	 * @param dbadapter
	 * @return
	 */
	public boolean isEmpty(IAlarmDb dbadapter) {
		
		return (mSQLiteDatabase.query(DB_TABLE, new String[] { IAlarmDb.KEY_TIME },
				null, null, null, null, null) == null);
	}
	
	/**
	 * update the table
	 * @param time
	 * @param tag
	 * @param onoff
	 * @param soundpath
	 * @param days
	 * @param sleep
	 * @return
	 */
	public boolean upDateDB(String time, String tag, String onoff, String soundpath, String days, String sleep) {
		String sql = "UPDATE " + DB_TABLE + " SET " + KEY_TAG + " = '" + tag + "', " + KEY_ONOFF + " = '" + onoff + "', "+ KEY_SOUND_PATH + " = '" + soundpath + "', " + KEY_DAYS + " = '" + days + "', "  + KEY_SLEEP + "='" + sleep + 
		"' WHERE (" + KEY_TIME  + "='" + time + "');";
		mSQLiteDatabase.execSQL(sql);
		
		return true;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// create the table if is none
			String DB_CREATE = "CREATE TABLE " + DB_TABLE + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_TIME + " TEXT," 
			+ KEY_TAG + " TEXT," 
			+ KEY_ONOFF + " TEXT," 
			+ KEY_SOUND_PATH + " TEXT," 
			+ KEY_DAYS + " TEXT," 
			+ KEY_SLEEP + " TEXT )";
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
}