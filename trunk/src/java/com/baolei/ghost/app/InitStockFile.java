package com.baolei.ghost.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

public class InitStockFile {

	private static String filePath = "C:/data/from/sh/SH600000.TXT";
	public static String fromFilePath = "C:/data/from/";
	public static String toFilePath = "C:/data/to/";

	/**
	 * 从国信的stock文件格式，解析存储为我们需要的格式
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		File root = new File(fromFilePath);
		String[] floderlist = root.list();
		for (int i = 0; i < floderlist.length; i++) {
			File floder = new File(fromFilePath + "/" + floderlist[i]);
			String[] stockFiles = floder.list();
			for (int j = 0; j < stockFiles.length; j++) {
				File stockFile = new File(floder.getPath() + "/"
						+ stockFiles[j]);
				initStock(stockFile.getPath());
			}
		}
		System.out.println("[----------全部完成-----------]");
	}

	// public static void main(String[] args) {
	//
	// // 线程池
	// ExecutorService exec = Executors.newCachedThreadPool();
	// // 只能5个线程同时访问
	// final Semaphore semp = new Semaphore(2);
	// File root = new File(fromFilePath);
	// String[] floderlist = root.list();
	// for (int i = 0; i < floderlist.length; i++) {
	// File floder = new File(fromFilePath + "/" + floderlist[i]);
	// String[] stockFiles = floder.list();
	//
	// for (int j = 0; j < stockFiles.length; j++) {
	// final File stockFile = new File(floder.getPath() + "/"
	// + stockFiles[j]);
	// Runnable run = new Runnable() {
	// public void run() {
	// // 获取许可
	// try {
	// semp.acquire();
	// initStock(stockFile.getPath());
	// // 访问完后，释放
	// semp.release();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// };
	// exec.execute(run);
	// }
	// }
	// // exec.shutdown();
	//
	// System.out.println("[----------全部完成-----------]");
	// }

	public static void initStock(String filePath) {

		File read = new File(filePath);
		String code = read.getName().split(".TXT")[0];
		System.out.println(code + " " + new Date() + " 开始读取！");
		DataPool dataPool = new DataPool();
		try {
			StockUtil.initDayStockMap(filePath, dataPool.getDayStockMap(),
					dataPool.getDayStockList());
			System.out.println(code + " " + new Date() + " initDayStockMap完毕！");
			StockUtil.initWeekStockMap(dataPool.getDayStockMap(),
					dataPool.getWeekStockMap(), dataPool.getWeekStockList());
			System.out
					.println(code + " " + new Date() + " initWeekStockMap完毕！");
			StockUtil.initMonthStockMap(dataPool.getDayStockMap(),
					dataPool.getMonthStockMap(), dataPool.getMonthStockList());
			System.out.println(code + " " + new Date()
					+ " initMonthStockMap完毕！");
			// 更新bbi
			updateStockPool(dataPool);
			System.out.println(code + " " + new Date() + " 更新bbi完毕！");
			StockUtil.initBbiMap(dataPool.getDayStockMap(),
					dataPool.getDayStockList());
			System.out.println(code + " " + new Date() + " 分析day bbi完毕！");
			StockUtil.initBbiMap(dataPool.getWeekStockMap(),
					dataPool.getWeekStockList());
			System.out.println(code + " " + new Date() + " 分析week bbi完毕！");
			StockUtil.initBbiMap(dataPool.getMonthStockMap(),
					dataPool.getMonthStockList());
			System.out.println(code + " " + new Date() + " 分析month bbi完毕！");
			writeStockBbiFile(dataPool.getDayStockList());
			writeStockBbiFile(dataPool.getWeekStockList());
			writeStockBbiFile(dataPool.getMonthStockList());

			System.out.println(code + " " + new Date() + " 写入完毕！");
			System.out.println("[----------我是分割线----------------]");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void updateStockPool(DataPool fromDataPool)
			throws IOException, ParseException {
		String code = fromDataPool.getDayStockList().get(0).getCode();
		String folder = code.substring(0, 2);
		String tofile = toFilePath + folder.toLowerCase() + "/" + code + ".txt";
		// String fromfile = fromFilePath + folder.toLowerCase() + "/" + code +
		// ".txt";
		// StockUtil.initDayStockMap(fromfile, fromDataPool.getDayStockMap(),
		// fromDataPool.getDayStockList());
		DataPool toDataPool = StockUtil.initToStockPool(tofile);
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		// 看看最新的数据对的上吗，对不上就减10，不行再减10
		for (int stockType = 0; stockType <= 2; stockType++) {
			List<StockDO> fromStockList = null;
			Map<String, StockDO> toStockMap = null;
			if (stockType == 0) {
				fromStockList = fromDataPool.getDayStockList();
				toStockMap = toDataPool.getDayStockMap();
			} else if (stockType == 1) {
				fromStockList = fromDataPool.getWeekStockList();
				toStockMap = toDataPool.getWeekStockMap();
			} else if (stockType == 2) {
				fromStockList = fromDataPool.getMonthStockList();
				toStockMap = toDataPool.getMonthStockMap();
			}

			int count = fromStockList.size() - 1;
			for (; count >= 0; count = count - 10) {
				StockDO fromStock = fromStockList.get(count);

				StockDO toStock = toStockMap.get(dateFormat.format(fromStock
						.getTime()));
				if ((toStock != null)
						&& (toStock.getClose() == fromStock.getClose())
						&& (toStock.getBbi() > 0)) {
					break;
				}
			}
			if (count > 0) {
				// fromStockList从count开始能对上了，那就从count开始算bbi,将count之前的bbi设置到fromDataPool
				for (int i = 0; i < count; i++) {
					StockDO fromStock = fromStockList.get(i);
					StockDO toStock = toStockMap.get(dateFormat
							.format(fromStock.getTime()));
					if (toStock != null) {
						fromStock.setBbi(toStock.getBbi());
					} else {
						fromStock.setBbi(0);
					}

				}
			}
		}

	}

	/**
	 * 如果是写日数据，则先清空再写入，如果是些其他周期数据，则以追加方式来写
	 * 
	 * @param stockList
	 */
	public static void writeStockBbiFile(List<StockDO> stockList) {
		if (stockList.size() > 0) {
			String code = stockList.get(0).getCode();
			String folder = code.substring(0, 2);
			String filePath = toFilePath + folder.toLowerCase() + "/" + code
					+ ".txt";
			try {
				File file = new File(filePath);
				if (file.exists()
						&& stockList.get(0).getPeriod()
								.equalsIgnoreCase(Constant.STOCK_PERIOD_DAY)) {
					file.delete();
					file.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true), "GBK"));
				DateFormat dateFormat = new SimpleDateFormat(
						StockUtil.dateFormatString);
				for (StockDO stock : stockList) {
					// [0]日期 [1]open [2]high; [3]low [4]close [5] vol [6]bbi
					// [7]period
					bw.write(dateFormat.format(stock.getTime()));
					bw.write(" ");
					bw.write(new Float(stock.getOpen()).toString());
					bw.write(" ");
					bw.write(new Float(stock.getHigh()).toString());
					bw.write(" ");
					bw.write(new Float(stock.getLow()).toString());
					bw.write(" ");
					bw.write(new Float(stock.getClose()).toString());
					bw.write(" ");
					bw.write(new Float(stock.getVol()).toString());
					bw.write(" ");
					bw.write(new Float(stock.getBbi()).toString());
					bw.write(" ");
					bw.write(stock.getPeriod());
					bw.write(" ");
					bw.newLine();
				}
				bw.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
