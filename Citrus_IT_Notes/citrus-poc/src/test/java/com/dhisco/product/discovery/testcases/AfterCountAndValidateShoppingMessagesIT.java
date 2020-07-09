package com.dhisco.product.discovery.testcases;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerAfterTestSupport;
import com.dhisco.product.discovery.utils.CassandraConnector;
import com.dhisco.product.discovery.utils.ProcessManager;
import com.dhisco.product.discovery.utils.KafkaAdminTasksSupport;

@Component
public class AfterCountAndValidateShoppingMessagesIT extends TestRunnerAfterTestSupport {

	@Autowired
	private CassandraConnector cassandraConnector;

	@Autowired
	private KafkaAdminTasksSupport kafkaAdminTasksSupport;

	@Autowired
	private ProcessManager processManager;

	@Autowired
	private BasicDataSource appconfigDb;

	public AfterCountAndValidateShoppingMessagesIT() {
		setNamePattern("countAndValidateShoppingMessages");

	}

	@Override
	public void afterTest(TestRunner runner) {
		// Killing all long running process started while beforeTest phase.
		processManager.killAllProcesses();

		runner.sql(eb -> eb.dataSource(appconfigDb)
				.sqlResource("classpath:testcases/sql/CountAndValidateShoppingMessagesIT_teardown.sql"));

		/*
		 * runner.zookeeper(eb ->
		 * eb.delete("/brokers/topics/p2d_updates_final"));
		 */
		/*
		 * kafkaAdminTasksSupport.deleteTopics("127.0.0.1:2181",
		 * Arrays.asList("p2d_updates_final", "stp-admin-scheduler",
		 * "product.discovery.property.create",
		 * "product.discovery.property.state",
		 * "product.discovery.property.shop"));
		 */

		cassandraConnector.connect("valdbsdevcas01a.asp.dhisco.com", 9042)
				.executeCql("src/test/resources/testcases/cql/CountAndValidateShoppingMessagesIT_teardown.cql");

	}
}
