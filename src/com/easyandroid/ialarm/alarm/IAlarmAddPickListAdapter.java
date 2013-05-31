package com.easyandroid.ialarm.alarm;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroid.ialarm.R;

public class IAlarmAddPickListAdapter extends BaseAdapter {

	private class buttonViewHolder {
		TextView textAlarmPickItem;
		ImageView imgAlarmPicSelectedTag;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	private buttonViewHolder holder;

	public IAlarmAddPickListAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
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
			convertView = mInflater.inflate(R.layout.view_listrow_alarmaddpick, null);
			holder = new buttonViewHolder();
			holder.textAlarmPickItem = (TextView) convertView.findViewById(valueViewID[0]);
			holder.imgAlarmPicSelectedTag = (ImageView) convertView.findViewById(valueViewID[1]);

			convertView.setTag(holder);
		}

		HashMap<String, Object> appInfo = mAppList.get(position);

		if (appInfo != null) {

			String strItem = (String) appInfo.get(keyString[0]);
			String strSelected = (String) appInfo.get(keyString[1]);

			holder.textAlarmPickItem.setText(strItem);

			if (strSelected.equals("1")) {
				holder.imgAlarmPicSelectedTag.setImageResource(R.drawable.img_selected);
			} else {
				holder.imgAlarmPicSelectedTag.setImageResource(R.drawable.img_unselected);
			}
		}

		return convertView;

	}
}
