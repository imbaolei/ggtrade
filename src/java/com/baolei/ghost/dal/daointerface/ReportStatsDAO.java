package com.baolei.ghost.dal.daointerface;

import java.util.List;
import java.util.Map;

import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.ReportStatsDO;
import com.baolei.ghost.dal.dataobject.StockDO;

public interface ReportStatsDAO {

	public void insertReportStats(final List<ReportStatsDO> recordList) ;
	public Integer insertReportStats(ReportStatsDO record) ;
	public ReportStatsDO selectReportStatsByCode(String code);
	public int deleteReportStatsByCode(String code);
	List<ReportStatsDO> seleteReportStatsByConditions(Map param);
}
