package netpart.partitioner.impl.iptables;

import java.util.List;

import netpart.node.Node;
import netpart.partitioner.Partition;
import netpart.partitioner.PartitionType;

/**
 * Iptables partition bean
 * 
 * @author amsalqur
 *
 */
public class IpTablesPartition extends Partition {

	public IpTablesPartition(PartitionType partitionType, List<List<Node>> groups) {
		super(partitionType, groups);
	}
}
