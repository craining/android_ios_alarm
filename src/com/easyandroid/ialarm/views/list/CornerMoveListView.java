package com.easyandroid.ialarm.views.list;

import com.easyandroid.ialarm.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

public class CornerMoveListView extends ListView implements Runnable {
	private float mLastDownY = 0f;
	private int mDistance = 0;
	private int mStep = 10;
	private boolean mPositive = false;

	public CornerMoveListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CornerMoveListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CornerMoveListView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			int x = (int) event.getX();
			int y = (int) event.getY();
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			} else {
				if (itemnum == 0) {
					if (itemnum == (getAdapter().getCount() - 1)) {
						// just have one item
						setSelector(R.drawable.app_list_corner_round);
					} else {
						// the first item
						setSelector(R.drawable.app_list_corner_round_top);
					}
				} else if (itemnum == (getAdapter().getCount() - 1))
					// the last item
					setSelector(R.drawable.app_list_corner_round_bottom);
				else {
					// the middle tiem
					setSelector(R.drawable.app_list_corner_shape);
				}
			}

//			if (mLastDownY == 0f && mDistance == 0) {
//				mLastDownY = event.getY();
//				return true;
//			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
//			if (mDistance != 0) {
//				mStep = 1;
//				mPositive = mDistance >= 0;
//				this.post(this);
//				return true;
//			}
//
//			mLastDownY = 0f;
//			mDistance = 0;
			break;

		case MotionEvent.ACTION_MOVE:
//			if (mLastDownY != 0f) {
//				mDistance = (int) (mLastDownY - event.getY());
//				try {
//					if ((mDistance < 0 && getFirstVisiblePosition() == 0 && getChildAt(0).getTop() == 0) || (mDistance > 0 && getLastVisiblePosition() == getCount() - 1)) {
//						mDistance /= 2;
//						scrollTo(0, mDistance);
//						return true;
//					}
//				} catch (Exception e) {
//					Log.e("IListView", e.toString());
//				}
//
//			}
//			mDistance = 0;
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void run() {
		mDistance += mDistance > 0 ? -mStep : mStep;
		scrollTo(0, mDistance);
		if ((mPositive && mDistance <= 0) || (!mPositive && mDistance >= 0)) {
			scrollTo(0, 0);
			mDistance = 0;
			mLastDownY = 0f;
			return;
		}
		mStep += 1;
		this.postDelayed(this, 10);
	}
}
