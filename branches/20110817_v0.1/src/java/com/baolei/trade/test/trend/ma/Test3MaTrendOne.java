package com.baolei.trade.test.trend.ma;

public class Test3MaTrendOne extends Test3MaTrendDT{
	@Override
	public boolean needDingTou(String dateString) {
		return false;
	}

	@Override
	public void dingTou(String dateString) {
	}
}
