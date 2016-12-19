package com.okdeer.mall.activity;

import net.sf.json.JSONObject;

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
					//System.out.println("中奖概率为"+randonNo);
					return i;
				}
				count += iArr[i];
			}
		}
		return null;
 	}
 	
 	public static void main(String[] args){
 		double[] weight = {800,1,500,6699,1500,0,400,0};
 		int[] count={0,0,0,0,0,0,0,0};
 		for(int i = 0;i<300;i++){
 			Integer  prizeNo = isHadPrize(weight,10000);
 			if(prizeNo != null){
 				// 剩余数量小于0 显示已领完
 				count[prizeNo.intValue()]++;
 			}
 		}
 		
 		for(int j=0;j<count.length;j++){
 			System.out.println("中奖概率为"+count[j]);
 		}
 	}
}
