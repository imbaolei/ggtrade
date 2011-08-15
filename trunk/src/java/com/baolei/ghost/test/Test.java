package com.baolei.ghost.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

public abstract class Test {

	protected Log log = LogFactory.getLog(getClass());
	DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);

	// private List<StockDO> stockList;

	public abstract boolean needBuy(StockDO stockDO);

	public void execute(List<StockDO> stockList) {
		for (StockDO stockDO : stockList) {
			if (needBuy(stockDO)) {
				buy(stockDO);
				continue;
			} else if (needSale(stockDO)) {
				sale(stockDO);
				continue;
			} else {
				noBuyNoSale(stockDO);
			}
		}

	}

	public void printReport(List<StockDO> stockList) {
		for (StockDO stockDO : stockList) {
			StringBuffer sb = new StringBuffer();
			sb.append(dateFormat.format(stockDO.getTime()))
					.append(" - account :")
					.append(stockDO.getReport().getAccount()).append(" ")
					.append(stockDO.getReport().getNotes());
			if (log.isInfoEnabled()) {
				log.info(sb.toString());
			}

		}
	}

	public abstract void noBuyNoSale(StockDO stockDO);

	public abstract void buy(StockDO stockDO);

	public abstract boolean needSale(StockDO stockDO);

	public abstract void sale(StockDO stockDO);

}
