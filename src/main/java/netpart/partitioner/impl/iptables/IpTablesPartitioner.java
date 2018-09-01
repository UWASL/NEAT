package netpart.partitioner.impl.iptables;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import netpart.node.Node;
import netpart.partitioner.PartitionType;
import netpart.partitioner.Partitioner;
import netpart.ssh.SSHManager;

/**
 * An Iptables partitioner implementation
 * 
 * @author amsalqur
 *
 */
// FIXME Refactor Strings
public class IpTablesPartitioner implements Partitioner<IpTablesPartition> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IpTablesPartitioner.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IpTablesPartition fullPartition(List<Node>... groups) {

		for (int i = 0; i < groups.length - 1; i++) {
			for (int j = i + 1; j < groups.length; j++) {
				partialPartition(groups[i], groups[j]);
			}
		}

		return new IpTablesPartition(PartitionType.FULL, Arrays.asList(groups));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IpTablesPartition partialPartition(List<Node> groupA, List<Node> groupB) {

		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				installDuplexFlowRule(nodeA.getHost(), nodeB.getHost());
			}
		}
		return new IpTablesPartition(PartitionType.PARTIAL, Arrays.asList(groupA, groupB));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IpTablesPartition simplexPartition(List<Node> groupA, List<Node> groupB) {

		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				LOGGER.info("installing simplex flow rule between {} and {}", nodeA.getHost(), nodeB.getHost());
				installFlowRule(nodeA.getHost(), nodeB.getHost());
			}
		}
		return new IpTablesPartition(PartitionType.SIMPLEX, Arrays.asList(groupA, groupB));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void heal(IpTablesPartition partition) {
		
		switch (partition.getPartitionType()) {
		case PARTIAL:
			healFullPartition(partition);
			break;
		case FULL:
			healFullPartition(partition);
			break;

		case SIMPLEX:
			//FIXME Support Simplex
			break;

		default:
			break;
		}
	}

	/**
	 * Installs flaw rules denying the traffic from both hosts
	 * 
	 * @param srcIp the first host
	 * @param dstIp the second host
	 */
	private void installDuplexFlowRule(String srcIp, String dstIp) {
		
		LOGGER.info("installing duplex flow rule between {} and {}", srcIp, dstIp);
		installFlowRule(srcIp, dstIp);
		installFlowRule(dstIp, srcIp);
	}

	/**
	 * Installs a single flow rule denying the traffic from the source to the destination
	 * 
	 * @param srcIp the source IP address 
	 * @param dstIp the destination IP address
	 */
	private void installFlowRule(String srcIp, String dstIp) {
		
		try {
			String inFlowRule = "sudo iptables -A INPUT -j DROP -s " + srcIp;
			SSHManager.INSTANCE.execute(dstIp, inFlowRule);
		} catch (IOException | JSchException e) {
			LOGGER.error("exception while installing iptables rule", e);
			throw new RuntimeException("exception while installing iptables rule", e);
		}
	}

	/**
	 * Heals a full network partition
	 * 
	 * @param partition the partition to heal
	 */
	private void healFullPartition(IpTablesPartition partition) {
		
		List<List<Node>> groups = partition.getGroups();
		for (int i = 0; i < groups.size()-1; i++) {
			for (int j = i + 1; j < groups.size(); j++) {
				healPartitionedGroups(groups.get(i), groups.get(j));
			}
		}
	}

	/**
	 * Heals the partition between the two given groups
	 *  
	 * @param groupA the first group of nodes
	 * @param groupB the second group of nodes
	 */
	private void healPartitionedGroups(List<Node> groupA, List<Node> groupB) {
		
		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				LOGGER.info("Removing flow rule between {} and {}", nodeA.getHost(), nodeB.getHost());
				deleteFlowRule(nodeA.getHost(), nodeB.getHost());
				deleteFlowRule(nodeB.getHost(), nodeA.getHost());
			}
		}
	}
	
	/**
	 * Deletes the flow between the given hosts
	 * 
	 * @param srcIp the source host IP
	 * @param dstIp the destination host IP
	 */
	private void deleteFlowRule(String srcIp, String dstIp) {
		
		String deleteRule = "sudo iptables -D INPUT -j DROP -s " + srcIp;
		try {
			SSHManager.INSTANCE.execute(dstIp, deleteRule);
		} catch (IOException | JSchException e) {
			LOGGER.error("exception while deleting iptables rule", e);
			throw new RuntimeException("exception while deleting iptables rule", e);
		}
	}
}
