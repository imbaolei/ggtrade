package com.baolei.ghost.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.app.InitStockFile;
import com.baolei.ghost.dal.dataobject.StockDO;

/**
 * @author B
 * 
 */

@Service("stockUtil")
public class StockUtil {

	public static String dateFormatString = "yyyy/MM/dd";

	public static DecimalFormat decimalFormat = new DecimalFormat("#.000");

	/**
	 * 判断stockDO 是不是这个月的第 firstDay 个交易日
	 * 
	 * @param stockList
	 * @param stockDO
	 * @param firstDay
	 * @return
	 */
	public static boolean isFirstDayOfMonth(List<StockDO> stockList,
			StockDO stockDO, int firstDay) {
		int index = stockList.indexOf(stockDO);
		if (index > 0) {
			List<StockDO> monthList = getStockListOfMonth(stockList, stockDO);
			int monthIndex = monthList.indexOf(stockDO);
			if (monthIndex == (firstDay - 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到这个stockDO日期之前的 在 这个月的stockList
	 * 
	 * @param stockList
	 * @param stockDO
	 * @return
	 */
	public static List<StockDO> getStockListOfMonth(List<StockDO> stockList,
			StockDO stockDO) {
		List<StockDO> tmpStockList = new ArrayList<StockDO>();
		int index = stockList.indexOf(stockDO);
		if (index <= 0) {
			return tmpStockList;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(stockDO.getTime());
		int month = calendar.get(Calendar.MONTH);
		// 这个月的第一天 距离 stockDO的天数
		int monthFirstIndex = 0;
		for (int i = index - 1; i > 0; i--) {
			StockDO tmpStockDO = stockList.get(i);
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.setTime(tmpStockDO.getTime());
			int tmpMonth = tmpCalendar.get(Calendar.MONTH);
			if (tmpMonth != month) {
				break;
			}
			monthFirstIndex++;
		}
		monthFirstIndex = index - monthFirstIndex;
		// 截取到 monthFirstIndex 到 index 这天的 list，因为subList是前闭后开 所以index需要+1
		tmpStockList = stockList.subList(monthFirstIndex, index + 1);
		return tmpStockList;
	}

	/**
	 * 得到这个stockDO日期之前 count天的List 包含stockDO 这天
	 * 
	 * @param stockList
	 * @param stockDO
	 * @return
	 */
	public static List<StockDO> getStockListOfPreCount(List<StockDO> stockList,
			StockDO stockDO, int count) {
		List<StockDO> tmpStockList = new ArrayList<StockDO>();
		int index = stockList.indexOf(stockDO);
		if (index <= 0) {
			return tmpStockList;
		}
		int startIndex = 0;
		if ((index + 1) >= count) {
			startIndex = index + 1 - count;
		}
		tmpStockList = stockList.subList(startIndex, index + 1);
		return tmpStockList;
	}

	public static boolean isLLV(List<StockDO> stockList, StockDO stockDO,
			int count) {
		List<StockDO> tmpStockList = getStockListOfPreCount(stockList, stockDO,
				count);
		// 如果不够count天，则不是count天内最低价
		if (tmpStockList.size() < count) {
			return false;
		}
		float llv = tmpStockList.get(0).getLow();
		for (StockDO tmpStock : tmpStockList) {
			if (tmpStock.getLow() <= llv) {
				llv = tmpStock.getLow();
			}
		}
		if (stockDO.getLow() <= llv) {
			return true;
		}
		return false;
	}

	public static boolean isHHV(List<StockDO> stockList, StockDO stockDO,
			int count) {
		List<StockDO> tmpStockList = getStockListOfPreCount(stockList, stockDO,
				count);
		// 如果不够count天，则不是count天内最低价
		if (tmpStockList.size() < count) {
			return false;
		}
		float hhv = tmpStockList.get(0).getHigh();
		for (StockDO tmpStock : tmpStockList) {
			if (tmpStock.getHigh() >= hhv) {
				hhv = tmpStock.getHigh();
			}
		}
		if (stockDO.getHigh() >= hhv) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 数据源是stockMap 计算time这一天的 周期day的atr值
	 * 
	 * @param stockList
	 * @param stockDO
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public float atr(List<StockDO> stockList, StockDO stockDO, int day) {
		List<Float> list = new ArrayList<Float>();
		// ATR : MA(TR,m)
		for (int i = 0; i < day; i++) {
			list.add(tr(stockList, stockDO));
		}
		Float sum = new Float(0);
		for (Float tmpfloat : list) {
			sum = sum + tmpfloat;
		}
		return Float.parseFloat(decimalFormat.format(sum / list.size()));
	}

	public float tr(List<StockDO> stockList, StockDO stockDO) {
		// TR :
		// MAX(MAX((HIGH-LOW),ABS(REF(CLOSE,1)-HIGH)),ABS(REF(CLOSE,1)-LOW));
		float theHL = Math.abs(stockDO.getHigh() - stockDO.getLow());
		StockDO preStockDO = pre(stockList, stockDO);
		float preCH = Math.abs(preStockDO.getClose() - stockDO.getHigh());
		float preCL = Math.abs(preStockDO.getClose() - stockDO.getLow());
		return Math.max(theHL, Math.max(preCH, preCL));
	}

	/**
	 * 取stockList中time时间的前一天 和pre(Map<String, StockDO> stockMap, Date time) 性能一样
	 * 
	 * @param stockList
	 * @param stockDO
	 * @return
	 * @throws ParseException
	 */
	public StockDO pre(List<StockDO> stockList, StockDO stockDO) {
		int index = stockList.indexOf(stockDO);
		if (index <= 0) {
			return new StockDO();
		}
		StockDO preStockDO = stockList.get(index - 1);
		return preStockDO;
	}

	public static Date preWeekDay(Map<String, StockDO> stockMap, Date time)
			throws ParseException {
		Date pre = null;
		Set<String> dayStringSet = stockMap.keySet();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (String dayString : dayStringSet) {
			// 如果dayString在time之后 则没有前一周
			if (dateFormat.parse(dayString).compareTo(time) >= 0) {
				return null;
			} else {
				Calendar cal_7 = Calendar.getInstance();
				cal_7.setTime(time);
				cal_7.add(Calendar.DATE, -7);
				Date time_7 = cal_7.getTime();
				Date day = dateFormat.parse(dayString);
				// time7天前的时间 <= day 需要 < time
				if (day.compareTo(time_7) >= 0 && time.compareTo(day) > 0) {
					pre = day;
					break;
				}
			}
		}
		return pre;
	}

	public static Date preMonthDay(Map<String, StockDO> stockMap, Date theDay)
			throws ParseException {
		Date pre = null;
		Set<String> dayStringSet = stockMap.keySet();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (String dayString : dayStringSet) {
			// 如果dayString在time之后 则没有前一周
			if (dateFormat.parse(dayString).compareTo(theDay) >= 0) {
				return null;
			} else {
				Date firstDayOfLastMonth = CalendarUtil
						.getFirstDayOfLastMonth(theDay);
				Date lastDayOfLastMonth = CalendarUtil
						.getLastDayOfLastMonth(theDay);
				Date day = dateFormat.parse(dayString);
				// firstDayOfLastMonth <= day 需要 < = lastDayOfLastMonth
				if (day.compareTo(firstDayOfLastMonth) >= 0
						&& lastDayOfLastMonth.compareTo(day) >= 0) {
					pre = day;
					break;
				}
			}
		}
		return pre;
	}

	public static void initBbiMap(Map<String, StockDO> stockMap,
			List<StockDO> stockList) throws ParseException {
		for (int i = 23; i < stockList.size(); i++) {
			StockDO stockDO = stockList.get(i);
			if (stockDO.getBbi() != 0) {
				// 如果该stock今天的bbi已经计算，则不用再计算了，因为计算bbi很费时
				continue;
			} else {
				stockDO.setBbi(BBI(stockMap, stockList, stockDO.getTime()));
			}

		}
	}

	/**
	 * 取stockList中time时间的前一天 和pre(Map<String, StockDO> stockMap, Date time) 性能一样
	 * 
	 * @param stockList
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	private static Date pre(List<StockDO> stockList, Date time)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		Date pre = null;
		StockDO firstDay = stockList.get(0);
		// 如果是stockList的第一天，那么没有前一天
		if (timeString.equals(dateFormat.format(firstDay.getTime()))) {
			return null;
		}
		for (int i = 1; i < stockList.size(); i++) {
			if (timeString
					.equals(dateFormat.format(stockList.get(i).getTime()))) {
				pre = stockList.get(i - 1).getTime();
				break;
			}
		}
		return pre;
	}

	/**
	 * 取stockList中time时间的前count天的时间
	 * 
	 * @param stockList
	 * @param time
	 * @param count
	 * @return
	 */
	public static List<Date> pre(List<StockDO> stockList, Date time, int count) {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		StockDO firstDay = stockList.get(0);
		// 如果是stockList的第一天，那么没有前一天
		if (timeString.equals(dateFormat.format(firstDay.getTime()))) {
			return null;
		}
		for (int i = 1; i < stockList.size(); i++) {
			if (timeString
					.equals(dateFormat.format(stockList.get(i).getTime()))) {
				if (i <= count) {
					// 如果在time时间前,stockList没有count天
					return null;
				} else {
					List<Date> preDateList = new ArrayList<Date>();
					// 比如count是20 这会儿i应该是21
					for (int j = i - count - 1; j < i; j++) {
						preDateList.add(stockList.get(j).getTime());
					}
					return preDateList;
				}
			}
		}
		return null;
	}

	public static Date pre(Map<String, StockDO> stockMap, Date time) {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		Date pre = null;
		Set<String> dayStringSet = stockMap.keySet();
		for (String dayString : dayStringSet) {
			// 如果tmpPre是null 表示是stockMap的第一天,则没有前一天
			if (pre == null) {
				if (timeString.equals(dayString)) {
					return null;
				}
			} else {
				if (timeString.equals(dayString)) {
					break;
				}
			}
			try {
				pre = dateFormat.parse(dayString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return pre;
	}

	/**
	 * 取stockMap中time时间的前count天的时间
	 * 
	 * @param pdStockList
	 * @param time
	 * @param count
	 * @return
	 */
	public static List<Date> pre(Map<String, StockDO> stockMap, Date time,
			int count) {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		Date pre = null;
		Queue<Date> queue = new LinkedList<Date>();
		Set<String> dayStringSet = stockMap.keySet();
		for (String dayString : dayStringSet) {
			// 如果pre是null 表示是stockMap的第一天,则没有前一天
			if (pre == null) {
				if (timeString.equals(dayString)) {
					return null;
				}
			} else {
				if (timeString.equals(dayString)) {
					break;
				}
			}
			try {
				pre = dateFormat.parse(dayString);
			} catch (ParseException e) {
				return null;
			}

			if (queue.size() >= count) {
				queue.remove();
				queue.add(pre);
			} else {
				queue.add(pre);
			}
		}
		if (queue.size() == count) {
			Object[] dateObjects = queue.toArray();
			List<Date> dates = new ArrayList<Date>();
			for (Object date : dateObjects) {
				dates.add((Date) date);
			}
			return dates;
		} else {
			return null;
		}
	}

	/**
	 * 取stockMap前count天的最低价 如果是0 表示没有最低价
	 * 
	 * @param stockMap
	 * @param count
	 * @return
	 */
	public static float preLow(Map<String, StockDO> stockMap, Date time,
			int count) {
		List<Date> dates = pre(stockMap, time, count);
		if (dates == null || dates.size() < count) {
			return 0;
		}
		float low = 0;
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (Date date : dates) {
			StockDO stockDO = stockMap.get(dateFormat.format(date));
			if (low == 0) {
				low = stockDO.getLow();
			} else {
				if (stockDO.getLow() < low) {
					low = stockDO.getLow();
				}
			}
		}
		return low;
	}

	/**
	 * 取stockMap前count天的最高价 如果是0 表示没有最高价
	 * 
	 * @param stockMap
	 * @param count
	 * @return
	 */
	public static float preHide(Map<String, StockDO> stockMap, Date time,
			int count) {
		List<Date> dates = pre(stockMap, time, count);
		if (dates == null || dates.size() < count) {
			return 0;
		}
		float high = 0;
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (Date date : dates) {
			StockDO stockDO = stockMap.get(dateFormat.format(date));
			if (high == 0) {
				high = stockDO.getHigh();
			} else {
				if (stockDO.getLow() > high) {
					high = stockDO.getLow();
				}
			}
		}
		return high;
	}

	private static Date off(List<StockDO> stockList, Date time)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		Date off = null;
		for (int i = 0; i < stockList.size() - 1; i++) {
			if (timeString
					.equals(dateFormat.format(stockList.get(i).getTime()))) {
				off = stockList.get(i + 1).getTime();
				break;
			}
		}
		return off;
	}

	public static void initDayStockMap(String filePath,
			Map<String, StockDO> dayStockMap, List<StockDO> dayStockList)
			throws IOException, ParseException {
		File read = new File(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath), "GBK"));
		String code = read.getName().split(".TXT")[0];
		String temp = null;
		temp = br.readLine();
		int line = 1;
		while (temp != null) {
			if (line == 1 || line == 2) {
				// 如果是第一行和第二行 略过，第一行是说明，第二行是表头
				line++;
				temp = br.readLine();
				continue;
			}
			String[] data = temp.split(" ");
			// [0]日期 [1]open [2]high; [3]low [4]close [5] vol
			StockDO stockDO = new StockDO();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = dateFormat.parse(data[0]);
			stockDO.setTime(date);
			stockDO.setOpen(Float.parseFloat(data[1]));
			stockDO.setHigh(Float.parseFloat(data[2]));
			stockDO.setLow(Float.parseFloat(data[3]));
			stockDO.setClose(Float.parseFloat(data[4]));
			stockDO.setVol(Float.parseFloat(data[5]));
			stockDO.setPeriod(Constant.STOCK_PERIOD_DAY);
			stockDO.setCode(code);
			dayStockMap.put(data[0], stockDO);
			dayStockList.add(stockDO);
			temp = br.readLine();
		}
		br.close();
	}

	public static DataPool initStockPool(String code) {
		String folder = code.substring(0, 2);
		String tofile = InitStockFile.toFilePath + folder.toLowerCase() + "/"
				+ code + ".txt";
		DataPool dataPool = initToStockPool(tofile);
		return dataPool;
	}

	/**
	 * 初始化 to文件夹得stock数据
	 * 
	 * @return
	 */
	public static DataPool initToStockPool(String filePath) {
		DataPool dataPool = new DataPool();
		File read = new File(filePath);
		if (!read.exists()) {
			return dataPool;
		}
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "GBK"));
			String code = read.getName().toUpperCase().split(".TXT")[0];
			String temp = null;
			temp = br.readLine();
			int num = 1;
			while (temp != null) {
				String[] data = temp.split(" ");
				// [0]日期 [1]open [2]high; [3]low [4]close [5] vol [6]bbi
				// [7]period
				StockDO stockDO = new StockDO();
				DateFormat dateFormat = new SimpleDateFormat(
						StockUtil.dateFormatString);
				Date date = dateFormat.parse(data[0]);
				stockDO.setId(num);
				num++;
				stockDO.setTime(date);
				stockDO.setOpen(Float.parseFloat(data[1]));
				stockDO.setHigh(Float.parseFloat(data[2]));
				stockDO.setLow(Float.parseFloat(data[3]));
				stockDO.setClose(Float.parseFloat(data[4]));
				stockDO.setVol(Float.parseFloat(data[5]));
				stockDO.setBbi((Float.parseFloat(data[6])));
				stockDO.setCode(code);
				if (data[7].equalsIgnoreCase(Constant.STOCK_PERIOD_DAY)) {
					stockDO.setPeriod(Constant.STOCK_PERIOD_DAY);
					dataPool.getDayStockList().add(stockDO);
					dataPool.getDayStockMap().put(
							dateFormat.format(stockDO.getTime()), stockDO);
				} else if (data[7].equalsIgnoreCase(Constant.STOCK_PERIOD_WEEK)) {
					stockDO.setPeriod(Constant.STOCK_PERIOD_WEEK);
					dataPool.getWeekStockList().add(stockDO);
					dataPool.getWeekStockMap().put(
							dateFormat.format(stockDO.getTime()), stockDO);
				} else if (data[7]
						.equalsIgnoreCase(Constant.STOCK_PERIOD_MONTH)) {
					stockDO.setPeriod(Constant.STOCK_PERIOD_MONTH);
					dataPool.getMonthStockList().add(stockDO);
					dataPool.getMonthStockMap().put(
							dateFormat.format(stockDO.getTime()), stockDO);
				}
				temp = br.readLine();
			}
			br.close();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataPool;
	}

	/**
	 * 根据 日数据计算周数据
	 * 
	 * @param dayStockMap
	 * @throws ParseException
	 */
	public static void initWeekStockMap(Map<String, StockDO> dayStockMap,
			Map<String, StockDO> weekStockMap, List<StockDO> weekStockList)
			throws ParseException {
		Set<Entry<String, StockDO>> entrySet = dayStockMap.entrySet();
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		for (Entry<String, StockDO> entry : entrySet) {
			String dayString = entry.getKey();
			StockDO dayStockDO = entry.getValue();
			Date day = dateFormat.parse(dayString);
			List<Date> periodDays = CalendarUtil.getWeekDays(day);
			StockDO targetStockDO = getPeriodStockDO(dayStockMap, periodDays);
			if (targetStockDO != null) {
				targetStockDO.setPeriod(Constant.STOCK_PERIOD_WEEK);
				targetStockDO.setCode(dayStockDO.getCode());
			}
			// 如果weekStockList的大小是0 那还没有周数据 weekStockDO可以直接填入weekStockList
			if (weekStockList.size() == 0) {
				if (targetStockDO != null) {
					weekStockMap.put(
							dateFormat.format(targetStockDO.getTime()),
							targetStockDO);
					weekStockList.add(targetStockDO);
				}
			} else {
				// 如果有了周数据，判断dayString天所属周的数据是否已经录入，已录入就跳过
				StockDO lastWeekStockDO = weekStockList.get(weekStockList
						.size() - 1);
				// 因为周的日期使用这一周最后一天为周日期，所有如果是新的数据肯定在最后lastWeekStockDO的日期之后
				if (lastWeekStockDO != null
						&& day.after(lastWeekStockDO.getTime())) {
					if (targetStockDO != null) {
						weekStockMap.put(
								dateFormat.format(targetStockDO.getTime()),
								targetStockDO);
						weekStockList.add(targetStockDO);
					}
				}
			}
		}
	}

	/**
	 * 根据 日数据计算周数据
	 * 
	 * @param dayStockMap
	 * @throws ParseException
	 */
	public static void initMonthStockMap(Map<String, StockDO> dayStockMap,
			Map<String, StockDO> monthStockMap, List<StockDO> monthStockList)
			throws ParseException {
		Set<Entry<String, StockDO>> entrySet = dayStockMap.entrySet();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (Entry<String, StockDO> entry : entrySet) {
			String dayString = entry.getKey();
			StockDO dayStockDO = entry.getValue();
			Date time = dateFormat.parse(dayString);
			List<Date> periodDays = CalendarUtil.getMonthDays(time);
			StockDO targetStockDO = getPeriodStockDO(dayStockMap, periodDays);
			targetStockDO.setPeriod(Constant.STOCK_PERIOD_MONTH);
			targetStockDO.setCode(dayStockDO.getCode());
			// 如果weekStockList的大小是0 那还没有周数据 weekStockDO可以直接填入weekStockList
			if (monthStockList.size() == 0) {
				if (targetStockDO != null) {
					monthStockMap.put(
							dateFormat.format(targetStockDO.getTime()),
							targetStockDO);
					monthStockList.add(targetStockDO);
				}
			} else {
				// 如果有了周数据，判断dayString天所属周的数据是否已经录入，已录入就跳过
				StockDO lastStockDO = monthStockList
						.get(monthStockList.size() - 1);
				// 因为周的日期使用这一周最后一天为周日期，所有如果是新的数据肯定在最后lastWeekStockDO的日期之后
				if (lastStockDO != null && time.after(lastStockDO.getTime())) {
					if (targetStockDO != null) {
						monthStockMap.put(
								dateFormat.format(targetStockDO.getTime()),
								targetStockDO);
						monthStockList.add(targetStockDO);
					}
				}
			}
		}
	}

	public static float getTheTimeBbiOfPeriod(
			Map<String, StockDO> periodStockMap, StockDO stockDO)
			throws ParseException {
		Set<Entry<String, StockDO>> entrySet = periodStockMap.entrySet();
		Map<String, StockDO> newWeekStockMap = new TreeMap<String, StockDO>();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (Entry<String, StockDO> entry : entrySet) {
			if (dateFormat.parse(entry.getKey()).compareTo(stockDO.getTime()) >= 0) {
				entry.getValue().setClose(stockDO.getClose());
				StockDO tmpStockDO = new StockDO();
				tmpStockDO.setTime(stockDO.getTime());
				tmpStockDO.setOpen(entry.getValue().getOpen());
				tmpStockDO.setClose(stockDO.getClose());
				tmpStockDO.setBbi(0f);
				newWeekStockMap.put(dateFormat.format(tmpStockDO.getTime()),
						tmpStockDO);
				break;
			} else {
				newWeekStockMap.put(entry.getKey(), entry.getValue());
			}
		}

		return BBI(newWeekStockMap, stockDO.getTime());
	}

	/**
	 * 根据dayStockMap数据源 取time这天所属周的周数据
	 * 
	 * @param dayStockMap
	 * @param time
	 * @return
	 */
	private static StockDO getWeekStockDOByDate(
			Map<String, StockDO> dayStockMap, Date time) {
		StockDO weekStockDO = new StockDO();
		List<Date> weekDays = CalendarUtil.getWeekDays(time);
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		// 根据日期设置周开盘价
		for (int i = 0; i < weekDays.size(); i++) {
			String dateString = dateFormat.format(weekDays.get(i));
			StockDO dayStockDO = dayStockMap.get(dateString);
			if (dayStockDO != null) {
				weekStockDO.setOpen(dayStockDO.getOpen());
				break;
			}
		}
		// 根据日期设置周收盘价 和周日期(这一周有价格的最后一天)
		for (int i = weekDays.size() - 1; i >= 0; i--) {
			String dateString = dateFormat.format(weekDays.get(i));
			StockDO dayStockDO = dayStockMap.get(dateString);
			if (dayStockDO != null) {
				weekStockDO.setClose(dayStockDO.getClose());
				weekStockDO.setTime(dayStockDO.getTime());
				break;
			}
		}
		// 如果没有周数据 返回null
		if (weekStockDO.getTime() == null) {
			return null;
		}
		return weekStockDO;
	}

	/**
	 * periodDays 是这周或者这个月的所有日期 根据数据源dayStockMap 取这个周期的stockDO
	 * 
	 * @param dayStockMap
	 * @param periodDays
	 * @return
	 */
	private static StockDO getPeriodStockDO(Map<String, StockDO> dayStockMap,
			List<Date> periodDays) {
		StockDO periodStockDO = new StockDO();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		// 根据日期设置周开盘价
		for (int i = 0; i < periodDays.size(); i++) {
			String dateString = dateFormat.format(periodDays.get(i));
			StockDO dayStockDO = dayStockMap.get(dateString);
			if (dayStockDO != null) {
				periodStockDO.setOpen(dayStockDO.getOpen());
				break;
			}
		}
		// 根据日期设置周收盘价 和周日期(这一周有价格的最后一天)
		for (int i = periodDays.size() - 1; i >= 0; i--) {
			String dateString = dateFormat.format(periodDays.get(i));
			StockDO dayStockDO = dayStockMap.get(dateString);
			if (dayStockDO != null) {
				periodStockDO.setClose(dayStockDO.getClose());
				periodStockDO.setTime(dayStockDO.getTime());
				break;
			}
		}
		// 如果没有周数据 返回null
		if (periodStockDO.getTime() == null) {
			return null;
		}
		return periodStockDO;
	}

	private static float BBI(Map<String, StockDO> stockMap, Date time)
			throws NumberFormatException, ParseException {
		int m1 = 3;
		int m2 = 6;
		int m3 = 12;
		int m4 = 24;
		return Float.parseFloat(decimalFormat.format((MA(stockMap, time, m1)
				+ MA(stockMap, time, m2) + MA(stockMap, time, m3) + MA(
				stockMap, time, m4)) / 4));
	}

	private static float BBI(Map<String, StockDO> stockMap,
			List<StockDO> stockList, Date time) throws NumberFormatException,
			ParseException {
		int m1 = 3;
		int m2 = 6;
		int m3 = 12;
		int m4 = 24;
		return Float.parseFloat(decimalFormat.format((MA(stockMap, stockList,
				time, m1)
				+ MA(stockMap, stockList, time, m2)
				+ MA(stockMap, stockList, time, m3) + MA(stockMap, stockList,
				time, m4)) / 4));
	}

	/**
	 * 
	 * 数据源是stockMap 计算time这一天的 周期day的atr值
	 * 
	 * @param stockMap
	 * @param time
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public static float atr(Map<String, StockDO> stockMap, Date time, int day)
			throws ParseException {
		// ATR : MA(TR,m)
		List<Float> list = new ArrayList<Float>();
		Date theDate = time;
		for (int i = 0; i < day; i++) {
			list.add(tr(stockMap, theDate));
			theDate = pre(stockMap, theDate);
		}
		Float sum = new Float(0);
		for (Float tmpfloat : list) {
			sum = sum + tmpfloat;
		}
		return Float.parseFloat(decimalFormat.format(sum / list.size()));
	}

	public static float atr(Map<String, StockDO> stockMap, Date time)
			throws ParseException {
		return atr(stockMap, time, 20);
	}

	/**
	 * 性能优化过的接口 不建议使用其他的ma
	 * 
	 * @param stockList
	 * @param stockDO
	 * @param count
	 * @return
	 */
	public static float MA(List<StockDO> stockList, StockDO stockDO, int count) {
		int index = stockList.indexOf(stockDO);
		// 如果stockDO 所在的位置之前没有count数量的 数据
		if (count > (index + 1)) {
			return 0;
		}
		List<StockDO> subList = stockList.subList(index + 1 - count, index + 1);
		Float sum = new Float(0);
		for (StockDO tmpStock : subList) {
			sum = sum + tmpStock.getClose();
		}
		return Float.parseFloat(decimalFormat.format(sum / subList.size()));
	}

	/**
	 * 从stockMap中计算 time这一天的count平均移动值
	 * 
	 * @param stockMap
	 * @param time
	 * @param count
	 * @return
	 * @throws ParseException
	 */
	public static float MA(Map<String, StockDO> stockMap, Date time, int count) {
		// list是day这个周期的所有close值
		List<Float> list = new ArrayList<Float>();
		Date theDate = time;
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (int i = 0; i < count; i++) {
			StockDO stockDO = stockMap.get(dateFormat.format(theDate));
			list.add(stockDO.getClose());
			theDate = pre(stockMap, theDate);
			// 如果没有pre Date,则返回说明没有ma
			if (theDate == null) {
				return 0;
			}
		}
		Float sum = new Float(0);
		for (Float tmpFloat : list) {
			sum = sum + tmpFloat;
		}
		return Float.parseFloat(decimalFormat.format(sum / list.size()));
	}

	/**
	 * 从stockMap中计算 time这一天的count平均移动值
	 * 
	 * @param stockMap
	 * @param time
	 * @param count
	 * @return
	 * @throws ParseException
	 */
	public static float MA(Map<String, StockDO> stockMap,
			List<StockDO> stockList, Date time, int count)
			throws ParseException {
		// list是day这个周期的所有close值
		List<Float> list = new ArrayList<Float>();
		Date theDate = time;
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (int i = 0; i < count; i++) {
			StockDO stockDO = stockMap.get(dateFormat.format(theDate));
			list.add(stockDO.getClose());
			theDate = pre(stockList, theDate);
		}
		Float sum = new Float(0);
		for (Float tmpFloat : list) {
			sum = sum + tmpFloat;
		}
		return Float.parseFloat(decimalFormat.format(sum / list.size()));
	}

	/**
	 * 将stockDO list 转换为 日期为key的Map<String, StockDO>
	 * 
	 * @param stockList
	 * @return
	 */
	public static Map<String, StockDO> toStockMap(List<StockDO> stockList) {
		Map<String, StockDO> stockMap = new TreeMap<String, StockDO>();
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (StockDO stockDO : stockList) {
			stockMap.put(dateFormat.format(stockDO.getTime()), stockDO);
		}
		return stockMap;
	}

	public static float tr(Map<String, StockDO> stockMap, Date time)
			throws ParseException {
		// TR :
		// MAX(MAX((HIGH-LOW),ABS(REF(CLOSE,1)-HIGH)),ABS(REF(CLOSE,1)-LOW));
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		String timeString = dateFormat.format(time);
		StockDO stockDO = stockMap.get(timeString);
		float theHL = Math.abs(stockDO.getHigh() - stockDO.getLow());
		StockDO preStockDO = stockMap.get(dateFormat.format(pre(stockMap,
				dateFormat.parse(timeString))));
		float preCH = Math.abs(preStockDO.getClose() - stockDO.getHigh());
		float preCL = Math.abs(preStockDO.getClose() - stockDO.getLow());
		return Math.max(theHL, Math.max(preCH, preCL));
	}

}
