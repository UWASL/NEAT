package netpart.crasher.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netpart.crasher.INodeCrasher;
import netpart.node.Node;
import netpart.ssh.SSHManager;

/**
 * A generic implementation of {@link INodeCrasher} that will force an immediate reboot on the node
 * 
 * @author htakruri
 *
 */
public class RebootNodeCrasher implements INodeCrasher {

	private static final Logger LOGGER = LoggerFactory.getLogger(RebootNodeCrasher.class);
	private static final String RESTART_COMMAND = "sudo reboot -f";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void crash(Node node) {
		try {
			SSHManager.INSTANCE.execute(node.getHost(), RESTART_COMMAND);
		} catch (Exception e) {
			LOGGER.info("exception while crashing node {}", node.getHost(), e);
			throw new RuntimeException("exception while crashing node", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fix(Node node) {
		// Do nothing the node will restart by itself
	}
}
