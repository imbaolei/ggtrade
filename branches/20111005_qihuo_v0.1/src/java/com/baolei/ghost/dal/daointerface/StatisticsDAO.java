package com.baolei.ghost.dal.daointerface;

import java.util.Date;
import java.util.List;

import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;

public interface StatisticsDAO {
	
	public void insertStatisticses(final List<StatisticsDO> recordList);

	public Integer insertStatistics(StatisticsDO record);

	public List<ReportDO> selectStatisticsByCode(String code);

	public int deleteStatisticsByDate(Date date);

}
