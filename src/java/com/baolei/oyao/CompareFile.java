package com.baolei.oyao;

import java.util.ArrayList;
import java.util.List;

public class CompareFile {
	
	private static String path1 ="c:/1.xlsx";
	private static String path2 = "c:/2.xlsx";
	
	public static void main(String[] args) {
		HuohaoUtil hu = new HuohaoUtil();
		List<Goods> goodsList1 = hu.readHuohaoExcel(path1);
		List<Goods> goodsList2 = hu.readHuohaoExcel(path2);
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		
		for(Goods goods : goodsList1){
			list1.add(goods.getNo());
		}
		for(Goods goods : goodsList2){
			list2.add(goods.getNo());
		}
		
		compare(list1,list2);
	}
	
	public static void compare(List<String> list1 ,List<String> list2){
		for(String s2 : list2){
			if(!list1.contains(s2)){
				System.out.println("list1 中没有 "+s2);
			}
		}
		for(String s1 : list1){
			if(!list2.contains(s1)){
				System.out.println("list2 中没有 "+s1);
			}
		}
	}
	
	
}
