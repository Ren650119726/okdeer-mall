/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年9月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.okdeer.mall.system.service.impl.InvitationCodeServiceImpl.PageCallBack;

/**
 * ClassName: PageCallUtils 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年9月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class PageCallUtils {

	public static <T> List<T> pageQueryByIds(List<String> ids, PageCallBack<T> pageCallBack, final int pageSize) {
		List<T> resultList = Lists.newArrayList();
		if (ids.size() > pageSize) {
			// 如果list太大，分批查询
			int page = ids.size() % pageSize == 0 ? ids.size() / pageSize : ids.size() / pageSize + 1;
			for (int i = 0; i < page; i++) {
				int fromIndex = i * pageSize;
				int toIndex = fromIndex + pageSize;
				if (toIndex > ids.size()) {
					toIndex = ids.size();
				}
				List<String> indexList = ids.subList(fromIndex, toIndex);
				List<T> tempList = pageCallBack.callBackHandle(indexList);

				resultList.addAll(tempList);
			}
		} else {
			List<T> tempList = pageCallBack.callBackHandle(ids);
			resultList.addAll(tempList);
		}
		return resultList;
	}

	public interface PageCallBack<T> {

		List<T> callBackHandle(List<String> idList);
	}
}
