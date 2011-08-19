package com.baolei.ghost.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtil {
	
	static DecimalFormat decimalFormat = new DecimalFormat("#.00");
	public static float roundDown(float value,int scale){
		BigDecimal bd = new BigDecimal(value);
		bd.setScale(scale, BigDecimal.ROUND_DOWN);
		Float returnValue = bd.floatValue();
		returnValue = Float.parseFloat(decimalFormat.format(returnValue));
		return returnValue;
	}

}
