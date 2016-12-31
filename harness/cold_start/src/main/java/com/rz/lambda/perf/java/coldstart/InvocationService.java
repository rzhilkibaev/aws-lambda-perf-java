package com.rz.lambda.perf.java.coldstart;

import com.rz.lambda.perf.java.base.Utils;

public abstract class InvocationService {

	private final String name;
	private final boolean cold;

	public InvocationService(String name, boolean cold) {
		this.name = name;
		this.cold = cold;
	}

	public void beforeInvoke(String functionName) throws Exception {
		if (cold) {
			Utils.redeployLambda(Utils.getAwsResourcePrefix() + "_" + functionName, 1_000);
		}
	}

	public abstract void invoke(String functionName) throws Exception;

	public String getName() {
		return name;
	}

	public boolean isCold() {
		return cold;
	}
}