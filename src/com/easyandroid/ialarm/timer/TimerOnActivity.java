package com.easyandroid.ialarm.timer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easyandroid.ialarm.R;

public class TimerOnActivity extends Activity {

	private MediaPlayer mediaPlayer;
	private Button btnOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dlg_timeron);

		Bundle bundle = getIntent().getExtras();
		String getSoundPath = bundle.getString("soundpath");
		btnOk = (Button) findViewById(R.id.btn_timerok);

		if (getSoundPath.equals(getString(R.string.timer_setting_sleep))) {
			// finish && sleep phone
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			if (pm.isScreenOn()) {
				// phone sleep
				Toast.makeText(getBaseContext(), "Still cannot go to sleep state!", Toast.LENGTH_LONG).show();

				// KeyguardManager km = (KeyguardManager)
				// getSystemService(Context.KEYGUARD_SERVICE);
				// KeyguardLock keyguardLock = km.newKeyguardLock("Lock");
				//
				// if(!km.inKeyguardRestrictedInputMode() ) {
				// keyguardLock.reenableKeyguard();
				// Log.e("", "lock keyboard ");
				// }
				// PowerManager pm1 = (PowerManager)
				// getSystemService(Context.POWER_SERVICE);
				// WakeLock wk =
				// pm1.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				// this.getClass().getCanonicalName());
				// wk.release();

				// WindowManager.LayoutParams lp = getWindow().getAttributes();
				// lp.screenBrightness = (float) (0.0);
				// getWindow().setAttributes(lp);
				PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
				wakeLock.acquire();
				wakeLock.release();
			}
			finish();
		} else {

			if (getSoundPath != null && !getSoundPath.equals("")) {
				playTimerSound(getSoundPath); 
			}

			PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
			wakeLock.acquire();
//			wakeLock.release();
		}

		btnOk.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
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
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				if(mediaPlayer != null) {
					mediaPlayer.start();
				}
//				Log.e("MediaPlayer on complated", "restart");
			}
		});
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
		Log.e("", "timeron activity destroyed! ");
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
