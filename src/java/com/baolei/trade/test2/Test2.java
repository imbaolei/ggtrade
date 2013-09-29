package com.baolei.trade.test2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;

/**
 * @author lei.baol 测试 两种 股票，一种用来做 买点买点的判断 pdStockList，一种是用来交易 jyStockList
 *         如果判断的和交易的相同，则设置为同一个即可 execute前 需要 先 initAccount()
 * 
 */
public abstract class Test2 {

	protected Log log = LogFactory.getLog(getClass());
	protected DateFormat dateFormat = new SimpleDateFormat(
			StockUtil.dateFormatString);
	protected DecimalFormat decimalFormat = new DecimalFormat("#.00");
	protected List<StockDO> stockList;
	protected Map<String, StockDO> stockMap;
	protected float rate = 0f;
	protected float cash; // 现金
	protected float toucun; // HighRisk高风险头寸
	protected float share; // HighRisk高风险份额
	protected int transCount = 0;
	protected float totalFee = 0;
	protected float planBuyPoint;
	protected float planBuyToucun;
	protected float planBuyShare;
	protected float planSalePoint;
	/**
	 * lastBuyStockDO 上次买入时stockDO 用来计算上次买入到这次交易之间的 资金变化情况 如果其他业务逻辑需要用，不能影响上述逻辑
	 */
	protected StockDO lastBuyStockDO;
	protected float totalMoney;
	protected boolean reportFilterSwitch = true;

	protected String startDateString = "1997/1/1";

	public float getRateHR() {
		return rate;
	}

	public void setRateHR(float rate) {
		this.rate = rate;
	}

	protected float fee(float money) {
		float fee = money * rate;
		fee = NumberUtil.roundDown(fee, 2);
		return fee;
	}

	/**
	 * @param jyStockList
	 *            交易的stock
	 */
	public void initStockList(List<StockDO> pdStockList,
			List<StockDO> jyStockList) {
		this.stockList = jyStockList;
		stockMap = StockUtil.toStockMap(jyStockList);
	}

	private Date getStartDate() {
		Date startDate = null;
		try {
			startDate = dateFormat.parse(startDateString);
			int i = stockList.size()-1;
			for(; i >=0;i--){
				StockDO jyStockDO = stockList.get(i);
				if(jyStockDO.getLow() <= 0){
					break;
				}
				float change = Math.abs(jyStockDO.getClose()/jyStockDO.getOpen());
				if(change < 0.85 || change > 1.15){
					break;
				}
			}
			StockDO nextDayJyStock = stockList.get(i+1);
			if(nextDayJyStock.getTime().after(startDate)){
				startDate = nextDayJyStock.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;
	}

	public void execute() {
		Date startDate = getStartDate();
		for (StockDO stockDO : stockList) {
			// 从startDate开始计算
			if (stockDO.getTime().before(startDate)) {
				continue;
			}
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

	public void printReport() {
		for (StockDO stockDO : stockList) {
			if (reportFilter(stockDO) && reportFilterSwitch) {
				continue;
			}
			ReportDO report = stockDO.getReport();
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
			String shareHR = "";
			if (report.getShareHR() != null && report.getShareHR() > 0) {
				shareHR = " 持有份额 ： " + report.getShareHR();
			}
			String buyPrice = "";
			if (Constant.REPORT_STATUS_BUY.equals(stockDO.getReport()
					.getStatus())) {
				buyPrice = " 买点： " + report.getPrice();
			}

			StringBuffer sb = new StringBuffer();
			sb.append(date).append(close).append(shareHR).append(account)
					.append(status).append(fee).append(buyPrice).append(note)
					.append(transCount).append(totalFee).append(totalMoney);
			log.info(sb.toString());
			if (Constant.REPORT_STATUS_SALE.equals(stockDO.getReport()
					.getStatus())) {
				sb = new StringBuffer();
				String salePrice = " - 卖点 ：" + report.getPrice();
				;
				sb.append(salePrice);
				String shouyi = " - 这次交易收益 ：" + report.getShouyi();
				sb.append(shouyi);
				String shouyiPersent = " - 这次交易收益率 ：" + report.getPercent()
						+ "%";
				sb.append(shouyiPersent);
				log.info(sb.toString());
			}
			
		}
	}

	public boolean reportFilter(StockDO stockDO) {
		ReportDO report = stockDO.getReport();
		String status = report.getStatus();
		if (Constant.REPORT_STATUS_BUY.equals(status)) {
			return false;
		}

		if (Constant.REPORT_STATUS_SALE.equals(status)) {
			return false;
		}
		if(stockList.indexOf(stockDO) == (stockList.size()-1)){
			return false;
		}

		return true;

	}


	public abstract boolean needBuy(String dateString);

	public abstract void buy(String dateString);

	public abstract boolean needSale(String dateString);

	public abstract void sale(String dateString);

	public abstract void noBuyNoSale(String dateString);

	public void initCash(float cash) {
		this.cash = cash;
	}

}
