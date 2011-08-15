package com.baolei.trade.web.manage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private StockDAO stockDAO;

	@RequestMapping(params = "m=manage")
	public String manage(HttpServletRequest request, ModelMap model) {
		String code = request.getParameter("code");
		String ma = request.getParameter("ma");
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		stockList = maManage(stockList,code,ma);
		stockDAO.updateStocksByIdBatch(stockList);
		return "manage/manage_price_data";
	}
	
	public List<StockDO> maManage(List<StockDO> stockList,String code ,String ma){
		Map<String, StockDO> stockMap = StockUtil.toStockMap(stockList);
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for(StockDO stockDO : stockList){
			log.info(dateFormat.format(stockDO.getTime()));
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
		return stockList;
	}
	
	@RequestMapping(params = "m=index")
	public String index() {
		return "manage/manage_price_data";
	}

}
