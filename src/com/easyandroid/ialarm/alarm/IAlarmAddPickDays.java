package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.easyandroid.ialarm.R;
import com.easyandroid.ialarm.views.list.CornerMoveListView;

public class IAlarmAddPickDays extends   Activity {

	private CornerMoveListView ilistDays;
	private ArrayList<String> arraySelectedDays = new ArrayList<String>();
	private Button btnPickReturn;

	private static final String TAG_NAME = "dayname";
	private static final String TAG_SELECTED = "selected";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmadd_pickdays);

		ilistDays = (CornerMoveListView) findViewById(R.id.list_alarmadd_pickdays);
		btnPickReturn = (Button) findViewById(R.id.btn_pickdaysreturn);

		Bundle bundle = getIntent().getExtras();
		String selectedDays = bundle.getString("passData");
		//put the repeat days in a array
		for(String a : selectedDays.split(",")) {
			arraySelectedDays.add(a);
		}
		
		updateSoundListView();
		btnPickReturn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// return the selected repeat days in a string
				String selectedDays = "";
				for(int o=0; o<arraySelectedDays.size(); o++) {
					selectedDays = selectedDays  + arraySelectedDays.get(o) + ",";
				}
				Bundle bundle = new Bundle();
				bundle.putString("DataKey", selectedDays);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
				overridePendingTransition(R.anim.activity_push_right_in, R.anim.activity_push_right_out);
			}
		});
		ilistDays.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Log.e("", "alarm tag pick item on clicked!!");
				//if repeat day is selected, then delete, else add
				if(arg2 == 0) {
					arraySelectedDays = new ArrayList<String>();
					arraySelectedDays.add(getString(IAlarm.ALARM_REPEAT_DAYS[arg2]));
				} else {
					boolean exist = false;
					int existIndex = 0;
					boolean existO = false;
					int oIndex = 0;
					
					for(int j=0; j<arraySelectedDays.size(); j++) {
						if(arraySelectedDays.get(j).equals(getString(IAlarm.ALARM_REPEAT_DAYS[0]))) {
							existO = true;
							oIndex = j;
						}
						if(arraySelectedDays.get(j).equals(getString(IAlarm.ALARM_REPEAT_DAYS[arg2]))) {
							exist = true;
							existIndex = j;
						}
					}
					if(existO) {
						arraySelectedDays.remove(oIndex);
					}
					if(exist) {
						arraySelectedDays.remove(existIndex);
					} else {
						arraySelectedDays.add(getString(IAlarm.ALARM_REPEAT_DAYS[arg2]));
					}
				}
				
				if(arraySelectedDays.size() == 0) {
					arraySelectedDays.add(getString(IAlarm.ALARM_REPEAT_DAYS[0]));
				}
				
				updateSoundListView();
			}
		});
	}

	/**
	 * ¸üÐÂÏÔÊ¾listView
	 */
	private void updateSoundListView() {
		ArrayList<HashMap<String, Object>> alarmDaysListItem = new ArrayList<HashMap<String, Object>>();
		for (int i :  IAlarm.ALARM_REPEAT_DAYS) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(TAG_NAME, getString(i));
			boolean selected = false;
			for(int m=0; m<arraySelectedDays.size(); m++) {
				if(arraySelectedDays.get(m).equals(getString(i))) {
					selected = true;
				}
			}
			if( selected ) {
				map.put(TAG_SELECTED, "1");
			} else {
				map.put(TAG_SELECTED, "0");
			}
			alarmDaysListItem.add(map);
		}
		
		IAlarmAddPickListAdapter listItemAdapter = new IAlarmAddPickListAdapter(IAlarmAddPickDays.this, alarmDaysListItem, R.layout.view_listrow_alarmaddpick, new String[] { TAG_NAME, TAG_SELECTED },
				new int[] { R.id.text_alarmpicktitle, R.id.img_alarmpickselected });
		ilistDays.setAdapter(listItemAdapter);
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
