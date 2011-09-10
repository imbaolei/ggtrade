package com.baolei.ghost.dal.daointerface;

import java.util.List;
import java.util.Map;

import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;

public interface ReportDAO {

	public void insertReports(final List<ReportDO> recordList) ;
	public Integer insertReport(ReportDO record) ;
	public StockDO selectReportByCode(String code);
	public int deleteReportByCode(String code);
	List<ReportDO> seleteReportsByConditions(Map param);
	List<String> seleteCodesFromReport(Map param);
	int countCodesFromReport(Map param);
}
