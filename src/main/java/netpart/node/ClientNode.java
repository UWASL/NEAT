package netpart.node;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import netpart.ClientAgent;
import netpart.clientwrapper.IClientWrapper;
import netpart.ssh.SSHManager;

/**
 * A client node in the system
 * 
 * @author htakruri
 *
 */
public class ClientNode extends Node {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientNode.class);
	private static long SLEEP_PERIOD = TimeUnit.SECONDS.toMillis(1);

	private String agentPid;
	Class<? extends IClientWrapper> clientWrapperClass;
	private IClientWrapper clientWrapper = null;

	/**
	 * @param nodeName the client node name
	 * @param host the client host name
	 * @param port the client ssh port
	 * @param username the client ssh username
	 * @param sshKeyPath the client ssh key path
	 * @param clientWrapperClass the client wrapper for this client node
	 */
	public ClientNode(String nodeName, String host, Integer port, String username, String sshKeyPath,
			Class<? extends IClientWrapper> clientWrapperClass) {

		super(nodeName, host, port, username, sshKeyPath);
		this.clientWrapperClass = clientWrapperClass;

		try {
			SSHManager.INSTANCE.connect(host, port, username, sshKeyPath);
		} catch (JSchException e) {
			throw new RuntimeException("exception while initializing client node", e);
		}
	}

	/**
	 * Returns the client wrapper associated with this client node
	 * 
	 * @return the client wrapper associated with this client node
	 */
	public IClientWrapper getClientWrapper() {

		try {
			if (clientWrapper == null) {
				String conn = "rmi://" + getHost() + ":" + ClientAgent.PORT + "/CLIENT_WRAPPER";
				clientWrapper = (IClientWrapper) Naming.lookup(conn);
			}
			return clientWrapper;

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			LOGGER.error("exception while getting client wrapper", e);
			throw new RuntimeException("exception while getting client wrapper", e);
		}
	}

	private String getJarPath() {
		return new java.io.File(ClientNode.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
	}

	/**
	 * Starts the client agent
	 * 
	 * @return Implementation-specific result from {@link IClientWrapper#start}
	 */
	public String start() {

		try {
			// FIXME - REFACTOR COMMANDS
			LOGGER.info("uploading client agent to {}", getHost());
			SSHManager.INSTANCE.uploadFile(getHost(), getJarPath(), "/tmp/netpart-agent.jar");
			SSHManager.INSTANCE.execute(getHost(), "nohup java -jar /tmp/netpart-agent.jar client-agent " + getHost()
					+ " " + clientWrapperClass.getCanonicalName() + " > /tmp/netpart-agent.log 2>&1 &");
			agentPid = SSHManager.INSTANCE
					.execute(getHost(), "ps -ef | grep -v grep | grep /tmp/netpart-agent.jar | awk '{ print $2 }'")
					.trim();
			LOGGER.info("starting client agent on {} with pid {}", getNodeName(), agentPid);
			Thread.sleep(SLEEP_PERIOD);
			return getClientWrapper().start();
		} catch (Exception e) {
			LOGGER.error("exception while initializing client node", e);
			throw new RuntimeException("exception while initializing client node", e);
		}
	}

	/**
	 * Stops the client agent
	 * 
	 * @return Implementation-specific result from {@link IClientWrapper#stop}
	 */
	public String stop() {

		try {
			LOGGER.info("stopping client on %s with pid %s", getNodeName(), agentPid);
			String result = getClientWrapper().stop();
			// FIXME REFACTOR COMMAND
			SSHManager.INSTANCE.execute(getHost(), "kill -9 " + agentPid);
			return result;
		} catch (IOException | JSchException e) {
			LOGGER.error("error while stopping client", e);
			throw new RuntimeException("error while stopping client", e);
		}
	}
}
