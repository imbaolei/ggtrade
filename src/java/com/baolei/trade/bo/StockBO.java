package com.baolei.trade.bo;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.baolei.ghost.dal.dataobject.StockDO;


@Service("stockBO")
public class StockBO {
	
	protected Log log = LogFactory.getLog(getClass());

	public List<StockDO> getStockList() {
		return null;
		
	}
}
