package com.rz.lambda.perf.java.coldstart;

import static com.google.common.collect.ImmutableList.of;

import java.util.List;

import com.rz.lambda.perf.java.base.Utils;

public class Main {

	public static void main(String[] args) {
		List<String> functionNames = Utils.makeCartesianProduct(of("dummy"), of("256", "512", "1024", "1536"));
		List<InvocationService> invocationServices = of(new DefaultLambdaInvocationService(), new ColdLambdaInvocationService());
		int invocations = 3;
		long executionTimeoutMinutes = 5;
		String outputCsvFileName = "output.csv";
		new ResponseTimeTest(functionNames, invocationServices, invocations, executionTimeoutMinutes, outputCsvFileName)
		.run();
	}

	public static class ColdLambdaInvocationService extends InvocationService {

		@Override
		public void beforeInvoke(String param) throws Exception {
			Utils.redeployLambda(param, 1_000);
		}

		@Override
		public void invoke(String param) throws Exception {
			Utils.invokeLambda(param);
		}
	}

	public static class DefaultLambdaInvocationService extends InvocationService {

		@Override
		public void beforeInvoke(String param) throws Exception {
			// nothing here
		}

		@Override
		public void invoke(String param) throws Exception {
			Utils.invokeLambda(param);
		}
	}

}
