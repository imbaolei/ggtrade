package com.baolei.trade.test.trend.ma;

public class Test3MaTrend extends Test3MaTrendDingTou{
	@Override
	public boolean needDingTou(String dateString) {
		return false;
	}

	@Override
	public void dingTou(String dateString) {
	}
}
