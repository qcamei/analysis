package net.mofancy.analysis.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	/**
	 * 取得一个日期当天的开始时间
	 * @param date
	 * @return
	 */
	public static Date getStartTimeByDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date time = calendar.getTime();
		return time;
	}
	/**
	 * 取得一个日期当天的结束时间
	 * @param date
	 * @return
	 */
	public static Date getEndTimeByDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date time = calendar.getTime();
		return time;
	}
	/**
	 * 取得当前时间指定日前/后的时间
	 * @param num 正数据向后，负数为向前
	 * @param date
	 * @return
	 */
	public static Date getDateByOffset(Date date, int num){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, num);
		return c.getTime();
	}
	
	//根据日期获取当前月的第一天
	public static Date getCurrentMonthFirstDateByGivenDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return getStartTimeByDate(calendar.getTime());
	}
	
	//根据给定的日期获取当前月最后一天
	public static Date getCurrentMonthLastDateByGivenDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getEndTimeByDate(calendar.getTime());
	}
	/**
	 * 根据给定的日期获取当前星期第一天
	 * @param date
	 * @return
	 */
	public static Date getCurrentWeekFirstDateByGivenDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		
		return getStartTimeByDate(calendar.getTime());
	}
	/**
	 * 根据给定的日期获取当前星期最后一天
	 * @param date
	 * @return
	 */
	public static Date getCurrentWeekLastDateByGivenDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
		
		return getEndTimeByDate(calendar.getTime());
	}
	/**
	 * 传入两个日期，计算时间差，以天为单位
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getDayBetween(Date d1, Date d2){
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		return (int)(c2.getTime().getTime() - c1.getTime().getTime())/24/60/60/1000;
	}
}
