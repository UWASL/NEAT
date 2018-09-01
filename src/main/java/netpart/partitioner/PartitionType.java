package netpart.partitioner;

/**
 * The supported network partitioning types
 * 
 * @author htakruri
 *
 */
public enum PartitionType {
	
	/**
	 * FULL partition represents a complete communication disconnection between the nodes in the system
	 */
	FULL, 
	
	/**
	 * PARTIAL partition represents a partial communication disconnection between the nodes in the system
	 */
	PARTIAL, 
	
	/**
	 * SIMPLEX partition allows packets to traverse only from source to destination nodes, but not in the other direction
	 */
	SIMPLEX;
}
