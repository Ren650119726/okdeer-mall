
package com.okdeer.mall.activity.wxchat.bo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class NewsContent {

	@JsonProperty("news_item")
	private List<News> newsItem = Lists.newArrayList();

	public List<News> getNewsItem() {
		return newsItem;
	}

	public void setNewsItem(List<News> newsItem) {
		this.newsItem = newsItem;
	}

}
