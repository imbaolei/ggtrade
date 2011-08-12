package com.baolei.ghost.app2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;

public class TxdFileParser implements DataParser {

	private String filePath = "c:/tmp/";

	@Override
	public List<String> reader(String code) {
		List<String> stockList = new ArrayList<String>();
		filePath = filePath + code + ".txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "GBK"));
			String temp = null;
			temp = br.readLine();
			while (temp != null) {
				stockList.add(temp);
				temp = br.readLine();
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stockList;
	}

	@Override
	public List<StockDO> parse(String code) {
		List<StockDO> stockDOList = new ArrayList<StockDO>();
		List<String> sDataList  = reader(code);
		for (String temp : sDataList) {
			String[] data = temp.split(" ");
			// [0]日期 [1]open [2]high; [3]low [4]close [5] vol
			StockDO stockDO = new StockDO();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date;
			try {
				date = dateFormat.parse(data[0]);
				stockDO.setTime(date);
				stockDO.setOpen(Float.parseFloat(data[1]));
				stockDO.setHigh(Float.parseFloat(data[2]));
				stockDO.setLow(Float.parseFloat(data[3]));
				stockDO.setClose(Float.parseFloat(data[4]));
				stockDO.setVol(Float.parseFloat(data[5]));
				stockDO.setPeriod(Constant.STOCK_PERIOD_DAY);
				stockDO.setCode(code);
				stockDOList.add(stockDO);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return stockDOList;
	}

}
