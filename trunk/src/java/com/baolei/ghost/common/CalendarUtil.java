package com.baolei.ghost.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baolei.ghost.dal.dataobject.StockDO;

@Service("calendarUtil")
public class CalendarUtil {
	
	
	/**
	 * 判断stockDO 是不是这个月的第一个交易日
	 * @param stockList
	 * @param stockDO
	 * @return
	 */
	public boolean isFirstDayOfMonth(List<StockDO> stockList,StockDO stockDO){
		int index = stockList.indexOf(stockDO);
		if(index>0){
			StockDO preStockDO = stockList.get(index-1);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(stockDO.getTime());
			int month = calendar.get(Calendar.MONTH);
			Calendar preCalendar = Calendar.getInstance();
			preCalendar.setTime(preStockDO.getTime());
			int premonth = preCalendar.get(Calendar.MONTH);
			if(month > premonth){
				return true;
			}
		}
		return false;
		
	}

	/**
	 * 得到这一周所有的日期,以星期日为第一天
	 * @param time
	 * @return
	 */
	public static List<Date> getWeekDays(Date time) {
		List<Date> weekDays = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		//cal.add(Calendar.DATE, 7);   
		weekDays.add(cal.getTime());
		for (int i = 1; i <= 6; i++) {
			cal.add(Calendar.DATE, 1);
			weekDays.add(cal.getTime());
		}
		return weekDays;
	}

	/**
	 * 得到这一周所有的日期,以星期日为第一天
	 * @param time
	 * @return
	 */
	public static List<Date> getMonthDays(Date time) {
		List<Date> monthDays = new ArrayList<Date>();
		Calendar calnow = Calendar.getInstance();
		Calendar calfirst = Calendar.getInstance();
		Calendar callast = Calendar.getInstance();
		calnow.setTime(time);
		calfirst.setTime(time);
		callast.setTime(time);
		resetHMS(calnow);
		resetHMS(calfirst);
		resetHMS(callast);
		//now   是今天在本月的第几天  
		int now = calnow.get(Calendar.DAY_OF_MONTH);
		//max是本月一共有多少天  
		int max = calnow.getActualMaximum(Calendar.DAY_OF_MONTH);
		calfirst.add(Calendar.DATE, 1 - now);
		callast.add(Calendar.DATE, max - now);
		for (; !calfirst.after(callast); calfirst.add(Calendar.DATE, 1)) {
			monthDays.add(calfirst.getTime());
		}
		return monthDays;
	}

	public static Date getFirstDayOfLastMonth(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DATE, 1);
		resetHMS(calendar);
		return calendar.getTime();
	}
	
	public static Date getLastDayOfLastMonth(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, -1);
		resetHMS(calendar);
		return calendar.getTime();
	}

	public static void resetHMS(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public static void main(String[] args) {
		Date now = new Date();
		Date laststart = getLastDayOfLastMonth(now);
		System.out.println(laststart);
//		List weekDays = getMonthDays(now);
//		for (int i = 0; i < weekDays.size(); i++) {
//			System.out.println(weekDays.get(i));
//		}
	}

}
