package com.baolei.ghost.app;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class Archer {

	public static void main(String[] args) throws HttpException, IOException {
		Timer timer = new Timer();
		timer.schedule(new MyTask(), 1000, 4000);
	}
}

class MyTask extends TimerTask {
	@Override
	public void run() {
		System.out.println(getSinaFuturesPrice("c0905"));
	}

	float getSinaFuturesPrice(String code) {
		String url = "http://hq.sinajs.cn/rn=1405570457&list=";
		HttpClient httpclient = new HttpClient();
		GetMethod getMethod = new GetMethod(url + code);
		String response = "";
		try {
			int statusCode = httpclient.executeMethod(getMethod);
			response = getMethod.getResponseBodyAsString();
//			System.out.print(response);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getMethod.releaseConnection();
		if(response.length() > 20){
			String[] rStrings = response.split(",");
			return Float.parseFloat(rStrings[8]);
		}
		else{
			return -1;
		}
		

	}
}