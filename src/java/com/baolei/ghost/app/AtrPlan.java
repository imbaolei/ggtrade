 package com.baolei.ghost.app;

import java.math.BigDecimal;

public class AtrPlan {

	private static float money = 400000;

	private static float atr = 0.44f;

	private static float buyPoint = 11.5f;

	private static int n = 2;
	
	private static int num;

	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		calJcPlan(calJcPlan(calJcPlan(calBuy(), atr, 1), atr, 2), atr, 3);
		calTheLost();
	}

	public static float calBuy() {
		BigDecimal amount = new BigDecimal(money);
		num = amount.divide(new BigDecimal(100)).divide(new BigDecimal(atr), 0, BigDecimal.ROUND_DOWN).divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN).intValue() * 100;
		System.out.print("第一次计划买点:" + buyPoint + ";买入" + num + "股;");
		float stopLoss = new BigDecimal(buyPoint).subtract(
				new BigDecimal(2f).multiply(new BigDecimal(atr))).setScale(n,
				BigDecimal.ROUND_DOWN).floatValue();
		System.out.println("计划止损:" + stopLoss + ";");
		return buyPoint;
	}

	public static float calJcPlan(float jcPoint, float atr, int i) {
		// float jcPointNext = new BigDecimal(jcPoint + 0.5f* atr).setScale(n,
		// BigDecimal.ROUND_DOWN).floatValue();
		float jcPointNext = new BigDecimal(jcPoint).add(
				new BigDecimal(0.5f).multiply(new BigDecimal(atr))).setScale(n,
				BigDecimal.ROUND_DOWN).floatValue();
		// float tmpf = jcPoint - atr*2f;
		float stopLoss = new BigDecimal(jcPointNext).subtract(
				(new BigDecimal(atr)).multiply(new BigDecimal(2))).setScale(n,
				BigDecimal.ROUND_DOWN).floatValue();

		System.out.print("计划第" + i + "次加仓:" + jcPointNext + ";");
		System.out.println("操作完计划止损:" + stopLoss + ";");
		return jcPointNext;
	}
	
	public static void calTheLost(){
		float lost1 = 2*atr*num;
		float lost2 = (float) (2*atr*num + 1.5*atr*num);
		float lost3 = (float) (2*atr*num + 1.5*atr*num + 1*atr*num);
		float lost4 = (float) (2*atr*num + 1.5*atr*num + 1*atr*num+ 0.5*atr*num);
		System.out.println("如果第一次止损会损失大概 " + lost1);
		System.out.println("如果第二次止损会损失大概 " + lost2);
		System.out.println("如果第三次止损会损失大概 " + lost3);
		System.out.println("如果第四次止损会损失大概 " + lost4);
	}

}
