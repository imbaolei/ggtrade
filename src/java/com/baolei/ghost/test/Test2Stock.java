package com.baolei.ghost.test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

public abstract class Test2Stock {

	protected Log log = LogFactory.getLog(getClass());
	protected DateFormat dateFormat = new SimpleDateFormat(
			StockUtil.dateFormatString);
	protected DecimalFormat decimalFormat = new DecimalFormat("#.00");

	protected List<StockDO> pdStockList;

	protected List<StockDO> jyStockList;

	protected Map<String, StockDO> pdStockMap;

	protected Map<String, StockDO> jyStockMap;

	public void init(List<StockDO> pdStockList, List<StockDO> jyStockList) {
		this.pdStockList = pdStockList;
		this.jyStockList = jyStockList;
		pdStockMap = StockUtil.toStockMap(pdStockList);
		jyStockMap = StockUtil.toStockMap(jyStockList);
	}

	public void execute() {
		for (StockDO stockDO : pdStockList) {
			String dateString = dateFormat.format(stockDO.getTime());
			if (needBuy(dateString)) {
				buy(dateString);
				continue;
			} else if (needSale(dateString)) {
				sale(dateString);
				continue;
			} else {
				noBuyNoSale(dateString);
			}
		}

	}

	public void printReport(List<StockDO> stockList) {
		for (StockDO stockDO : stockList) {
			Report report = stockDO.getReport();
			String date = dateFormat.format(stockDO.getTime());
			String account = " - account: " + report.getAccount().toString();
			String status = " ";
			if (StringUtils.isNotEmpty(report.getStatus())) {
				status = " - " + report.getStatus();
			}
			String fee = " ";
			String close = " " + new Float(stockDO.getClose()).toString();
			if (report.getFee() != null && report.getFee() > 0) {
				fee = " - 手续费：" + report.getFee();
			}
			String totalFee = " ";
			if (report.getTotalFee() != null && report.getTotalFee() > 0) {
				totalFee = " - 总手续费：" + report.getTotalFee();
			}
			String transCount = " ";
			if (report.getTransCount() != null && report.getTransCount() > 0) {
				transCount = " - 总交易次数 ：" + report.getTransCount();
			}
			String note = "";
			if (StringUtils.isNotEmpty(report.getNotes())) {
				note = " - " + report.getNotes();
			}
			StringBuffer sb = new StringBuffer();
			sb.append(date).append(close).append(account).append(status)
					.append(fee).append(transCount).append(totalFee)
					.append(note);
			if (log.isInfoEnabled()) {
				log.info(sb.toString());
			}

		}
	}

	public abstract boolean needBuy(String dateString);

	public abstract void noBuyNoSale(String dateString);

	public abstract void buy(String dateString);

	public abstract boolean needSale(String dateString);

	public abstract void sale(String dateString);

}
