package com.easyandroid.ialarm.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.easyandroid.ialarm.R;

public class IAlarmAddPickTag extends Activity {

	private String selectedTag = "";
	private Button btnPickReturn;
	private EditText editTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmadd_picktag);

		btnPickReturn = (Button) findViewById(R.id.btn_picktagreturn);
		editTag = (EditText) findViewById(R.id.edit_alarmtag);

		Bundle bundle = getIntent().getExtras();
		selectedTag = bundle.getString("passData");
		editTag.setText(selectedTag);
		editTag.setSelection(selectedTag.length());
		editTag.setFocusable(true);

		//open the keyboard , but failed
        InputMethodManager imm = (InputMethodManager) editTag.getContext().getSystemService(INPUT_METHOD_SERVICE); 
        imm.showSoftInput(editTag, InputMethodManager.SHOW_FORCED);


		btnPickReturn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// return the alarm tag
				String getTag = editTag.getText().toString();
				if(TextUtils.isEmpty(getTag)) {
					selectedTag = getString(R.string.alarm_title);
				} else {
					selectedTag = getTag;
				}
				Bundle bundle = new Bundle();
				bundle.putString("DataKey", selectedTag);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
				overridePendingTransition(R.anim.activity_push_right_in, R.anim.activity_push_right_out);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_CANCELED);
			finish();
			overridePendingTransition(R.anim.activity_push_right_in, R.anim.activity_push_right_out);
			return false;
		}
		return false;
	}

}
