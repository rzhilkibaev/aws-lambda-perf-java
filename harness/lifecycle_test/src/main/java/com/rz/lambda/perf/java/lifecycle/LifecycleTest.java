package com.rz.lambda.perf.java.lifecycle;

import java.time.Instant;

import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.rz.lambda.perf.java.base.Utils;

public class LifecycleTest {

	public static void main(String[] args) throws Exception {

		String lambdaFunctionName = Utils.getAwsResourcePrefix() + "_dummy_256";

		long startTime = Instant.now().toEpochMilli();

		Utils.redeployLambda(lambdaFunctionName);

		System.out.println("Waiting for the lambda to deploy");
		Thread.sleep(5_000);

		Utils.invokeLambda(lambdaFunctionName);
		Utils.invokeLambda(lambdaFunctionName);
		Utils.invokeLambda(lambdaFunctionName);

		System.out.println("Waiting for the logs to show up in CW");
		Thread.sleep(30_000);

		printLogs(startTime, lambdaFunctionName);
	}

	private static void printLogs(long startTime, String lambdaFunctionName) {
		Utils.getAwsLogs().filterLogEvents(
				new FilterLogEventsRequest()
						.withStartTime(startTime)
						.withLogGroupName("/aws/lambda/" + lambdaFunctionName))
				.getEvents()
				.forEach(e -> System.out.print(e.getMessage()));
	}

}
