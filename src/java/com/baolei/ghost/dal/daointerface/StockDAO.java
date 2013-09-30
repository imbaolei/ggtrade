package com.baolei.ghost.dal.daointerface;

import java.util.List;

import com.baolei.ghost.dal.dataobject.StockDO;

public interface StockDAO {
	
	StockDO selectStockById(Integer id);
	
	StockDO selectStockByCode(String code);
	
	List<StockDO> selectAllStock();
	
	int deleteStockById(Integer id);
	
	int deleteStockByCode(String code);
	
	Integer insertStock(StockDO record);
    
    void insertStocks(List<StockDO> stockList);

    int updateStockById(StockDO record);

    int updateStockByIdSelective(StockDO record);

	void updateStocksByIdBatch(List<StockDO> stockList);
}
