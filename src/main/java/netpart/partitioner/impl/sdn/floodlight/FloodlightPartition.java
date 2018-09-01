package netpart.partitioner.impl.sdn.floodlight;

import java.util.ArrayList;
import java.util.List;

import netpart.node.Node;
import netpart.partitioner.Partition;
import netpart.partitioner.PartitionType;

/**
 * Floodligh partition bean
 * 
 * @author htakruri
 *
 */
public class FloodlightPartition extends Partition {

	private List<String> ruleIds;

	public FloodlightPartition(PartitionType partitionType, List<List<Node>> groups, List<String> ruleIds) {
		super(partitionType, groups);
		this.ruleIds = new ArrayList<>(ruleIds);
	}

	public List<String> getRuleIds() {
		return ruleIds;
	}

	public void setRuleIds(List<String> ruleIds) {
		this.ruleIds = ruleIds;
	}
}
