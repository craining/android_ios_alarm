package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroid.ialarm.R;

public class IAlarmListAdapter extends BaseAdapter {

	private boolean showDeleteBtn = false;
	private int onEditClickedPosition = -1;

	private Animation animAlphaShow;
	private Animation animAlphaHide;
	private Animation animTranslateRightin;
	private Animation animTranslateLeftin;

	private class buttonViewHolder {
		ImageView imgEdit;
		TextView textTime;
		TextView textTag;
		ImageView imgOnOff;
		Button btnDelete;
		ImageView imgArrow;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	private buttonViewHolder holder;

	public IAlarmListAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		valueViewID = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, valueViewID, 0, to.length);
		animAlphaShow = AnimationUtils.loadAnimation(c, R.anim.listview_alpha_show);
		animAlphaHide = AnimationUtils.loadAnimation(c, R.anim.listview_alpha_hide);
		animTranslateRightin = AnimationUtils.loadAnimation(c, R.anim.listview_translate_rightin);
		animTranslateLeftin = AnimationUtils.loadAnimation(c, R.anim.listview_translate_leftin);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		mAppList.remove(position);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			holder = (buttonViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.view_listrow_alarm, null);
			holder = new buttonViewHolder();

			holder.imgEdit = (ImageView) convertView.findViewById(valueViewID[0]);
			holder.textTime = (TextView) convertView.findViewById(valueViewID[1]);
			holder.textTag = (TextView) convertView.findViewById(valueViewID[2]);
			holder.imgOnOff = (ImageView) convertView.findViewById(valueViewID[3]);
			holder.btnDelete = (Button) convertView.findViewById(valueViewID[4]);
			holder.imgArrow = (ImageView) convertView.findViewById(valueViewID[5]);

			convertView.setTag(holder);
		}

		HashMap<String, Object> appInfo = mAppList.get(position);

		if (appInfo != null) {
			int strImgEdit = (Integer) appInfo.get(keyString[0]);
			String strAlarmTime = (String) appInfo.get(keyString[1]);
			String strAlarmTag = (String) appInfo.get(keyString[2]);
			String strOnOff = (String) appInfo.get(keyString[3]);
			String strbtnDelete = (String) appInfo.get(keyString[4]);
			String strImgArrow = (String) appInfo.get(keyString[5]);

			holder.textTime.setText(strAlarmTime);
			holder.textTag.setText(strAlarmTag);
			if (strImgEdit == 1) {
				holder.imgEdit.setVisibility(View.VISIBLE);
				holder.imgOnOff.setVisibility(View.GONE);
				holder.imgArrow.setVisibility(View.VISIBLE);
				holder.imgEdit.setVisibility(View.VISIBLE);
				holder.imgEdit.setImageResource(R.drawable.edit_normal_ico);
			} else {
				if (strOnOff.equals("1")) {
					holder.imgOnOff.setImageResource(R.drawable.auto_on_alarm);
//					Log.e("", "on");
				} else {
					holder.imgOnOff.setImageResource(R.drawable.auto_off_alarm);
//					Log.e("", "off");
				}
				holder.imgEdit.setVisibility(View.GONE);
				holder.imgOnOff.setVisibility(View.VISIBLE);
				holder.imgArrow.setVisibility(View.GONE);
				holder.imgEdit.setVisibility(View.GONE);
			}
			if (onEditClickedPosition == position) {
				if (showDeleteBtn) {
					holder.imgEdit.setImageResource(R.drawable.edit_pressed_ico);
					holder.imgOnOff.setVisibility(View.GONE);
					holder.imgArrow.setVisibility(View.GONE);
					holder.btnDelete.setVisibility(View.VISIBLE);
				} else {
					holder.imgEdit.setImageResource(R.drawable.edit_normal_ico);
					holder.imgOnOff.setVisibility(View.VISIBLE);
					holder.imgArrow.setVisibility(View.VISIBLE);
					holder.btnDelete.setVisibility(View.GONE);
				}
			} else {
				if (strImgEdit == 1) {
					holder.btnDelete.setVisibility(View.GONE);
					holder.imgArrow.setVisibility(View.VISIBLE);
					holder.imgEdit.setImageResource(R.drawable.edit_normal_ico);
				}
			}

			holder.imgEdit.setOnClickListener(new lvButtonListener(position));
			holder.btnDelete.setOnClickListener(new lvButtonListener(position));
			holder.imgOnOff.setOnClickListener(new lvButtonListener(position));
		}
		return convertView;
	}

	class lvButtonListener implements OnClickListener {
		private int position;

		lvButtonListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			int vid = v.getId();

			if (vid == holder.imgEdit.getId()) {
				// clicked the edit icon of alarm list view
				// Log.e("", "clicked:" + position);
				if (showDeleteBtn) {
					showDeleteBtn = false;
				} else {
					showDeleteBtn = true;
				}
				onEditClickedPosition = position;
				IAlarmListAdapter.this.notifyDataSetChanged();
			}
			if (vid == holder.btnDelete.getId()) {
				// delete the alarm
				removeItem(position);
				showDeleteBtn = false;
				onEditClickedPosition = -1;
				IAlarm.arrayAlarmTime.remove(position);
				IAlarm.arrayAlarmTag.remove(position);
				IAlarm.arrayAlarmOnoff.remove(position);
				IAlarm.arrayAlarmSound.remove(position);
				IAlarm.arrayAlarmDays.remove(position);
				IAlarm.arrayAlarmSleep.remove(position);

				IAlarm.updateAlarmDb(mContext);
			}

			if (vid == holder.imgOnOff.getId()) {
				// alarm on, off
				if (IAlarm.arrayAlarmOnoff.get(position).equals("1")) {
					IAlarm.arrayAlarmOnoff.set(position, "0");
				} else {
					IAlarm.arrayAlarmOnoff.set(position, "1");
				}
				IAlarm.updateAlarmDb(mContext);
			}

		}
	}
}






// holder.imgEdit.startAnimation(animTranslateLeftin);
// // holder.imgOnOff.startAnimation(animAlphaHide);//
// 如何等动画播放完毕再隐藏控件？？？
// animTranslateLeftin.setAnimationListener(new
// AnimationListener() {
// @Override
// public void onAnimationEnd(Animation animation) {
// // TODO Auto-generated method stub
// Log.e("", "animation1 end");
// holder.imgEdit.setVisibility(View.VISIBLE);
// holder.imgOnOff.setVisibility(View.GONE);
// holder.imgArrow.setVisibility(View.VISIBLE);
// holder.imgEdit.setVisibility(View.VISIBLE);
// holder.imgEdit.setImageResource(R.drawable.edit_normal_ico);
// }
//
// @Override
// public void onAnimationRepeat(Animation animation) {
// }
//
// @Override
// public void onAnimationStart(Animation animation) {
// Log.e("", "animation start");
// }
//
// });