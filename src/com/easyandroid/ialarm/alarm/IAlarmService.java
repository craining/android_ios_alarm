package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.easyandroid.ialarm.IAlarmActivity;
import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;

public class IAlarmService extends Service {

	private ArrayList<String> arrayAlarmTime = new ArrayList<String>();
	private ArrayList<String> arrayAlarmTag = new ArrayList<String>();
	private ArrayList<String> arrayAlarmOnOff = new ArrayList<String>();
	private ArrayList<String> arrayAlarmDays = new ArrayList<String>();
	private ArrayList<String> arrayAlarmSoundPath = new ArrayList<String>();
	private ArrayList<String> arrayAlarmSleep = new ArrayList<String>();

	private ArrayList<String> arrayAlarmTimeEnable = new ArrayList<String>();
	private ArrayList<String> arrayAlarmTagEnable = new ArrayList<String>();
	private ArrayList<String> arrayAlarmDaysEnable = new ArrayList<String>();
	private ArrayList<String> arrayAlarmSoundPathEnable = new ArrayList<String>();
	private ArrayList<String> arrayAlarmSleepEnable = new ArrayList<String>();

	private PowerManager pm;
	private WakeLock wakeLock;

	private Thread mClockThread;

	private boolean onChecking = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.e("Alarm service", " create   !");
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
		wakeLock.acquire();

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e("Alarm service", " start   !");
		//read the database, get the information of the alarm(s), 
		//if exist the alarm, then start the listen thread
		//else stop self.
		if (!getAlarmsInfo()) {
			stopSelf();
		} else {
			// Listen thread
			mClockThread = new looperThread();
			mClockThread.start();
		}

		super.onStart(intent, startId);
	}

	/**
	 * get the information of alarm(s) which is on
	 */
	private boolean getAlarmsInfo() {
		IAlarmDb alarmdb = new IAlarmDb(getBaseContext());
		alarmdb.open();
		arrayAlarmTime = IAlarmDb.getColumnInf(IAlarmDb.KEY_TIME);
		arrayAlarmTag = IAlarmDb.getColumnInf(IAlarmDb.KEY_TAG);
		arrayAlarmOnOff = IAlarmDb.getColumnInf(IAlarmDb.KEY_ONOFF);
		arrayAlarmDays = IAlarmDb.getColumnInf(IAlarmDb.KEY_DAYS);
		arrayAlarmSoundPath = IAlarmDb.getColumnInf(IAlarmDb.KEY_SOUND_PATH);
		arrayAlarmSleep = IAlarmDb.getColumnInf(IAlarmDb.KEY_SLEEP);
		alarmdb.close();
		
		for (int i = 0; i < arrayAlarmTime.size(); i++) {
			if (arrayAlarmOnOff.get(i).equals("1")) {
				arrayAlarmTimeEnable.add(arrayAlarmTime.get(i));
				arrayAlarmTagEnable.add(arrayAlarmTag.get(i));
				arrayAlarmDaysEnable.add(arrayAlarmDays.get(i));
				arrayAlarmSoundPathEnable.add(arrayAlarmSoundPath.get(i));
				arrayAlarmSleepEnable.add(arrayAlarmSleep.get(i));
			}
		}
		if (arrayAlarmTimeEnable.size() > 0) {
			onChecking = true;
			// showNotification();
		} else {
			onChecking = false;
			// clearNotification();
		}
		return onChecking;
	}

	@Override
	public void onDestroy() {
		Log.e("Alarm service", " destroy   !");
		onChecking = false;
		super.onDestroy();
	}

	/**
	 * listen the time changed every second
	 */
	private void check() {
		Log.e("service", " Checking   !");
		for (int i = 0; i < arrayAlarmTimeEnable.size(); i++) {
			if (startNewMinute() && getNowTime().equals(arrayAlarmTimeEnable.get(i))) {
				//Only the time second is zero can do alarm opera
				// judge the repeat days
				boolean alarm = false;
				for (String a : (arrayAlarmDaysEnable.get(i)).split(",")) {
					if (a.equals(IAlarmService.this.getString(R.string.alarm_days_0))) {
//						Log.e("ALARM", "Alarm on£¡£¡£¡£¡£¡£¡");
						alarmOn(i);
					} else {
						if (a.equals(GetDayOfWeek())) {
							alarm = true;
						}
					}
				}
				if (alarm) {
//					Log.e("ALARM", "Alarm on£¡£¡£¡£¡£¡£¡");
					alarmOn(i);
				}
			}
		}
	}

	/**
	 * start the alarm activity
	 * 
	 * @param i
	 */
	private void alarmOn(int i) {
		
		IAlarmHelper.alarmOver = true;
		Intent intent = new Intent(IAlarmService.this, IAlarmActivity.class);
		Bundle b = new Bundle();
		b.putString("days", arrayAlarmDaysEnable.get(i));
		b.putString("time", getNowTime());
		b.putString("sleep", arrayAlarmSleepEnable.get(i));
		b.putString("tag", arrayAlarmTagEnable.get(i));
		b.putString("soundpath", arrayAlarmSoundPathEnable.get(i));
		intent.putExtras(b);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/**
	 * thread for listen time
	 * 
	 * @author ZhuangYu
	 * 
	 */
	class looperThread extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				do {
					check();
					sleep(1000);
				} while (onChecking);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean startNewMinute() {
		Calendar ca = Calendar.getInstance();
		int second = ca.get(Calendar.SECOND);
		return second == 0 ? true : false;
	}

	private String getNowTime() {
		Calendar ca = Calendar.getInstance();
		int minute = ca.get(Calendar.MINUTE);
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		return IAlarmHelper.timeFormat(hour) + ":" + IAlarmHelper.timeFormat(minute);
	}

	private String GetDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return IAlarmService.this.getString(IAlarm.ALARM_REPEAT_DAYS[dayOfWeek]);
	}
}
