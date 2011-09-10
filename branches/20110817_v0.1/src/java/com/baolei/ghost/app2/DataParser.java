package com.baolei.ghost.app2;

import java.util.List;

import com.baolei.ghost.dal.dataobject.StockDO;

public interface DataParser {
	List<StockDO> parse(String code);
}
