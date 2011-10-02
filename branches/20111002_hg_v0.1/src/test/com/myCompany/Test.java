package com.myCompany;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Test {   
        public static void main(String[] args) {   
                //创建一个可重用固定线程数的线程池   
                ExecutorService pool = Executors.newFixedThreadPool(2);   
                //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口   
                for(Integer i = 0 ;i <= 10 ; i++){
                	Thread t = new TradeExecuteThread(i.toString());
                	pool.execute(t);
                }
                //关闭线程池   
                pool.shutdown();   
        }   
}   
  
class TradeExecuteThread extends Thread {
	private String code;
	
	protected Log log = LogFactory.getLog(getClass());
	
	public TradeExecuteThread(String code){
		this.code = code;
	}
	public void run() {
		log.info("start execute " + code + " ...");
//		executeTrade(code);
		log.info("end execute " + code + " ...");
	}
}
