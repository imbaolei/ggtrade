package com.baolei.oyao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 读取货号
 * 
 * @author B
 * 
 */
public class HebingHuohao {

//	private static String path11 = "c:/10-23石家庄上衣库存.xls"; // 读取sjz的fordoo
//	private static String path12 = "c:/10-23石家庄裤子库存.xls"; // 读取sjz的fordoo
//	private static String path2 = "c:/foodor_wuhan_10.11.xls";// 读取wh的fordoo
	private static String path11 = "c:/cele库存10-23.xls"; // 读取cele
	private static String path12 = ""; 
	private static String path2 = ""; 
	private static String endpath = "c:/end.xlsx";

	public static Map<String, Goods> readExcel(String path, int noCellNum, int numCellNum,
			int priceCellNum) {
		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
		if(path.equalsIgnoreCase("")){
			return sourceGoodsMap;
		}
		
		try {
			HSSFWorkbook xwb = new HSSFWorkbook(new FileInputStream(path));
			HSSFSheet sheet = xwb.getSheetAt(0);
			
			// 取得货号和价格
			for (int i = sheet.getFirstRowNum(); i <= sheet
					.getPhysicalNumberOfRows(); i++) {
				HSSFRow row1 = sheet.getRow(i);
				if (row1 == null) {
					continue;
				}
				HSSFCell noCell = row1.getCell(noCellNum);
				HSSFCell numCell = row1.getCell(numCellNum);
				HSSFCell priceCell = row1.getCell(priceCellNum);

				// 没有货号，或者有货号没有价格，说明不是商品
				if (noCell == null || priceCell == null
						|| priceCell.getCellType() != 0) {
					continue;
				}

				String no = noCell.getStringCellValue();
				Float price = new Float(priceCell.getNumericCellValue());
				Float num = new Float(numCell.getNumericCellValue());
				Goods theGoods = sourceGoodsMap.get(no);
				// 去重
				if (theGoods != null) {
					theGoods.setNum(num + theGoods.getNum());
				} else {
					Goods goods = new Goods();
					goods.setNo(no);
					goods.setNum(num);
					goods.setPrice(price);
					sourceGoodsMap.put(no, goods);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sourceGoodsMap;
	}
	
	public static List<Goods> setGoodsMapToList(List<Goods> goodsList,Map<String, Goods> goodsMap){
		List<Goods> newGoodsList = new ArrayList<Goods>();
		for(Goods sourceGoods : goodsList){
			Goods goods = goodsMap.get(sourceGoods.getNo());
			if(goods == null){
				System.out.println(sourceGoods.getNo() + "找不到!");
			}else{
				newGoodsList.add(goods);
			}
			
		}
		return newGoodsList;
	}
	
	public static List<Goods> readHuohaoExcel(String path) {
	    List<Goods> sourceGoodsList = new ArrayList<Goods>();
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(path));
			XSSFSheet sheet = xwb.getSheetAt(0);
			// 取得第一个表的货号
			for (int i = sheet.getFirstRowNum(); i <= sheet
					.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				Goods goods = new Goods();
				goods.setNo(row.getCell(0).toString().trim());
				sourceGoodsList.add(goods);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sourceGoodsList;
	}
	
	
	
	public static Map<String, Goods> hebing(Map<String, Goods> map1,Map<String, Goods> map2){
		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
		for (Entry<String, Goods> entry1 : map1.entrySet()) {
			String goodsNo1 = entry1.getKey();
			Goods goods1 = entry1.getValue();
			Goods theGoods = sourceGoodsMap.get(goodsNo1);
			if( theGoods != null){
				theGoods.setNum(theGoods.getNum() + goods1.getNum());
			}else{
				sourceGoodsMap.put(goodsNo1, goods1);
			}
		}
		for (Entry<String, Goods> entry2 : map2.entrySet()) {
			String goodsNo2 = entry2.getKey();
			Goods goods2 = entry2.getValue();
			Goods theGoods = sourceGoodsMap.get(goodsNo2);
			if( theGoods != null){
				theGoods.setNum(theGoods.getNum() + goods2.getNum());
			}else{
				sourceGoodsMap.put(goodsNo2, goods2);
			}
		}
		return sourceGoodsMap;
	}

	public static void main(String[] args) throws IOException {
//		// 读取第一个系列sjz上衣 0 26 28 sjz裤子 0 27 29
//		Map<String, Goods> sourceGoodsMap11 =readExcel(path11,0,26,28);
//		Map<String, Goods> sourceGoodsMap12 =readExcel(path12,0,27,29);
//		//读取第二个系列wh  1 23 24
//		Map<String, Goods> sourceGoodsMap2 =readExcel(path2,1,23,24);
		
		// 读取第一个系列cele库存2 19 20 sjz裤子 0 27 29
		Map<String, Goods> sourceGoodsMap11 =readExcel(path11,2,19,20);
		Map<String, Goods> sourceGoodsMap12 =readExcel(path12,0,27,29);
		Map<String, Goods> sourceGoodsMap2 =readExcel(path2,1,23,24);
		
		Map<String, Goods> sourceGoodsMap1 = hebing(sourceGoodsMap11,sourceGoodsMap12);
		Map<String, Goods> sourceGoodsMapEnd = hebing(sourceGoodsMap1,sourceGoodsMap2);
		
		List<Goods> goodsList = new ArrayList<Goods>();
		int xiaoyu5 = 0 ;
		for (Entry<String, Goods> entry2 : sourceGoodsMapEnd.entrySet()) {
			Goods goods2 = entry2.getValue();
			if (goods2.getNum() >= 3) {
				goodsList.add(goods2);
			}else{
				xiaoyu5++;
			}
		}
		writeFile(goodsList);
		System.out.println(xiaoyu5);

	}

	public static void writeFile(List<Goods> goodsList) {
		OutputStream os;
		try {
			os = new FileOutputStream(endpath);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("sheet1");
			int i = 0;
			for (Goods goods : goodsList) {
				XSSFRow row = sheet.createRow(i);
				System.out.println(goods.getNo());
				row.createCell(0).setCellValue(goods.getNo());
				row.createCell(1).setCellValue(goods.getNum());
				row.createCell(2).setCellValue(goods.getPrice());
				i++;
			}
			wb.write(os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
