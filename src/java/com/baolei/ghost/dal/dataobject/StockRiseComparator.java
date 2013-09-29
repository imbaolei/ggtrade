package com.baolei.ghost.dal.dataobject;

import java.util.Comparator;

public class StockRiseComparator implements Comparator<PriceDO> {

	@Override
	public int compare(PriceDO o1, PriceDO o2) {
		if(o1.getRise() > o2.getRise()){
			return 1;
		}else{
			return 0;
		}
	}

}
