package com.easyandroid.ialarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyandroid.ialarm.alarm.IAlarm;
import com.easyandroid.ialarm.alarm.IAlarmAddActivity;
import com.easyandroid.ialarm.alarm.IAlarmDb;
import com.easyandroid.ialarm.alarm.IAlarmListAdapter;
import com.easyandroid.ialarm.alarm.IAlarmOnActivity;
import com.easyandroid.ialarm.alarm.IAlarmService;
import com.easyandroid.ialarm.timer.SelectSoundActivity;
import com.easyandroid.ialarm.timer.TimerOnActivity;
import com.easyandroid.ialarm.views.list.IListView;
import com.easyandroid.ialarm.views.tab.ITabView;
import com.easyandroid.ialarm.views.tab.ITabView.OnTabClickListener;
import com.easyandroid.ialarm.views.tab.ITabView.TabMember;
import com.easyandroid.ialarm.views.wheel.NumericWheelAdapter;
import com.easyandroid.ialarm.views.wheel.OnWheelChangedListener;
import com.easyandroid.ialarm.views.wheel.OnWheelScrollListener;
import com.easyandroid.ialarm.views.wheel.WheelView;
import com.easyandroid.ialarm.worldtime.IWorldtimeListAdapter;
import com.easyandroid.ialarm.worldtime.SelectCityActivity;
import com.easyandroid.ialarm.worldtime.WorldTime;

public class IAlarmActivity extends Activity {
	private static final int TAB_ONE = 1;
	private static final int TAB_TWO = 2;
	private static final int TAB_THREE = 3;
	private static final int TAB_FOUR = 4;
	private ITabView mTabs;
	private LinearLayout mTabLayout_One;
	private LinearLayout mTabLayout_Two;
	private RelativeLayout mTabLayout_Three;
	private RelativeLayout mTabLayout_Four;
	private static final int MSG_STOPWATCH_TIMER = 111;// Timer start
	private static final int MSG_STOPWATCH_NEWTIMER = 112;// Timer count
	private static final int MSG_STOPWATCH_CLEAR = 113;// Timer reset
	private static final int MSG_UPDATE_ALARMLIST = 114;// update alarm list
														// view
	private static final int MSG_RESET_TIMER_VIEW = 115;// Timer end
	private static final int MSG_UPDATE_DB = 116;// update database
	private static final int MSG_UPDATE_WORLDTIME_LIST = 117;// update world
																// clock list
																// view
	private Handler myClockHandler = new clockHandler();
	private PowerManager pm;
	private PowerManager.WakeLock wakeLockTimer;
	private PowerManager.WakeLock wakeLockStopwatch;
	private boolean updateThreadOff;

	// About the world time
	private static final String TAG_LIST_CITY_EDIT = "cityedit";
	private static final String TAG_LIST_CITYNAME = "cityname";
	private static final String TAG_LIST_CITYCLOCK = "cityclockview";
	private static final String TAG_LIST_CITYCLOCKNUM = "cityclocknum";
	private static final String TAG_LIST_CITYCLOCKDATE = "cityclockdate";
	private static final String TAG_LIST_CITYCLOCKDELETETAG = "cityclickdeletetag";
	private static final String TAG_LIST_CITYCLOCKDELETE = "cityclockdelete";
	private ArrayList<String> arrayCityNames = new ArrayList<String>();// name
																		// of
																		// city
	private ArrayList<int[]> arrayCityTimes = new ArrayList<int[]>();// city
																		// time(hour,
																		// minute,
																		// second)
	private ArrayList<String> arrayCityDate = new ArrayList<String>();// date of
																		// city
	private Button btnWorldtimeEdit;
	private ImageView btnWorldtimeAdd;
	private IListView ilistWorldtime;
	private static final int REQUEST_SELECT_WORLDTIME_CITY = 101;
	private boolean worldtimeEditting;
	private boolean updateWorldTimeNot;
	private Thread worldtimeUpdate;

	// About Alarm
	private Button btnAlarmEdit;
	private ImageView btnAlarmAdd;
	private IListView ilistAlarm;
	private static String TAG_LIST_ALARM_EDIT = "alarmedit";
	private static String TAG_LIST_ALARM_TIME = "alarmtime";
	private static String TAG_LIST_ALARM_TAG = "alarmtag";
	private static String TAG_LIST_ALARM_ONOFF = "alarmonoff";
	private static String TAG_LIST_ALARM_DELETE = "alarmdelete";
	private static String TAG_LIST_ALARM_ARROW = "alarmarrow";
	public static boolean alarmEditting = false;
	private static final int REQUEST_ADD_ALARM = 102;
	public static boolean updateAlarm = false;
	public static boolean updateDb = false;
	private Thread threadUpdateAlarmList;
	// private boolean updateAlarmNot;

	// About stopwatch
	private TableLayout tabBtnStopwatch;
	private RelativeLayout layoutShowStowatch;
	private Button btnStopwatchCtrl;
	private Button btnStopwatchCount;
	private boolean stopwatchStart;
	private Thread stopWatchThread;
	private long stopwatchAllCounts;
	private long stopwatchNewCounts;
	private TextView textStopwatchMinute_1a;
	private TextView textStopwatchMinute_1b;
	private TextView textStopwatchSecond_1a;
	private TextView textStopwatchSecond_1b;
	private TextView textStopwatchMsecond_1a;
	private TextView textStopwatchMinute_2a;
	private TextView textStopwatchMinute_2b;
	private TextView textStopwatchSecond_2a;
	private TextView textStopwatchSecond_2b;
	private TextView textStopwatchMsecond_2a;
	private ArrayList<String> countStopwatchCount = new ArrayList<String>();
	private ArrayList<String> countStopwatchTime = new ArrayList<String>();
	private IListView ilistStopwatch;
	private static final String TAG_LIST_COUNT = "listcounts";
	private static final String TAG_LIST_TIME = "listtime";

	// About timer
	private RelativeLayout layoutTimerBottom;
	private RelativeLayout layoutBtnCtrl;
	// private LinearLayout layoutWheelTimer;
	private Button timer_btnSelectSound;
	private Button timer_btnCtrl;
	private boolean timerStart = false;
	private LinearLayout timerShowTime;
	private LinearLayout timerShowTimePick;
	private TextView textTimerHour1;
	private TextView textTimerHour2;
	// private TextView textTimerHourDoublePoint;
	private TextView textTimerMinute1;
	private TextView textTimerMinute2;
	private TextView textTimerSecond1;
	private TextView textTimerSecond2;
	private TimerCount mTimer;
	private SoundPool wheel_sound_pool;
	private int int_sound_wheel;
	private static final int TIMER_START_HOUR = 0;// the default time of timer
	private static final int TIMER_START_MINUTE = 1;// the default time of timer
	private static final int REQUEST_SELECT_TIMER_SOUND = 100;
	private static String getSelectedSoundPath = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics dm = new DisplayMetrics();
		// get screen size to match it
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		IAlarmHelper.SCREEN_HEIGHT = dm.heightPixels;
		IAlarmHelper.SCREEN_WEDTH = dm.widthPixels;
		setContentView(R.layout.main);

		// start from service, to show the alarm window.
		if (IAlarmHelper.alarmOver) {
			IAlarmHelper.alarmOver = false;
			Bundle bundle = getIntent().getExtras();
			Intent intent = new Intent(IAlarmActivity.this, IAlarmOnActivity.class);
			Bundle b = new Bundle();
			b.putString("days", bundle.getString("days"));
			b.putString("time", bundle.getString("time"));
			b.putString("sleep", bundle.getString("sleep"));
			b.putString("tag", bundle.getString("tag"));
			b.putString("soundpath", bundle.getString("soundpath"));
			intent.putExtras(b);
			startActivity(intent);
			if (!updateThreadOff) {
				finish();
			}
		} else {
			initMainView();
		}

	}

	private void initMainView() {
		mTabs = (ITabView) this.findViewById(R.id.Tabs);
		mTabLayout_One = (LinearLayout) this.findViewById(R.id.TabLayout_One);
		mTabLayout_Two = (LinearLayout) this.findViewById(R.id.TabLayout_Two);
		mTabLayout_Three = (RelativeLayout) this.findViewById(R.id.TabLayout_Three);
		mTabLayout_Four = (RelativeLayout) this.findViewById(R.id.TabLayout_Four);

		mTabs.addTabMember(new TabMember(TAB_ONE, getString(R.string.tab_one), R.drawable.tab1));
		mTabs.addTabMember(new TabMember(TAB_TWO, getString(R.string.tab_two), R.drawable.tab2));
		mTabs.addTabMember(new TabMember(TAB_THREE, getString(R.string.tab_three), R.drawable.tab3));
		mTabs.addTabMember(new TabMember(TAB_FOUR, getString(R.string.tab_four), R.drawable.tab4));

		// show the first view (world time)
		mTabLayout_One.setVisibility(View.VISIBLE);
		mTabLayout_Two.setVisibility(View.GONE);
		mTabLayout_Three.setVisibility(View.GONE);
		mTabLayout_Four.setVisibility(View.GONE);

		getSelectedTimerSound();

		threadUpdateAlarmList = new alarmUpdateViewThread();
		worldtimeUpdate = new worldtimeUpdateViewThread();

		worldtimeInitView();// show the world time view
		alarmInitView();// show the alarm view
		stopwatchInitView();// show the stop watch view
		timerInitView();// show the timer view

		mTabs.setOnTabClickListener(new OnTabClickListener() {
			@Override
			public void onTabClick(int tabId) {
				if (tabId == TAB_ONE) {
					// world time
					updateWorldTimeNot = false;
					// alarmEditting = false;
					mTabLayout_One.setVisibility(View.VISIBLE);
					mTabLayout_Two.setVisibility(View.GONE);
					mTabLayout_Three.setVisibility(View.GONE);
					mTabLayout_Four.setVisibility(View.GONE);
				} else if (tabId == TAB_TWO) {
					// alarm
					updateWorldTimeNot = true;
					// worldtimeEditting = false;
					mTabLayout_One.setVisibility(View.GONE);
					mTabLayout_Two.setVisibility(View.VISIBLE);
					mTabLayout_Three.setVisibility(View.GONE);
					mTabLayout_Four.setVisibility(View.GONE);
				} else if (tabId == TAB_THREE) {
					// stop watch
					updateWorldTimeNot = true;
					// worldtimeEditting = false;
					// alarmEditting = false;
					mTabLayout_One.setVisibility(View.GONE);
					mTabLayout_Two.setVisibility(View.GONE);
					mTabLayout_Three.setVisibility(View.VISIBLE);
					mTabLayout_Four.setVisibility(View.GONE);
				} else if (tabId == TAB_FOUR) {
					// timer
					updateWorldTimeNot = true;
					// worldtimeEditting = false;
					// alarmEditting = false;
					mTabLayout_One.setVisibility(View.GONE);
					mTabLayout_Two.setVisibility(View.GONE);
					mTabLayout_Three.setVisibility(View.GONE);
					mTabLayout_Four.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/*************************** World time ***************************************************************/

	/**
	 * 初始化世界时间页面布局
	 */
	private void worldtimeInitView() {

		ilistWorldtime = (IListView) findViewById(R.id.lis_worldtime);
		btnWorldtimeEdit = (Button) findViewById(R.id.btn_worldtime_edit);
		btnWorldtimeAdd = (ImageView) findViewById(R.id.imgbtn_worldtime_add);
		ilistWorldtime.setEnabled(false);

		if (!worldtimeUpdate.isAlive()) {
			worldtimeUpdate.start();
		}

		btnWorldtimeAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// add one city
				Intent i = new Intent(IAlarmActivity.this, SelectCityActivity.class);
				startActivityForResult(i, REQUEST_SELECT_WORLDTIME_CITY);
				overridePendingTransition(R.anim.activity_push_up, R.anim.activity_alpha_action);
			}
		});
		btnWorldtimeEdit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// edit the world time
				if (worldtimeEditting) {
					worldtimeEditting = false;
				} else {
					worldtimeEditting = true;
				}
				updateWorldtimeList(worldtimeEditting);
			}
		});

		updateWorldtimeList(worldtimeEditting);
	}

	/**
	 * get the array list of added cities
	 */
	private void getAllCityTime() {

		arrayCityNames = new ArrayList<String>();
		arrayCityTimes = new ArrayList<int[]>();
		arrayCityDate = new ArrayList<String>();

		// get current time
		Calendar calendar = Calendar.getInstance();
		int currentSecond = calendar.get(Calendar.SECOND);
		int currentMinute = calendar.get(Calendar.MINUTE);
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

		WorldTime.citiesIds = WorldTime.getCities();
		if (WorldTime.citiesIds != null && WorldTime.citiesIds.length > 0) {

			for (int m = 1; m < WorldTime.citiesIds.length; m++) {
				// Log.e("", "    ids " + m);
				String[] times = (WorldTime.getCityTime(IAlarmActivity.this, currentHour + ":" + currentMinute + ":" + currentSecond, WorldTime.gmtData[WorldTime.citiesIds[m]])).split(":");
				int hour = Integer.parseInt(times[0]);
				int minute = Integer.parseInt(times[1]);
				int second = Integer.parseInt(times[2]);
				int[] setTime = new int[3];
				setTime[0] = hour;
				setTime[1] = minute;
				setTime[2] = second;
				arrayCityNames.add(getString(WorldTime.CityName[WorldTime.citiesIds[m]]));
				arrayCityTimes.add(setTime);
				arrayCityDate.add(times[3]);
			}

		}

	}

	/**
	 * update the world time list view：
	 * 
	 * @param ilistWorldtime
	 */
	private void updateWorldtimeList(boolean eidt) {
		if (eidt) {
			btnWorldtimeEdit.setText(R.string.worldtime_editover);
			btnWorldtimeEdit.setBackgroundResource(R.drawable.blue_button_pressed);
		} else {
			btnWorldtimeEdit.setText(R.string.worldtime_edit);
			btnWorldtimeEdit.setBackgroundResource(R.drawable.blue_button_default);
		}
		getAllCityTime();

		ArrayList<HashMap<String, Object>> worldtimeListItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < arrayCityNames.size(); i++) {
			HashMap<String, Object> map_day = new HashMap<String, Object>();
			if (eidt) {
				map_day.put(TAG_LIST_CITY_EDIT, 1);
			} else {
				map_day.put(TAG_LIST_CITY_EDIT, 0);
			}
			map_day.put(TAG_LIST_CITYNAME, arrayCityNames.get(i));
			map_day.put(TAG_LIST_CITYCLOCK, arrayCityTimes.get(i));
			map_day.put(TAG_LIST_CITYCLOCKNUM, IAlarmHelper.timeFormat(arrayCityTimes.get(i)[0]) + ":" + IAlarmHelper.timeFormat(arrayCityTimes.get(i)[1]));
			map_day.put(TAG_LIST_CITYCLOCKDATE, arrayCityDate.get(i));
			map_day.put(TAG_LIST_CITYCLOCKDELETE, "0");

			worldtimeListItem.add(map_day);
		}
		IWorldtimeListAdapter listItemAdapter = new IWorldtimeListAdapter(IAlarmActivity.this, worldtimeListItem, R.layout.view_listrow_worldtime, new String[] { TAG_LIST_CITY_EDIT,
				TAG_LIST_CITYNAME, TAG_LIST_CITYCLOCK, TAG_LIST_CITYCLOCKNUM, TAG_LIST_CITYCLOCKDATE, TAG_LIST_CITYCLOCKDELETETAG, TAG_LIST_CITYCLOCKDELETE }, new int[] { R.id.img_worldtimeedit,
				R.id.text_cityname, R.id.clockview, R.id.text_clocknum, R.id.text_date, R.id.img_editdelete, R.id.btn_editdelete });
		ilistWorldtime.setAdapter(listItemAdapter);
		ilistWorldtime.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// Log.e("", "worldtime item on clicked!!");
			}
		});
	}

	class worldtimeUpdateViewThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				do {
					// Log.e("",
					// "+++++++++++++++++++++++++++++++worldtime thread!!");
					myClockHandler.sendEmptyMessageDelayed(MSG_UPDATE_WORLDTIME_LIST, 0);
					sleep(1000);
				} while (!updateThreadOff);
			} catch (Exception e) {
				Log.e(" ", "worldtime  Thread Error!");
				e.printStackTrace();
			}
		}
	}

	/*************************** Alarm ***************************************************************/

	private void alarmInitView() {
		btnAlarmEdit = (Button) findViewById(R.id.btn_alarmtime_edit);
		btnAlarmAdd = (ImageView) findViewById(R.id.imgbtn_alarmtime_add);
		ilistAlarm = (IListView) findViewById(R.id.lis_alarm);

		if (!threadUpdateAlarmList.isAlive()) {
			threadUpdateAlarmList.start();
		}

		getAllAlarms();
		updateAlarmList(alarmEditting);
		btnAlarmEdit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// edit alarm list
				if (alarmEditting) {
					alarmEditting = false;
				} else {
					alarmEditting = true;
				}
				updateAlarm = true;
				// updateAlarm = true;
				// updateAlarmList(alarmEditting);
			}
		});
		btnAlarmAdd.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// if add a new alarm time, just deliver (int)-1;
				Intent i = new Intent(IAlarmActivity.this, IAlarmAddActivity.class);
				Bundle b = new Bundle();
				b.putInt("passData", -1);
				i.putExtras(b);
				startActivityForResult(i, REQUEST_ADD_ALARM);
				overridePendingTransition(R.anim.activity_push_up, R.anim.activity_alpha_action);
			}
		});
	}

	/**
	 * 读取数据库获得所有预设闹钟
	 */
	private void getAllAlarms() {

		IAlarm.arrayAlarmTime = new ArrayList<String>();
		IAlarm.arrayAlarmTag = new ArrayList<String>();
		IAlarm.arrayAlarmOnoff = new ArrayList<String>();// "1" is alarm on,
															// else off
		IAlarm.arrayAlarmSound = new ArrayList<String>();
		IAlarm.arrayAlarmDays = new ArrayList<String>();
		IAlarm.arrayAlarmSleep = new ArrayList<String>();// "1" is sleep on,
															// else sleep off;

		IAlarmDb alarmdb = new IAlarmDb(IAlarmActivity.this);
		alarmdb.open();
		IAlarm.arrayAlarmTime = IAlarmDb.getColumnInf(IAlarmDb.KEY_TIME);
		IAlarm.arrayAlarmTag = IAlarmDb.getColumnInf(IAlarmDb.KEY_TAG);
		IAlarm.arrayAlarmOnoff = IAlarmDb.getColumnInf(IAlarmDb.KEY_ONOFF);
		IAlarm.arrayAlarmSound = IAlarmDb.getColumnInf(IAlarmDb.KEY_SOUND_PATH);
		IAlarm.arrayAlarmDays = IAlarmDb.getColumnInf(IAlarmDb.KEY_DAYS);
		IAlarm.arrayAlarmSleep = IAlarmDb.getColumnInf(IAlarmDb.KEY_SLEEP);
		alarmdb.close();
	}

	/**
	 * update alarm list view
	 * 
	 * @param ilistWorldtime
	 */
	private void updateAlarmList(boolean eidt) {

		if (eidt) {
			btnAlarmEdit.setText(R.string.alarm_editok);
			btnAlarmEdit.setBackgroundResource(R.drawable.blue_button_pressed);
			ilistAlarm.setEnabled(true);
		} else {
			btnAlarmEdit.setText(R.string.alarm_edit);
			btnAlarmEdit.setBackgroundResource(R.drawable.blue_button_default);
			ilistAlarm.setEnabled(false);
		}

		ArrayList<HashMap<String, Object>> alarmListItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < IAlarm.arrayAlarmTime.size(); i++) {
			HashMap<String, Object> map_day = new HashMap<String, Object>();
			if (eidt) {
				map_day.put(TAG_LIST_ALARM_EDIT, 1);
			} else {
				map_day.put(TAG_LIST_ALARM_EDIT, 0);
			}
			map_day.put(TAG_LIST_ALARM_TIME, IAlarm.arrayAlarmTime.get(i));
			map_day.put(TAG_LIST_ALARM_TAG, IAlarm.arrayAlarmTag.get(i));
			map_day.put(TAG_LIST_ALARM_ONOFF, IAlarm.arrayAlarmOnoff.get(i));
			map_day.put(TAG_LIST_ALARM_DELETE, "0");
			map_day.put(TAG_LIST_ALARM_ARROW, "0");

			alarmListItem.add(map_day);
		}
		IAlarmListAdapter listItemAdapter = new IAlarmListAdapter(IAlarmActivity.this, alarmListItem, R.layout.view_listrow_alarm, new String[] { TAG_LIST_ALARM_EDIT, TAG_LIST_ALARM_TIME,
				TAG_LIST_ALARM_TAG, TAG_LIST_ALARM_ONOFF, TAG_LIST_ALARM_DELETE, TAG_LIST_ALARM_ARROW }, new int[] { R.id.img_alarmedit, R.id.text_alarmclocknum, R.id.text_alarmtype,
				R.id.img_alarm_onoff, R.id.btn_alarmdelete, R.id.img_alarm_arrow });
		ilistAlarm.setAdapter(listItemAdapter);
		ilistAlarm.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// Only if is the state of editing, can do the editing opera
				if (alarmEditting) {
					Intent i = new Intent(IAlarmActivity.this, IAlarmAddActivity.class);
					Bundle b = new Bundle();
					b.putInt("passData", arg2);
					i.putExtras(b);
					startActivityForResult(i, REQUEST_ADD_ALARM);
					overridePendingTransition(R.anim.activity_push_up, R.anim.activity_alpha_action);
				}

			}
		});
	}

	/**
	 * thread of updating the alarm list view
	 * 
	 */
	class alarmUpdateViewThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				do {
					// Log.e("",
					// "---------------------------------alarm thread!!");
					if (updateDb || updateAlarm) {
						myClockHandler.sendEmptyMessageDelayed(MSG_UPDATE_ALARMLIST, 0);
						myClockHandler.sendEmptyMessageDelayed(MSG_UPDATE_DB, 0);
					}
					sleep(50);
				} while (!updateThreadOff);
			} catch (Exception e) {
				Log.e(" ", "alarm  Thread Error!");
				e.printStackTrace();
			}
		}
	}

	/*************************** stop watch ***************************************************************/

	private void stopwatchInitView() {
		layoutShowStowatch = (RelativeLayout) findViewById(R.id.layout_show_stopwatchtime);
		btnStopwatchCtrl = (Button) findViewById(R.id.btn_stopwatch_ctrl);
		btnStopwatchCount = (Button) findViewById(R.id.btn_stopwatch_count);
		tabBtnStopwatch = (TableLayout) findViewById(R.id.tab_btn);

		// match the different screen
		int height = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.28);
		RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, height);
		param1.setMargins(0, 0, 0, 0);
		layoutShowStowatch.setLayoutParams(param1);
		int layoutHeight = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.092);
		int layoutLR = (int) (IAlarmHelper.SCREEN_WEDTH * 0.075);
		int layoutTop = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.3);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(IAlarmHelper.SCREEN_WEDTH, layoutHeight);
		param.setMargins(layoutLR, layoutTop, layoutLR, 0);
		tabBtnStopwatch.setLayoutParams(param);
		btnStopwatchCtrl.setMinimumHeight(layoutHeight);
		btnStopwatchCount.setMinimumHeight(layoutHeight);

		textStopwatchMinute_1a = (TextView) findViewById(R.id.text_stopwatch_1_minute_1);
		textStopwatchMinute_1b = (TextView) findViewById(R.id.text_stopwatch_1_minute_2);
		textStopwatchSecond_1a = (TextView) findViewById(R.id.text_stopwatch_1_second_1);
		textStopwatchSecond_1b = (TextView) findViewById(R.id.text_stopwatch_1_second_2);
		textStopwatchMsecond_1a = (TextView) findViewById(R.id.text_stopwatch_1_msecond_1);
		textStopwatchMinute_2a = (TextView) findViewById(R.id.text_stopwatch_2_minute_1);
		textStopwatchMinute_2b = (TextView) findViewById(R.id.text_stopwatch_2_minute_2);
		textStopwatchSecond_2a = (TextView) findViewById(R.id.text_stopwatch_2_second_1);
		textStopwatchSecond_2b = (TextView) findViewById(R.id.text_stopwatch_2_second_2);
		textStopwatchMsecond_2a = (TextView) findViewById(R.id.text_stopwatch_2_msecond_1);

		ilistStopwatch = (IListView) findViewById(R.id.list_stopwatch);
		// ilistStopwatch.setVerticalScrollBarEnabled(false);
		updateStopwatchListView();
		ilistStopwatch.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			}
		});

		btnStopwatchCtrl.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// stop watch started and stopped.
				// If started then get the lock to avoid the phone sleep;
				// If stopped then release the lock;
				if (stopwatchStart) {
					stopwatchStart = false;
					btnStopwatchCtrl.setText(R.string.stopwatch_btn_start);
					btnStopwatchCount.setText(R.string.stopwatch_btn_clear);
					btnStopwatchCtrl.setBackgroundResource(R.drawable.btn_bg_green);
					btnStopwatchCount.setTextColor(R.color.color_white);
					releaseLock(wakeLockStopwatch);
				} else {
					btnStopwatchCtrl.setText(R.string.stopwatch_btn_stop);
					btnStopwatchCount.setText(R.string.stopwatch_btn_count);
					btnStopwatchCount.setEnabled(true);
					btnStopwatchCtrl.setBackgroundResource(R.drawable.btn_bg_red);
					btnStopwatchCount.setTextColor(R.color.color_white);
					stopwatchStart = true;
					stopWatchThread = new stopWatchCount();
					stopWatchThread.start();
					getLock(pm, wakeLockStopwatch);
				}
			}
		});
		btnStopwatchCount.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// the counting of the stop watch
				if (stopwatchStart) {
					btnStopwatchCount.setText(R.string.stopwatch_btn_count);
					myClockHandler.sendEmptyMessageDelayed(MSG_STOPWATCH_NEWTIMER, 0);
					btnStopwatchCount.setTextColor(R.color.color_white);
				} else {
					btnStopwatchCount.setEnabled(false);
					btnStopwatchCount.setTextColor(R.color.color_lightgrey);
					myClockHandler.sendEmptyMessageDelayed(MSG_STOPWATCH_CLEAR, 0);
				}
			}
		});
	}

	/**
	 * thread of the stop watch running
	 * 
	 */
	class stopWatchCount extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				do {
					stopwatchAllCounts++;
					stopwatchNewCounts++;
					myClockHandler.sendEmptyMessageDelayed(MSG_STOPWATCH_TIMER, 0);
					sleep(100);
				} while (stopwatchStart);
			} catch (Exception e) {
				Log.e(" ", "Stopwatch Thread Error!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * To update the two stop watch views,
	 */
	private void updateStopwatchView() {
		// Get the whole time
		long alreadyAllMinutes = stopwatchAllCounts / 600;
		long alreadyAllSeconds = (stopwatchAllCounts % 600) / 10;
		long alreadyAllMseconds = (stopwatchAllCounts % 600) % 10;
		// Get the time from the last time of counting
		long alreadyNewMinutes = stopwatchNewCounts / 600;
		long alreadyNewSeconds = (stopwatchNewCounts % 600) / 10;
		long alreadyNewMseconds = (stopwatchNewCounts % 600) % 10;

		setTextView(textStopwatchMinute_1a, alreadyNewMinutes / 10);
		setTextView(textStopwatchMinute_1b, alreadyNewMinutes % 10);
		setTextView(textStopwatchSecond_1a, alreadyNewSeconds / 10);
		setTextView(textStopwatchSecond_1b, alreadyNewSeconds % 10);
		setTextView(textStopwatchMsecond_1a, alreadyNewMseconds);

		setTextView(textStopwatchMinute_2a, alreadyAllMinutes / 10);
		setTextView(textStopwatchMinute_2b, alreadyAllMinutes % 10);
		setTextView(textStopwatchSecond_2a, alreadyAllSeconds / 10);
		setTextView(textStopwatchSecond_2b, alreadyAllSeconds % 10);
		setTextView(textStopwatchMsecond_2a, alreadyAllMseconds);
	}

	/**
	 * update the count list view in the bottom of stop watch view
	 */
	private void updateStopwatchListView() {
		// Get the counting times and its time to the array list and add it to
		// the list view
		long alreadyNewMinutes = stopwatchNewCounts / 600;
		long alreadyNewSeconds = (stopwatchNewCounts % 600) / 10;
		long alreadyNewMseconds = (stopwatchNewCounts % 600) % 10;
		int counts = countStopwatchCount.size() + 1;
		// Only if the stop watch is at the state of running can do count
		if (stopwatchStart) {
			countStopwatchCount.add(getString(R.string.stopwatch_btn_count) + ":  " + counts + "          ");
			countStopwatchTime.add(IAlarmHelper.timeFormat((int) alreadyNewMinutes) + ":" + IAlarmHelper.timeFormat((int) alreadyNewSeconds) + "." + alreadyNewMseconds);
		}
		ArrayList<HashMap<String, Object>> stopwatchListItem = new ArrayList<HashMap<String, Object>>();
		int allCounts = countStopwatchCount.size();
		if (allCounts < 4) {
			for (int a = allCounts - 1; a >= 0; a--) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(TAG_LIST_COUNT, countStopwatchCount.get(a));
				map.put(TAG_LIST_TIME, countStopwatchTime.get(a));
				stopwatchListItem.add(map);
			}
			for (int b = 0; b <= 4 - allCounts; b++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(TAG_LIST_COUNT, "");
				map.put(TAG_LIST_TIME, "");
				stopwatchListItem.add(map);
			}
		} else {
			for (int a = allCounts - 1; a >= 0; a--) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(TAG_LIST_COUNT, countStopwatchCount.get(a));
				map.put(TAG_LIST_TIME, countStopwatchTime.get(a));
				stopwatchListItem.add(map);
			}
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(IAlarmActivity.this, stopwatchListItem, R.layout.view_listrow_stopwatch, new String[] { TAG_LIST_COUNT, TAG_LIST_TIME }, new int[] {
				R.id.text_counts, R.id.text_time });
		ilistStopwatch.setAdapter(listItemAdapter);
	}

	/*************************** TIMER ***************************************************************/

	private void timerInitView() {

		wheel_sound_pool = new SoundPool(2, AudioManager.STREAM_NOTIFICATION, 4);
		int_sound_wheel = wheel_sound_pool.load(IAlarmActivity.this, R.raw.sound_wheel, 0);
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23));
		hours.setLabel(getString(R.string.text_hour));

		final WheelView mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setLabel(getString(R.string.text_minute));
		mins.setCyclic(true);
		// match the screen
		if (IAlarmHelper.SCREEN_HEIGHT >= 800) {
			mins.setVisibleItems(7);
			hours.setVisibleItems(7);
		} else if (IAlarmHelper.SCREEN_HEIGHT <= 480 && IAlarmHelper.SCREEN_HEIGHT >= 330) {
			mins.setVisibleItems(5);
			hours.setVisibleItems(5);
		} else {
			mins.setVisibleItems(3);
			hours.setVisibleItems(3);
			hours.setLabel(getString(R.string.text_hour_2));
			mins.setLabel(getString(R.string.text_minute_2));
		}

		timer_btnSelectSound = (Button) findViewById(R.id.btn_selectsound);
		timer_btnCtrl = (Button) findViewById(R.id.btn_timerctrl);
		timerShowTime = (LinearLayout) findViewById(R.id.layout_showtime);
		timerShowTimePick = (LinearLayout) findViewById(R.id.layot_timepick);

		textTimerHour1 = (TextView) findViewById(R.id.text_timer_hour_1);
		textTimerHour2 = (TextView) findViewById(R.id.text_timer_hour_2);
		// textTimerHourDoublePoint = (TextView)
		// findViewById(R.id.text_timer_doublepoint);
		textTimerMinute1 = (TextView) findViewById(R.id.text_timer_minute_1);
		textTimerMinute2 = (TextView) findViewById(R.id.text_timer_minute_2);
		textTimerSecond1 = (TextView) findViewById(R.id.text_timer_second_1);
		textTimerSecond2 = (TextView) findViewById(R.id.text_timer_second_2);

		timer_btnSelectSound.setText(getString(R.string.selectsound_btn) + "   " + IAlarmHelper.pathToName(getSelectedSoundPath));

		layoutTimerBottom = (RelativeLayout) findViewById(R.id.layout_selectsound_timer);
		layoutBtnCtrl = (RelativeLayout) findViewById(R.id.layout_ctrl_timer);
		// layoutWheelTimer = (LinearLayout) findViewById(R.id.layot_timepick);

		int btnHeight = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.09);
		int marginTop = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.53);

		// The wheel still cannot march the screen size of 240*320
		//
		// if(IAlarmHelper.SCREEN_HEIGHT <= 330 ) {
		// int height = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.3);
		// LinearLayout.LayoutParams param0 = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		// height);
		// param0.setMargins(0, 0, 0, 0);
		// layoutWheelTimer.setLayoutParams(param0);
		// }

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, btnHeight);
		param.setMargins(0, marginTop, 0, 0);
		layoutTimerBottom.setLayoutParams(param);

		int layoutTwoMarTop = (int) (IAlarmHelper.SCREEN_HEIGHT * 0.66);

		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, btnHeight);
		param2.setMargins(0, layoutTwoMarTop, 0, 0);
		layoutBtnCtrl.setLayoutParams(param2);
		// Control the button text of timer view. (start, stop)
		if (timerStart) {
			timer_btnCtrl.setText(getString(R.string.timer_stop));
			timerShowTime.setVisibility(View.VISIBLE);
			timerShowTimePick.setVisibility(View.GONE);
		} else {
			timer_btnCtrl.setText(getString(R.string.timer_start));
			timerShowTime.setVisibility(View.GONE);
			timerShowTimePick.setVisibility(View.VISIBLE);
		}

		// set the default time of the timer
		hours.setCurrentItem(TIMER_START_HOUR);
		mins.setCurrentItem(TIMER_START_MINUTE);

		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {
				if (hours.getCurrentItem() == 0 && mins.getCurrentItem() == 0) {
					hours.setCurrentItem(0);
					mins.setCurrentItem(1);
				}
			}
		};

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);

		timer_btnSelectSound.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// select the timer sound or sleep
				Intent i = new Intent(IAlarmActivity.this, SelectSoundActivity.class);
				startActivityForResult(i, REQUEST_SELECT_TIMER_SOUND);
				overridePendingTransition(R.anim.activity_push_up, R.anim.activity_alpha_action);
			}
		});
		timer_btnCtrl.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// timer start and stop
				if (timerStart) {
					timerStart = false;
					timer_btnCtrl.setText(getString(R.string.timer_start));
					timerShowTime.setVisibility(View.GONE);
					timerShowTimePick.setVisibility(View.VISIBLE);
					timer_btnCtrl.setBackgroundResource(R.drawable.btn_bg_green);
					if (mTimer != null) {
						mTimer.cancel();
					}
				} else {
					// set the timer number view,
					// and get the clock to avoid the phone sleeping
					int allSeconds = hours.getCurrentItem() * 3600 + mins.getCurrentItem() * 60;
					timerStart = true;
					timer_btnCtrl.setText(getString(R.string.timer_stop));
					timerShowTime.setVisibility(View.VISIBLE);
					timerShowTimePick.setVisibility(View.GONE);
					timer_btnCtrl.setBackgroundResource(R.drawable.btn_bg_red);
					mTimer = new TimerCount(allSeconds * 1000, 1000);
					mTimer.start();
					getLock(pm, wakeLockTimer);
				}
			}
		});
	}

	/**
	 * the wheel of timer, play the tip sound
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

	/**
	 * get the name of selected timer sound, if null, set default
	 */
	private void getSelectedTimerSound() {
		if (IAlarmHelper.FILENAME_SAVE_TIMERSOUND_PATH.exists()) {
			getSelectedSoundPath = IAlarmHelper.androidFileload(IAlarmActivity.this, IAlarmHelper.FILENAME_SAVE_TIMERSOUND);
		} else {
			if (IAlarmHelper.getSystemRingList(getBaseContext()).size() != 0) {
				getSelectedSoundPath = IAlarmHelper.getSystemRingList(getBaseContext()).get(0);
				IAlarmHelper.androidFileSave(getBaseContext(), IAlarmHelper.FILENAME_SAVE_TIMERSOUND, getSelectedSoundPath);
			} else {
				Toast.makeText(getBaseContext(), R.string.timer_null_ringtone, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * do the timer
	 */
	class TimerCount extends CountDownTimer {
		public TimerCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// timer is ended
			// Log.e("", "TIMER OVER!!!");
			myClockHandler.sendEmptyMessage(MSG_RESET_TIMER_VIEW);
		}

		@Override
		public void onTick(long millisUntilFinished) {

			// timer is running, with update view opera
			long leftAllSeconds = millisUntilFinished / 1000;
			long leftHours = leftAllSeconds / 3600;
			long leftMinutes = (leftAllSeconds % 3600) / 60;
			long leftSeconds = (leftAllSeconds % 3600) % 60;

			setTextView(textTimerHour1, leftHours / 10);
			setTextView(textTimerHour2, leftHours % 10);

			setTextView(textTimerMinute1, leftMinutes / 10);
			setTextView(textTimerMinute2, leftMinutes % 10);
			setTextView(textTimerSecond1, leftSeconds / 10);
			setTextView(textTimerSecond2, leftSeconds % 10);
		}
	}

	/**
	 * the opera of timer ended
	 */
	private void timerOn() {
		releaseLock(wakeLockTimer);
		timer_btnCtrl.setText(getString(R.string.timer_start));
		timerShowTime.setVisibility(View.GONE);
		timerShowTimePick.setVisibility(View.VISIBLE);
		finish();// If the main activity is running back, should do restart
		Intent intent = new Intent(IAlarmActivity.this, TimerOnActivity.class);
		Bundle b = new Bundle();
		b.putString("soundpath", getSelectedSoundPath);
		intent.putExtras(b);
		startActivity(intent);
	}

	/**
	 * set the text of text view
	 * 
	 * @param imgview
	 * @param num
	 */
	public void setTextView(TextView textview, long num) {
		textview.setText((int) num + "");
	}

	/**
	 * release the lock
	 * 
	 * @param wl
	 */
	private void releaseLock(WakeLock wl) {
		if (wl != null && wl.isHeld()) {
			wl.release();
			wl = null;
		}
	}

	/**
	 * get the lock
	 * 
	 * @param pm
	 * @param wk
	 */
	private void getLock(PowerManager pm, WakeLock wk) {
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wk = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
		wk.acquire();
	}

	/**
	 * handler，get the message and do something
	 * 
	 * @author Zhuang
	 * 
	 */
	private class clockHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STOPWATCH_TIMER: {
				updateStopwatchView();
				break;
			}
			case MSG_STOPWATCH_NEWTIMER: {
				updateStopwatchListView();
				stopwatchNewCounts = 0;
				break;
			}
			case MSG_STOPWATCH_CLEAR: {
				// reset the stop watch and update the view
				stopwatchAllCounts = 0;
				stopwatchNewCounts = 0;
				countStopwatchCount = new ArrayList<String>();
				countStopwatchTime = new ArrayList<String>();
				updateStopwatchView();
				updateStopwatchListView();
				break;
			}
			case MSG_UPDATE_ALARMLIST: {
				// update the list view of alarm
				// Log.e("" , "updateAlarm ?????");
				if (updateAlarm) {
					Log.e("", "Update Alarm List !!!!!");
					updateAlarm = false;
					updateAlarmList(alarmEditting);
				}
				break;
			}
			case MSG_RESET_TIMER_VIEW: {
				// reset timer view
				setTextView(textTimerHour1, 0);
				setTextView(textTimerHour2, 0);
				setTextView(textTimerMinute1, 0);
				setTextView(textTimerMinute2, 0);
				setTextView(textTimerSecond1, 0);
				setTextView(textTimerSecond2, 0);
				timerShowTime.setVisibility(View.GONE);
				timerShowTimePick.setVisibility(View.VISIBLE);
				timerOn();
			}
			case MSG_UPDATE_DB: {
				// update the database when the alarm changed or added
				if (updateDb) {
					updateDb = false;
					getAllAlarms();
					stopService(new Intent(IAlarmActivity.this, IAlarmService.class));
					startService(new Intent(IAlarmActivity.this, IAlarmService.class));
				}
				break;
			}
			case MSG_UPDATE_WORLDTIME_LIST: {
				// update the world time view when the city is added or deleted
				if (!worldtimeEditting && !updateWorldTimeNot) {
					updateWorldtimeList(false);
					Log.e("", "update worldtimelist");
				}
				break;
			}
			default:
				break;
			}
		}
	}

	/**
	 * get the result data
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_SELECT_TIMER_SOUND) {
			// from the timer sound select view
			if (resultCode == RESULT_OK) {
				getSelectedSoundPath = data.getExtras().getString("DataKey");
				IAlarmHelper.androidFileSave(getBaseContext(), IAlarmHelper.FILENAME_SAVE_TIMERSOUND, getSelectedSoundPath);
			}
			timer_btnSelectSound.setText(getString(R.string.selectsound_btn) + "   " + IAlarmHelper.pathToName(getSelectedSoundPath));
		} else if (requestCode == REQUEST_SELECT_WORLDTIME_CITY) {
			// from the the city added view of world time
			if (resultCode == RESULT_OK) {
				updateWorldtimeList(false);
			}
		} else if (requestCode == REQUEST_ADD_ALARM) {
			// from alarm add view
			if (resultCode == RESULT_OK) {
				alarmEditting = false;
				updateDb = true;
				updateAlarm = true;
			} else {
				alarmEditting = false;
				updateAlarm = true;
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.e("", "Main activity destroied!");
		releaseLock(wakeLockStopwatch);
		releaseLock(wakeLockTimer);
		updateThreadOff = true;

		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (stopwatchStart && timerStart) {
				Toast.makeText(getBaseContext(), "stop watch and the timer are running!", Toast.LENGTH_SHORT).show();
			} else if (stopwatchStart && !timerStart) {
				Toast.makeText(getBaseContext(), "stop watch is running!", Toast.LENGTH_SHORT).show();
			} else if (!stopwatchStart && timerStart) {
				Toast.makeText(getBaseContext(), "timer is running !", Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);

	}

}