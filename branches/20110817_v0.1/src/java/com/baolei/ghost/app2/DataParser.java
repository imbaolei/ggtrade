package com.baolei.ghost.app2;

import java.util.List;

import com.baolei.ghost.dal.dataobject.StockDO;

public interface DataParser {
	/**
	 * 解析数据文件格式
	 * @param code
	 * @return
	 */
	List<StockDO> parse(String code);
	String getStockName(String code);
	/**
	 * 取最新的股票数据
	 * @param code
	 * @return
	 */
	StockDO getLastPrice(String code);
	
	/**
	 * 取得股票数据的文件路径
	 * @return
	 */
	String getStockPath();
}
