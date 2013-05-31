package com.easyandroid.ialarm.views.clock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;

public class IClockView extends View implements Runnable {
	private Bitmap bitmap;
	private float scale;
	private float handCenterWidthScale;
	private float handCenterHeightScale;
	private int minuteHandSize;
	private int hourHandSize;
	private int secondHandSize;
	private Handler handler = new Handler();
	private int in_radius = 7;// the hour and minute pin tail circle
	private int in_point_radius = 2;// second pin tail circle(red)
	private int hourStrokeWidth = 9;// hour pin tail width
	private int minuteStrokeWidth = 9;// minute pin tail width
	private int secondStrokeWidth = 1;// second pin width
	private int lastRadis = 4;
	private int clockColorResourceID;// color of minute and hour pin
	private int startHour = 0;
	private int startSecond = 0;
	private int startMinute = 0;

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public void setStartSecond(int startSecond) {
		this.startSecond = startSecond;
	}

	public void setStartHour(int startHour) {
		if (startHour < 0) {
			startHour += 24;
		}
		this.startHour = startHour;

	}

	public IClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// read the value form xml
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clockbg_white);
		scale = attrs.getAttributeFloatValue(null, "scale", 1);
		handCenterWidthScale = attrs.getAttributeFloatValue(null, "handCenterWidthScale", bitmap.getWidth() / 2);
		handCenterHeightScale = attrs.getAttributeFloatValue(null, "handCenterHeightScale", bitmap.getHeight() / 2);
		// set the pins' long to match the screen
		minuteHandSize = (int) (IAlarmHelper.SCREEN_WEDTH * 0.1);
		hourHandSize = (int) (IAlarmHelper.SCREEN_WEDTH * 0.06);
		secondHandSize = (int) (IAlarmHelper.SCREEN_WEDTH * 0.1);

		in_radius = (int) (IAlarmHelper.SCREEN_WEDTH * 0.015);
		in_point_radius = (int) (IAlarmHelper.SCREEN_WEDTH * 0.005); 
		hourStrokeWidth = (int) (IAlarmHelper.SCREEN_WEDTH * 0.01875); 
		minuteStrokeWidth = (int) (IAlarmHelper.SCREEN_WEDTH * 0.01875); 
		secondStrokeWidth = (int) (IAlarmHelper.SCREEN_WEDTH * 0.003); 
		lastRadis = (int) (IAlarmHelper.SCREEN_WEDTH * 0.008);
 
		handler.postDelayed(this, 1000);
	}

	public void run() {
		// redraw clock View
		invalidate();
		handler.postDelayed(this, 1000);
		startSecond++;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// set the size of the view 
		setMeasuredDimension((int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);

		if (startSecond == 60) {
			startSecond = 0;
			startMinute++;
		}
		if (startMinute == 60) {
			startMinute = 0;
			startHour++;
		}
		if (startHour == 24) {
			startHour = 0;
		}

		if (startHour <= 18 && 6 <= startHour) {
			// in the day
			clockColorResourceID = Color.BLACK; 
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clockbg_white);
		} else {
			// at night
			clockColorResourceID = Color.WHITE; 
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clockbg_black);
		}

		Rect src = new Rect();
		Rect target = new Rect();
		src.left = 0;
		src.top = 0;
		src.right = bitmap.getWidth();
		src.bottom = bitmap.getHeight();

		target.left = 0;
		target.top = 0;
		target.bottom = (int) (src.bottom * scale);
		target.right = (int) (src.right * scale);

		// draw clock background, a bitmap
		canvas.drawBitmap(bitmap, src, target, paint);
		// get the center point of clock view
		float centerX = bitmap.getWidth() * scale * handCenterWidthScale;
		float centerY = bitmap.getHeight() * scale * handCenterHeightScale;
		// get the Radian of pins
		double secondRadian = Math.toRadians((360 - ((startSecond * 6) - 90)) % 360);
		double minuteRadian = Math.toRadians((360 - ((startMinute * 6) - 90)) % 360);
		double hourRadian = Math.toRadians((360 - ((startHour * 30) - 90)) % 360 - (30 * startMinute / 60));

		//draw the hour pin, (triangle)
		paint.setColor(clockColorResourceID);
		Path path2 = new Path();
		path2.moveTo((float) (centerX + hourStrokeWidth * Math.sin(90 - hourRadian)), (float) (centerY - hourStrokeWidth * Math.cos(90 - hourRadian)));
		path2.lineTo((float) (centerX - hourStrokeWidth * Math.sin(90 - hourRadian)), (float) (centerY + hourStrokeWidth * Math.cos(90 - hourRadian)));
		path2.lineTo((int) (centerX + hourHandSize * Math.cos(hourRadian)), (int) (centerY - hourHandSize * Math.sin(hourRadian)));
		path2.close();
		canvas.drawPath(path2, paint);

		//draw the minute pin, (triangle)
		paint.setColor(clockColorResourceID);
		Path path = new Path();
		path.moveTo((float) (centerX + minuteStrokeWidth * Math.sin(90 - minuteRadian)), (float) (centerY - minuteStrokeWidth * Math.cos(90 - minuteRadian)));
		path.lineTo((float) (centerX - minuteStrokeWidth * Math.sin(90 - minuteRadian)), (float) (centerY + minuteStrokeWidth * Math.cos(90 - minuteRadian)));
		path.lineTo((int) (centerX + minuteHandSize * Math.cos(minuteRadian)), (int) (centerY - minuteHandSize * Math.sin(minuteRadian)));
		path.close();
		canvas.drawPath(path, paint);

		// draw a circle at the pins tail
		paint.setColor(clockColorResourceID);
		canvas.drawCircle(centerX, centerY, in_radius, paint);

		// draw another circle at the clock center
		paint.setColor(Color.WHITE);
		canvas.drawCircle(centerX, centerY, lastRadis, paint);

		// draw the second pin tail, a red point
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2);
		canvas.drawCircle(centerX, centerY, in_point_radius, paint);
		// draw the second pin , a line
		paint.setStrokeWidth(secondStrokeWidth);
		paint.setColor(Color.RED);
		canvas.drawLine(centerX, centerY, (int) (centerX + secondHandSize * Math.cos(secondRadian)), (int) (centerY - secondHandSize * Math.sin(secondRadian)), paint);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		handler.removeCallbacks(this);
	}

}
