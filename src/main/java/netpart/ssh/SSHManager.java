package netpart.ssh;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * @author htakruri
 *
 */
public enum SSHManager {

	INSTANCE;
	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	/**
	 * Make an ssh connection to the server
	 * 
	 * @param host the host name of the server
	 * @param port the ssh port
	 * @param username the ssh username
	 * @param key the authentication key
	 * @throws JSchException
	 */
	public void connect(String host, Integer port, String username, String key) throws JSchException {

		JSch.setConfig("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		jsch.addIdentity(key);

		Session session = jsch.getSession(username, host, port);
		if (sessions.putIfAbsent(host, session) == null) {
			session.connect();
		}
	}

	/**
	 * Disconnects the connection to the given host
	 * 
	 * @param host
	 * @throws JSchException
	 */
	public void disconnect(String host) throws JSchException {
		sessions.get(host).disconnect();
	}

	/**
	 * Uploads a file to the given host
	 * 
	 * @param host the host name to upload the file to
	 * @param localPath the local path of the file
	 * @param remotePath the remote path of the file
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void uploadFile(String host, String localPath, String remotePath) throws JSchException, SftpException {

		ChannelSftp channel = (ChannelSftp) sessions.get(host).openChannel("sftp");
		channel.connect();
		channel.put(localPath, remotePath, ChannelSftp.OVERWRITE);
	}

	/**
	 * Executes a command on the given host
	 * 
	 * @param host the host to execute the command on
	 * @param command the command to be executed
	 * @return the command output
	 * @throws IOException
	 * @throws JSchException
	 */
	public String execute(String host, String command) throws IOException, JSchException {

		ChannelExec channel = (ChannelExec) sessions.get(host).openChannel("exec");
		channel.setCommand(command);
		channel.connect();
		String result = IOUtils.toString(channel.getInputStream(), StandardCharsets.UTF_8.name());
		channel.disconnect();
		return result;
	}

	/**
	 * Closes all open ssh connections
	 */
	public void closeAllConnections() {

		for (Session session : sessions.values()) {
			session.disconnect();
		}
	}
}
