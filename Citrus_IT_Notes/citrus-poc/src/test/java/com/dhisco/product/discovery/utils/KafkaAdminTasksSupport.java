package com.dhisco.product.discovery.utils;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.utils.Time;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import kafka.admin.RackAwareMode;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;

@Component
public class KafkaAdminTasksSupport {

	private int sessionTimeOut = 15 * 1000;
	private int connectionTimeOut = 10 * 1000;
	private int maxInFlightRequests = 10;

	public KafkaAdminTasksSupport() {
	}

	public KafkaAdminTasksSupport(int sessionTimeOut, int connectionTimeOut, int maxInFlightRequests) {
		super();
		this.sessionTimeOut = sessionTimeOut;
		this.connectionTimeOut = connectionTimeOut;
		this.maxInFlightRequests = maxInFlightRequests;
	}

	public void createTopics(String zookeeperHost, List<NewTopic> topics) {
		Assert.isTrue(Objects.nonNull(zookeeperHost) && !zookeeperHost.isEmpty(), "Zookeeper Host must be specified!");
		Assert.isTrue(Objects.nonNull(topics) && topics.size() > 0, "Topics must be specified!");

		AdminZkClient adminZkClient = createAdminZkClient(zookeeperHost);

		Properties topicConfig = new Properties();
		topicConfig.put("cleanup.policy", "delete");

		topics.forEach(topic -> {
			adminZkClient.createTopic(topic.name(), topic.numPartitions(), topic.replicationFactor(), topicConfig,
					RackAwareMode.Disabled$.MODULE$);
		});
	}

	public void deleteTopics(String zookeeperHost, List<String> topics) {
		AdminZkClient adminZkClient = createAdminZkClient(zookeeperHost);
		topics.forEach(adminZkClient::deleteTopic);
	}

	private AdminZkClient createAdminZkClient(String zookeeperHost) {
		return new AdminZkClient(KafkaZkClient.apply(zookeeperHost, false, sessionTimeOut, connectionTimeOut,
				maxInFlightRequests, Time.SYSTEM, "", ""));
	}

}
