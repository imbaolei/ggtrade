package com.baolei.ghost.app2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ParserUtil {
	public static List<String> reader(String filePath,String code, int num) {
		List<String> stockList = new ArrayList<String>();
		filePath = filePath + code + ".txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath), "GBK"));
			String temp = null;

			if (num > 0) {
				for (int i = 0; i < num; i++) {
					temp = br.readLine();
					stockList.add(temp);
				}
			} else {
				temp = br.readLine();
				while (temp != null) {
					stockList.add(temp);
					temp = br.readLine();
				}
			}

			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stockList;
	}
	
	
}
