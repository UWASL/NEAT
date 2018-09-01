package example.basic;

import java.util.Arrays;
import java.util.List;

import netpart.ITest;
import netpart.node.Node;
import netpart.node.ServerNode;
import netpart.partitioner.impl.iptables.IpTablesPartition;
import netpart.partitioner.impl.iptables.IpTablesPartitioner;

/**
 * 
 * @author htakruri
 *
 */
public class BasicTest implements ITest {

	public void test(String[] args) {

		String key = "/path/to/id_rsa";
		String username = "root";
		Integer port = 22;

		ServerNode s1 = new ServerNode("s1", "10.10.1.1", port, username, key);
		ServerNode s2 = new ServerNode("s2", "10.10.1.2", port, username, key);
		ServerNode s3 = new ServerNode("s3", "10.10.1.3", port, username, key);

		IpTablesPartitioner partitioner = new IpTablesPartitioner();

		List<Node> groupA = Arrays.asList((Node) s1);
		List<Node> groupB = Arrays.asList((Node) s2, s3);

		IpTablesPartition partiton = partitioner.fullPartition(groupA, groupB);
		
		// Test system correctness here

		partitioner.heal(partiton);
		
		// Verify system state here
	}
}
