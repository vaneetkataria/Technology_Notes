package com.dhisco.product.discovery.testcases;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeTestSupport;
import com.dhisco.product.discovery.utils.CassandraConnector;
import com.dhisco.product.discovery.utils.KafkaAdminTasksSupport;
import com.dhisco.product.discovery.utils.ProcessManager;

@Component
public class BeforeCountAndValidateShoppingMessagesIT extends TestRunnerBeforeTestSupport {

	@Autowired
	private CassandraConnector cassandraConnector;

	@Autowired
	private KafkaAdminTasksSupport kafkaAdminTasksSupport;

	@Autowired
	private BasicDataSource appconfigDb;

	@Autowired
	private ProcessManager processManager;

	public BeforeCountAndValidateShoppingMessagesIT() {
		setNamePattern("countAndValidateShoppingMessages");

	}

	@Override
	public void beforeTest(TestRunner runner) {

		runner.sql(eb -> eb.dataSource(appconfigDb)
				.sqlResource("classpath:testcases/sql/CountAndValidateShoppingMessagesIT_setup.sql"));

		// Initiate Necessary Kafka Topics for this TestCase
		/*
		 * kafkaAdminTasksSupport.createTopics("127.0.0.1:2181",
		 * Arrays.asList(new NewTopic("p2d_updates_final", 1, (short) 1), new
		 * NewTopic("stp-admin-scheduler", 1, (short) 1), new
		 * NewTopic("product.discovery.property.create", 1, (short) 1), new
		 * NewTopic("product.discovery.property.state", 1, (short) 1), new
		 * NewTopic("product.discovery.property.shop", 1, (short) 1)));
		 */

		// Initiate necessary key spaces and column families in Cassandra
		cassandraConnector.connect("valdbsdevcas01a.asp.dhisco.com", 9042)
				.executeCql("src/test/resources/testcases/cql/CountAndValidateShoppingMessagesIT_setup.cql");

		// Start Config Service
		processManager.executeLongRunningProcess(
				"java -jar D:\\workspace\\projects\\rategain\\repos\\DS\\p2d_configurationservice\\target\\configuration-service-1.0.83-SNAPSHOT.jar --spring.profiles.active=test --server.port=8086",
				"Started ConfigurationService", "APPLICATION FAILED TO START");

		// Start Product Discovery
		processManager.executeLongRunningProcess(
				"java -jar D:\\workspace\\projects\\rategain\\repos\\DS\\product-discovery\\target\\product-discovery-1.2.1-SNAPSHOT.jar --spring.profiles.active=test",
				"Started ProductDiscoveryApplication", "APPLICATION FAILED TO START");

	}

}
