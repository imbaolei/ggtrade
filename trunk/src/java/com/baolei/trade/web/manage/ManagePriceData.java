package com.baolei.trade.web.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.bo.TradeBO;

@Controller
@RequestMapping("/manage/manage_price_data.do")
public class ManagePriceData {
	protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private StockDAO stockDAO;
	
	@Autowired
	private TradeBO tradeBO;

	@RequestMapping(params = "m=manage")
	public String manage(HttpServletRequest request, ModelMap model) {
		String code = request.getParameter("code");
		String ma = request.getParameter("ma");
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		stockList = tradeBO.maManage(stockList,ma);
		stockDAO.updateStocksByIdBatch(stockList);
		return "manage/manage_price_data";
	}
	
	
	
	@RequestMapping(params = "m=index")
	public String index() {
		return "manage/manage_price_data";
	}

}
