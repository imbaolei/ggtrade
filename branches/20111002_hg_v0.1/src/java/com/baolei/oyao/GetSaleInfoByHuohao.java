package com.baolei.oyao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author B
 * 
 *         -
 */
public class GetSaleInfoByHuohao {

	private static String path1 = "c:/foodor10-29.xlsx";
	private static String path11 = "c:/10-23石家庄上衣库存.xls"; // 读取sjz的fordoo
	private static String path12 = "c:/10-23石家庄裤子库存.xls"; // 读取sjz的fordoo
	private static String path2 = "c:/foodor_wuhan_10.11.xls";// 读取wh的fordoo
	private static String endpath = "c:/end.xlsx";

	public static void main(String[] args) throws IOException {
		HuohaoUtil hu = new HuohaoUtil();
		List<Goods> goodsList = hu.readHuohaoExcel(path1);
		// 读取第一个系列sjz上衣 0 1 26 28 sjz裤子 0 27 29
		int[] s11 = { 0, 26, 28, 1 ,2};
		Map<String, Goods> sourceGoodsMap11 = hu.readExcel(path11, s11);
		int[] s12 = { 0, 27, 29, 1 ,2};
		Map<String, Goods> sourceGoodsMap12 = hu.readExcel(path12, s12);
		int[] s2 = { 1, 23, 24, 2 };
		// 读取第二个系列wh 1 23 24
		Map<String, Goods> sourceGoodsMap2 = hu.readExcel(path2, s2);

		Map<String, Goods> sourceGoodsMap1 = hu.hebing(sourceGoodsMap11,
				sourceGoodsMap12);
		Map<String, Goods> sourceGoodsMapEnd = hu.hebing(sourceGoodsMap1,
				sourceGoodsMap2);
		List<Goods> newGoodsList =hu.setGoodsMapToList(goodsList, sourceGoodsMapEnd);
		hu.writeFile(newGoodsList,endpath);

	}
}
