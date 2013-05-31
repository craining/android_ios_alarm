package com.easyandroid.ialarm.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.easyandroid.ialarm.IAlarmActivity;
import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;

public class IAlarmOnActivity extends Activity {

	private MediaPlayer mediaPlayer;
	private Button btnAlarmOk1;
	private Button btnAlarmSleep;
	private Button btnAlarmOk2;
	private TextView textAlarmTag;
	private TableLayout layoutOkSleep;
	private LinearLayout layoutOk;

	private String getSleep;
	private String getTag;
	private String getSoundPath;
	private String getTime;
	private String getDays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dlg_alarmon);

		btnAlarmOk1 = (Button) findViewById(R.id.btn_alarmok_1);
		btnAlarmOk2 = (Button) findViewById(R.id.btn_alarmok_2);
		btnAlarmSleep = (Button) findViewById(R.id.btn_alarmsleep);
		textAlarmTag = (TextView) findViewById(R.id.text_alarmon);
		layoutOk = (LinearLayout) findViewById(R.id.layout_ok);
		layoutOkSleep = (TableLayout) findViewById(R.id.layout_ok_sleep);
		// light the screen
		PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
		wakeLock.acquire();
		// wakeLock.release();
		Bundle bundle = getIntent().getExtras();
		getDays = bundle.getString("days");
		getTime = bundle.getString("time");
		getSleep = bundle.getString("sleep");
		getTag = bundle.getString("tag");
		getSoundPath = bundle.getString("soundpath");

		textAlarmTag.setText(getTag);
		if (!getSoundPath.equals(getString(R.string.alarm_sound_null))) {
			playTimerSound(getSoundPath);
		}

		// sleep on
		if (getSleep.equals("1")) {
			layoutOkSleep.setVisibility(View.VISIBLE);
			layoutOk.setVisibility(View.GONE);
		} else {
			layoutOk.setVisibility(View.VISIBLE);
			layoutOkSleep.setVisibility(View.GONE);
		}

		btnAlarmOk1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// if the repeat days is never, after this alarm, update the database to off state if not sleep
				if (getDays.equals(getString(R.string.alarm_days_0) + ",")) {
					closeAlarmInDb(getTime, getTag, getSoundPath, getDays, getSleep);
				}
				finish();
			}
		});
		btnAlarmOk2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// if the repeat days is never and go to state sleep, then not close the alarm
				if (getDays.equals(getString(R.string.alarm_days_0) + ",")) {
					closeAlarmInDb(getTime, getTag, getSoundPath, getDays, getSleep);
				}
				finish();
			}
		});
		btnAlarmSleep.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Go into the sleep mode. save the sleep time(s) to the file for sleep service; 
				if (IAlarmHelper.FILE_SAVE_SLEEP_ALARM.exists()) {
					String sleepAlarms = IAlarmHelper.androidFileload(IAlarmOnActivity.this, IAlarmHelper.FILENAME_SAVE_SLEEP_ALARM);
					sleepAlarms = sleepAlarms + getTime + "-";
					Log.e("Save  sleeping alarms: ", sleepAlarms);
					IAlarmHelper.androidFileSave(IAlarmOnActivity.this, IAlarmHelper.FILENAME_SAVE_SLEEP_ALARM, sleepAlarms);
				} else {
					IAlarmHelper.androidFileSave(IAlarmOnActivity.this, IAlarmHelper.FILENAME_SAVE_SLEEP_ALARM, (getTime + "-"));
				}
				startService(new Intent(IAlarmOnActivity.this, IAlarmSleepService.class));
				finish();
			}
		});

	}

	private void playTimerSound(String soundPath) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(soundPath);
			mediaPlayer.prepare();
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.reset();
			}
			mediaPlayer.start();
		} catch (Exception e) {
			Log.e("PLAY Sound error!!!!", e + "");
		}
		//play end, repay
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
				Log.e("MediaPlayer on complated", "Release");
			}
		});
		//play error
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer player, int arg1, int arg2) {
				mediaPlayer.release();
				mediaPlayer = null;
				Log.e("MediaPlayer on error", "Release");
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}

	private void closeAlarmInDb(String time, String tag, String soundpath, String days, String sleep) {
		IAlarmDb alarmdb = new IAlarmDb(getBaseContext());
		alarmdb.open();
		alarmdb.upDateDB(time, tag, "0", soundpath, days, sleep);
		alarmdb.close();
		IAlarmActivity.updateDb = true;
		IAlarmActivity.updateAlarm = true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
