package com.baolei.trade.web.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.bo.StockBO;

@Controller
@RequestMapping("/report/compare_stocks.do")
public class CompareStocks {
	protected Log log = LogFactory.getLog(getClass());

	@Autowired 
	private StockBO stockBO;

	private StockUtil stockUtil;

	private int period = 200;
	
	private Integer ma  =20;

	DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);

	String startDateString = "2005/01/01";
	
	String endDateString = "2007/01/01";

	String szStockCode = "SH999999";
	
	String codes = "SH999999,SZ399005,SZ399006,SZ399001";

	@RequestMapping(params = "m=compare")
	public String compare(HttpServletRequest request, ModelMap model) {
		Map<String, Map<String, Float>> codeDateRiseMap = new TreeMap<String, Map<String, Float>>();
		List<String> codeList = new ArrayList<String>();
		List<String> dateList = new ArrayList<String>();
		String returntype = request.getParameter("returntype");
		String risetype = request.getParameter("risetype");
		try {
			if (StringUtils.isNotEmpty(codes)) {
				Date startDate = dateFormat.parse(startDateString);
				Date endDate = dateFormat.parse(endDateString);
				for (String code : codes.split(",")) {
					List<StockDO> stockList = stockBO.getStockListByFile(code);
					stockList = stockBO.initStockListMa(stockList, ma.toString());
					codeList.add(code);
					Map<String, Float> dateRiseMap = new TreeMap<String, Float>();

					
					// 计算涨幅
					for (StockDO stockDO : stockList) {
						if (stockDO.getTime().after(startDate) && stockDO.getTime().before(endDate)) {
							StockDO beforeStockDO = calBeforeTime(stockList,
									stockDO);
							float rise = 0;
							if(StringUtils.isNotEmpty(risetype)&&"ma".equalsIgnoreCase(risetype)){
								rise = calMaRise(beforeStockDO, stockDO);
							}else{
								rise = calRise(beforeStockDO, stockDO);
							}
							
							String dateString = dateFormat.format(stockDO
									.getTime());
							dateRiseMap.put(dateString, rise);
						}
					}
					
					codeDateRiseMap.put(code, dateRiseMap);
				}
				// 根据上证指数 计算 时间
				List<StockDO> szStockList = stockBO
						.getStockListByFile(szStockCode);
				for (StockDO stockDO : szStockList) {
					if (stockDO.getTime().after(startDate) && stockDO.getTime().before(endDate)) {
						String dateString = dateFormat.format(stockDO
								.getTime());
						dateList.add(dateString);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		
		model.addAttribute("codeList", codeList);
		model.addAttribute("dateList", dateList);
		model.addAttribute("codeDateRiseMap", codeDateRiseMap);
		if(StringUtils.isNotEmpty(returntype) && "xml".equalsIgnoreCase(returntype)){
			return "report/compare_stocks_xml";
		}else{
			return "report/compare_stocks";
		}
		
	}
	
	/**
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "m=compareAll")
	public String compareList(HttpServletRequest request, ModelMap model) {
		String filePath = "D:/java/project/data/tdx/";
		List<String> codeList = stockBO.getAllCodes(filePath);
		List<List<StockDO>> stockListList = new ArrayList<List<StockDO>>();
		for(String code : codeList){
			List<StockDO> stockList = stockBO.getStockListByFile(code);
			stockListList.add(stockList);
		}
		for(List<StockDO> stockList : stockListList){
			stockBO.calStockListMaRise(stockList, ma.toString(), period, startDateString, endDateString);
		}
		return codes;
		
	}
	
	

	private float calRise(StockDO beforeStockDO, StockDO afterStockDO) {
		if (beforeStockDO != null && afterStockDO != null) {
			float beforePrice = beforeStockDO.getClose();
			float afterPrice = afterStockDO.getClose();
			float rise = (afterPrice - beforePrice) / beforePrice * 100;
			rise = NumberUtil.roundDown(rise, 2);
			return rise;
		}
		return 0;
	}
	
	private float calMaRise(StockDO beforeStockDO, StockDO afterStockDO) {
		if (beforeStockDO != null && afterStockDO != null ) {
			log.info(beforeStockDO.getCode() + " "+ dateFormat.format(beforeStockDO.getTime()) + " " +beforeStockDO.getMa());
			if(beforeStockDO.getMa(ma.toString()) <= 0 || afterStockDO.getMa(ma.toString()) <= 0){
				return 0;
			}
			float beforePrice = beforeStockDO.getMa(ma.toString());
			float afterPrice = afterStockDO.getMa(ma.toString());
			float rise = (afterPrice - beforePrice) / beforePrice * 100;
			rise = NumberUtil.roundDown(rise, 2);
			return rise;
		}
		return 0;
	}

	private StockDO calBeforeTime(List<StockDO> stockList, StockDO stockDO) {
		int index = stockList.indexOf(stockDO);
		if (index - period > 0) {
			return stockList.get(index - period);
		}
		return null;
	}

}
