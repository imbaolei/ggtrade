package com.baolei.trade.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

@Service("tradeBO")
public class TradeBO {
	
	protected Log log = LogFactory.getLog(getClass());
	
	
	
	public List<StockDO> maManage(List<StockDO> stockList,String ma){
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for(StockDO stockDO : stockList){
//			log.info(dateFormat.format(stockDO.getTime()));
			JSONObject json = new JSONObject();
			if(StringUtils.isNotEmpty(ma)){
				String[] mas = ma.split(",");
				for(String tmpma : mas){
					float floatMa = StockUtil.MA(stockList, stockDO, Integer.parseInt(tmpma));
					json.put(tmpma, floatMa);
				}
			}
			stockDO.setMa(json.toString());
		}
		return stockList;
	}

}
