package netpart.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import netpart.ssh.SSHManager;

/**
 * A server node in the system
 * 
 * @author htakruri
 *
 */
public class ServerNode extends Node {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerNode.class);
	
	/**
	 * @param nodeName the server node name
	 * @param host the server host name
	 * @param port the server ssh port
	 * @param username the server ssh username
	 * @param sshKeyPath the server ssh key path
	 */
	public ServerNode(String nodeName, String host, Integer port, String username, String sshKeyPath) {
		super(nodeName, host, port, username, sshKeyPath);

		try {
			SSHManager.INSTANCE.connect(host, port, username, sshKeyPath);
		} catch (JSchException e) {
			LOGGER.error("exception while initializing server node", e);
			throw new RuntimeException("exception while initializing server node", e);
		}
	}
}
