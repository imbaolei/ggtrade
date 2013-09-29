package com.baolei.ghost.app2;

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

@Service("dzhTxtParser")
public class DzhTxtParser implements DataParser{
	
	protected Log log = LogFactory.getLog(getClass());

	private String filePath = "D:/java/project/data/dzh/txt/";
	private String dateFormatString = "yyyy-MM-dd";

	@Override
	public List<PriceDO> parse(String code) {
		List<PriceDO> stockDOList = new ArrayList<PriceDO>();
		List<String> sDataList = ParserUtil.reader(filePath,code, 0);
		for (String temp : sDataList) {
			// 如果这行数据有中文 时间  则不是价格数据
			if (!temp.contains(".")) {
				continue;
			}
			PriceDO stockDO = parseDataLine(temp);
			stockDO.setCode(code);
			stockDOList.add(stockDO);
		}
		return stockDOList;
	}
	
	private PriceDO parseDataLine(String dataLine) {
		String[] data = dataLine.split("\\t");
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
		
		return code;
	}

	@Override
	public PriceDO getLastPrice(String code) {
		List<String> sDataList = ParserUtil.reader(filePath,code, 0);
		String lastLine = sDataList.get(sDataList.size()-1);
		PriceDO stockDO = parseDataLine(lastLine);
		return stockDO;
	}

	@Override
	public String getStockPath() {
		return filePath;
	}

}
