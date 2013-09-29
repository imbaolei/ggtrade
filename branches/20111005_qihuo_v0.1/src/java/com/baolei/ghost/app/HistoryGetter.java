package com.baolei.ghost.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.baolei.ghost.dal.dataobject.PriceDO;

public class HistoryGetter {

	private static final Logger logger = Logger.getLogger(HistoryGetter.class);
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) throws ParserException,
			ParseException {
		NodeFilter filter = new NodeClassFilter(Div.class);
		Parser parser = new Parser();
		parser
				.setURL("http://vip.stock.finance.sina.com.cn/q/view/vFutures_History.php?page=1&breed=c0909&start=2008-12-01&end=2009-02-28&jys=2&pz=2&hy=4&name=%B4%F3%B6%B90909");
		parser.setEncoding("GBK");
		NodeList list = parser.extractAllNodesThatMatch(filter);
		Div historyDiv = null;
		for (int i = 0; i < list.size(); i++) {
			Div node = (Div) list.elementAt(i);
			if ("historyList".equals(node.getAttribute("class"))) {
				historyDiv = node;
			}
		}
		parser = new Parser(historyDiv.toHtml());
		parser.setEncoding("GBK");
		filter = new NodeClassFilter(TableTag.class);
		list = parser.extractAllNodesThatMatch(filter);
		List<PriceDO> stockList = new ArrayList<PriceDO>();

		for (int i = 1; i <= list.size(); i++) {
			TableTag tag = (TableTag) list.elementAt(i);
			TableRow[] rows = tag.getRows();
			for (int j = 1; j <= rows.length; j++) {
				PriceDO stockDO = new PriceDO();
				TableRow tr = rows[j];
				TableColumn[] td = tr.getColumns();
				for (int k = 1; k <= td.length; k++) {
					try {
						Date date = dateFormat.parse(td[k - 1].getText());
					} catch (ParseException e) {
						// 如果日期解析出错,则不是期货数据
						continue;
					}
					if (k == 1) {
						// 日期
						stockDO.setTime(dateFormat.parse(td[k - 1].getText()));
					}
					if (k == 2) {
						// 收盘价
						stockDO.setClose(Float.parseFloat(td[k - 1].getText()));
					}
					if (k == 3) {
						// 开盘价
						stockDO.setOpen(Float.parseFloat(td[k - 1].getText()));
					}
					if (k == 4) {
						// 最高价
						stockDO.setHigh(Float.parseFloat(td[k - 1].getText()));
					}
					if (k == 5) {
						// 最低价
						stockDO.setLow(Float.parseFloat(td[k - 1].getText()));
					}
					if (k == 6) {
						// 成交量
						stockDO.setClose(Float.parseFloat(td[k - 1].getText()));
					}
				}
				stockList.add(stockDO);
			}
		}
		
		for(PriceDO stockDO : stockList){
			System.out.print(dateFormat.format(stockDO.getTime()));
		}
	}

}
