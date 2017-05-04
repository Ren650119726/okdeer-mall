package com.okdeer.mall.order.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.okdeer.mall.order.service.PageCallBack;

public class PageQueryUtils {
	
	private static final int pageSize = 100;
	
	public static <T> List<T> pageQueryByIds(List<String> ids, PageCallBack<T> pageCallBack) {
		List<T> resultList = Lists.newArrayList();
		pageQueryByIds(ids, pageCallBack,pageSize);
		return resultList;
	}
	
	public static <T> List<T> pageQueryByIds(List<String> ids, PageCallBack<T> pageCallBack,final int pageSize) {
		List<T> resultList = Lists.newArrayList();
		if (ids.size() > pageSize) {
			// 如果list太大，分批查询
			int page = ids.size() % pageSize == 0 ? ids.size() / pageSize : ids.size() / pageSize + 1;
			for (int i = 0; i < page; i++) {
				int fromIndex = i * pageSize;
				int toIndex = fromIndex + pageSize - 1;
				if (toIndex > ids.size()) {
					toIndex = ids.size();
				}
				List<String> indexList = ids.subList(fromIndex, toIndex);
				List<T> tempList = pageCallBack.callBackHandle(indexList);
				
				resultList.addAll(tempList);
			}
		}else{
			List<T> tempList = pageCallBack.callBackHandle(ids);
			resultList.addAll(tempList);
		}
		return resultList;
	}
	
}
