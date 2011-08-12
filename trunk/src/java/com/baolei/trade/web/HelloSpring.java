package com.baolei.trade.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.thoughtworks.xstream.XStream;

@Controller
public class HelloSpring {

	@Autowired
	private StockDAO stockDAO;

	@RequestMapping("/hellospring.do")
	public String hello(ModelMap model) {
		String code = "SZ399300";
//		DataPool toDataPool = StockUtil.initStockPool("SZ399300");
		// JsonConfig jsonConfig = new JsonConfig();
		// jsonConfig.registerJsonValueProcessor(Date.class,
		// new DateJsonValueProcessor());
		// JSONArray ja =
		// JSONArray.fromObject(toDataPool.getDayStockList(),jsonConfig);
		XStream xstream = new XStream();
		xstream.alias("stock", StockDO.class);
		xstream.alias("stocks", List.class);
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		String xml = xstream.toXML(stockList);
		model.addAttribute("spring", xml);
		return "hellospring";
	}

}
