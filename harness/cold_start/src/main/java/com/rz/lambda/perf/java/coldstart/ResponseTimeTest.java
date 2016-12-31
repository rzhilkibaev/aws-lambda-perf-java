package com.rz.lambda.perf.java.coldstart;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseTimeTest {

	private final Logger log = LoggerFactory.getLogger(ResponseTimeTest.class);
	private final List<String> functionNames;
	private final List<InvocationService> invocationServices;
	private final int invocations;
	private final long executionTimeoutMinutes;
	private final String csvFileName;

	public ResponseTimeTest(List<String> functionNames,
			List<InvocationService> invocationServices,
			int invocations,
			long executionTimeoutMinutes,
			String csvFileName) {
		this.functionNames = functionNames;
		this.invocationServices = invocationServices;
		this.invocations = invocations;
		this.executionTimeoutMinutes = executionTimeoutMinutes;
		this.csvFileName = csvFileName;
	}

	public void run() {

		ExecutorService executorService = Executors.newFixedThreadPool(functionNames.size());
		List<Future<List<InvocationResult>>> futures = functionNames.stream()
				.map(this::createCallable)
				.map(executorService::submit)
				.collect(Collectors.toList());

		shutdownExecutorService(executorService);

		List<InvocationResult> results = futures.stream()
				.map(this::getResult)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

		writeToCsv(results);
	}

	private void writeToCsv(List<InvocationResult> results) {
		Path csvPath = Paths.get(csvFileName);
		try (BufferedWriter csvWriter = Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			// write header
			csvWriter.write("ts_utc,logical_name,memory_size_mb,invoker,state,duration_ms\n");
			for (InvocationResult r : results) {
				String[] pair = r.functionName.split("_");
				String functionLogicalName = pair[0];
				String memorySize = pair[1];

				csvWriter.write(new StringJoiner(",")
						.add(r.timestamp.toString())
						.add(functionLogicalName)
						.add(memorySize)
						.add(r.invocationService.getName())
						.add(r.invocationService.isCold() ? "cold": "warm")
						.add(String.valueOf(r.durationMs))
						.toString() + "\n");
			}
		} catch (IOException e) {
			new RuntimeException("Failed to write to csv file " + csvFileName, e);
		}
	}

	private void shutdownExecutorService(ExecutorService executorService) {
		try {
			executorService.shutdown();
			boolean done = executorService.awaitTermination(executionTimeoutMinutes, TimeUnit.MINUTES);
			if (done) {
				log.info("Done");
			} else {
				log.info("Execution timeout reached, terminating");
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
	}

	private List<InvocationResult> getResult(Future<List<InvocationResult>> future) {
		try {
			return future.get(executionTimeoutMinutes, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error("Execution timeout reached");
		}
		return Collections.emptyList();
	}

	private Callable<List<InvocationResult>> createCallable(String functionName) {
		return () -> {
			List<InvocationResult> invocationResults = new ArrayList<>();
			for (int i = 0; i < invocations; i++) {
				// in a thread each function is invoked by multiple invocation services
				for (InvocationService invocationService : invocationServices) {
					invocationResults.add(invoke(functionName, invocationService));
				}
			}
			return invocationResults;
		};
	}

	private InvocationResult invoke(String functionName, InvocationService invocationService) {
		InvocationResult invocationResult = new InvocationResult();
		invocationResult.invocationService = invocationService;
		invocationResult.functionName = functionName;
		try {
			invocationService.beforeInvoke(functionName);

			invocationResult.timestamp = Instant.now();
			invocationService.invoke(functionName);
			invocationResult.durationMs = Instant.now().toEpochMilli() - invocationResult.timestamp.toEpochMilli();

			log.debug("Sucessfull invocation invocationResult={}", invocationResult);
		} catch (Exception e) {
			log.debug("Failed invocation invocationResult={}", invocationResult, e);
		}
		return invocationResult;
	}

	private final class InvocationResult {
		public Instant timestamp;
		public InvocationService invocationService;
		public String functionName;
		public long durationMs;

		@Override
		public String toString() {
			return "timestamp=" + timestamp
					+ ", invocationService=" + invocationService.getClass().getSimpleName()
					+ ", fullFunctionName=" + functionName
					+ ", durationMs=" + durationMs;
		}
	}

}
