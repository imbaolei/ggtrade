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
	 * 判断stockDO 是不是这个月的第  firstDay 个交易日
	 * @param stockList
	 * @param stockDO
	 * @param firstDay
	 * @return
	 */
	public static boolean isFirstDayOfMonth(List<StockDO> stockList,StockDO stockDO,int firstDay){
		int index = stockList.indexOf(stockDO);
		if(index>0){
			List<StockDO> monthList = getStockListOfMonth(stockList,stockDO);
			int monthIndex = monthList.indexOf(stockDO);
			if(monthIndex == (firstDay-1)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 得到这个stockDO日期之前的 在 这个月的stockList
	 * @param stockList
	 * @param stockDO
	 * @return
	 */
	public static List<StockDO> getStockListOfMonth(List<StockDO> stockList,StockDO stockDO){
		List<StockDO> tmpStockList = new ArrayList<StockDO>();
		int index = stockList.indexOf(stockDO);
		if(index <= 0){
			return tmpStockList;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(stockDO.getTime());
		int month = calendar.get(Calendar.MONTH);
		//这个月的第一天 距离 stockDO的天数
		int monthFirstIndex = 0;
		for(int i  = index-1 ; i> 0 ;i--){
			StockDO tmpStockDO = stockList.get(i);
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.setTime(tmpStockDO.getTime());
			int tmpMonth = tmpCalendar.get(Calendar.MONTH);
			if(tmpMonth != month){
				break;
			}
			monthFirstIndex++;
		}
		monthFirstIndex = index - monthFirstIndex;
		//截取到 monthFirstIndex 到 index 这天的 list，因为subList是前闭后开 所以index需要+1
		tmpStockList = stockList.subList(monthFirstIndex, index+1);
		return tmpStockList;
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
