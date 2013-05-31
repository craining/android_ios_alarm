package com.easyandroid.ialarm.worldtime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.easyandroid.ialarm.IAlarmHelper;
import com.easyandroid.ialarm.R;

public class WorldTime {

	static final String TAG = "WorldTime";
	public static int WorldTimeMode = 0;
	final static String DEFALUT_DATE_FORMAT = "yyyy-MM-dd";

	public final static int WORLDTIME_VIEW_MODE = 0;
	public final static int WORLDTIME_EDIT_MODE = 1;
	public final static int WORLDTIME_EMPTY_MODE = 2;

	public static int[] citiesIds;

	public static final char gmtData[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
			0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x13, 0x03, 0x03, 0x04, 0x14, 0x05, 0x05,
			0x05, 0x05, 0x15, 0x15, 0x15, 0x45, 0x06, 0x16, 0x07, 0x07, 0x07, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x09, 0x09, 0x19, 0x19, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0c, 0x0c, 0x4c,
			0x0c, 0x0c, 0x0e, 0x2a, 0x29, 0x28, 0x28, 0x28, 0x28, 0x27, 0x27, 0x27, 0x27, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25,
			0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x25, 0x24, 0x24, 0x33, 0x23, 0x23, 0x23, 0x23, 0x23 };

	// city name
	public static final int[] CityName = { R.string.Reykjavik, R.string.Casablanca, R.string.Lisbon, R.string.Dublin, R.string.London, R.string.Lagos, R.string.Algiers, R.string.Madrid,
			R.string.Barcelona, R.string.Paris, R.string.Brussels, R.string.Amsterdam, R.string.Geneva, R.string.Zurich, R.string.Frankfurt, R.string.Oslo, R.string.Copenhagen, R.string.Rome,
			R.string.Berlin, R.string.Prague, R.string.Zagreb, R.string.Vienna, R.string.Stockholm, R.string.Budapest, R.string.Belgrade, R.string.Warsaw, R.string.CapeTown, R.string.Johannesburg,
			R.string.Harare, R.string.Sofia, R.string.Athens, R.string.Tallinn, R.string.Helsinki, R.string.Bucharest, R.string.Minsk, R.string.Istanbul, R.string.Kyiv, R.string.Odesa,
			R.string.Cairo, R.string.Ankara, R.string.Jerusalem, R.string.Beirut, R.string.Amman, R.string.Khartoum, R.string.Nairobi, R.string.AddisAbaba, R.string.Aden, R.string.Riyadh,
			R.string.Antananarivo, R.string.Kuwaitcity, R.string.Tehran, R.string.Moscow, R.string.Baghdad, R.string.AbuDhabi, R.string.Kabul, R.string.Karachi, R.string.Tashkent, R.string.Islamabad,
			R.string.Lahore, R.string.Mumbai, R.string.NewDelhi, R.string.Kolkata, R.string.Kathmandu, R.string.Dhaka, R.string.Yangon, R.string.Bangkok, R.string.Hanoi, R.string.Jakarta,
			R.string.KualaLumpur, R.string.Singapore, R.string.HongKong, R.string.Perth, R.string.Beijing, R.string.Manila, R.string.Shanghai, R.string.Taipei, R.string.Seoul, R.string.Tokyo,
			R.string.Darwin, R.string.Adelaide, R.string.Brisbane, R.string.Melbourne, R.string.Canberra, R.string.Sydney, R.string.Vladivostok, R.string.Suva, R.string.Wellington, R.string.Chatham,
			R.string.Kamchatka, R.string.Anadyr, R.string.Honolulu, R.string.Kiritimati, R.string.Anchorage, R.string.Vancouver, R.string.SanFrancisco, R.string.Seattle, R.string.LosAngeles,
			R.string.Phoenix, R.string.Aklavik, R.string.Edmonton, R.string.Denver, R.string.Guatemala, R.string.SanSalvador, R.string.Tegucigalpa, R.string.Managua, R.string.MexicoCity,
			R.string.Winnipeg, R.string.Houston, R.string.Minneapolis, R.string.StPaul, R.string.NewOrleans, R.string.Chicago, R.string.Montgomery, R.string.Indianapolis, R.string.Lima,
			R.string.Kingston, R.string.Bogota, R.string.SantoDomingo, R.string.LaPaz, R.string.Caracas, R.string.SanJuan, R.string.Havana, R.string.Atlanta, R.string.Detroit, R.string.WashingtonDC,
			R.string.Philadelphia, R.string.Toronto, R.string.Ottawa, R.string.Nassau, R.string.NewYork, R.string.Montreal, R.string.Boston, R.string.Halifax, R.string.Santiago, R.string.Asuncion,
			R.string.StJohns, R.string.BuenosAires, R.string.Montevideo, R.string.Brasilia, R.string.SaoPaulo, R.string.RiodeJaneiro };

	public static ArrayList<Integer> StoredCityId = new ArrayList<Integer>();
	public static ArrayList<Integer> StoredCityIndex = new ArrayList<Integer>();
	public static ArrayList<Integer> StoredCityGmt = new ArrayList<Integer>();

	/**
	 * 根据时区数组获得时区
	 * 
	 * @param zone
	 * @return
	 */
	public static String getCityGmt(char zone) {
		StringBuilder txt = new StringBuilder("GMT");
		if (((char) zone & 0x20) == '\0') {
			txt.append("+");
		} else {
			txt.append("-");
		}

		int h = (int) (zone & 0x0f);

		if (h >= 10) {
			char ch1 = (char) (h / 10 + 48);
			txt.append(ch1);
			char ch2 = (char) (h % 10 + 48);
			txt.append(ch2);

		} else {
			char ch = (char) (h + 48);
			txt.append(ch);
		}

		if ((char) (zone & 0x40) != '\0') {
			txt.append(":45");
		} else if ((char) (zone & 0x10) != '\0') {
			txt.append(":30");
		} else {
			txt.append(":00");
		}

		// Log.e("", txt.toString());
		return txt.toString();

	}

	public static String getCityTime(Context con, String nowTime, char gmt) {

		// String time = "-:-:-:-";// 时:分:秒:日期
		String date = "-";
		TimeZone nowTz = Calendar.getInstance().getTimeZone();// 获得当前时区，

		long timeZone = 0;
		String time1 = getCityGmt(gmt).substring(3, getCityGmt(gmt).length());
		String time2 = time1.substring(1, time1.length());

		// Log.e("gmt", getCityGmt(gmt));

		if (time1.charAt(0) == '+') {
			timeZone = +(Integer.parseInt(time2.split(":")[0]) * 3600 + Integer.parseInt(time2.split(":")[1]) * 60);
		} else {
			timeZone = -(Integer.parseInt(time2.split(":")[0]) * 3600 + Integer.parseInt(time2.split(":")[1]) * 60);
		}

		long nowTimeZone = nowTz.getRawOffset() / 1000;
		long timeDistance = 0;

		if (nowTimeZone < timeZone) {
			timeDistance = nowTimeZone - timeZone;
		} else {
			timeDistance = timeZone - nowTimeZone;
		}

		String[] nowTimes = nowTime.split(":");

		int hour = Integer.parseInt(nowTimes[0]);
		int minute = Integer.parseInt(nowTimes[1]);
		int second = Integer.parseInt(nowTimes[2]);

		int newHour = 0;
		int newMinute;
		long finalTime = hour * 3600 + minute * 60 + second + timeDistance;
//		Log.e("Time distance", "" +timeDistance);

		newMinute = (int) ((finalTime % 3600) / 60);
//		Log.e("new minutes", "" +newMinute);

		if (finalTime / 3600 < 0) {
			newHour = (int) (finalTime / 3600 + 24);
		} else if (finalTime / 3600 < 24) {
			newHour = (int) (finalTime / 3600);
		} else {
			newHour = (int) (finalTime / 3600 - 24);
		}
		if (newMinute < 0) {
			newMinute += 60;
			newHour--;
		} else if (newMinute > 60) {
			newMinute -= 60;
			newHour++;
		}
		if (newHour < 0) {
			newHour += 24;
			date = con.getString(R.string.worldtime_date_yestoday);
		} else if (newHour < 24) {
			date = con.getString(R.string.worldtime_date_today);
		} else {
			newHour -= 24;
			date = con.getString(R.string.worldtime_date_tomorrow);
		}
//		Log.e(finalTime / 3600 + "", newHour + "");

//		Log.e("", time1);
//		Log.e("new time", newHour + ":" + newMinute + ":" + second + ":" + date);

		return newHour + ":" + newMinute + ":" + second + ":" + date;

	}

	public static int getCount() {
		return CityName.length;
	}

	/**
	 * 添加城市， 保存城市的id号，中间用 ”：“隔开
	 * 
	 * @param context
	 * @param cityIndex
	 * @param cityGmt
	 */
	public synchronized static void addNewCity(int cityIndex) {
		String toSaveCityIds = "";
		String preCityIds = "";
		if (IAlarmHelper.FILE_SAVE_WORLDCITY_IDS.exists()) {
			preCityIds = IAlarmHelper.readFile(IAlarmHelper.FILE_SAVE_WORLDCITY_IDS);
		}
		toSaveCityIds = preCityIds + ":" + cityIndex;
		// Log.e("add id", "" + cityIndex);
		// Log.e("add save", toSaveCityIds);
		IAlarmHelper.writeFile(toSaveCityIds, IAlarmHelper.FILE_SAVE_WORLDCITY_IDS);
	}

	/**
	 * 删除城市
	 */
	public synchronized static void deleteCity(int cityIndex) {
		// Log.e("delete id", "" + cityIndex);
		String toSaveCityIds = "";
		String preCityIds = "";

		if (IAlarmHelper.FILE_SAVE_WORLDCITY_IDS.exists()) {
			preCityIds = IAlarmHelper.readFile(IAlarmHelper.FILE_SAVE_WORLDCITY_IDS);
			String[] array_preCityIds = preCityIds.split(":");

			// Log.e("Left File content", preCityIds + "cccc");

			for (int a = 1; a < array_preCityIds.length; a++) {
				if (!array_preCityIds[a].equals(cityIndex + "")) {
					toSaveCityIds = toSaveCityIds + ":" + array_preCityIds[a];
				}
			}

		}

		// Log.e("deleted save", toSaveCityIds);
		IAlarmHelper.writeFile(toSaveCityIds, IAlarmHelper.FILE_SAVE_WORLDCITY_IDS);
	}

	/**
	 * 获得所有已选的城市
	 * 
	 * @param context
	 */
	public synchronized static int[] getCities() {
		String[] str_ids;
		if (IAlarmHelper.FILE_SAVE_WORLDCITY_IDS.exists()) {
			String allCityIds = IAlarmHelper.readFile(IAlarmHelper.FILE_SAVE_WORLDCITY_IDS);
			// Log.e("All Cities IDs", allCityIds + "mmmm");
			str_ids = allCityIds.split(":");
			int[] ids = new int[str_ids.length];
			for (int i = 0; i < str_ids.length; i++) {
				if (!str_ids[i].equals("")) {
					ids[i] = Integer.parseInt(str_ids[i]);
				}

			}
			return ids;
		} else {
			return new int[0];
		}

	}

	public static boolean get24HourMode(final Context context) {
		return android.text.format.DateFormat.is24HourFormat(context);
	}

	public static String getDateMode(final Context context) {
		String tmp = Settings.System.getString(context.getContentResolver(), "date_format");
		if (null == tmp)
			return DEFALUT_DATE_FORMAT;
		else
			return tmp;
	}
}
