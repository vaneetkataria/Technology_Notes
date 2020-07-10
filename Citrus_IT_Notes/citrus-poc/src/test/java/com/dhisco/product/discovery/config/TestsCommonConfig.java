package com.dhisco.product.discovery.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestsCommonConfig {

	@Bean(destroyMethod = "close")
	public BasicDataSource appconfigDb() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
		dataSource.setUrl("jdbc:mariadb://localhost:3307");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setInitialSize(1);
		dataSource.setMaxActive(5);
		dataSource.setMaxIdle(2);
		return dataSource;
	}

}
