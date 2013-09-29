package com.baolei.trade.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baolei.ghost.app2.TxdFileParser;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;

@Service("tradeBO")
public class TradeBO {
	
	@Autowired
	TxdFileParser txdFileParser;
	
	protected Log log = LogFactory.getLog(getClass());
	
	public List<PriceDO> getStockListByFile(String code){
		List<PriceDO> stockList = txdFileParser.parse(code);
		return stockList;
	}
	
	public List<PriceDO> initStockListMa(List<PriceDO> stockList){
		return stockList;
		
	}
	
	
	
	public List<PriceDO> maManage(List<PriceDO> stockList,String ma){
		DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
		for(PriceDO stockDO : stockList){
//			log.info(dateFormat.format(stockDO.getTime()));
			JSONObject json = new JSONObject();
			if(StringUtils.isNotEmpty(ma)){
				String[] mas = ma.split(",");
				for(String tmpma : mas){
					float floatMa = PriceUtil.MA(stockList, stockDO, Integer.parseInt(tmpma));
					json.put(tmpma, floatMa);
				}
			}
			stockDO.setMa(json.toString());
		}
		return stockList;
	}

}
