package com.baolei.trade.bo;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baolei.ghost.app2.DataParser;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;

@Service("priceBO")
public class PriceBO {

	@Autowired
	@Qualifier("txdFileParser")
	DataParser dataParser;

	@Autowired
	PriceUtil priceUtil;

	DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
	protected Log log = LogFactory.getLog(getClass());

	public List<PriceDO> getPriceListByFile(String code) {
		List<PriceDO> priceList = dataParser.parse(code);
		return priceList;
	}
	
	
	public List<String> getAllCodes(String filePath) {
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
	
	/**
	 * 转换为 0~100之间的一个数字
	 * @param index
	 * @param length
	 * @return
	 */
	public int convertRank(int index,int length){
		float rankfloat = index/length;
		int rankint = 0;
		if(length > 100){
			rankint = 100 - (int) rankfloat;
		}else{
			rankint = 100 - (int)rankfloat*100;
		}
		return rankint;
	}

	/**
	 * 计算 从startDateString 到 endDateString周期内的 ma均线的 在 period周期 内的涨跌幅
	 * 
	 * @param priceList
	 * @param ma
	 * @param period
	 * @param startDateString
	 * @param endDateString
	 * @return
	 */
	public List<PriceDO> calpriceListMaRise(List<PriceDO> priceList, String ma,
			int period, String startDateString, String endDateString) {
		try {
			Date startDate = dateFormat.parse(startDateString);
			Date endDate = dateFormat.parse(endDateString);
			// 计算涨幅
			for (PriceDO priceDO : priceList) {
				if (priceDO.getTime().after(startDate)
						&& priceDO.getTime().before(endDate)) {
					PriceDO beforePriceDO = calBeforeTime(priceList, priceDO,
							period);
					float rise = calMaRise(beforePriceDO, priceDO, ma);
					priceDO.setRise(rise);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return priceList;
	}

	public PriceDO calPriceMaRise(List<PriceDO> priceList, PriceDO priceDO,
			String ma, int period) {
		PriceDO beforePriceDO = calBeforeTime(priceList, priceDO, period);
		float rise = calMaRise(beforePriceDO, priceDO, ma);
		priceDO.setRise(rise);
		return priceDO;
	}

	private PriceDO calBeforeTime(List<PriceDO> priceList, PriceDO priceDO,
			int period) {
		int index = priceList.indexOf(priceDO);
		if (index - period > 0) {
			return priceList.get(index - period);
		}
		return null;
	}

	private float calMaRise(PriceDO beforePriceDO, PriceDO afterPriceDO,
			String ma) {
		if (beforePriceDO != null && afterPriceDO != null) {
			log.info(beforePriceDO.getCode() + " "
					+ dateFormat.format(beforePriceDO.getTime()) + " "
					+ beforePriceDO.getMa());
			if (beforePriceDO.getMa(ma) <= 0 || afterPriceDO.getMa(ma) <= 0) {
				return 0;
			}
			float beforePrice = beforePriceDO.getMa(ma);
			float afterPrice = afterPriceDO.getMa(ma);
			float rise = (afterPrice - beforePrice) / beforePrice * 100;
			rise = NumberUtil.roundDown(rise, 2);
			return rise;
		}
		return 0;
	}

	public List<PriceDO> initPriceListMa(List<PriceDO> priceList, String ma) {
		if (StringUtils.isEmpty(ma)) {
			ma = "20,25,60,90,200,350";
		}

		for (PriceDO priceDO : priceList) {
			// log.info(dateFormat.format(priceDO.getTime()));
			JSONObject json = new JSONObject();
			if (StringUtils.isNotEmpty(ma)) {
				String[] mas = ma.split(",");
				for (String tmpma : mas) {
					float floatMa = PriceUtil.MA(priceList, priceDO,
							Integer.parseInt(tmpma));
					json.put(tmpma, floatMa);
				}
			}
			priceDO.setMa(json.toString());
		}
		return priceList;
	}

	public List<PriceDO> initPriceListAtr(List<PriceDO> priceList, int time) {
		if (time <= 0) {
			time = 20;
		}
		for (PriceDO priceDO : priceList) {
			priceDO.setAtr(priceUtil.atr(priceList, priceDO, time));
		}
		return priceList;
	}
}
