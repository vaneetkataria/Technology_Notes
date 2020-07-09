package com.dhisco.product.discovery.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/*
 * Supports holding Process Objects created by a test in BeforeTest part 
 * so that they can be destroyed in afterTest Part.
 * 
 * This class assumes that only one testcase is running at a time.
 */

@Component
public class ProcessRegistry {

	private Map<String, Process> processes = new HashMap<>();

	public void register(String name, Process process) {
		processes.put(name, process);
	}

	public List<Process> unregisterAll() {
		return processes.values().stream().collect(Collectors.toList());
	}

}
