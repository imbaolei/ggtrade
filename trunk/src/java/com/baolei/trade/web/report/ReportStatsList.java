package com.baolei.trade.web.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.dal.daointerface.ReportStatsDAO;
import com.baolei.ghost.dal.dataobject.ReportStatsDO;

@Controller
@RequestMapping("/report/report_stats_list.do")
public class ReportStatsList {
	
	@Autowired
	private ReportStatsDAO reportStatsDAO;
	
	@RequestMapping(params = "m=listall")
	public String listall(HttpServletRequest request, ModelMap model) {
		Map param = new HashMap();
		List<ReportStatsDO> reportStatsList = reportStatsDAO.seleteReportStatsByConditions(param);
		model.addAttribute("reportStatsList", reportStatsList);
		return "report/report_stats_list";
	}

	@RequestMapping("m=index")
	public String index() {
		return "report/report_stats_list";
	}

}
