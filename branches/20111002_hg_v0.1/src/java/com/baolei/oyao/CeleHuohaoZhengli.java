package com.baolei.oyao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CeleHuohaoZhengli {

	private static String path11 = "c:/cele库存10-23.xls"; // 读取cele
	private static String path12 = "";
	private static String path2 = "";
	private static String endpath = "c:/end.xlsx";

	public static void main(String[] args) {
		HuohaoUtil hu = new HuohaoUtil();
		// 读取第一个系列cele库存2 19 20 sjz裤子 0 27 29
		Map<String, Goods> sourceGoodsMap11 = hu.readExcel(path11, 2, 19, 20);
		Map<String, Goods> sourceGoodsMap12 = hu.readExcel(path12, 0, 27, 29);
		Map<String, Goods> sourceGoodsMap2 = hu.readExcel(path2, 1, 23, 24);
		Map<String, Goods> sourceGoodsMap1 = hu.hebing(sourceGoodsMap11,
				sourceGoodsMap12);
		Map<String, Goods> sourceGoodsMapEnd = hu.hebing(sourceGoodsMap1,
				sourceGoodsMap2);

		List<Goods> goodsList = new ArrayList<Goods>();
		int xiaoyu5 = 0;
		for (Entry<String, Goods> entry2 : sourceGoodsMapEnd.entrySet()) {
			Goods goods2 = entry2.getValue();
			if (goods2.getNum() >= 3) {
				goodsList.add(goods2);
			} else {
				xiaoyu5++;
			}
		}
//		hu.writeFile(goodsList, endpath);
		System.out.println(xiaoyu5);
		filterCeleTypeList(goodsList);

	}

	public static HashMap<String, List<Goods>> filterCeleTypeList(List<Goods> goodsList) {
		HuohaoUtil hu = new HuohaoUtil();
		HashMap<String, List<Goods>> targetMap = new HashMap<String, List<Goods>>();
		List<Goods> nanxieList1 = new ArrayList<Goods>();
		List<Goods> nanxieList2 = new ArrayList<Goods>();
		List<Goods> nanxieList3 = new ArrayList<Goods>();
		List<Goods> nanxieList4 = new ArrayList<Goods>();
		List<Goods> nanxieList0 = new ArrayList<Goods>();

		List<Goods> nvxieList1 = new ArrayList<Goods>();
		List<Goods> nvxieList2 = new ArrayList<Goods>();
		List<Goods> nvxieList3 = new ArrayList<Goods>();
		List<Goods> nvxieList4 = new ArrayList<Goods>();
		List<Goods> nvxieList0 = new ArrayList<Goods>();

		List<Goods> otherList = new ArrayList<Goods>();
		for (Goods goods : goodsList) {
			if (hu.isFirst(goods.getNo(), "M")) {
				if (hu.isEqual(goods.getNo(), "1", 3)
						|| hu.isEqual(goods.getNo(), "A", 3)) {
					nanxieList1.add(goods);
				} else if (hu.isEqual(goods.getNo(), "2", 3)
						|| hu.isEqual(goods.getNo(), "B", 3)) {
					nanxieList2.add(goods);
				} else if (hu.isEqual(goods.getNo(), "3", 3)
						|| hu.isEqual(goods.getNo(), "C", 3)) {
					nanxieList3.add(goods);
				} else {
					nanxieList4.add(goods);
				}
			} else if (hu.isFirst(goods.getNo(), "W")) {
				if (hu.isEqual(goods.getNo(), "1", 3)
						|| hu.isEqual(goods.getNo(), "A", 3)) {
					nvxieList1.add(goods);
				} else if (hu.isEqual(goods.getNo(), "2", 3)
						|| hu.isEqual(goods.getNo(), "B", 3)) {
					nvxieList2.add(goods);
				} else if (hu.isEqual(goods.getNo(), "3", 3)
						|| hu.isEqual(goods.getNo(), "C", 3)) {
					nvxieList3.add(goods);
				} else {
					nvxieList4.add(goods);
				}
			} else {
				otherList.add(goods);
			}
		}
		System.out.println("cele man chun");
		hu.printGoods(nanxieList1);
		System.out.println("cele man xia");
		hu.printGoods(nanxieList2);
		System.out.println("cele man qiu");
		hu.printGoods(nanxieList3);
		System.out.println("cele man dong");
		hu.printGoods(nanxieList4);
		
		System.out.println("cele woman chun");
		hu.printGoods(nvxieList1);
		System.out.println("cele woman xia");
		hu.printGoods(nvxieList2);
		System.out.println("cele woman qiu");
		hu.printGoods(nvxieList3);
		System.out.println("cele woman dong");
		hu.printGoods(nvxieList4);
		
		System.out.println("cele other");
		hu.printGoods(otherList);
		
		return targetMap;
	}
	
	

}
