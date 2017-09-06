package com.okdeer.mall.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.common.consts.LogConstants;

public class MockUtils {

	private static final Logger logger = LoggerFactory.getLogger(MockUtils.class);
	
	/**
	 * @Description: 获取mock请求数据
	 * @param filePath 文件路径
	 * @param clazz 请求对应对象的class
	 * @return   
	 * @author maojj
	 * @date 2017年9月4日
	 */
	public static <T> T getMockSingleData(String filePath,Class<T> clazz){
		List<T> mockList = Lists.newArrayList();
		try {
			List<String> dataList = getMockData(filePath);
			for(String data : dataList){
				mockList.add(JsonMapper.nonDefaultMapper().fromJson(data, clazz));
			}
		} catch (IOException e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		}
		if(CollectionUtils.isNotEmpty(mockList)){
			return mockList.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * @Description: mock一个数据集合
	 * @param filePath
	 * @param clazz
	 * @return   
	 * @author maojj
	 * @date 2017年9月6日
	 */
	public static <T> List<T> getMockListData(String filePath,Class<T> clazz){
		List<List<T>> mockList = MockUtils.getMockData(filePath, clazz);
		if(CollectionUtils.isEmpty(mockList)){
			return Lists.newArrayList();
		}else{
			return mockList.get(0);
		}
	}
	
	/**
	 * @Description: 获取mock列表数据
	 * @param filePath 文件路径
	 * @param clazz 请求对象的class
	 * @return   
	 * @author maojj
	 * @date 2017年9月4日
	 */
	public static <T> List<List<T>> getMockData(String filePath,Class<T> clazz){
		List<List<T>> mockList = Lists.newArrayList();
		List<T> mockData = null;
		JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
		try {
			List<String> dataList = getMockData(filePath);
			for(String data : dataList){
				mockData = jsonMapper.fromJson(data, jsonMapper.contructCollectionType(List.class, clazz));
				mockList.add(mockData);
			}
		} catch (IOException e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		}
		return mockList;
	}
	
	/**
	 * @Description: 获取mock的字符串
	 * @param filePath 文件路径
	 * @return
	 * @throws IOException   
	 * @author maojj
	 * @date 2017年9月4日
	 */
	public static List<String> getMockData(String filePath) throws IOException {
		List<String> dataList = Lists.newArrayList();
		ClassPathResource resource = new ClassPathResource(filePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(resource.getFile()));
			StringBuilder sb = null;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Begin")) {
					sb = new StringBuilder();
				} else if (line.startsWith("End")) {
					dataList.add(sb.toString());
				} else {
					sb.append(line);
				}
			}
		} catch (Exception e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		return dataList;
	}
}
