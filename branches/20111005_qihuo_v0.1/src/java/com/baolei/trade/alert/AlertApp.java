package com.baolei.trade.alert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.baolei.trade.alert.domian.AlertConfig;
import com.baolei.trade.alert.domian.AlertStockDO;
import com.baolei.trade.util.HttpHelper;



public class AlertApp {

	static String startTimeString = "9:00:00";

	static String endTimeString = "16:00:00";

	public static void main(String[] args) throws ParseException,
			InterruptedException {
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat miaoFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date today = Calendar.getInstance().getTime();
		String todayString = dayFormat.format(today);
		Date startTime = miaoFormat.parse(todayString + " " + startTimeString);
		Date endTime = miaoFormat.parse(todayString + " " + endTimeString);
		Date now = new Date();

		List<AlertStockDO> stocks = AlertStockLoader.getStockAlertConfigs();
		HttpHelper hh = new HttpHelper();
		QQStockHelper qqHelper = new QQStockHelper();
		while (now.after(startTime) && now.before(endTime)) {
			String url = qqHelper.getQqStockUrl(stocks);
			String response = hh.getUrl(url);
			stocks = qqHelper.qqStockPaser(response, stocks);
			alert(stocks);
			Thread.sleep(10000);
			now = new Date();
		}
	}

	public static void alert(List<AlertStockDO> stocks) {
		// 检查stock list ，检验哪些需要alert，把需要alert的加入到shouldAlertList
		for (AlertStockDO stock : stocks) {
			List<AlertConfig> shouldAlertList = new ArrayList<AlertConfig>();
			for (AlertConfig ac : stock.getAlertConfigs()) {
				// 如果是alert type 是超过多少 并且 没有 通知过 并且 这会儿的价格 大于设置价格
				if (ac.getType().equalsIgnoreCase("up")
						&& ac.getAlertCount() <= 0
						&& stock.getClose() >= ac.getPrice()) {
					// 加入之前再检查下，如果upAlert这会儿价格为15 但是alert价格有两个12 13，则alert
					// 13
					// for (AlertConfig actmp : shouldAlertList) {
					// // 后者大于前者，则remove前者
					// if (ac.getPrice() > actmp.getPrice()) {
					// shouldAlertList.remove(actmp);
					// }
					// }
					ac.setAlertCount(ac.getAlertCount() + 1);
					ac.setMsg(stock.getName() + "已经高于" + ac.getPrice());
					shouldAlertList.add(ac);
				} else if (ac.getType().equalsIgnoreCase("down")
						&& ac.getAlertCount() <= 0
						&& stock.getClose() <= ac.getPrice()) {
					// 加入之前再检查下，如果downAlert这会儿价格为15 但是alert价格有两个12 13，则alert 12
					// for (AlertConfig actmp : shouldAlertList) {
					// // 后者小于前者，则remove前者
					// if (ac.getPrice() < actmp.getPrice()) {
					// shouldAlertList.remove(actmp);
					// }
					// }
					ac.setAlertCount(ac.getAlertCount() + 1);
					ac.setMsg(stock.getName() + "已经低于" + ac.getPrice());
					shouldAlertList.add(ac);
				} else if (ac.getType().equalsIgnoreCase("up")
						&& ac.getAlertCount() > 0
						&& stock.getClose() < ac.getPrice()) {
					ac.setAlertCount(ac.getAlertCount() - 1);
					ac.setMsg(stock.getName() + "已经低于" + ac.getPrice());
					shouldAlertList.add(ac);
				} else if (ac.getType().equalsIgnoreCase("down")
						&& ac.getAlertCount() > 0
						&& stock.getClose() > ac.getPrice()) {
					ac.setAlertCount(ac.getAlertCount() - 1);
					ac.setMsg(stock.getName() + "已经高于" + ac.getPrice());
					shouldAlertList.add(ac);
				}
				
			}
			for (AlertConfig ac : shouldAlertList) {
				SmsHelper sh = new SmsHelper();
				 sh.send(ac.getMsg());
				System.out.println(ac.getMsg());
			}
			if(shouldAlertList.size() == 0 ){
				System.out.println(stock.getName() + "is no alert! 现在价格是 ：" + stock.getClose());
			}
		}
	}

}
