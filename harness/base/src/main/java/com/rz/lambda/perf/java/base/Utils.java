package com.rz.lambda.perf.java.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.logs.AWSLogsClient;

public final class Utils {

	private static final String awsResourcePrefix = getSystemPropertyOrFail("aws_resource_prefix");
	private static final String apiId = getSystemPropertyOrFail("api_id");
	private static final Regions awsRegion = Regions.fromName(getSystemPropertyOrFail("aws_region"));
	private static final AWSLambdaClient awsLambda = new AWSLambdaClient().withRegion(awsRegion);
	private static final AWSLogsClient awsLogs = new AWSLogsClient().withRegion(awsRegion);

	public static List<String> makeCartesianProduct(List<String> list1, List<String> list2) {
		List<String> product = new ArrayList<>(list1.size() * list2.size());
		list1.forEach(i1 -> list2.forEach(i2 -> product.add(i1 + i2)));
		return product;
	}

	public static Regions getAwsRegion() {
		return awsRegion;
	}

	public static String getApiId() {
		return apiId;
	}

	public static String getAwsResourcePrefix() {
		return awsResourcePrefix;
	}

	public static AWSLambdaClient getAwsLambda() {
		return awsLambda;
	}

	public static AWSLogsClient getAwsLogs() {
		return awsLogs;
	}

	public static void redeployLambda(String name, long sleepMillis) {
		awsLambda.updateFunctionConfiguration(
				new UpdateFunctionConfigurationRequest()
						.withFunctionName(name)
						.withDescription(UUID.randomUUID().toString()));
		sleep(sleepMillis); // allow for redeploy
	}

	public static void invokeLambda(String name) {
		awsLambda.invoke(new InvokeRequest().withFunctionName(name));
	}

	public static void callApiGwEndpoint(String path) {
		String url = "https://" + apiId + ".execute-api." + awsRegion.getName() + ".amazonaws.com/test" + path;
		try (InputStream in = new URL(url).openConnection().getInputStream()) {
			while (in.read() != -1)
				;
		} catch (IOException e) {
			throw new RuntimeException("Error while calling api gw endpoint; url=" + url, e);
		}
	}

	public static String getSystemPropertyOrFail(String name) {
		String value = System.getProperty(name);
		if (value != null && !value.isEmpty()) {
			return value;
		}
		throw new IllegalStateException("system property " + name + " is not set");
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for redeploy", e);
		}
	}

	private Utils() {
		// prevent instantiation
	}

}
