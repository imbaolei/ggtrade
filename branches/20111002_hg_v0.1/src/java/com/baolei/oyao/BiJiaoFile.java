package com.baolei.oyao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author B
 * 
 *         -
 */
public class BiJiaoFile {

	private static String path1 = "c:/虎都10.19价格.xlsx";
	private static String path2 = "c:/foodor_wuhan_10.11.xls";
	private static String endpath = "c:/end.xlsx";

	public static void main(String[] args) throws IOException {
		List<Goods> goodsList = HebingHuohao.readHuohaoExcel(path1);
		Map<String, Goods> sourceGoodsMap2 =HebingHuohao.readExcel(path2,1,23,24);
		List<Goods> newGoodsList =HebingHuohao.setGoodsMapToList(goodsList, sourceGoodsMap2);
		HebingHuohao.writeFile(newGoodsList);
//		XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(path1));
//		XSSFSheet sheet = xwb.getSheetAt(0);
//		List<String> huohaoList = new ArrayList<String>();
//		// 取得第一个表的货号
//		for (int i = sheet.getFirstRowNum(); i <= sheet
//				.getPhysicalNumberOfRows(); i++) {
//			XSSFRow row = sheet.getRow(i);
//			if (row == null) {
//				continue;
//			}
//			huohaoList.add(row.getCell(0).toString().trim());
//		}
//		HSSFWorkbook xwb2 = new HSSFWorkbook(new FileInputStream(path2));
//		HSSFSheet sheet2 = xwb2.getSheetAt(0);
//		List<Goods> goodsList = new ArrayList<Goods>();
//		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
//		// 取得第二个表的货号和价格
//		for (int i = sheet2.getFirstRowNum(); i <= sheet2
//				.getPhysicalNumberOfRows(); i++) {
//			HSSFRow row2 = sheet2.getRow(i);
//			if (row2 == null) {
//				continue;
//			}
//			HSSFCell noCell = row2.getCell(2);
//			HSSFCell priceCell = row2.getCell(20);
//			HSSFCell numCell = row2.getCell(19);
//			if (noCell == null || priceCell.getCellType() != 0) {
//				continue;
//			}
//
//			String no = noCell.getStringCellValue();
//			Float price = new Float(priceCell.getNumericCellValue());
//			Float num = new Float(numCell.getNumericCellValue());
//			Goods goods = new Goods();
//			goods.setNo(no);
//			goods.setNum(num);
//			goods.setPrice(price);
//			sourceGoodsMap.put(no, goods);
//		}
//		for (String huohao : huohaoList) {
//			Goods goods = sourceGoodsMap.get(huohao);
//			if (goods == null) {
//				goods = new Goods();
//				goods.setNo(huohao);
//			}
//			goodsList.add(goods);
//
//		}
//
//		writeFile(goodsList);
//		
//	}
//
//	public static void writeFile(List<Goods> goodsList) {
//		OutputStream os;
//		try {
//			os = new FileOutputStream(endpath);
//			XSSFWorkbook wb = new XSSFWorkbook();
//			XSSFSheet sheet = wb.createSheet("sheet1");
//			int i = 0;
//			for (Goods goods : goodsList) {
//				XSSFRow row = sheet.createRow(i);
//				System.out.println(goods.getNo());
//				row.createCell(0).setCellValue(goods.getNo());
//				row.createCell(1).setCellValue(goods.getNum());
//				row.createCell(2).setCellValue(goods.getPrice());
//				i++;
//			}
//			wb.write(os);
//			os.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}
}
