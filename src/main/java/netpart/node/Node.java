package netpart.node;

/**
 * A basic node in the system
 * 
 * @author htakruri
 *
 */
public abstract class Node {

	private String nodeName;
	private String host;
	private Integer port;
	private String username;
	private String sshKeyPath;

	/**
	 * @param nodeName the node name
	 * @param host the host name
	 * @param port the ssh port
	 * @param username the ssh username
	 * @param sshKeyPath the ssh key path
	 */
	public Node(String nodeName, String host, Integer port, String username, String sshKeyPath) {

		this.nodeName = nodeName;
		this.host = host;
		this.port = port;
		this.username = username;
		this.sshKeyPath = sshKeyPath;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSshKeyPath() {
		return sshKeyPath;
	}

	public void setSshKeyPath(String sshKeyPath) {
		this.sshKeyPath = sshKeyPath;
	}
}
