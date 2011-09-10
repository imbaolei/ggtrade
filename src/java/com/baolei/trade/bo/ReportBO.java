package com.baolei.trade.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;

@Service("reportBO")
public class ReportBO {
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private ReportDAO reportDAO;
	
	public List<String> getCodesFromReport(int start,int count){
		Map param = new HashMap();
		if(start >= 0 && count > 0 ){
			param.put("start", start);
			param.put("count", count);
		}
		List<String> codeList = reportDAO.seleteCodesFromReport(param);
		return codeList;
	}
	
	public int countCodesFromReport(){
		Map param = new HashMap();
		return reportDAO.countCodesFromReport(param);
	}
	
	public Map<String,List<ReportDO>> listReportByCodes(List<String> codes){
		Map param = new HashMap();
		param.put("codes", codes);
		List<ReportDO> reportList = reportDAO.seleteReportsByConditions(param);
		Map<String,List<ReportDO>> reportMap = new TreeMap<String,List<ReportDO>>();
		for(ReportDO reportDO:reportList){
			String code = reportDO.getCode();
			if(reportMap.get(code) != null){
				reportMap.get(code).add(reportDO);
			}else{
				List report = new ArrayList<ReportDO>();
				report.add(reportDO);
				reportMap.put(code, report);
			}
		}
		return reportMap;
		
	}
}
