package com.baolei.trade.test.trend.ma;


/**
 * 一次投入 无定投 追随趋势
 * @author baolei
 *
 */
public class Test3MaTrendOne extends Test3MaTrendDT{
	@Override
	public boolean needDingTou(String dateString) {
		return false;
	}

	@Override
	public void dingTou(String dateString) {
	}
	
	
	
}
