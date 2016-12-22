package com.rz.lambda.perf.java.lifecycle;

import java.time.Instant;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;

public class LifecycleTest {

	private static final String lambdaFunctionName;
	private static final AWSLambdaClient awsLambda;
	private static final AWSLogsClient awsLogs;

	static {
		lambdaFunctionName = getSystemPropertyOrFail("aws_resource_prefix") + "_lifecycle";
		String region = getSystemPropertyOrFail("aws_region");
		awsLambda = new AWSLambdaClient().withRegion(Regions.fromName(region));
		awsLogs = new AWSLogsClient().withRegion(Regions.fromName(region));
	}

	public static void main(String[] args) throws Exception {

		long startTime = Instant.now().toEpochMilli();

		redeployLambda();

		System.out.println("Waiting for the lambda to deploy");
		Thread.sleep(5_000);

		invokeLambdaThreeTimes();

		System.out.println("Waiting for the logs to show up in CW");
		Thread.sleep(30_000);

		printLogs(startTime);
	}

	private static void redeployLambda() {
		awsLambda.updateFunctionConfiguration(
				new UpdateFunctionConfigurationRequest()
						.withFunctionName(lambdaFunctionName)
						.withDescription(UUID.randomUUID().toString()));
	}

	private static void printLogs(long startTime) {
		awsLogs.filterLogEvents(
				new FilterLogEventsRequest()
						.withStartTime(startTime)
						.withLogGroupName("/aws/lambda/" + lambdaFunctionName))
				.getEvents()
				.forEach(e -> System.out.print(e.getMessage()));
	}

	private static void invokeLambdaThreeTimes() {
		InvokeRequest request = new InvokeRequest().withFunctionName(lambdaFunctionName);
		awsLambda.invoke(request);
		awsLambda.invoke(request);
		awsLambda.invoke(request);
	}

	private static String getSystemPropertyOrFail(String name) {
		String value = System.getProperty(name);
		if (value != null && !value.isEmpty()) {
			return value;
		}
		throw new RuntimeException("system property " + name + " is not set");
	}

}
