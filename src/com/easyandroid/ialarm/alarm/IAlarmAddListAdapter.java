package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroid.ialarm.R;

public class IAlarmAddListAdapter extends BaseAdapter {

	private boolean sleepOn;
	private boolean sleepChanged;

	private class buttonViewHolder {
		TextView textAlarmSetItem;
		TextView textAlarmInfo;
		ImageView imgAlarmInfo;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	private buttonViewHolder holder;

	public IAlarmAddListAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		valueViewID = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, valueViewID, 0, to.length);
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
			convertView = mInflater.inflate(R.layout.view_listrow_addalarm, null);
			holder = new buttonViewHolder();
			holder.textAlarmSetItem = (TextView) convertView.findViewById(valueViewID[0]);
			holder.textAlarmInfo = (TextView) convertView.findViewById(valueViewID[1]);
			holder.imgAlarmInfo = (ImageView) convertView.findViewById(valueViewID[2]);

			convertView.setTag(holder);
		}

		HashMap<String, Object> appInfo = mAppList.get(position);

		if (appInfo != null) {

			String strItem = (String) appInfo.get(keyString[0]);
			String strTextInfo = (String) appInfo.get(keyString[1]);
			String strImgInfo = (String) appInfo.get(keyString[2]);

			holder.textAlarmSetItem.setText(strItem);
			if (strTextInfo.equals("-1")) {
				holder.textAlarmInfo.setVisibility(View.GONE);
			} else {
				holder.textAlarmInfo.setVisibility(View.VISIBLE);
				holder.textAlarmInfo.setText(strTextInfo);
			}
			if (position == 2) {
				if (sleepChanged) {
					if(sleepOn) {
						holder.imgAlarmInfo.setImageResource(R.drawable.auto_on);
					} else {
						holder.imgAlarmInfo.setImageResource(R.drawable.auto_off);
					}
				} else {
					if (strImgInfo.equals("1")) {
						sleepOn = true;
						holder.imgAlarmInfo.setImageResource(R.drawable.auto_on);
					} else {
						sleepOn = false;
						holder.imgAlarmInfo.setImageResource(R.drawable.auto_off);
					}
				}
			}else {
				holder.imgAlarmInfo.setImageResource(R.drawable.listarrow);
			}
		} 

		holder.imgAlarmInfo.setOnClickListener(new lvButtonListener(position));
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
			if (vid == holder.imgAlarmInfo.getId()) {
				sleepChanged = true;
				if (!sleepOn) {
					sleepOn = true;
					holder.imgAlarmInfo.setImageResource(R.drawable.auto_on);
					IAlarmAddActivity.alarmSleep = "1";
				} else {
					sleepOn = false;
					holder.imgAlarmInfo.setImageResource(R.drawable.auto_off);
					IAlarmAddActivity.alarmSleep = "0";
				}

				IAlarmAddListAdapter.this.notifyDataSetChanged();
			}
		}
	}
}
