package com.rz.lambda.perf.java.coldstart;

import static com.rz.lambda.perf.java.base.Utils.getAwsResourcePrefix;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rz.lambda.perf.java.base.Utils;

public class ColdStartTest {

	private static final Logger log = LoggerFactory.getLogger(ColdStartTest.class);
	private static final int[] memorySizes = { 256, 512, 1024, 1536 };
	private static final int invocationsPerMemorySize = 3;
	private static final long executionTimeoutMinutes = 60L;

	public static void main(String[] args) throws Exception {

		Path csvPath = Paths.get(ColdStartTest.class.getSimpleName() + ".csv");
		try (BufferedWriter csvWriter = Files.newBufferedWriter(csvPath, CREATE, TRUNCATE_EXISTING)) {

			csvWriter.write("ts_utc,mem_mb,duration_ms\n"); // write header

			ExecutorService executorService = Executors.newFixedThreadPool(memorySizes.length);
			log.info("Running with memory sizes {} and {} invocations per memory size",
					memorySizes,
					invocationsPerMemorySize);
			for (int memorySize : memorySizes) {
				executorService.submit(() -> runForMemorySize(memorySize, csvWriter));
			}

			executorService.shutdown();
			boolean done = executorService.awaitTermination(executionTimeoutMinutes, TimeUnit.MINUTES);
			if (done) {
				log.info("Done");
			} else {
				log.info("Execution timeout reached, terminating");
				executorService.shutdownNow();
			}
		}
	}

	private static void runForMemorySize(int memorySize, BufferedWriter csvWriter) {
		for (int i = 0; i < invocationsPerMemorySize; i++) {
			invokeCold(memorySize, csvWriter);
		}
	}

	private static void invokeCold(int memorySize, BufferedWriter csvWriter) {
		String functionName = getFunctionName(memorySize);
		Utils.redeployLambda(functionName);
		sleep(1_000); // allow for redeploy
		Instant startTs = Instant.now();
		try {
			Utils.invokeLambda(functionName);
		} catch (Exception e) {
			log.error("Error invoking lambda {} at {}", functionName, startTs, e);
		}
		long durationMs = Instant.now().toEpochMilli() - startTs.toEpochMilli();

		log.debug("Invocation timestamp={}, memorySize={}, durationMs={}", startTs, memorySize, durationMs);

		logCsvLine(csvWriter, startTs, memorySize, durationMs);
	}

	private static String getFunctionName(int memorySize) {
		return getAwsResourcePrefix() + "_dummy_" + memorySize;
	}

	private static synchronized void logCsvLine(BufferedWriter csvWriter, Instant ts, int memorySize, long duration) {
		try {
			csvWriter.write(ts + "," + memorySize + "," + duration + "\n");
		} catch (IOException e) {
			log.error("Error wihle writing into the csv file", e);
		}
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for redeploy", e);
		}
	}

}
