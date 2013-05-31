package com.easyandroid.ialarm.timer;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;
import com.easyandroid.ialarm.views.list.CornerMoveListView;

public class SelectSoundActivity extends Activity {

	private Button btnCancle;
	private Button btnOk;
	private CornerMoveListView ipListView_sounds;
	private CornerMoveListView ipListView_sleepMode;
	private int ipListView_sounds_Position = 0;

	private final String TAG_SOUND_NAME = "soundname";
	private final String TAG_IMAGE = "image";

	private ArrayList<String> arrayRingPathsTemp = new ArrayList<String>();
	private ArrayList<String> arraySelectList = new ArrayList<String>();

	private String getSelectedSoundPath = "";
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selectsound);

		btnCancle = (Button) findViewById(R.id.btn_cancle);
		btnOk = (Button) findViewById(R.id.btn_ok);

		ipListView_sleepMode = (CornerMoveListView) findViewById(R.id.ilist_sounds_sleepmode);
		ipListView_sounds = (CornerMoveListView) findViewById(R.id.ilist_sounds);
		
		getSelectedSoundPath = IAlarmHelper.androidFileload(getBaseContext(), IAlarmHelper.FILENAME_SAVE_TIMERSOUND);// 获取之前设置的铃声
		// get sound list of system
		arrayRingPathsTemp = IAlarmHelper.getSystemRingList(SelectSoundActivity.this);

		arraySelectList.add(getString(R.string.timer_setting_sleep));
		for (int i = 0; i < arrayRingPathsTemp.size(); i++) {
			arraySelectList.add(arrayRingPathsTemp.get(i));
		}
		updateSoundsListView();
		mediaPlayer = new MediaPlayer();
		ipListView_sounds.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// clicked sound list item
				getSelectedSoundPath = arraySelectList.get(arg2+1);
				ipListView_sounds_Position = arg2;
				updateSoundsListView();
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				playSound(getSelectedSoundPath);
			}
		});
		ipListView_sleepMode.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// clicked sleep mode item
				getSelectedSoundPath = arraySelectList.get(0);
				ipListView_sounds_Position = arg2;
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				updateSoundsListView();
			}
		});

		btnCancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
			}
		});
		btnOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.e("send", getSelectedSoundPath);
				IAlarmHelper.androidFileSave(getBaseContext(), IAlarmHelper.FILENAME_SAVE_TIMERSOUND, getSelectedSoundPath);
				Bundle bundle = new Bundle();
				bundle.putString("DataKey", getSelectedSoundPath);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
				overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
			}
		});

	}

	private void updateSoundsListView() {
		
		//set view of sleep mode list;
		ArrayList<HashMap<String, Object>> sleepmodeListItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(TAG_SOUND_NAME, IAlarmHelper.pathToName(arraySelectList.get(0)));
		if(arraySelectList.get(0).equals(getSelectedSoundPath)) {
			map.put(TAG_IMAGE, R.drawable.img_selected);
		} else {
			map.put(TAG_IMAGE, R.drawable.img_unselected);
		}
		sleepmodeListItem.add(map);

		SimpleAdapter sleepListItemAdapter = new SimpleAdapter(this, sleepmodeListItem, R.layout.view_listrow_soundselect, new String[] { TAG_SOUND_NAME, TAG_IMAGE }, new int[] { R.id.text_soundlist,
				R.id.img_soundlist });
		ipListView_sleepMode.setAdapter(sleepListItemAdapter);
		
		//set view of sound list;
		
		ArrayList<HashMap<String, Object>> soundsListItem = new ArrayList<HashMap<String, Object>>();
		int ringCounts =  arraySelectList.size();
		for (int i = 1; i < ringCounts; i++) {
			HashMap<String, Object> map2 = new HashMap<String, Object>();
			map2.put(TAG_SOUND_NAME, IAlarmHelper.pathToName(arraySelectList.get(i)));
			if (arraySelectList.get(i).equals(getSelectedSoundPath)) {
				ipListView_sounds_Position = i-1;
				map2.put(TAG_IMAGE, R.drawable.img_selected);
			} else {
				map2.put(TAG_IMAGE, R.drawable.img_unselected);
			}
			soundsListItem.add(map2);
		}
		SimpleAdapter soundsListItemAdapter = new SimpleAdapter(this, soundsListItem, R.layout.view_listrow_soundselect, new String[] { TAG_SOUND_NAME, TAG_IMAGE }, new int[] { R.id.text_soundlist,
				R.id.img_soundlist });
		ipListView_sounds.setAdapter(soundsListItemAdapter);
		ipListView_sounds.setSelection(ipListView_sounds_Position);
		
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
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
				Log.e("MediaPlayer on complated", "restart");
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
			overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
