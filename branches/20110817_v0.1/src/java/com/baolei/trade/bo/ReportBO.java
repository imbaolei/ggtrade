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

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Ma3Tc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTc;

@Service("reportBO")
public class ReportBO {
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private TradeBO tradeBO;

	@Autowired
	private StockBO stockBO;
	
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
		return listReportByReportList(reportList);
	}
	
	public Map<String,List<ReportDO>> listReportByReportList(List<ReportDO> reportList){
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
	
	
	public void executeTrade(String code) {
		Ma3Tc test = new Ma3LLVTc();
		float account = 100000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		List<StockDO> stockList = getInitStockList(code);
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		List<ReportDO> reportList = getAllTradeReport(stockList);
		// test.printReport();
		reportDAO.deleteReportByCode(code);
		reportDAO.insertReports(reportList);
	}
	
	public List<ReportDO> getAllTradeReport(List<StockDO> stockList) {
		List<ReportDO> reportList = new ArrayList<ReportDO>();
		for (StockDO stockDO : stockList) {
			String status = stockDO.getReport().getStatus();
			if (Constant.REPORT_STATUS_SALE.equals(status)
					|| Constant.REPORT_STATUS_BUY.equals(status)) {
				ReportDO reportDO = stockDO.getReport();
				reportDO.setCode(stockDO.getCode());
				reportList.add(reportDO);
			}
		}
		return reportList;
	}
	
	private List<StockDO> getInitStockList(String code) {
		List<StockDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initStockListMa(stockList, "");
		return stockList;
	}
	
	
}
