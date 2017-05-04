package com.okdeer.mall.order.service;

import java.util.List;



public interface PageCallBack<T> {

	List<T> callBackHandle(List<String> idList);
}
