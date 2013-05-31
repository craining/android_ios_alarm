package com.easyandroid.ialarm.views.tab;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.easyandroid.ialarm.IAlarmHelper;

public class ITabView extends View {
	private Paint mPaint; 
	private Paint mActiveTextPaint;//selected
	private Paint mInactiveTextPaint;//unselected
	private ArrayList<TabMember> mTabMembers;//tab members
	private int mActiveTab;
	private OnTabClickListener mOnTabClickListener = null;

	private int tabHalfHeight = 50;
	private int tabIcoDis = 22;//distance of the icon form tab top 

	public ITabView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTabMembers = new ArrayList<TabMember>();

		mPaint = new Paint();
		mActiveTextPaint = new Paint();
		mInactiveTextPaint = new Paint();

		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xFFFFFF00);
		mPaint.setAntiAlias(true);

		mActiveTextPaint.setTextAlign(Align.CENTER);

		mActiveTextPaint.setColor(0xFFFFFFFF);
		mActiveTextPaint.setAntiAlias(true);

		mInactiveTextPaint.setTextAlign(Align.CENTER);

		mInactiveTextPaint.setColor(0xFF999999);
		mInactiveTextPaint.setAntiAlias(true);
		mActiveTab = 0;

		if (IAlarmHelper.SCREEN_HEIGHT >= 700) {
			tabHalfHeight = 25;
			tabIcoDis = 10;
			mActiveTextPaint.setTextSize(20);
			mInactiveTextPaint.setTextSize(20);
		} else if (IAlarmHelper.SCREEN_HEIGHT <= 500 && IAlarmHelper.SCREEN_HEIGHT >= 330) {
			tabHalfHeight = 20;
			tabIcoDis = 5;
			mActiveTextPaint.setTextSize(12);
			mInactiveTextPaint.setTextSize(12);
		} else {
			Log.e("", "min size ");
			tabIcoDis = 2;
			tabHalfHeight = 15;
			mActiveTextPaint.setTextSize(8);
			mInactiveTextPaint.setTextSize(8);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Rect r = new Rect();
		this.getDrawingRect(r);

		// the pixel of ever tab member
		int singleTabWidth = r.right / (mTabMembers.size() != 0 ? mTabMembers.size() : 1);

		// draw the background
		canvas.drawColor(0xFF000000);
		mPaint.setColor(0xFF434343);
		canvas.drawLine(r.left, r.top + 1, r.right, r.top + 1, mPaint);

		int color = 46;

		// draw a white(half)
		for (int i = 0; i < tabHalfHeight; i++) {
			mPaint.setARGB(255, color, color, color);
			canvas.drawRect(r.left, r.top + i + 1, r.right, r.top + i + 2, mPaint);
			color--;
		}

		// draw ever tab member
		for (int i = 0; i < mTabMembers.size(); i++) {
			TabMember tabMember = mTabMembers.get(i);

			Bitmap icon = BitmapFactory.decodeResource(getResources(), tabMember.getIconResourceId());
			Bitmap iconColored = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Bitmap.Config.ARGB_8888);
			Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			Canvas iconCanvas = new Canvas();
			iconCanvas.setBitmap(iconColored);

			//selected and unselected icon
			if (mActiveTab == i) {
				p.setShader(new LinearGradient(0, 0, icon.getWidth(), icon.getHeight(), 0xFFFFFFFF, 0xFF54C7E1, Shader.TileMode.CLAMP));
			} else {
				p.setShader(new LinearGradient(0, 0, icon.getWidth(), icon.getHeight(), 0xFFA2A2A2, 0xFF5F5F5F, Shader.TileMode.CLAMP));
			}

			iconCanvas.drawRect(0, 0, icon.getWidth(), icon.getHeight(), p);

			for (int x = 0; x < icon.getWidth(); x++) {
				for (int y = 0; y < icon.getHeight(); y++) {
					if ((icon.getPixel(x, y) & 0xFF000000) == 0) {
						iconColored.setPixel(x, y, 0x00000000);
					}
				}
			}
			// get the position of tab icon
			int tabImgX = singleTabWidth * i + (singleTabWidth / 2 - icon.getWidth() / 2);

			//draw tab icon
			if (mActiveTab == i) {
				mPaint.setARGB(37, 255, 255, 255);
				canvas.drawRoundRect(new RectF(r.left + singleTabWidth * i + 3, r.top + 3, r.left + singleTabWidth * (i + 1) - 3, r.bottom - 2), 5, 5, mPaint);
				canvas.drawBitmap(iconColored, tabImgX, r.top + tabIcoDis, null);// +20
				canvas.drawText(tabMember.getText(), singleTabWidth * i + (singleTabWidth / 2), r.bottom - 2, mActiveTextPaint);
			} else {
				canvas.drawBitmap(iconColored, tabImgX, r.top + tabIcoDis, null);
				canvas.drawText(tabMember.getText(), singleTabWidth * i + (singleTabWidth / 2), r.bottom - 2, mInactiveTextPaint);
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		Rect r = new Rect();
		this.getDrawingRect(r);
		float singleTabWidth = r.right / (mTabMembers.size() != 0 ? mTabMembers.size() : 1);

		int pressedTab = (int) ((motionEvent.getX() / singleTabWidth) - (motionEvent.getX() / singleTabWidth) % 1);

		mActiveTab = pressedTab;

		if (this.mOnTabClickListener != null) {
			this.mOnTabClickListener.onTabClick(mTabMembers.get(pressedTab).getId());
		}

		this.invalidate();

		return super.onTouchEvent(motionEvent);
	}

	public void addTabMember(TabMember tabMember) {
		mTabMembers.add(tabMember);
	}

	public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
		mOnTabClickListener = onTabClickListener;
	}

	public static class TabMember {
		protected int mId;
		protected String mText;
		protected int mIconResourceId;

		public TabMember(int Id, String Text, int iconResourceId) {
			mId = Id;
			mIconResourceId = iconResourceId;
			mText = Text;
		}

		public int getId() {
			return mId;
		}

		public String getText() {
			return mText;
		}

		public int getIconResourceId() {
			return mIconResourceId;
		}

		public void setText(String Text) {
			mText = Text;
		}

		public void setIconResourceId(int iconResourceId) {
			mIconResourceId = iconResourceId;
		}
	}

	public static interface OnTabClickListener {
		public abstract void onTabClick(int tabId);
	}

}
