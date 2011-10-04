package com.baolei.trade.bo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baolei.ghost.app2.DataParser;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.daointerface.ReportStatsDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.ReportStatsDO;
import com.baolei.ghost.dal.dataobject.StockDO;

@Service("reportStatsBO")
public class ReportStatsBO {

	protected Log log = LogFactory.getLog(getClass());

	@Autowired
	private ReportDAO reportDAO;

	@Autowired
	private ReportStatsDAO reportStatsDAO;

	@Autowired
	@Qualifier("txdFileParser") 
	DataParser dataParser;

	public void initReportStats(String code) {
		ReportStatsDO reportStatsDO = getReportStats(code);
		reportStatsDAO.deleteReportStatsByCode(code);
		reportStatsDAO.insertReportStats(reportStatsDO);
	}

	public ReportStatsDO getReportStats(String code) {
		List<ReportDO> reportList = reportDAO.selectReportByCode(code, "time");
		ReportStatsDO reportStatsDO = new ReportStatsDO();
		int winNum = 0;
		float winPercent = 0f;
		float win = 0f;
		int lossNum = 0;
		float lossPercent = 0f;
		float loss = 0f;
		int totalNum = 0;
		float totalPercent = 0f;
		float averagePercent = 0f;
		Date lastTradeTime = null;
		Date startTradeTime = null;
		float lastTradePrice = 0;
		float lastPercent = 0;
		float maxWinPercent = 0;
		float maxLossPercent = 0;
		for (int i = 0; i < reportList.size(); i++) {
			ReportDO reportDO = reportList.get(i);
			if (Constant.REPORT_STATUS_SALE.equals(reportDO.getStatus())) {
				totalNum++;
//				totalPercent = totalPercent + reportDO.getPercent();
				if (reportDO.getPercent() > 0) {
					winNum++;
					if(win == 0){
						win = 1+reportDO.getPercent()*0.01f;
					}else{
						win = win*(1+reportDO.getPercent()*0.01f);
					}
					if (reportDO.getPercent() > maxWinPercent) {
						maxWinPercent = reportDO.getPercent();
					}
				} else {
					lossNum++;
//					lossPercent = lossPercent + reportDO.getPercent();
					if(loss == 0){
						loss = 1+reportDO.getPercent()*0.01f;
					}else{
						loss = loss*(1+reportDO.getPercent()*0.01f);
					}
					if (reportDO.getPercent() < maxLossPercent) {
						maxLossPercent = reportDO.getPercent();
					}
				}
			}
		}
		if(reportList.size() > 0 ){
			ReportDO lastReportDO = reportList.get(reportList.size() - 1);
			// 如果最后一个report的状态是buy，那证明还没平仓，计算收益
			if (Constant.REPORT_STATUS_BUY.equals(lastReportDO.getStatus())) {
				StockDO lastStockDO = dataParser.getLastPrice(code);
				lastPercent = (lastStockDO.getClose() - lastReportDO.getPrice())
						/ lastReportDO.getPrice() * 100;
				lastPercent = NumberUtil.roundDown(lastPercent, 2);

				lastTradeTime = lastReportDO.getTime();
				lastTradePrice = lastReportDO.getPrice();
			}
			ReportDO startReportDO = reportList.get(0);
			startTradeTime = startReportDO.getTime();
		}
		
		
		
		float total = 0;
		if(loss == 0){
			total = win;
		}else if(win == 0){
			total = loss;
		}else{
			total = loss*win;
		}
		if(loss != 0 ){
			lossPercent = NumberUtil.roundDown((1-loss)*100, 2);
		}
		if(win != 0 ){
			winPercent = NumberUtil.roundDown((win-1)*100, 2);
		}
		if(total != 0 ){
			totalPercent = NumberUtil.roundDown((total-1)*100, 2);
		}
		if(totalNum > 0 ){
			averagePercent = totalPercent / totalNum;
			averagePercent = NumberUtil.roundDown(averagePercent, 2);
		}
		String name = dataParser.getStockName(code);
		reportStatsDO.setName(name);
		reportStatsDO.setCode(code);
		reportStatsDO.setWinNum(winNum);
		reportStatsDO.setWinPercent(winPercent);
		reportStatsDO.setLossNum(lossNum);
		reportStatsDO.setLossPercent(lossPercent);
		reportStatsDO.setTotalNum(totalNum);
		reportStatsDO.setTotalPercent(totalPercent);
		reportStatsDO.setAveragePercent(averagePercent);
		reportStatsDO.setLastTradeTime(lastTradeTime);
		reportStatsDO.setLastTradePrice(lastTradePrice);
		reportStatsDO.setLastPercent(lastPercent);
		reportStatsDO.setMaxWinPercent(maxWinPercent);
		reportStatsDO.setMaxLossPercent(maxLossPercent);

		return reportStatsDO;
	}

}
