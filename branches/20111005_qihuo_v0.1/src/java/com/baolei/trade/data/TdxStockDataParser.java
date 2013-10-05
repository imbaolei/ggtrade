package com.baolei.trade.data;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
//				System.out.println(file + " "  + statisticsDO.getCode() + " " + statisticsDO.getName());
				if(row.getCell(3) != null ){
					//第四列:open
					statisticsDO.setOpen(Float.parseFloat(String.valueOf(row.getCell(3).getNumericCellValue())));
				}
				if(row.getCell(4) != null ){
					//第五列：high
					statisticsDO.setHigh(Float.parseFloat(String.valueOf(row.getCell(4).getNumericCellValue())));
				}
				if(row.getCell(5) != null ){
					//第六列:low
					statisticsDO.setLow(Float.parseFloat(String.valueOf(row.getCell(5).getNumericCellValue())));
				}
				if(row.getCell(6) != null ){
					//第七列:close
					statisticsDO.setClose(Float.parseFloat(String.valueOf(row.getCell(6).getNumericCellValue())));
				}
				if(row.getCell(7) != null ){
					//第八列:振幅
					statisticsDO.setChangeRate(Float.parseFloat(String.valueOf(row.getCell(7).getNumericCellValue())));
				}
				
				
				
				//计算强度
//				double tmp = (double)(i - 1)/(double)totalNum*100;
//				
//				int riseRank = (int) Math.floor(100- tmp);
//				statisticsDO.setRiseRank(riseRank);
				statisticsDO.setTime(time);
				statisticsList.add(statisticsDO);
				
			}
			
			statisticsList = jisuanStockRank(statisticsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statisticsList;
	}
	
	private List<StatisticsDO> jisuanStockRank(List<StatisticsDO> list){
		//排除 没有 上市的 list1
		List<StatisticsDO> list1 = new ArrayList<StatisticsDO>();
		for(StatisticsDO sd : list){
			if(sd.getOpen() > 0 || sd.getClose() > 0 || sd.getChangeRate() > 0){
				list1.add(sd);
			}
		}
		//根据 rise rate 排序
		Collections.sort(list1, new Comparator<StatisticsDO>() {
            public int compare(StatisticsDO arg0, StatisticsDO arg1) {
                return ((new Float(arg1.getRiseRate())).compareTo((new Float(arg0.getRiseRate()))));
            }
        });
		//计算 rise rank
		List<StatisticsDO> list2 = new ArrayList<StatisticsDO>();
		for(int i = 0 ; i < list1.size();i++ ){
			double tmp = (double)(i+1)/(double)list1.size()*100;
			
			int riseRank = (int) Math.floor(100- tmp);
			StatisticsDO sd = list1.get(i);
			sd.setRiseRank(riseRank);
			list2.add(sd);
		}
		return list2;
	}

}
