package com.baolei.ghost.dal.daointerface;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baolei.ghost.dal.dataobject.PriceDO;


public interface PriceDAO {
    Integer insertPrice(PriceDO record);
    
    void insertPrices(List<PriceDO> priceList);

    int updatePriceById(PriceDO record);

    int updatePriceByIdSelective(PriceDO record);

    PriceDO selectPriceById(Integer id);
    
    List<PriceDO> selectPriceByCodeAndPeriod(String code,String period);

    int deletePriceById(Integer id);
    
    int deletePriceByCode(String code);

	void updatePricesByIdBatch(List<PriceDO> priceList);
}