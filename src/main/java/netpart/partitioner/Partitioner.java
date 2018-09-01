package netpart.partitioner;

import java.util.List;

import netpart.node.Node;

/**
 * 
 * @author htakruri
 *
 * @param <T> partition class
 */
public interface Partitioner<T extends Partition> {

	/**
	 * Creates a complete network partition between the given groups of nodes
	 * 
	 * @param groups the groups to partition from each other
	 * @return a {@link Partition} object containing the partition details
	 */
	T fullPartition(List<Node>... groups);

	/**
	 * Creates a partial network partitioning between groupA and groupB
	 * 
	 * @param groupA the first group of nodes
	 * @param groupB the second groups of nodes
	 * @return a {@link Partition} object containing the partition details
	 */
	T partialPartition(List<Node> groupA, List<Node> groupB);

	/**
	 * Creates a simplex network partition between groupA and groupB, where traffic can only flow from groupA to groupB
	 * 
	 * @param groupA the first group of nodes
	 * @param groupB the second group of nodes
	 * @return a {@link Partition} object containing the partition details
	 */
	T simplexPartition(List<Node> groupA, List<Node> groupB);

	/**
	 * Heals the given partition
	 * 
	 * @param partition the partition to heal
	 */
	void heal(T partition);
}
