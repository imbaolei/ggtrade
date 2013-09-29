package com.baolei.trade.alert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.baolei.trade.alert.domian.AlertConfig;
import com.baolei.trade.alert.domian.AlertStockDO;



public class AlertStockLoader {

	public static List<AlertStockDO> getStockAlertConfigs() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("stock_alert_configs.txt");
		BufferedReader reader;
		List<AlertStockDO> stocks = new ArrayList<AlertStockDO>();
		try {
			reader = new BufferedReader(new FileReader(url.getFile()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tmps = line.split(",");
				AlertStockDO stockDO = new AlertStockDO();
				for (String tmp : tmps) {
					String[] configSplits = tmp.split(" ");
					if (configSplits[0].equalsIgnoreCase("code")) {
						stockDO.setCode(configSplits[1]);
					} else if (configSplits[0].equalsIgnoreCase("up")) {
						AlertConfig ac = new AlertConfig();
						ac.setType("up");
						ac.setPrice(Float.parseFloat(configSplits[1]));
						stockDO.getAlertConfigs().add(ac);
					} else if (configSplits[0].equalsIgnoreCase("down")) {
						AlertConfig ac = new AlertConfig();
						ac.setType("down");
						ac.setPrice(Float.parseFloat(configSplits[1]));
						stockDO.getAlertConfigs().add(ac);
					}
				}
				stocks.add(stockDO);
			}
			return stocks;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stocks;
	}
	
//	public static List<StockDO> getStocks(){
		
//	}
}
