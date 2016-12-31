package com.rz.lambda.perf.java.coldstart;

public abstract class InvocationService {

	public void beforeInvoke(String param) throws Exception {
		// do nothing by default
	}

	public abstract void invoke(String param) throws Exception;
}