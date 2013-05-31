package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;
import com.easyandroid.ialarm.views.list.CornerMoveListView;

public class IAlarmAddPickSound extends Activity {

	private CornerMoveListView ilistSound;
	private CornerMoveListView ilistSoundSlient;
	private int ilistSoundPosition;
	private String selectedSoundPath = "";
	private Button btnPickReturn;

	ArrayList<String> arraySoundsTemp = new ArrayList<String>();
	ArrayList<String> arraySounds = new ArrayList<String>();

	private static final String TAG_NAME = "tagname";
	private static final String TAG_SELECTED = "selected";
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmadd_picksound);

		ilistSound = (CornerMoveListView) findViewById(R.id.list_alarmadd_picksound);
		ilistSoundSlient = (CornerMoveListView) findViewById(R.id.list_alarmadd_picksound_slient);
		btnPickReturn = (Button) findViewById(R.id.btn_picksoundreturn);

		Bundle bundle = getIntent().getExtras();
		selectedSoundPath = bundle.getString("passData");

		arraySounds.add(getString(R.string.alarm_sound_null));

		arraySoundsTemp = IAlarmHelper.getSystemRingList(IAlarmAddPickSound.this);
		for (int i = 0; i < arraySoundsTemp.size(); i++) {
			arraySounds.add(arraySoundsTemp.get(i));
		}

		updateSoundListView();
		mediaPlayer = new MediaPlayer();
		btnPickReturn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// return the selected sound path
				Bundle bundle = new Bundle();
				bundle.putString("DataKey", selectedSoundPath);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
				overridePendingTransition(R.anim.activity_push_right_in, R.anim.activity_push_right_out);
			}
		});
		ilistSound.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("", "alarm tag pick item on clicked!!");
				selectedSoundPath = arraySounds.get(arg2 + 1);
				ilistSoundPosition = arg2;
				updateSoundListView();

				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				playSound(selectedSoundPath);
			}
		});

		ilistSoundSlient.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("", "alarm tag pick item on clicked!!");
				selectedSoundPath = arraySounds.get(0);
				ilistSoundPosition = 0;
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				updateSoundListView();
			}
		});
	}

	private void updateSoundListView() {

		ArrayList<HashMap<String, Object>> alarmSlientListItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put(TAG_NAME, IAlarmHelper.pathToName(arraySounds.get(0)));
		if (selectedSoundPath.equals(arraySounds.get(0))) {
			map1.put(TAG_SELECTED, "1");
		} else {
			map1.put(TAG_SELECTED, "0");
		}
		alarmSlientListItem.add(map1);
		IAlarmAddPickListAdapter listSlientItemAdapter = new IAlarmAddPickListAdapter(IAlarmAddPickSound.this, alarmSlientListItem, R.layout.view_listrow_alarmaddpick, new String[] { TAG_NAME,
				TAG_SELECTED }, new int[] { R.id.text_alarmpicktitle, R.id.img_alarmpickselected });
		ilistSoundSlient.setAdapter(listSlientItemAdapter);

		ArrayList<HashMap<String, Object>> alarmTagListItem = new ArrayList<HashMap<String, Object>>();
		for (int j = 1; j < arraySounds.size(); j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(TAG_NAME, IAlarmHelper.pathToName(arraySounds.get(j)));
			if (selectedSoundPath.equals(arraySounds.get(j))) {
				ilistSoundPosition = j - 1;
				map.put(TAG_SELECTED, "1");
			} else {
				map.put(TAG_SELECTED, "0");
			}
			alarmTagListItem.add(map);
		}
		IAlarmAddPickListAdapter listItemAdapter = new IAlarmAddPickListAdapter(IAlarmAddPickSound.this, alarmTagListItem, R.layout.view_listrow_alarmaddpick, new String[] { TAG_NAME, TAG_SELECTED },
				new int[] { R.id.text_alarmpicktitle, R.id.img_alarmpickselected });
		ilistSound.setAdapter(listItemAdapter);
		ilistSound.setSelection(ilistSoundPosition);
	}

	private void playSound(String soundPath) {
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
		// play end, replay!
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
//				Log.e("MediaPlayer on complated", "restart");
			}
		});
		// play error!
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_CANCELED);
			finish();
			overridePendingTransition(R.anim.activity_push_right_in, R.anim.activity_push_right_out);
		}
		return false;
	}
}