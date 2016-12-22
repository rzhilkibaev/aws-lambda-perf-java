import java.util.HashMap;
import java.util.Map;

// https://aws.amazon.com/blogs/compute/container-reuse-in-lambda/
public class Lambda {

	private int numberOfRequestsHandled = 0;

	static {
		// this is executed only once
		// when the container is created on first invocation
		System.out.println("static init");
	}

	{
		// this is executed only once
		// when the container is created on first invocation
		System.out.println("init");
	}

	public Lambda() {
		// this is executed only once
		// when the container is created on first invocation
		System.out.println("ctor");
	}

	public Map<String, String> handle(Map<String, String> request) {
		// container is unpaused/resumed/thawed
		numberOfRequestsHandled++;
		Map<String, String> response = new HashMap<>();
		response.put("lambdaAge", String.valueOf(numberOfRequestsHandled));
		return response;
		// container is paused/suspended/frozen
	}
}
