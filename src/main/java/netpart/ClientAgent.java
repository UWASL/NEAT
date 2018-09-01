package netpart;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netpart.clientwrapper.IClientWrapper;

/**
 * RMI client agent to run of the client node
 * 
 * @author htakruri
 *
 */
public class ClientAgent {

	public static final Integer PORT = 1200;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAgent.class);

	public static void main(String[] args) throws Exception {

		String hostname = args[1];
		String implementationClassName = args[2];

		System.setProperty("java.rmi.server.hostname", hostname);
		Registry registry = LocateRegistry.createRegistry(PORT);

		LOGGER.info("RMI binding: rmi://" + hostname + ":" + PORT + "/CLIENT_WRAPPER");
		registry.rebind("CLIENT_WRAPPER", (IClientWrapper) Class.forName(implementationClassName).newInstance());
	}
}
