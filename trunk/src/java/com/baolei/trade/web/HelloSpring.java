package com.baolei.trade.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.thoughtworks.xstream.XStream;

@Controller
public class HelloSpring {

	@RequestMapping("/hellospring.html")
	public String hello(ModelMap model) {
		DataPool toDataPool = StockUtil.initStockPool("SH600000");
		// JsonConfig jsonConfig = new JsonConfig();
		// jsonConfig.registerJsonValueProcessor(Date.class,
		// new DateJsonValueProcessor());
		// JSONArray ja =
		// JSONArray.fromObject(toDataPool.getDayStockList(),jsonConfig);
		XStream xstream = new XStream();
		xstream.alias("stock", StockDO.class);
		xstream.alias("stocks", List.class);

		String xml = xstream.toXML(toDataPool.getDayStockList());
		model.addAttribute("spring", xml);
		return "hellospring";
	}

}
