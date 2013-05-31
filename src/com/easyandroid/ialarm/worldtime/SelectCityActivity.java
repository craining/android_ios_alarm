package com.easyandroid.ialarm.worldtime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyandroid.ialarm.R;

public class SelectCityActivity extends Activity {

	private ListView mAddCityList;
	private EditText mEditText;
	private ArrayList<Integer> mCityIndex = new ArrayList<Integer>();
	private Button btnCancle;
	private BaseAdapter mAdapter;
	private int mCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addcity);
		mEditText = (EditText) findViewById(R.id.search_src_text);
		mEditText.addTextChangedListener(mTextWatcher);
		initSearchData();
		mAddCityList = (ListView) findViewById(R.id.addcityListView);

		mAdapter = new AddCityAdapter(this);
		mAddCityList.setAdapter(mAdapter);

		btnCancle = (Button) findViewById(R.id.search_cancel_button);

		btnCancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);

			}
		});

		mAddCityList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				boolean alreadyExisted = false;

				// Log.e("clicked ", "" + mCityIndex.get(arg2));

				for (int i = 1; i < WorldTime.citiesIds.length; i++) {
					if (WorldTime.citiesIds[i] == mCityIndex.get(arg2)) {
						alreadyExisted = true;
					}
				}

				if (!alreadyExisted) {
					WorldTime.addNewCity(mCityIndex.get(arg2));
					setResult(RESULT_OK);
				} else {
					Toast.makeText(getBaseContext(), R.string.worldtime_cityexisted, Toast.LENGTH_SHORT).show();
				}
				finish();
				overridePendingTransition(R.anim.activity_alpha_action, R.anim.activity_push_down);
			}

		});

	}

	private class AddCityAdapter extends BaseAdapter {
		// private Context mContext;
		private LayoutInflater mInflater;

		public AddCityAdapter(Context context) {
			// mContext = context;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mCount;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout;
			TextView cityName;
			TextView cityGmt;

			if (convertView == null) {
				layout = (LinearLayout) mInflater.inflate(R.layout.view_listrow_addcity, parent, false);
			} else {
				layout = (LinearLayout) convertView;
			}
			cityName = (TextView) layout.findViewById(R.id.addCityName);
			cityGmt = (TextView) layout.findViewById(R.id.addCityGmt);

			final int index = mCityIndex.get(position);
			if (position < mCount) {
				boolean flag = false;
				for (int i = 0; i < WorldTime.StoredCityIndex.size(); i++) {
					if (index == WorldTime.StoredCityIndex.get(i)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					cityName.setText(getString(WorldTime.CityName[index]) + "+");
					cityGmt.setText(WorldTime.getCityGmt(WorldTime.gmtData[index]));
					cityName.setTextColor(Color.BLACK);
					cityGmt.setTextColor(Color.BLACK);
				} else {
					cityName.setText(getString(WorldTime.CityName[index]));
					cityName.setTextColor(Color.BLACK);
					cityGmt.setText(WorldTime.getCityGmt(WorldTime.gmtData[index]));
					cityGmt.setTextColor(Color.BLACK);
				}
				cityName.setTag(null);
				cityGmt.setTag(null);
			}
			return layout;
		}

	}

	private void updateSearchData(String input) {
		int count = WorldTime.getCount();
		mCityIndex.clear();
		mCount = 0;
		for (int i = 0; i < count; i++) {
			if (getString(WorldTime.CityName[i]).toLowerCase().startsWith(input.toLowerCase())) {
				mCityIndex.add(i);
				mCount++;

			}
		}
	}

	private void initSearchData() {
		int count = WorldTime.getCount();
		mCount = count;
		for (int i = 0; i < count; i++) {
			mCityIndex.add(i);
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			updateSearchData(s.toString());
			mAdapter.notifyDataSetChanged();
		}
	};

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
