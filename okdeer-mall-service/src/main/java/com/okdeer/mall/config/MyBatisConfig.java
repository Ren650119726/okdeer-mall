package com.okdeer.mall.config;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.dal.CatMybatisPlugins;


@Configuration
@EnableTransactionManagement
public class MyBatisConfig implements TransactionManagementConfigurer {


	private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);

	@Autowired
	private DataSourceProperties properties;
	
	@Bean
	public DruidDataSource getDataSource() {
		logger.info("DruidDataSource开始连接数据源...");
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName(this.properties.getDriverClass());
		ds.setUrl(this.properties.getUrl());
		ds.setUsername(this.properties.getUsername());
		ds.setPassword(this.properties.getPassword());
		ds.setMaxActive(this.properties.getMaxActive());
		ds.setInitialSize(this.properties.getInitialSize());
		ds.setValidationQuery(this.properties.getValidationQuery());
		ds.setPoolPreparedStatements(this.properties.isPoolPreparedStatements());
		ds.setMaxPoolPreparedStatementPerConnectionSize(this.properties.getMaxPoolPreparedStatementPerConnectionSize());
		ds.setTestWhileIdle(true);
		ds.setTestOnBorrow(false);
		ds.setTestOnReturn(false);
		try {
			ds.setFilters(this.properties.getFilters());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ds.setConnectionProperties(this.properties.getConnectionProperties());
		return ds;
	}

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setConfigLocation(new ClassPathResource("mybatis/mybatis-mall-config.xml"));

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        CatMybatisPlugins catMybatis  = new CatMybatisPlugins();
        //添加插件
        bean.setPlugins(new Interceptor[]{pageHelper,catMybatis});

        try {
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }
}
