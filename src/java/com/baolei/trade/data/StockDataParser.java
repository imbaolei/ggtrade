package com.baolei.trade.data;

import java.util.Date;
import java.util.List;

import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StockDO;

public interface StockDataParser {
	
	public List<StockDO> getStockList();
	
	public List<StatisticsDO> getStatisticsListByFile(String file,Date time);

}
