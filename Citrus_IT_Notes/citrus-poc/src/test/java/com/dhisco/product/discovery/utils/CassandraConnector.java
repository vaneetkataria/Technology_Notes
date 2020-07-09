package com.dhisco.product.discovery.utils;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Session;

@Component
public class CassandraConnector {

	private Cluster cluster;

	private Session session;

	public CassandraConnector connect(String node, Integer port) {
		Builder b = Cluster.builder().addContactPoint(node);
		if (port != null) {
			b.withPort(port);
		}
		cluster = b.build();

		session = cluster.connect();
		return this;
	}

	public Session getSession() {
		return this.session;
	}

	public void close() {
		session.close();
		cluster.close();
	}

	public void executeCql(String filePath) {
		try {
			Files.readAllLines(Paths.get(filePath)).forEach(session::execute);
		} catch (Exception e) {
			throw new RuntimeException("Execution of Cql file" + filePath + "failed!!", e);
		}
	}

}
