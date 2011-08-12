package com.baolei.trade.web.manage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;

@Controller
@RequestMapping("/manage/manage_price_data.do")
public class ManagePriceData {
	
	@Autowired
	private StockDAO stockDAO;

	@RequestMapping(params = "m=manage")
	public String manage(HttpServletRequest request, ModelMap model) {
		String code = request.getParameter("code");
		String ma = request.getParameter("ma");
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		Map<String, StockDO> stockMap = StockUtil.toStockMap(stockList);
		for(StockDO stockDO : stockList){
			JSONObject json = new JSONObject();
			if(StringUtils.isNotEmpty(ma)){
				String[] mas = ma.split(",");
				for(String tmpma : mas){
					float floatMa = StockUtil.MA(stockMap, stockDO.getTime(), Integer.parseInt(tmpma));
					json.put(tmpma, floatMa);
				}
			}
			stockDO.setMa(json.toString());
		}
		
		return "manage/manage_price_data";
	}
	
	@RequestMapping(params = "m=index")
	public String index() {
		return "manage/manage_price_data";
	}

}
