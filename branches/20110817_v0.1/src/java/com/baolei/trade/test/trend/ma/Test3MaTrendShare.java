package com.baolei.trade.test.trend.ma;

public class Test3MaTrendShare extends Test3MaTrendDingTouShare{
	@Override
	public boolean needDingTou(String dateString) {
		return false;
	}

	@Override
	public void dingTou(String dateString) {
	}
}
