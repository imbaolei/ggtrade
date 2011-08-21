package com.baolei.trade.test.trend.kong.ma;

public class Test3MaTrendKongTouCunOne extends Test3MaTrendKongTouCunDT {
	@Override
	public boolean needDingTou(String dateString) {
		return false;
	}

	@Override
	public void dingTou(String dateString) {
	}
}
