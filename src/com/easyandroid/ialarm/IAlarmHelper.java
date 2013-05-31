package com.easyandroid.ialarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.util.EncodingUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class IAlarmHelper {

	private static final String FILE_ENCODING = "utf-8";

	public static final String FILENAME_SAVE_TIMERSOUND = "timersound.txt";
	public static final File FILENAME_SAVE_TIMERSOUND_PATH = new File("/data/data/com.easyandroid.ialarm/files/timersound.txt");

	public static final File DIR_TIMERSET_SLEEP_ON = new File("/data/data/com.easyandroid.ialarm/timersleep");

	public static final File FILE_SAVE_WORLDCITY_IDS = new File("/data/data/com.easyandroid.ialarm/files/worldtimecities.txt");
	
	public static final String FILENAME_SAVE_SLEEP_ALARM = "sleeping.txt";
	public static final File FILE_SAVE_SLEEP_ALARM = new File("/data/data/com.easyandroid.ialarm/files/sleeping.txt");
	
	public static boolean alarmOver = false;
	
	public static int SCREEN_HEIGHT = 0;
	public static int SCREEN_WEDTH = 0;

	/**
	 * (android) write to file
	 * 
	 * @param fileName
	 * @param toSave
	 * @return
	 */
	public static boolean androidFileSave(Context con, String fileName, String toSave) {
		Properties properties = new Properties();
		properties.put(FILE_ENCODING, toSave);
		try {
			FileOutputStream stream = con.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			properties.store(stream, "");
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * (android) read from file
	 * 
	 * @param fileName
	 * @return
	 */
	public static String androidFileload(Context con, String fileName) {
		Properties properties = new Properties();
		try {
			FileInputStream stream = con.openFileInput(fileName);
			properties.load(stream);
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return properties.get(FILE_ENCODING).toString();
	}

	/**
	 * (java)write to file
	 * 
	 * @param str
	 * @param path
	 */
	public static void writeFile(String str, File file) {
		FileOutputStream out;
		try {
			file.createNewFile();

			out = new FileOutputStream(file, false);
			String infoToWrite = str;
			out.write(infoToWrite.getBytes());
			out.close();

		} catch (IOException e) {
			Log.e("", "write error!");
		}
	}

	/**
	 * (java) read from file
	 * 
	 * @param path
	 * @return
	 */
	public static String readFile(File file) {
		String str = "";
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			int length = (int) file.length();
			byte[] temp = new byte[length];
			in.read(temp, 0, length);
			str = EncodingUtils.getString(temp, FILE_ENCODING);
			in.close();
		} catch (IOException e) {
			Log.e("", "read error!");

		}
		return str;
	}

	/**
	 * get the file name from its absolute path
	 * 
	 * @param path
	 * @return
	 */
	public static String pathToName(String path) {
		String[] parts = path.split("/");

		String fileName = parts[parts.length - 1];

		String[] nameParts = fileName.split("[.]");
		String getName = "";
		for (int m = 0; m < nameParts.length - 1; m++) {
			getName = getName + nameParts[m];
		}

		return getName;
	}

	/**
	 * get the sound list of the system
	 */
	public static ArrayList<String> getSystemRingList(Context con) {
		ArrayList<String> getArray = new ArrayList<String>();
		ContentResolver cr = con.getContentResolver();
		String[] cols = new String[] { MediaStore.Audio.Media.IS_RINGTONE, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME };
		Cursor cursor = cr.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cols, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				if (cursor.getString(0).equals("1")) {
					getArray.add(cursor.getString(2));
				}

			} while (cursor.moveToNext());
		}
		return getArray;
	}

	/**
	 * format the time
	 * @param time
	 * @return
	 */
	public static String timeFormat(int time) {
		String forReturn = "00";
		if(time < 10) {
			forReturn = "0" + time;
		} else {
			forReturn = "" + time;
		}
		
		return forReturn;
	}

}
