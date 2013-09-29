package com.baolei.trade.bo;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baolei.ghost.app2.DataParser;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

@Service("stockBO")
public class StockBO {

	@Autowired
	@Qualifier("txdFileParser")
	DataParser dataParser;

	@Autowired
	StockUtil stockUtil;

	DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
	protected Log log = LogFactory.getLog(getClass());

	public List<StockDO> getStockListByFile(String code) {
		List<StockDO> stockList = dataParser.parse(code);
		return stockList;
	}
	
	
	public List<String> getAllCodes(String filePath) {
		File file = new File(filePath);
		List<String> fileList = new ArrayList<String>();
		String[] files = file.list();
		System.out.println(files.length);
		for (String filename : files) {
			filename = filename.replace(".TXT", "");
			fileList.add(filename);
		}
		return fileList;
	}
	
	/**
	 * 转换为 0~100之间的一个数字
	 * @param index
	 * @param length
	 * @return
	 */
	public int convertRank(int index,int length){
		float rankfloat = index/length;
		int rankint = 0;
		if(length > 100){
			rankint = 100 - (int) rankfloat;
		}else{
			rankint = 100 - (int)rankfloat*100;
		}
		return rankint;
	}

	/**
	 * 计算 从startDateString 到 endDateString周期内的 ma均线的 在 period周期 内的涨跌幅
	 * 
	 * @param stockList
	 * @param ma
	 * @param period
	 * @param startDateString
	 * @param endDateString
	 * @return
	 */
	public List<StockDO> calStockListMaRise(List<StockDO> stockList, String ma,
			int period, String startDateString, String endDateString) {
		try {
			Date startDate = dateFormat.parse(startDateString);
			Date endDate = dateFormat.parse(endDateString);
			// 计算涨幅
			for (StockDO stockDO : stockList) {
				if (stockDO.getTime().after(startDate)
						&& stockDO.getTime().before(endDate)) {
					StockDO beforeStockDO = calBeforeTime(stockList, stockDO,
							period);
					float rise = calMaRise(beforeStockDO, stockDO, ma);
					stockDO.setRise(rise);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockList;
	}

	public StockDO calStockMaRise(List<StockDO> stockList, StockDO stockDO,
			String ma, int period) {
		StockDO beforeStockDO = calBeforeTime(stockList, stockDO, period);
		float rise = calMaRise(beforeStockDO, stockDO, ma);
		stockDO.setRise(rise);
		return stockDO;
	}

	private StockDO calBeforeTime(List<StockDO> stockList, StockDO stockDO,
			int period) {
		int index = stockList.indexOf(stockDO);
		if (index - period > 0) {
			return stockList.get(index - period);
		}
		return null;
	}

	private float calMaRise(StockDO beforeStockDO, StockDO afterStockDO,
			String ma) {
		if (beforeStockDO != null && afterStockDO != null) {
			log.info(beforeStockDO.getCode() + " "
					+ dateFormat.format(beforeStockDO.getTime()) + " "
					+ beforeStockDO.getMa());
			if (beforeStockDO.getMa(ma) <= 0 || afterStockDO.getMa(ma) <= 0) {
				return 0;
			}
			float beforePrice = beforeStockDO.getMa(ma);
			float afterPrice = afterStockDO.getMa(ma);
			float rise = (afterPrice - beforePrice) / beforePrice * 100;
			rise = NumberUtil.roundDown(rise, 2);
			return rise;
		}
		return 0;
	}

	public List<StockDO> initStockListMa(List<StockDO> stockList, String ma) {
		if (StringUtils.isEmpty(ma)) {
			ma = "20,25,60,90,200,350";
		}

		for (StockDO stockDO : stockList) {
			// log.info(dateFormat.format(stockDO.getTime()));
			JSONObject json = new JSONObject();
			if (StringUtils.isNotEmpty(ma)) {
				String[] mas = ma.split(",");
				for (String tmpma : mas) {
					float floatMa = StockUtil.MA(stockList, stockDO,
							Integer.parseInt(tmpma));
					json.put(tmpma, floatMa);
				}
			}
			stockDO.setMa(json.toString());
		}
		return stockList;
	}

	public List<StockDO> initStockListAtr(List<StockDO> stockList, int time) {
		if (time <= 0) {
			time = 20;
		}
		for (StockDO stockDO : stockList) {
			stockDO.setAtr(stockUtil.atr(stockList, stockDO, time));
		}
		return stockList;
	}
}
