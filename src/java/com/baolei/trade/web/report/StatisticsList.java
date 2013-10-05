package com.baolei.trade.web.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.baolei.ghost.dal.daointerface.StatisticsDAO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StatisticsIndustryDO;
import com.baolei.trade.bo.StatisticsBO;

@Controller
@RequestMapping("/report/statistics_list.do")
public class StatisticsList {

	@Autowired
	private StatisticsDAO statisticsDAO;

	@Autowired
	private StatisticsBO statisticsBO;

	private int rankThreshold = 80;

	/**
	 * 取某个时间点、强度前多少的股票 进行统计后排序，按照股票的行业 出现的次数排序
	 * 
	 * @param menkan
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=industry")
	public String industryStats(
			@RequestParam(value = "menkan", required = false) Integer menkan,
			HttpServletRequest request, ModelMap model) {
		String timeString = request.getParameter("time");
		if (menkan != null) {
			rankThreshold = menkan;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date time = dateFormat.parse(timeString);
			List<StatisticsDO> statList = statisticsDAO
					.selectStatisticsByTime(time);
			Map<String, List<StatisticsDO>> inStatsMap = new HashMap<String, List<StatisticsDO>>();
			Map<String, Integer> inCountMap = new HashMap<String, Integer>();
			for (StatisticsDO sd : statList) {
				if (sd.getRiseRank() >= rankThreshold) {
					// 如果不包含，估计是第一次
					if (!inStatsMap.containsKey(sd.getIndustry())) {
						List<StatisticsDO> indusStatList = new ArrayList<StatisticsDO>();
						indusStatList.add(sd);
						inStatsMap.put(sd.getIndustry(), indusStatList);

						Integer count = new Integer(1);
						inCountMap.put(sd.getIndustry(), count);
					} else {
						List<StatisticsDO> indusStatList = inStatsMap.get(sd
								.getIndustry());
						indusStatList.add(sd);
						inStatsMap.put(sd.getIndustry(), indusStatList);

						Integer count = inCountMap.get(sd.getIndustry());
						count++;
						inCountMap.put(sd.getIndustry(), count);
					}
				}
			}

			List<Entry<String, Integer>> inCountList = sortKeywordMap(inCountMap);
			model.addAttribute("inStatsMap", inStatsMap);
			model.addAttribute("inCountList", inCountList);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "report/statistics_list";
	}

	/**
	 * 取 强度 大于 mankan的 个股 ，根据所属行业统计后，按照行业排序
	 * 
	 * @param menkan
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=stock_rank")
	public String industryRank(
			@RequestParam(value = "start", required = false) String startTimeParam,
			@RequestParam(value = "end", required = false) String endTimeParam,
			@RequestParam(value = "type", required = false) String typeParam,
			@RequestParam(value = "industry", required = false) String industryParam,
			@RequestParam(value = "menkan", required = false) Integer menkanParam,
			@RequestParam(value = "code", required = false) String codeParam,
			HttpServletRequest request, ModelMap model) {
		
		String startTimeString = "1949-1-1";
		String endTimeString = "2049-1-1";
		String type = "week";
		int menkan = 80;
		String industry = "";
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map param = new HashMap();
			
			
			if (startTimeParam != null && !startTimeParam.isEmpty()) {
				startTimeString = startTimeParam;
			}
			if (endTimeParam != null && !endTimeParam.isEmpty()) {
				endTimeString = endTimeParam;
			}
			if (typeParam != null && !typeParam.isEmpty()) {
				type = typeParam;
			}
			if (menkanParam != null && menkanParam >= 0) {
				menkan = menkanParam;
			}
			
			if (industryParam != null && !industryParam.isEmpty()) {
				industry = industryParam;
				param.put("industry", industry);
			}
			
			if (codeParam != null && !codeParam.isEmpty()) {
				param.put("code", codeParam);
			}
			Date start = dateFormat.parse(startTimeString);
			Date end = dateFormat.parse(endTimeString);
			
			param.put("start", start);
			param.put("end", end);
			param.put("type", type);
			param.put("menkan", menkan);
			
			
			Map<Date, List<StatisticsDO>> resultMap = statisticsBO
					.seleteStockStatistics(param);

			model.addAttribute("resultMap", resultMap);
			model.addAttribute("dateFormat", dateFormat);
			model.addAttribute("industry", industry);
			model.addAttribute("start", dateFormat.format(start));
			model.addAttribute("end", dateFormat.format(end));
			model.addAttribute("type", type);
			model.addAttribute("menkan", menkan);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "report/stock_rank";
	}

	/**
	 * 取一个时间段类的 强度 大于 mankan的 个股 ，根据所属行业统计，按照行业排序
	 * 
	 * @param menkan
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=industry_rank")
	public String allIndustryRank(
			@RequestParam(value = "start", required = false) String startTimeParam,
			@RequestParam(value = "end", required = false) String endTimeParam,
			@RequestParam(value = "type", required = false) String typeParam,
			@RequestParam(value = "menkan", required = false) Integer menkanParam,
			HttpServletRequest request, ModelMap model) {
		
		String startTimeString = "1949-1-1";
		String endTimeString = "2049-1-1";
		String type = "week";
		int menkan = 80;

		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (startTimeParam != null && !startTimeParam.isEmpty()) {
				startTimeString = startTimeParam;
			}
			if (endTimeParam != null && !endTimeParam.isEmpty()) {
				endTimeString = endTimeParam;
			}
			if (typeParam != null && !typeParam.isEmpty()) {
				type = typeParam;
			}
			if (menkanParam != null && menkanParam >= 0) {
				menkan = menkanParam;
			}
			Date start = dateFormat.parse(startTimeString);

			Date end = dateFormat.parse(endTimeString);
			Map<Date, List<StatisticsIndustryDO>> resultMap = statisticsBO
					.seleteIndustryStatisticsByDate(start, end, menkan, type);

			model.addAttribute("resultMap", resultMap);
			model.addAttribute("dateFormat", dateFormat);
			model.addAttribute("start", dateFormat.format(start));
			model.addAttribute("end", dateFormat.format(end));
			model.addAttribute("type", type);
			model.addAttribute("menkan", menkan);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "report/industry_rank";
	}

	public List<Entry<String, Integer>> sortKeywordMap(
			Map<String, Integer> keywordMap) {
		List<Entry<String, Integer>> arrayList = new ArrayList<Entry<String, Integer>>(
				keywordMap.entrySet());
		Collections.sort(arrayList, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> e1,
					Entry<String, Integer> e2) {
				return (e2.getValue()).compareTo(e1.getValue());
			}
		});
		for (Entry<String, Integer> entry : arrayList) {
			System.out.println(entry.getKey() + "  " + entry.getValue());
		}
		return arrayList;
	}

}
