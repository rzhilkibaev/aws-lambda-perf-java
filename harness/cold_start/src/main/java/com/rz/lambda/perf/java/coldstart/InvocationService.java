package com.rz.lambda.perf.java.coldstart;

public abstract class InvocationService {

	public void beforeInvoke(String param) throws Exception {
	}

	public abstract void invoke(String param) throws Exception;
}