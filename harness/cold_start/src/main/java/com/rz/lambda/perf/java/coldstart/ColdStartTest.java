package com.rz.lambda.perf.java.coldstart;

import static com.rz.lambda.perf.java.base.Utils.getAwsResourcePrefix;

import com.google.common.math.Stats;
import com.rz.lambda.perf.java.base.Utils;

public class ColdStartTest {

	private static final int invocations = 3;

	public static void main(String[] args) throws Exception {
		runAndPrint("256");
		runAndPrint("512");
		runAndPrint("1024");
		runAndPrint("1536");
	}

	private static void runAndPrint(String memorySize) {
		long[] durations = runCase(getFunctionName(memorySize));
		Stats stats = Stats.of(durations);
		System.out.println(memorySize + ": mean=" + stats.mean() + ", min=" + stats.min() + ", max=" + stats.max());
	}

	private static long[] runCase(String functionName) {
		long[] durations = new long[invocations];
		for (int i = 0; i < invocations; i++) {
			durations[i] = invokeCold(functionName);
		}
		return durations;
	}

	private static String getFunctionName(String memorySize) {
		return getAwsResourcePrefix() + "_dummy_" + memorySize;
	}

	private static long invokeCold(String functionName) {
		Utils.redeployLambda(functionName);
		try {
			Thread.sleep(1_000); // allow for redeploy
		} catch (Throwable e) {
			e.printStackTrace();
		}
		long startMs = System.currentTimeMillis();
		try {
			Utils.invokeLambda(functionName);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis() - startMs;
	}

}
