package com.baolei.ghost.dal.dataobject;

import java.util.Comparator;

public class StockRiseComparator implements Comparator<StockDO> {

	@Override
	public int compare(StockDO o1, StockDO o2) {
		if(o1.getRise() > o2.getRise()){
			return 1;
		}else{
			return 0;
		}
	}

}
