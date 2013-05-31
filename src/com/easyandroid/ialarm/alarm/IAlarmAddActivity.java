package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;
import com.easyandroid.ialarm.views.list.CornerListView;
import com.easyandroid.ialarm.views.wheel.NumericWheelAdapter;
import com.easyandroid.ialarm.views.wheel.OnWheelChangedListener;
import com.easyandroid.ialarm.views.wheel.OnWheelScrollListener;
import com.easyandroid.ialarm.views.wheel.WheelView;

public class IAlarmAddActivity extends Activity {

	private Button btnCancle;
	private Button btnSave;
	private CornerListView alarmAddListSet;

	private WheelView hoursPick;
	private WheelView minutesPick;
	private SoundPool wheel_sound_pool;
	
	private RelativeLayout layoutBottom;
	private RelativeLayout layoutTop;
	private LinearLayout layoutWheel;
	
	private int int_sound_wheel;

	private final static String TAG_SET_ITEM = "setitem";
	private final static String TAG_SET_INFO = "setinfo";
	private final static String TAG_SET_IMGSLEEP = "sleeponoff";

	private int getAlarmId = -1;

	public static String alarmDays = "";
	public static String alarmSound = "";
	public static String alarmSoundPath = "";
	public static String alarmSleep = "";
	public static String alarmTag = "";
	public static String alarmTime = "";
	public static int alarmHours;
	public static int alarmMinutes;

	private static final int REQUEST_CODE_RPEAT = 103;
	private static final int REQUEST_CODE_SOUND = 104;
	private static final int REQUEST_CODE_TAG = 105;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addalarm);

		btnCancle = (Button) findViewById(R.id.btn_alarmaddcancle);
		btnSave = (Button) findViewById(R.id.btn_alarmaddok);
		alarmAddListSet = (CornerListView) findViewById(R.id.list_alarmadd);

		layoutBottom = (RelativeLayout) findViewById(R.id.layout_alarmadd_bottom);
		layoutTop = (RelativeLayout) findViewById(R.id.layout_showaddalarmlist);
//		layoutWheel = (LinearLayout) findViewById(R.id.alarmadd_wheellayout);
		
		int layoutToppestH = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.35);
		int layoutToppestPad = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.12);
		int layoutLR = (int) (IAlarmHelper.SCREEN_WEDTH * 0.0625);
		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, layoutToppestH);
		param2.setMargins(layoutLR, layoutToppestPad, layoutLR, 0);
		layoutTop.setLayoutParams(param2);
		
		
		int layoutTopDis = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.5);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, IAlarmHelper.SCREEN_HEIGHT-layoutTopDis);
		param.setMargins(0, layoutTopDis, 0, 0);
		layoutBottom.setLayoutParams(param);
		
		hoursPick = (WheelView) findViewById(R.id.wheel_alarmaddhour);
		hoursPick.setAdapter(new NumericWheelAdapter(0, 23));
		minutesPick = (WheelView) findViewById(R.id.wheel_alarmaddmins);
		minutesPick.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		minutesPick.setCyclic(true);

		//match the different screen
		if(IAlarmHelper.SCREEN_HEIGHT >= 800) {
			minutesPick.setVisibleItems(7);
			hoursPick.setVisibleItems(7);
		} else if(IAlarmHelper.SCREEN_HEIGHT <= 480) {
			minutesPick.setVisibleItems(5);
			hoursPick.setVisibleItems(5);
		}
		
		wheel_sound_pool = new SoundPool(2, AudioManager.STREAM_NOTIFICATION, 4);
		int_sound_wheel = wheel_sound_pool.load(IAlarmAddActivity.this, R.raw.sound_wheel, 0);

		//get the id of alarm list if is not -1, show alarm information for editing.
		//if is -1, show the default alarm information for editing.
		Bundle bundle = getIntent().getExtras();
		getAlarmId = bundle.getInt("passData");

		if (getAlarmId == -1) {
			// set default, alarm sound, repeat days, sleep state, tag and start time.
			if(IAlarmHelper.getSystemRingList(getBaseContext()).size() == 0) {
				alarmSoundPath = getString(R.string.alarm_sound_null);
			} else {
				alarmSoundPath = IAlarmHelper.getSystemRingList(getBaseContext()).get(0);
			}
			alarmSound = IAlarmHelper.pathToName(alarmSoundPath);
			alarmDays = getString(R.string.alarm_days_0) + ",";
			alarmSleep = "1";
			alarmTag = getString(R.string.alarm_title);
			Calendar calendar = Calendar.getInstance();
			alarmMinutes = calendar.get(Calendar.MINUTE);
			alarmHours = calendar.get(Calendar.HOUR_OF_DAY);
			alarmTime = alarmHours + ":" + alarmMinutes;
		} else {
			// show the alarm information for editing
			alarmSoundPath = IAlarm.arrayAlarmSound.get(getAlarmId);
			alarmSound = IAlarmHelper.pathToName(alarmSoundPath);
			alarmDays = IAlarm.arrayAlarmDays.get(getAlarmId);
			alarmSleep = IAlarm.arrayAlarmSleep.get(getAlarmId);
			alarmTag = IAlarm.arrayAlarmTag.get(getAlarmId);
			alarmTime = IAlarm.arrayAlarmTime.get(getAlarmId);

			alarmHours = Integer.parseInt(alarmTime.split(":")[0]);
			alarmMinutes = Integer.parseInt(alarmTime.split(":")[1]);

		}
		updateSetListView();

		hoursPick.setCurrentItem(alarmHours);
		minutesPick.setCurrentItem(alarmMinutes);
		addChangingListener(minutesPick, "min");
		addChangingListener(hoursPick, "hour");
		
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		};
		hoursPick.addChangingListener(wheelListener);
		minutesPick.addChangingListener(wheelListener);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}
			public void onScrollingFinished(WheelView wheel) {
			}
		};

		hoursPick.addScrollingListener(scrollListener);
		minutesPick.addScrollingListener(scrollListener);
		
		alarmAddListSet.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Log.e("", "add alarm item on clicked!!");
				
				if (arg2 == 0) {
					// set repeat days
					Intent i = new Intent(IAlarmAddActivity.this, IAlarmAddPickDays.class);
					Bundle b = new Bundle();
					b.putString("passData", alarmDays);
					i.putExtras(b);
					startActivityForResult(i, REQUEST_CODE_RPEAT);
					overridePendingTransition(R.anim.activity_push_left_in, R.anim.activity_push_left_out);

				} else if (arg2 == 1) {
					// set alarm sound
					Intent i = new Intent(IAlarmAddActivity.this, IAlarmAddPickSound.class);
					Bundle b = new Bundle();
					b.putString("passData", alarmSoundPath);
					i.putExtras(b);
					startActivityForResult(i, REQUEST_CODE_SOUND);
					overridePendingTransition(R.anim.activity_push_left_in, R.anim.activity_push_left_out);

				}  else if (arg2 == 3) {
					// set alarm tag
					Intent i = new Intent(IAlarmAddActivity.this, IAlarmAddPickTag.class);
					Bundle b = new Bundle();
					b.putString("passData", alarmTag);
					i.putExtras(b);
					startActivityForResult(i, REQUEST_CODE_TAG);
					overridePendingTransition(R.anim.activity_push_left_in, R.anim.activity_push_left_out);
				}
			}
		});

		btnCancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
			}
		});
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//if add new, judge the time(should not same), then update the database
				// if not, just update database
				alarmTime = IAlarmHelper.timeFormat(hoursPick.getCurrentItem()) + ":" + IAlarmHelper.timeFormat(minutesPick.getCurrentItem());
				if (getAlarmId != -1) {
					IAlarm.arrayAlarmTime.set(getAlarmId, alarmTime);
					IAlarm.arrayAlarmTag.set(getAlarmId, alarmTag);
					IAlarm.arrayAlarmOnoff.set(getAlarmId, "1");
					IAlarm.arrayAlarmSound.set(getAlarmId, alarmSoundPath);
					IAlarm.arrayAlarmDays.set(getAlarmId, alarmDays);
					IAlarm.arrayAlarmSleep.set(getAlarmId, alarmSleep);
					IAlarm.updateAlarmDb(IAlarmAddActivity.this);
					setResult(RESULT_OK);
					finish();
					overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
				} else {
					boolean alreadyExist = false;
					for (int o = 0; o < IAlarm.arrayAlarmTime.size(); o++) {
						if (IAlarm.arrayAlarmTime.get(o).equals(alarmTime)) {
							alreadyExist = true;
						}
					}
					if (alreadyExist) {
						Toast.makeText(getBaseContext(), R.string.alarm_add_error, Toast.LENGTH_SHORT).show();
					} else {
						IAlarm.arrayAlarmTime.add(alarmTime);
						IAlarm.arrayAlarmTag.add(alarmTag);
						IAlarm.arrayAlarmOnoff.add("1");
						IAlarm.arrayAlarmSound.add(alarmSoundPath);
						IAlarm.arrayAlarmDays.add(alarmDays);
						IAlarm.arrayAlarmSleep.add(alarmSleep);
						IAlarm.updateAlarmDb(IAlarmAddActivity.this);
						setResult(RESULT_OK);
						finish();
						overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
					}
				}
			}
		});
	}

	/**
	 * 更新显示设置列表的参数
	 */
	private void updateSetListView() {
		//show the set list of alarm
		ArrayList<HashMap<String, Object>> alarmSetListItem = new ArrayList<HashMap<String, Object>>();
		// repeat
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put(TAG_SET_ITEM, getString(R.string.alarm_repeat));
		map1.put(TAG_SET_INFO, alarmDays);
		map1.put(TAG_SET_IMGSLEEP, "-1");
		alarmSetListItem.add(map1);
		//sound
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put(TAG_SET_ITEM, getString(R.string.alarm_sound));
		map2.put(TAG_SET_INFO, alarmSound);
		map2.put(TAG_SET_IMGSLEEP, "-1");
		alarmSetListItem.add(map2);
		//sleep
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put(TAG_SET_ITEM, getString(R.string.alarm_sleep));
		map3.put(TAG_SET_INFO, "-1");
		map3.put(TAG_SET_IMGSLEEP, alarmSleep);
		alarmSetListItem.add(map3);
		//tag
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put(TAG_SET_ITEM, getString(R.string.alarm_tag));
		map4.put(TAG_SET_INFO, alarmTag);
		map4.put(TAG_SET_IMGSLEEP, "-1");
		alarmSetListItem.add(map4);

		IAlarmAddListAdapter listItemAdapter = new IAlarmAddListAdapter(IAlarmAddActivity.this, alarmSetListItem, R.layout.view_listrow_worldtime, new String[] { TAG_SET_ITEM, TAG_SET_INFO,
				TAG_SET_IMGSLEEP }, new int[] { R.id.text_alarmadd_item, R.id.text_alarmadd_info, R.id.img_alarmadd_arrow });
		alarmAddListSet.setAdapter(listItemAdapter);
	}

	

	/**
	 * play tip sound of the timer picker wheel
	 * 
	 * @param wheel
	 * @param label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// wheel.setLabel(newValue != 1 ? label + "s" : label);
				wheel_sound_pool.play(int_sound_wheel, 0.3f, 0.3f, 0, 0, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_TAG) {
			// from tag set view
			if (resultCode == RESULT_OK) {
				alarmTag = data.getExtras().getString("DataKey");
				updateSetListView();
			}

		} else if (requestCode == REQUEST_CODE_SOUND) {
			// from sound select view
			if (resultCode == RESULT_OK) {
				alarmSoundPath = data.getExtras().getString("DataKey");
				alarmSound = IAlarmHelper.pathToName(alarmSoundPath);
				Log.e("get selected Path", alarmSoundPath);
				updateSetListView();
			}

		} else if (requestCode == REQUEST_CODE_RPEAT) {
			// from repeat days view
			if (resultCode == RESULT_OK) {
				alarmDays = data.getExtras().getString("DataKey");
				Log.e("get", alarmDays);
				updateSetListView();
			}
		}
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
