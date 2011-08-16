package com.baolei.ghost.test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

/**
 * @author lei.baol
 * 测试 两种 股票，一种用来做 买点买点的判断 pdStockList，一种是用来交易 jyStockList
 * 如果判断的和交易的相同，则设置为同一个即可
 * execute前 需要 先 init()
 *
 */
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
			if(needDingTou(dateString)){
				dingTou(dateString);
			}
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

	public void printReport() {
		for (StockDO stockDO : jyStockList) {
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
			String totalMoney = " ";
			if (report.getTotalMoney() != null && report.getTotalMoney() > 0) {
				totalMoney = " - 总投入金额 ：" + report.getTotalMoney();
			}
			String note = "";
			if (StringUtils.isNotEmpty(report.getNotes())) {
				note = " - " + report.getNotes();
			}
			StringBuffer sb = new StringBuffer();
			sb.append(date).append(close).append(account).append(status)
					.append(fee).append(transCount).append(totalFee).append(totalMoney)
					.append(note);
			if (log.isInfoEnabled()) {
				log.info(sb.toString());
			}
			if(Constant.REPORT_STATUS_SALE.equals(stockDO.getReport().getStatus())){
				sb = new StringBuffer();
				String shouyi = " - 这次交易收益 ：" + report.getShouyi();
				sb.append(shouyi);
				log.info(sb.toString());
			}
		}
	}

	public abstract boolean needDingTou(String dateString);
	
	public abstract void dingTou(String dateString);
	
	public abstract boolean needBuy(String dateString);

	public abstract void buy(String dateString);

	public abstract boolean needSale(String dateString);

	public abstract void sale(String dateString);
	
	public abstract void noBuyNoSale(String dateString);

}
