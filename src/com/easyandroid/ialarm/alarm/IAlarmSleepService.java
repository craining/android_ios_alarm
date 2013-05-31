package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;

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

public class IAlarmSleepService extends Service {

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

	private ArrayList<String> arraySleepAlarmTime = new ArrayList<String>();
	private IAlarmDb alarmdb;
	private PowerManager pm;
	private WakeLock wakeLock;

	@Override
	public void onCreate() {
		Log.e("Sleep Service ", "created!");
		alarmdb = new IAlarmDb(getBaseContext());
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
		wakeLock.acquire();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e("Sleep Service ", "started!");
		arraySleepAlarmTime = new ArrayList<String>();
		if (IAlarmHelper.FILE_SAVE_SLEEP_ALARM.exists()) {
			String sleepAlarms = IAlarmHelper.androidFileload(IAlarmSleepService.this, IAlarmHelper.FILENAME_SAVE_SLEEP_ALARM);
			Log.e("content  sleeping alarms: ", sleepAlarms);
			for (String time : sleepAlarms.split("-")) {
				if (time != null && !time.equals("")) {
					arraySleepAlarmTime.add(time);
				}
			}
			Thread sleepThread = new sleepThread();
			sleepThread.start();

		} else {
			stopSelf();
		}

		Thread oneSleep = new sleepThread();
		oneSleep.start();
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.e("Sleep Service ", "destroyed!");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sleep thread, sleep 5 minutes
	 * 
	 * @author ZhuangYu
	 * 
	 */
	class sleepThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				sleep(300000);

				if (getAlarmsInfo()) {
					int allSleepingCounts = arraySleepAlarmTime.size();
					for (int m = 0; m < allSleepingCounts; m++) {
						for (int i = 0; i < arrayAlarmTimeEnable.size(); i++) {
							if (m<allSleepingCounts && arraySleepAlarmTime.get(m).equals(arrayAlarmTimeEnable.get(i))) {
								arraySleepAlarmTime.remove(m);
								allSleepingCounts --;
								String toSaveSleeping = "";
								for (int n = 0; n < arraySleepAlarmTime.size(); n++) {
									if (arraySleepAlarmTime.get(n) != null && !arraySleepAlarmTime.get(n).equals("")) {
										toSaveSleeping = toSaveSleeping + arraySleepAlarmTime.get(n) + "-";
									}
								}
//								Log.e("Save  sleeping alarms: ", toSaveSleeping);
								IAlarmHelper.androidFileSave(IAlarmSleepService.this, IAlarmHelper.FILENAME_SAVE_SLEEP_ALARM, toSaveSleeping);
							
								IAlarmHelper.alarmOver = true;
								Intent intent = new Intent(IAlarmSleepService.this, IAlarmActivity.class);
								Bundle b = new Bundle();
								b.putString("days", arrayAlarmDaysEnable.get(i));
								b.putString("time", arrayAlarmTimeEnable.get(i));
								b.putString("sleep", "1");
								b.putString("tag", arrayAlarmTagEnable.get(i));
								b.putString("soundpath", arrayAlarmSoundPathEnable.get(i));
								intent.putExtras(b);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								if(toSaveSleeping.equals("")) {
									stopSelf();
								}
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 */
	private synchronized boolean getAlarmsInfo() {
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
			return true;
		} else {
			return false;
		}
	}

}
