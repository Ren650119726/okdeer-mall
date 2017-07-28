package com.okdeer.mall.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;
import com.okdeer.common.consts.LogConstants;

public class MockUtils {

	private static final Logger logger = LoggerFactory.getLogger(MockUtils.class);
	
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
