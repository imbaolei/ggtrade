package com.baolei.trade.web.report;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.trade.bo.ReportBO;

@Controller
@RequestMapping("/report/report_list.do")
public class ReportList {

	private int defaultPageSize = 40;
	
	@Autowired
	private ReportBO reportBO;

	@RequestMapping(params = "m=list")
	public String list(HttpServletRequest request, ModelMap model) {
		List<String> codes = reportBO.getCodesFromReport(0, defaultPageSize);
		Map<String, List<ReportDO>> reportMap = reportBO
				.listReportByCodes(codes);
		model.addAttribute("reportMap", reportMap);
		return "report/report_list";
	}

	@RequestMapping("m=index")
	public String index() {
		return "report/report_list";
	}

}
