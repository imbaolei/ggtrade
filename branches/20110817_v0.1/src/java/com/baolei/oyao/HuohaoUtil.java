package com.baolei.oyao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
public class HuohaoUtil {

	public Map<String, Goods> readExcel(String path, int noCellNum,
			int numCellNum, int priceCellNum) {

		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
		if (path.equalsIgnoreCase("")) {
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

	/**
	 * 根据path读取excel中的order这几列
	 * 
	 * @param path
	 * @param order
	 * @return
	 */
	public Map<String, Goods> readExcel(String path, int[] order) {
		// Order 读取顺序 order=货号,数量,售价,衣服类型,季节
		int noCellNum = order[0];
		int numCellNum = order[1];
		int priceCellNum = order[2];
		int typeCellNum = order[3];
		int jijieCellNum = 0;
		if(order.length > 4){
			jijieCellNum = order[4];
		}
		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
		if (path.equalsIgnoreCase("")) {
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
				HSSFCell typeCell = row1.getCell(typeCellNum);
				HSSFCell jijieCell = row1.getCell(jijieCellNum);
				// 没有货号，或者有货号没有价格，说明不是商品
				if (noCell == null || priceCell == null
						|| priceCell.getCellType() != 0) {
					continue;
				}

				String no = noCell.getStringCellValue();
				Float price = new Float(priceCell.getNumericCellValue());
				Float num = new Float(numCell.getNumericCellValue());
				String type = typeCell.getStringCellValue();
				String jijie = jijieCell.getStringCellValue();
				Goods theGoods = sourceGoodsMap.get(no);
				// 去重
				if (theGoods != null) {
					theGoods.setNum(num + theGoods.getNum());
				} else {
					Goods goods = new Goods();
					goods.setNo(no);
					goods.setNum(num);
					goods.setPrice(price);
					goods.setType(type);
					goods.setJijie(jijie);
					sourceGoodsMap.put(no, goods);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sourceGoodsMap;
	}

	/**
	 * 根据goodsList中的货号，把goodsMap中比较全的商品信息copy到新的List中
	 * 
	 * @param goodsList
	 * @param goodsMap
	 * @return
	 */
	public List<Goods> setGoodsMapToList(List<Goods> goodsList,
			Map<String, Goods> goodsMap) {
		List<Goods> newGoodsList = new ArrayList<Goods>();
		for (Goods sourceGoods : goodsList) {
			Goods goods = goodsMap.get(sourceGoods.getNo());
			if (goods == null) {
				System.out.println(sourceGoods.getNo() + "找不到!");
			} else {
				newGoodsList.add(goods);
			}

		}
		return newGoodsList;
	}

	/**
	 * 支持 excel 2007
	 * @param path
	 * @return
	 */
	public List<Goods> readHuohaoExcel(String path) {
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

	/**
	 * 去除重复的货号，合并两个map中的商品
	 * 
	 * @param map1
	 * @param map2
	 * @return
	 */
	public Map<String, Goods> hebing(Map<String, Goods> map1,
			Map<String, Goods> map2) {
		Map<String, Goods> sourceGoodsMap = new HashMap<String, Goods>();
		for (Entry<String, Goods> entry1 : map1.entrySet()) {
			String goodsNo1 = entry1.getKey();
			Goods goods1 = entry1.getValue();
			Goods theGoods = sourceGoodsMap.get(goodsNo1);
			if (theGoods != null) {
				theGoods.setNum(theGoods.getNum() + goods1.getNum());
			} else {
				sourceGoodsMap.put(goodsNo1, goods1);
			}
		}
		for (Entry<String, Goods> entry2 : map2.entrySet()) {
			String goodsNo2 = entry2.getKey();
			Goods goods2 = entry2.getValue();
			Goods theGoods = sourceGoodsMap.get(goodsNo2);
			if (theGoods != null) {
				theGoods.setNum(theGoods.getNum() + goods2.getNum());
			} else {
				sourceGoodsMap.put(goodsNo2, goods2);
			}
		}
		return sourceGoodsMap;
	}

	/**
	 * 把商品写入excel
	 * 
	 * @param goodsList
	 */
	public void writeFile(List<Goods> goodsList, String endpath) {
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
				row.createCell(3).setCellValue(goods.getType());
				row.createCell(4).setCellValue(goods.getJijie());
				i++;
			}
			wb.write(os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据type或者货号过滤foodor成符合淘宝类目的列表
	 */
	public HashMap<String, List<Goods>> filterFoodorTypeList(
			List<Goods> goodsList) {
		HashMap<String, List<Goods>> targetMap = new HashMap<String, List<Goods>>();
		// 牛仔裤
		String niuzaikuS = "牛仔裤,休闲牛仔裤";
		List<Goods> niuzaiku = new ArrayList<Goods>();
		// 背心
		String beixinS = "毛背心";
		List<Goods> beixin = new ArrayList<Goods>();
		// 皮衣
		String piyiS = "皮衣";
		List<Goods> piyi = new ArrayList<Goods>();
		// 羽绒服
		String yurongfuS = "羽绒服";
		List<Goods> yurongfu = new ArrayList<Goods>();
		// 风衣
		String fengyiS = "毛料大衣,风衣";
		List<Goods> fengyi = new ArrayList<Goods>();
		// 棉衣
		String mianyiS = "棉服,棉服装,化纤休闲,化纤大衣,尼克服";
		List<Goods> mianyi = new ArrayList<Goods>();
		// 西服
		String xifuS = "单西,化纤休闲上衣,华纤休闲上衣,化纤套西,化纤休闲西服,化纤西服,化纤单西,毛料休闲上衣,棉休闲,棉休闲上衣,羊毛单西,派克西服,毛料西服,毛料单西,套西,西服,休闲西服";
		List<Goods> xifu = new ArrayList<Goods>();
		// 夹克
		String jiakeS = "夹克,夹克衫,茄克,夹克(豆绿）,夹克羊绒款,羊绒茄克,薄茄克,单夹克,单茄克,棉茄克,厚茄克,厚夹克,化纤派克,毛料派克";
		List<Goods> jiake = new ArrayList<Goods>();
		// 毛衣
		String maoyiS = "毛衫,厚羊毛衫,薄羊毛衫,厚毛衫,羊毛衫,薄毛衫";
		List<Goods> maoyi = new ArrayList<Goods>();
		// 休闲裤
		String xiuxiankuS = "休闲裤,男休闲裤,特价男休闲裤,特价休闲裤,男休闲裤（代销）";
		List<Goods> xiuxianku = new ArrayList<Goods>();
		// 西裤
		String xikuS = "毛涤裤,毛料西裤,毛涤西裤,化纤西裤,化纤西裤配裤,化纤西服配裤,化纤配套裤,化纤套西裤,化纤西服配套裤,化纤西服裤,套西西裤,特价西裤,条绒西裤,西裤,西裤(无折灰蓝条）,西裤(无折蓝条）,西裤(有折深蓝）,西裤(无折深蓝）,西裤(有折蓝条）,西裤（有折浅灰条）,西裤(无折蓝暗格）,西裤(毛料中灰蓝条）,西裤(无折灰蓝格）,西裤(无折毛料浅灰条）,西裤(无折浅灰条）";
		List<Goods> xiku = new ArrayList<Goods>();
		// T恤
		String txuS = "短袖,桑蚕丝T恤,T恤,棉T 恤,丝光棉短T,普通棉长T,长T,普通面长T,丝光棉长T,棉长T,丝光长T,毛T,针织短T,针织T恤衫,棉短T,短T,丝光短T,普通棉短T,丝光棉短T,普通棉短T,针织短T";
		List<Goods> txu = new ArrayList<Goods>();
		// 衬衫
		String chenshanS = "短衬,休闲衬衣,休闲短衬,休闲衬衫,休闲长衬,休闲长袖衬衫,休闲短袖衬衫,休闲短袖衬,休闲短衬衫,长衬,正装长袖衬(米黄翻领）,正装长袖衬(水红斜纹翻领）,正装长袖衬,正装长袖衬衣,正装长衬,正装长袖衬(白底蓝红条纹立领）,正装短袖衬衫（代销）,正装短袖衬,正装长袖衬(黑色立领带白纹）,正装长袖衬(白色）,正装长袖衬衫";
		List<Goods> chenshan = new ArrayList<Goods>();
		// 其他
		List<Goods> other = new ArrayList<Goods>();
		for (Goods goods : goodsList) {
			// 牛仔裤
			if (isInclude(goods, niuzaikuS)) {
				niuzaiku.add(goods);
				continue;
			} else if (isInclude(goods, beixinS)) {
				beixin.add(goods);
				continue;
			} else if (isInclude(goods, piyiS)) {
				piyi.add(goods);
				continue;
			} else if (isInclude(goods, yurongfuS)) {
				yurongfu.add(goods);
				continue;
			} else if (isInclude(goods, fengyiS)) {
				fengyi.add(goods);
				continue;
			} else if (isInclude(goods, mianyiS)) {
				mianyi.add(goods);
				continue;
			} else if (isInclude(goods, jiakeS)) {
				jiake.add(goods);
				continue;
			} else if (isInclude(goods, maoyiS)) {
				maoyi.add(goods);
				continue;
			} else if (isInclude(goods, xiuxiankuS)) {
				xiuxianku.add(goods);
				continue;
			} else if (isInclude(goods, xikuS)) {
				xiku.add(goods);
				continue;
			} else if (isInclude(goods, xifuS)) {
				xifu.add(goods);
				continue;
			} else if (isInclude(goods, txuS)) {
				txu.add(goods);
				continue;
			} else if (isInclude(goods, chenshanS)) {
				chenshan.add(goods);
				continue;
			} else {
				other.add(goods);
				continue;
			}
		}
		targetMap.put("牛仔裤", niuzaiku);
		targetMap.put("背心", beixin);
		targetMap.put("皮衣", piyi);
		targetMap.put("羽绒服", yurongfu);
		targetMap.put("风衣", fengyi);
		targetMap.put("棉衣", mianyi);
		targetMap.put("西服", xifu);
		targetMap.put("夹克", jiake);
		targetMap.put("毛衣", maoyi);
		targetMap.put("休闲裤", xiuxianku);
		targetMap.put("西裤", xiku);
		targetMap.put("T恤", txu);
		targetMap.put("衬衫", chenshan);
		targetMap.put("其他", other);
		return targetMap;
	}

	public boolean isInclude(Goods goods, String str) {
		String type = goods.getType().trim();
		for (String tmpS : str.split(",")) {
			if (tmpS.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

	public boolean isFirstOrSecond(String source, String target) {
		if (isFirst(source, target) || isSecond(source, target)) {
			return true;
		}
		return false;
	}

	public boolean isFirst(String source, String first) {
		if (source != null) {
			source = source.toLowerCase();
			first = first.toLowerCase();
			if (source.indexOf(first) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isEqual(String source, String target, int weizhi) {
		if (source != null) {
			source = source.toLowerCase();
			target = target.toLowerCase();
			weizhi = weizhi - 1;
			if (source.indexOf(target) == weizhi) {
				return true;
			}
		}
		return false;
	}

	public void printGoods(List<Goods> goodsList) {
		for(Goods goods : goodsList){
			System.out.println(goods.getNo());
		}
		System.out.println();
		
	}

	public boolean isSecond(String source, String second) {
		if (source != null) {
			source = source.toLowerCase();
			second = second.toLowerCase();
			if (source.indexOf(second) == 1) {
				return true;
			}
		}
		return false;
	}

	public boolean isInclude(String source, String target) {
		if (source != null) {
			if (source.indexOf(target) > -1) {
				return true;
			}
		}
		return false;
	}
}
