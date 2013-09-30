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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.PriceDO;

@Service("txdFileParser")
public class TxdFileParser implements DataParser {
	protected Log log = LogFactory.getLog(getClass());

	private String filePath = "D:/java/project/data/tdx/";
	private String dateFormatString = "yyyy/MM/dd";

	public List<String> reader(String code, int num) {
		List<String> stockList = new ArrayList<String>();
		String filePath = this.filePath + code + ".txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "GBK"));
			String temp = null;

			if (num > 0) {
				for (int i = 0; i < num; i++) {
					temp = br.readLine();
					stockList.add(temp);
				}
			} else {
				temp = br.readLine();
				while (temp != null) {
					stockList.add(temp);
					temp = br.readLine();
				}
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
	public List<PriceDO> parse(String code) {
		List<PriceDO> priceDOList = new ArrayList<PriceDO>();
		List<String> sDataList = reader(code, 0);
		for (String temp : sDataList) {
			// 如果这行数据没有 小数点 则不是价格数据
			if (!temp.contains(".")) {
				continue;
			}
			PriceDO stockDO = parseDataLine(temp);
			stockDO.setCode(code);
			priceDOList.add(stockDO);
		}
		return priceDOList;
	}

	private PriceDO parseDataLine(String dataLine) {
		String[] data = dataLine.split(" ");
		// [0]日期 [1]open [2]high; [3]low [4]close [5] vol
		PriceDO stockDO = new PriceDO();
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		try {
			Date date = dateFormat.parse(data[0]);
			stockDO.setTime(date);
			stockDO.setOpen(Float.parseFloat(data[1]));
			stockDO.setHigh(Float.parseFloat(data[2]));
			stockDO.setLow(Float.parseFloat(data[3]));
			stockDO.setClose(Float.parseFloat(data[4]));
			stockDO.setVol(Float.parseFloat(data[5]));
			stockDO.setPeriod(Constant.STOCK_PERIOD_DAY);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return stockDO;
	}

	@Override
	public String getStockName(String code) {
		List<String> sDataList = reader(code, 1);
		String stockName = "";
		for (String tmp : sDataList) {
			stockName = tmp.split(" ")[1];
		}
		return stockName;
	}

	@Override
	public PriceDO getLastPrice(String code) {
		List<String> sDataList = reader(code, 0);
		String lastLine = sDataList.get(sDataList.size()-1);
		PriceDO stockDO = parseDataLine(lastLine);
		return stockDO;
	}

	@Override
	public String getStockPath() {
		return filePath;
	}

}
