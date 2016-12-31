package com.rz.lambda.perf.java.coldstart;

import static com.google.common.collect.ImmutableList.of;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.rz.lambda.perf.java.base.Utils;

public class Main {

	private static final boolean COLD = true;
	private static final boolean WARM = false;

	public static void main(String[] args) {
		List<String> functionNames = Utils.makeCartesianProduct(of("dummy"), of("256", "512", "1024", "1536"));
		List<InvocationService> invocationServices = of(
				new ApiGwInvocationService(COLD),
				new ApiGwInvocationService(WARM),
				new LambdaInvocationService(COLD),
				new LambdaInvocationService(WARM));
		int invocations = 3;
		long executionTimeoutMinutes = 5;
		String outputCsvFileName = "output.csv";
		new ResponseTimeTest(functionNames, invocationServices, invocations, executionTimeoutMinutes, outputCsvFileName)
				.run();
	}

	public static class ApiGwInvocationService extends InvocationService {

		public ApiGwInvocationService(boolean cold) {
			super("api_gw", cold);
		}

		@Override
		public void invoke(String functionName) throws Exception {
			try (InputStream in = new URL("https://b8zm7jty53.execute-api.us-west-2.amazonaws.com/test/" + functionName).openConnection().getInputStream()) {
				while (in.read() != -1)
					;
			}
		}
	}

	public static class LambdaInvocationService extends InvocationService {

		public LambdaInvocationService(boolean cold) {
			super("lambda", cold);
		}

		@Override
		public void invoke(String functionName) throws Exception {
			Utils.invokeLambda(Utils.getAwsResourcePrefix() + "_" + functionName);
		}
	}

}
