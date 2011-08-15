package com.baolei.ghost.dal.daointerface;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.dataobject.StockDO;


public interface StockDAO {
    Integer insertStock(StockDO record);
    
    void insertStocks(List<StockDO> stockList);

    int updateStockById(StockDO record);

    int updateStockByIdSelective(StockDO record);

    StockDO selectStockById(Integer id);
    
    List<StockDO> selectStockByCodeAndPeriod(String code,String period);

    int deleteStockById(Integer id);
    
    int deleteStockByCode(String code);

	void updateStocksByIdBatch(List<StockDO> stockList);
}