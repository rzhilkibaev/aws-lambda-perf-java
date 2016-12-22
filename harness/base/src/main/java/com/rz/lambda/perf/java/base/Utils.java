package com.rz.lambda.perf.java.base;

import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.logs.AWSLogsClient;

public final class Utils {

	private static final Regions awsRegion = Regions.fromName(getSystemPropertyOrFail("aws_region"));
	private static final AWSLambdaClient awsLambda = new AWSLambdaClient().withRegion(awsRegion);
	private static final AWSLogsClient awsLogs = new AWSLogsClient().withRegion(awsRegion);

	public static String getAwsResourcePrefix() {
		return getSystemPropertyOrFail("aws_resource_prefix");
	}

	public static AWSLambdaClient getAwsLambda() {
		return awsLambda;
	}

	public static AWSLogsClient getAwsLogs() {
		return awsLogs;
	}

	public static void redeployLambda(String name) {
		awsLambda.updateFunctionConfiguration(
				new UpdateFunctionConfigurationRequest()
						.withFunctionName(name)
						.withDescription(UUID.randomUUID().toString()));
	}

	public static void invokeLambda(String name) {
		awsLambda.invoke(new InvokeRequest().withFunctionName(name));
	}

	private static String getSystemPropertyOrFail(String name) {
		String value = System.getProperty(name);
		if (value != null && !value.isEmpty()) {
			return value;
		}
		throw new RuntimeException("system property " + name + " is not set");
	}

	private Utils() {
		// prevent instantiation
	}

}
