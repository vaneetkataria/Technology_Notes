package com.dhisco.product.discovery.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessManager {

	private static final int COMMAND_EXECUTION_SUCCESSFUL = 0;
	private static final int COMMAND_EXECUTION_FAILED = 1;
	private static final String COMMAND_SPLILLER = " ";

	@Autowired
	private ProcessRegistry processRegistry;

	/**
	 * This Method must be used if subprocess to be invoked returns in a failure
	 * case but do not return in a success case . For example starting a server
	 * .
	 */
	public Process executeLongRunningProcess(String command, String successIndicator, String failureIndicator) {
		Process process = null;
		String line;
		int exitCode = -1;
		try {
			process = new ProcessBuilder(command.split(COMMAND_SPLILLER)).redirectErrorStream(true).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				if (line.contains(successIndicator)) {
					exitCode = COMMAND_EXECUTION_SUCCESSFUL;
					break;
				}
				if (line.contains(failureIndicator)) {
					exitCode = COMMAND_EXECUTION_FAILED;
					break;
				}
			}
			/*new Thread(() -> {
				while (true) {
					try {
						System.out.println(reader.readLine());
					} catch (IOException e) {
					}
				}
			});*/

		} catch (IOException e) {
			exitCode = COMMAND_EXECUTION_FAILED;
		}
		if (exitCode != COMMAND_EXECUTION_SUCCESSFUL)
			throw new RuntimeException("Exception occured while starting a process with command :" + command);

		processRegistry.register(command, process);
		return process;
	}

	/**
	 * This Method must be used if subprocess to be invoked returns either in a
	 * succee or a failure case. For example
	 */
	public Process executeShortRunningProcess(String command) {
		Process process;
		try {
			process = new ProcessBuilder(command.split(COMMAND_SPLILLER)).inheritIO().start();
			if (process.waitFor() != COMMAND_EXECUTION_SUCCESSFUL)
				throw new RuntimeException("Process to be started with command :" + command + " Failed!!");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(
					"Exception occured while starting a Process with command :" + command + " Failed!!", e);

		}

		return process;
	}

	public void killAllProcesses() {
		processRegistry.unregisterAll().forEach(this::killProcess);
	}

	private void killProcess(Process process) {
		if (process.isAlive()) {
			process.destroy();
			if (process.isAlive())
				process.destroyForcibly();
		}
	}

}
