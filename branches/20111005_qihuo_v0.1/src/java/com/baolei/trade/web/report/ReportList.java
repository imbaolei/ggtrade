package com.baolei.trade.web.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.trade.bo.ReportBO;

@Controller
@RequestMapping("/report/report_list.do")
public class ReportList {

	private int defaultPageSize = 100;
	
	@Autowired
	private ReportBO reportBO;
	
	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private PriceUtil stockUtil;

	@RequestMapping(params = "m=list")
	public String list(HttpServletRequest request, ModelMap model) {
		List<String> codes = reportBO.getCodesFromReport(0, defaultPageSize);
		Map<String, List<ReportDO>> reportMap = reportBO
				.listReportByCodes(codes);
//		Properties props  = stockUtil.getCodeProperties(); 
		model.addAttribute("reportMap", reportMap);
//		model.addAttribute("props", props);
		return "report/report_list";
	}
	
	@RequestMapping(params = "m=listall")
	public String listall(HttpServletRequest request, ModelMap model) {
		Map param = new HashMap();
		List<ReportDO> reportList = reportDAO.seleteReportsByConditions(param);
		Map<String, List<ReportDO>> reportMap = reportBO
				.listReportByReportList(reportList);
//		Properties props  = stockUtil.getCodeProperties(); 
		model.addAttribute("reportMap", reportMap);
//		model.addAttribute("props", props);
		return "report/report_list";
	}

	@RequestMapping("m=index")
	public String index() {
		return "report/report_list";
	}

}
