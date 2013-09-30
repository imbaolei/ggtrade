package com.baolei.trade.data;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StockDO;

@Service("tdxStockDataParser")
public class TdxStockDataParser implements StockDataParser {
	
	protected Log log = LogFactory.getLog(getClass());
	
	private String stockFilePath = "D:/tmp/stock/agu.xls";
	
	

	@Override
	public List<StockDO> getStockList() {
		List<StockDO> stockList = new ArrayList<StockDO>();
		HSSFWorkbook xwb;
		try {
			xwb = new HSSFWorkbook(new FileInputStream(stockFilePath));
			HSSFSheet sheet = xwb.getSheetAt(0);
			int numRow = sheet.getPhysicalNumberOfRows();
			int totalNum = numRow - 2 ;
			
			// 开始遍历
			for (int i = 1; i <= totalNum; i++) {
				HSSFRow row = sheet.getRow(i);
				StockDO stockDO = new StockDO();
				//第一列：代码  
				stockDO.setCode(String.valueOf(row.getCell(0).getStringCellValue()));
				//第二列：股票名称 
				stockDO.setName(row.getCell(1).getStringCellValue());
				// 第三列：细分行业
				stockDO.setIndustry(row.getCell(2).getStringCellValue());
				stockList.add(stockDO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockList;
	}

	@Override
	public List<StatisticsDO> getStatisticsListByFile(String file,Date time) {
		List<StatisticsDO> statisticsList = new ArrayList<StatisticsDO>();
		HSSFWorkbook xwb;
		try {
			xwb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = xwb.getSheetAt(0);
			  
			int numRow = sheet.getPhysicalNumberOfRows();
			int totalNum = numRow - 2; //去掉第一 、第二 和 最后一行 
			// 开始从第三行 开始遍历
			for (int i = 2; i <= totalNum; i++) {
				
				HSSFRow row = sheet.getRow(i);
				StatisticsDO statisticsDO = new StatisticsDO();
				//第一列：代码
				statisticsDO.setCode(String.valueOf(row.getCell(0).getStringCellValue()));
				//第二列：股票名称  
				statisticsDO.setName(row.getCell(1).getStringCellValue()); 
				if(row.getCell(2) != null){
					//第三列：涨幅
					statisticsDO.setRiseRate(Float.parseFloat(String.valueOf(row.getCell(2).getNumericCellValue())));
				}
				
				//第四列:open
				statisticsDO.setOpen(Float.parseFloat(String.valueOf(row.getCell(3).getNumericCellValue())));
				//第五列：high
				statisticsDO.setHigh(Float.parseFloat(String.valueOf(row.getCell(4).getNumericCellValue())));
				//第六列:low
				statisticsDO.setLow(Float.parseFloat(String.valueOf(row.getCell(5).getNumericCellValue())));
				//第七列:close
				statisticsDO.setClose(Float.parseFloat(String.valueOf(row.getCell(6).getNumericCellValue())));
				//第八列:振幅
				statisticsDO.setChangeRate(Float.parseFloat(String.valueOf(row.getCell(6).getNumericCellValue())));
				//计算强度
				double tmp = (double)(i - 1)/(double)totalNum*100;
				System.out.println(tmp);
				int riseRank = (int) Math.floor(100- tmp);
				statisticsDO.setRiseRank(riseRank);
				statisticsDO.setTime(time);
				statisticsList.add(statisticsDO);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statisticsList;
	}

}
