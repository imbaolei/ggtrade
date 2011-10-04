package com.baolei.ghost.app2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestTmp {
	
	private static String filePath = "C:/Users/gg/Desktop/虎都";
	
	public static void main(String[] args)  {
		List<String> fileList = getAllCodes();
		Set<String> fileSet = new HashSet<String>();
		for(String file:fileList){
			int i = file.indexOf("-");
			if(i > 0 ){
				file = file.substring(0, i);
				fileSet.add(file);
			}else{
				fileSet.add(file);
			}
			
		}
		for(String file:fileSet){
			System.out.println(file);
		}
	}
	
	public static List<String> getAllCodes() {
		File file = new File(filePath);
		List<String> fileList = new ArrayList<String>();
		String[] files = file.list();
		System.out.println(files.length);
		for (String filename : files) {
			filename = filename.replace(".TXT", "");
			fileList.add(filename);
		}
		return fileList;
	}

}
