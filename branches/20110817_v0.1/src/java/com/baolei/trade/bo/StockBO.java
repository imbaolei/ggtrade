package com.baolei.trade.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baolei.ghost.app2.DataParser;
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

	public List<StockDO> initStockListMa(List<StockDO> stockList, String ma) {
		if (StringUtils.isEmpty(ma)) {
			ma = "20,30,60,90,120";
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
	
	public List<StockDO> initStockListAtr(List<StockDO> stockList,int time){
		if(time <= 0){
			time = 20;
		}
		for (StockDO stockDO : stockList) {
			stockDO.setAtr(stockUtil.atr(stockList,stockDO, time));
		}
		return stockList;
	}
}
