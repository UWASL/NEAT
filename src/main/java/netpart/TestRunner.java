package netpart;

/**
 * The Java main class for running NEAT tests
 * 
 * @author htakruri
 * 
 */
public class TestRunner {

	private static void usage() {
		
		System.out.println("Usage: java -jar netpart.jar <class_name>");
		System.out.println("\tclass_name: the FQN of the system class to test");
	}
	
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		if ("client-agent".equals(args[0])) {
			ClientAgent.main(args);
		} else {
			Object instance = Class.forName(args[0]).newInstance();
			Class.forName(args[0]).getMethod("test", String[].class).invoke(instance, (Object) args);
		}
	}
}
