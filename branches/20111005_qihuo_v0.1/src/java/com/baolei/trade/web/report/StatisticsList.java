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

@Controller
@RequestMapping("/report/statistics_list.do")
public class StatisticsList {

	@Autowired
	private StatisticsDAO statisticsDAO;

	private int rankThreshold = 80;

	/**
	 * 取某个时间点 强度前多少的股票 进行统计后排序，按照股票的行业 出现的次数排序
	 * @param menkan
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=industry")
	public String industryStats(@RequestParam(value = "menkan", required = false) Integer menkan,HttpServletRequest request, ModelMap model) {
		String timeString = request.getParameter("time");
		if(menkan != null){
			rankThreshold  = menkan;
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
	 * 取某个时间点 强度前多少的股票 进行统计后排序，按照股票的行业 出现的次数排序
	 * @param menkan
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=industry_rank")
	public String industryRank(@RequestParam(value = "menkan", required = false) Integer menkan,HttpServletRequest request, ModelMap model) {
		String timeString = request.getParameter("time");
		if(menkan != null){
			rankThreshold  = menkan;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date time = dateFormat.parse(timeString);
			Map param = new HashMap();
			param.put("time", time);
			param.put("menkan", rankThreshold);
			List<StatisticsIndustryDO> industryList = statisticsDAO
					.seleteIndustryStatistics(param);
			model.addAttribute("industryList", industryList);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "report/industry_rank";
	}
	
	
	@RequestMapping(params = "m=all_industry_rank")
	public String allIndustryRank(@RequestParam(value = "menkan", required = false) Integer menkan,HttpServletRequest request, ModelMap model) {
		Map param = new HashMap();
		List<Date> dates = statisticsDAO.selectDatesFromStatistics(param);
		if(menkan != null){
			rankThreshold  = menkan;
		}
		
		Map resultMap = new TreeMap();
		for(Date date : dates){
			Map param2 = new HashMap();
			param2.put("time", date);
			param2.put("menkan", rankThreshold);
			List<StatisticsIndustryDO> industryList = statisticsDAO
					.seleteIndustryStatistics(param2);
			resultMap.put(date, industryList);
		}
		
		model.addAttribute("resultMap", resultMap);
		
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
