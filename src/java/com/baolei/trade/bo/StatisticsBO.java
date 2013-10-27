package com.baolei.trade.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baolei.ghost.dal.daointerface.StatisticsDAO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StatisticsIndustryDO;

@Service("statisticsBO")
public class StatisticsBO {
	
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private StatisticsDAO statisticsDAO;
	
	/**
	 * 取一个时间段内的行业统计数据
	 * @param start 开始时间
	 * @param end 结束时间
	 * @param menkan 强度门槛
	 * @param type month
	 * @return
	 */
	public Map<Date,List<StatisticsIndustryDO>> seleteIndustryStatisticsByDate(Date start,Date end,int menkan ,String type) {
		Map param = new HashMap();
		param.put("start", start);
		param.put("end", end);
		List<Date> dates = statisticsDAO.selectDatesFromStatistics(param);
		
		
		if("month".equals(type)){
			dates = getMonthDates(dates);
		}
		
		Map<Date,List<StatisticsIndustryDO>> resultMap = new TreeMap();
		for(Date date : dates){
			Map param2 = new HashMap();
			param2.put("time", date);
			param2.put("menkan", menkan);
			List<StatisticsIndustryDO> industryList = statisticsDAO
					.seleteIndustryStatistics(param2);
			resultMap.put(date, industryList);
		}
		return resultMap;
		
	}
	
	/**
	 * 根据时间和其他条件去统计数据
	 * @return
	 */
	public Map<Date,List<StatisticsDO>> seleteStockStatistics(Map param) {
		
		List<Date> dates = statisticsDAO.selectDatesFromStatistics(param);
		
		if("month".equals(param.get("type"))){
			dates = getMonthDates(dates);
		}
		
		Map<Date,List<StatisticsDO>> resultMap = new TreeMap();
		for(Date date : dates){
			param.put("time",date);
			param.put("order", "rise_rank");
			List<StatisticsDO> industryList = statisticsDAO
					.selectStatistics(param);
			resultMap.put(date, industryList);
		}
		return resultMap;
		
	}
	
	private List<Date> getMonthDates(List<Date> dates){
		Map<String,Date> monthDatesMap = new TreeMap<String,Date>();
		for(Date date : dates){
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			Integer year=c.get(Calendar.YEAR);
			Integer month=c.get(Calendar.MONTH);
			monthDatesMap.put(year.toString()+month.toString(), date);
		}
		dates = new ArrayList<Date>();
		for(Map.Entry<String,Date> e : monthDatesMap.entrySet()){
			dates.add(e.getValue());
		}
		return dates;
	}

}
