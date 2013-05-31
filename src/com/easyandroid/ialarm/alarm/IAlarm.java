package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;

import android.content.Context;

import com.easyandroid.ialarm.IAlarmActivity;
import com.easyandroid.ialarm.R;

public class IAlarm {

	public static ArrayList<String> arrayAlarmTime = new ArrayList<String>();
	public static ArrayList<String> arrayAlarmTag = new ArrayList<String>();
	public static ArrayList<String> arrayAlarmOnoff = new ArrayList<String>();
	public static ArrayList<String> arrayAlarmSound = new ArrayList<String>();
	public static ArrayList<String> arrayAlarmDays = new ArrayList<String>();
	public static ArrayList<String> arrayAlarmSleep = new ArrayList<String>();

	// Never, Mon, Tue, Wes ...
	public static int[] ALARM_REPEAT_DAYS = { R.string.alarm_days_0, R.string.alarm_days_1, R.string.alarm_days_2, R.string.alarm_days_3, R.string.alarm_days_4, R.string.alarm_days_5,
			R.string.alarm_days_6, R.string.alarm_days_7 };
	
	
	
	public static void updateAlarmDb(Context con) {
		IAlarmDb alarmdb = new IAlarmDb( con );
		alarmdb.open();
		// clear the database and readd
		alarmdb.clearTable();
		for (int i = 0; i < IAlarm.arrayAlarmTime.size(); i++) {
			alarmdb.insertData(IAlarm.arrayAlarmTime.get(i), IAlarm.arrayAlarmTag.get(i), IAlarm.arrayAlarmOnoff.get(i), IAlarm.arrayAlarmSound.get(i), IAlarm.arrayAlarmDays.get(i),
					IAlarm.arrayAlarmSleep.get(i));
		}
		alarmdb.close();
		IAlarmActivity.updateDb = true;
		IAlarmActivity.updateAlarm = true;
	}
	
}
