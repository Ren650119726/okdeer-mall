package com.okdeer.mall.activity;

public class PrizeFunctionTest {
	/**
 	 * 根据中奖概率执行中奖 
 	 * @param iArr
 	 * @return
 	 */
 	private static Integer isHadPrize(double[] iArr,int weightDeno){
 		double randonNo =  Math.random() * weightDeno;
 		double count = 0;
		//循环增加各个奖品的概率，判断是否中奖
		for (int  i = 0 ; i < iArr.length ; i++) {
			double step = count + iArr[i];  
			//如果概率为空，跳过该奖项
			if (iArr[i] != 0) {
				if (randonNo >= count && randonNo < step) {
					return i;
				}
				count += iArr[i];
			}
		}
		return null;
 	}
}
