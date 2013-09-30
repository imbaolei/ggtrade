package com.baolei.ghost.dal.daointerface;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StatisticsIndustryDO;

public interface StatisticsDAO {
	
	public void insertStatisticses(final List<StatisticsDO> recordList);

	public Integer insertStatistics(StatisticsDO record);

	public List<StatisticsDO> selectStatisticsByTime(Date date);

	public int deleteStatisticsByDate(Date date);
	
	public List<StatisticsIndustryDO> seleteIndustryStatistics(Map param);
	
	public List<Date> selectDatesFromStatistics(Map param);

}
