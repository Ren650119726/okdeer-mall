/** 
 *@Project: yschome-archive-service 
 *@Author: guocp
 *@Date: 2016年7月25日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */
package com.okdeer.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: DataSourceProperties 
 * @Description: 数据库配置项
 * @author guocp
 * @date 2016年7月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Configuration
@ConfigurationProperties(prefix=DataSourceProperties.PREFIX)
public class DataSourceProperties {

	public static final String PREFIX = "spring.datasource";

	//@Value("${spring.datasource.driverClass}")
	private String driverClass;

	//@Value("${spring.datasource.url}")
	private String url;

	//@Value("${spring.datasource.username}")
	private String username;

	//@Value("${spring.datasource.password}")
	private String password;

	//@Value("${spring.datasource.validationQuery}")
	private String validationQuery;

	//@Value("${spring.datasource.poolPreparedStatements}")
	private boolean poolPreparedStatements = true;

	//@Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize = 20;

	//@Value("${spring.datasource.initialSize}")
	private int initialSize = 20;

	//@Value("${spring.datasource.maxActive}")
	private int maxActive = 100;

	//@Value("${spring.datasource.maxWait}")
	private int maxWait = 60000;

	//@Value("${spring.datasource.filters}")
	private String filters;

	//@Value("${spring.datasource.connectionProperties}")
	private String connectionProperties;

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the initialSize
	 */
	public int getInitialSize() {
		return initialSize;
	}

	/**
	 * @param initialSize the initialSize to set
	 */
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	/**
	 * @return the maxActive
	 */
	public int getMaxActive() {
		return maxActive;
	}

	/**
	 * @param maxActive the maxActive to set
	 */
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	/**
	 * @return the maxWait
	 */
	public int getMaxWait() {
		return maxWait;
	}

	/**
	 * @param maxWait the maxWait to set
	 */
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	/**
	 * @return the validationQuery
	 */
	public String getValidationQuery() {
		return validationQuery;
	}

	/**
	 * @param validationQuery the validationQuery to set
	 */
	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	/**
	 * @return the poolPreparedStatements
	 */
	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	/**
	 * @param poolPreparedStatements the poolPreparedStatements to set
	 */
	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	/**
	 * @return the maxPoolPreparedStatementPerConnectionSize
	 */
	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	/**
	 * @param maxPoolPreparedStatementPerConnectionSize the maxPoolPreparedStatementPerConnectionSize to set
	 */
	public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	/**
	 * @return the filters
	 */
	public String getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(String filters) {
		this.filters = filters;
	}

	/**
	 * @return the connectionProperties
	 */
	public String getConnectionProperties() {
		return connectionProperties;
	}

	/**
	 * @param connectionProperties the connectionProperties to set
	 */
	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

}
