package com.dhisco.product.discovery.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;

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

	@Bean
	public HttpClient pdHttpClient() {
		return CitrusEndpoints.http().client().requestUrl("http://localhost:8087").build();
	}

}
