package com.easyandroid.ialarm.worldtime;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroid.ialarm.R;
import com.easyandroid.ialarm.views.clock.IClockView;

public class IWorldtimeListAdapter extends BaseAdapter {

	boolean showDeleteBtn = false;
	int onClickedPosition = -1;
	boolean notResetClockView;

	private class buttonViewHolder {
		ImageView editIcon;
		TextView cityName;
		IClockView iclickView;
		TextView timeView;
		TextView dateView;
		ImageView img_deleteCity;
		Button deleteCity;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;
	private buttonViewHolder holder;

	public IWorldtimeListAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
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
			convertView = mInflater.inflate(R.layout.view_listrow_worldtime, null);
			holder = new buttonViewHolder();
			holder.editIcon = (ImageView) convertView.findViewById(valueViewID[0]);
			holder.cityName = (TextView) convertView.findViewById(valueViewID[1]);
			holder.iclickView = (IClockView) convertView.findViewById(valueViewID[2]);
			holder.timeView = (TextView) convertView.findViewById(valueViewID[3]);
			holder.dateView = (TextView) convertView.findViewById(valueViewID[4]);
			holder.img_deleteCity = (ImageView) convertView.findViewById(valueViewID[5]);
			holder.deleteCity = (Button) convertView.findViewById(valueViewID[6]);

			convertView.setTag(holder);
		}

		HashMap<String, Object> appInfo = mAppList.get(position);

		if (appInfo != null) {
			int showEditIcon = (Integer) appInfo.get(keyString[0]);
			String cityName = (String) appInfo.get(keyString[1]);
			int[] clockViewTime = (int[]) appInfo.get(keyString[2]);
			String timeText = (String) appInfo.get(keyString[3]);
			String dateText = (String) appInfo.get(keyString[4]);
			// int showDeleteButton = (Integer) appInfo.get(keyString[5]);

			// 是否显示编辑图片
			if (showEditIcon == 1) {
				holder.editIcon.setVisibility(View.VISIBLE);
				holder.img_deleteCity.setVisibility(View.VISIBLE);
				holder.timeView.setVisibility(View.GONE);
				holder.dateView.setVisibility(View.GONE);
				holder.editIcon.setImageResource(R.drawable.edit_normal_ico);
			} else {
				holder.timeView.setVisibility(View.VISIBLE);
				holder.dateView.setVisibility(View.VISIBLE);
				holder.img_deleteCity.setVisibility(View.GONE);
				holder.editIcon.setVisibility(View.GONE);
			}
			// 设置显示城市名称
			holder.cityName.setText(cityName);

			// 设置显示时刻和日期
			holder.timeView.setText(timeText);
			holder.dateView.setText(dateText);

//			if(!notResetClockView) {
				// 设置钟表时间的现实
//				Log.e("", "clock view updated ");
				holder.iclickView.setStartHour(clockViewTime[0]);
				holder.iclickView.setStartMinute(clockViewTime[1]);
				holder.iclickView.setStartSecond(clockViewTime[2]);
//			}
			
			
			// Log.e("", "getView:" + position);
			// Log.e("getView", "holder.showDeleteBtn : " + showDeleteBtn);

			if (onClickedPosition == IWorldtimeListAdapter.this.getItemId(position)) {
				// Log.e("", "equal:" + position);
				if (showDeleteBtn) {
					// Log.e("", "show delete btn!");
					holder.deleteCity.setVisibility(View.VISIBLE);
					holder.img_deleteCity.setVisibility(View.GONE);
					holder.editIcon.setImageResource(R.drawable.edit_pressed_ico);
				} else {
					// Log.e("", "hide delete btn!");
					holder.deleteCity.setVisibility(View.GONE);
					holder.img_deleteCity.setVisibility(View.VISIBLE);
					holder.editIcon.setImageResource(R.drawable.edit_normal_ico);
				}
			} else {
				if (showEditIcon == 1) {
					holder.deleteCity.setVisibility(View.GONE);
					holder.img_deleteCity.setVisibility(View.VISIBLE);
					holder.editIcon.setImageResource(R.drawable.edit_normal_ico);
				}
			}
			holder.editIcon.setOnClickListener(new lvButtonListener(position));
			holder.deleteCity.setOnClickListener(new lvButtonListener(position));
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

			if (vid == holder.editIcon.getId()) {
				// 点击编辑图标
				// Log.e("", "clicked:" + position);
				if (showDeleteBtn) {
					showDeleteBtn = false;
				} else {
					showDeleteBtn = true;
				}
				onClickedPosition = position;
				notResetClockView = true;
				IWorldtimeListAdapter.this.notifyDataSetChanged();
			}
			if (vid == holder.deleteCity.getId()) {
				// 删除存储在本地的世界城市中的条目，
				if (WorldTime.citiesIds != null && (position + 1) < WorldTime.citiesIds.length) {
					WorldTime.deleteCity(WorldTime.citiesIds[position + 1]);
					WorldTime.citiesIds = WorldTime.getCities();
				}

				removeItem(position);
				showDeleteBtn = false;
				onClickedPosition = -1;
				notResetClockView = true;
				IWorldtimeListAdapter.this.notifyDataSetChanged();

			}
		}
	}
}
