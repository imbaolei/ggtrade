package com.baolei.ghost.app;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.baolei.ghost.AccountDO;
import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.plan.Plan;
import com.baolei.ghost.plan.hg.HGPlan;

public class TradeFile {

	private static String code = "SZ000848";

	private static float amount = 1000000;

	public static void main(String[] args) throws IOException, ParseException {
		System.out.println(new Date());
		String folder = code.substring(0, 2);
		String tofile = InitStockFile.toFilePath + folder.toLowerCase() + "/" + code + ".txt";
		DataPool dataPool = StockUtil.initToStockPool(tofile);
		AccountDO accountDO = new AccountDO(amount);
		
		Plan plan = new HGPlan(dataPool);
		for (StockDO stockDO : dataPool.getDayStockList()) {
			if (plan.canTrade(stockDO.getTime())) {
				plan.execute(stockDO, accountDO);
			}

		}
		float total = accountDO.getMoney() + plan.getTotalNum()*plan.getStopLoss() + plan.getBalance();
		System.out.println("最后账户总额为"+total);
		String statuss = "";
		int justwin = 0;
		int stoploss = 0 ;
		for(String status : plan.getExitStatus()){
			statuss = statuss+status + ";";
			if(Constant.EXIT_STATUS_JUSTWIN.equals(status)){
				justwin++;
			}
			if(Constant.EXIT_STATUS_STOPLOST.equals(status)){
				stoploss++;
			}
		}
		System.out.println("交易退出状态："+statuss);
		System.out.println("止赢次数:"+justwin+"; 止损次数:"+stoploss);
		System.out.println(new Date());
	}
}
