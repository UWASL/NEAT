package netpart.partitioner;

import java.util.List;

import netpart.node.Node;

/**
 * Basic partition object containing the details of the partition
 * 
 * @author htakruri
 *
 */
public class Partition {

	private PartitionType partitionType;
	private List<List<Node>> groups;
	
	/**
	 * Initialize this partition with the partition type and the group nodes
	 * 
	 * @param partitionType the partition type 
	 * @param groups the group nodes
	 */
	public Partition(PartitionType partitionType, List<List<Node>> groups) {
		this.partitionType = partitionType;
		this.groups = groups;
	}

	/**
	 * Returns the partition type of this partition
	 * 
	 * @return the partition type
	 */
	public PartitionType getPartitionType() {
		return partitionType;
	}

	/**
	 * @return the groups of this partition
	 */
	public List<List<Node>> getGroups() {
		return groups;
	}
}
