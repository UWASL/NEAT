package netpart.partitioner.impl.sdn.floodlight;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netpart.node.Node;
import netpart.partitioner.PartitionType;
import netpart.partitioner.Partitioner;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * A FloodLight partitioner implementation
 * 
 * @author htakruri
 *
 */
public class FloodlightPartitioner implements Partitioner<FloodlightPartition> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FloodlightPartitioner.class);
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String FLOW_TEMPLATE = "{" +
			"    \"switch\":\"%s\","+ 
			"    \"name\":\"rule_%s\"," + 
			"    \"priority\":\"32769\"," + 
			"    \"ipv4_src\":\"%s/32\"," + 
			"    \"ipv4_dst\":\"%s/32\"," + 
			"    \"eth_type\":\"0x0800\"," + 
			"    \"active\":\"true\"," + 
			"    \"actions\":\"output=\"" +
			"}";
	
	private static final String DELETE_FLOW = "{" +
			"    \"switch\":\"%s\","+ 
			"    \"name\":\"rule_%s\"" +
			"}";
	
	private String endpointURL; // e.g. http://127.0.0.1:8080/wm/staticflowpusher/json
	private String switchId;
	private OkHttpClient client = new OkHttpClient();

	/**
	 * @param endpointURL the REST endpoint url of FloodLight controller
	 * @param switchId the switch id to install the rules on
	 */
	public FloodlightPartitioner(String endpointURL, String switchId) {
		
		this.endpointURL = endpointURL;
		this.switchId = switchId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FloodlightPartition fullPartition(List<Node>... groups) {

		List<String> flowRules = new LinkedList<>();

		for (int i = 0; i < groups.length - 1; i++) {
			for (int j = i + 1; j < groups.length; j++) {
				flowRules.addAll(partialPartition(groups[i], groups[j]).getRuleIds());
			}
		}

		return new FloodlightPartition(PartitionType.FULL, Arrays.asList(groups), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FloodlightPartition partialPartition(List<Node> groupA, List<Node> groupB) {

		List<String> flowRules = new LinkedList<>();

		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				flowRules.addAll(installDuplexFlowRule(nodeA.getHost(), nodeB.getHost()));
			}
		}

		return new FloodlightPartition(PartitionType.PARTIAL, Arrays.asList(groupA, groupB), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FloodlightPartition simplexPartition(List<Node> groupA, List<Node> groupB) {

		List<String> flowRules = new LinkedList<>();
		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				LOGGER.info("installing simplex flow rule between {} and {}", nodeA.getHost(), nodeB.getHost());
				flowRules.add(installFlowRule(nodeA.getHost(), nodeB.getHost()));
			}
		}
		return new FloodlightPartition(PartitionType.SIMPLEX, Arrays.asList(groupA, groupB), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void heal(FloodlightPartition partition) {

		for (String ruleId : partition.getRuleIds()) {
			deleteFlowRule(ruleId);
		}
	}

	/**
	 * Installs flaw rules to deny traffic between two hosts
	 * 
	 * @param srcIp the first host
	 * @param dstIp the second host
	 * @return the installed rule ids
	 */
	private List<String> installDuplexFlowRule(String srcIp, String dstIp) {

		LOGGER.info("installing duplex flow rule between {} and {}", srcIp, dstIp);
		return Arrays.asList(installFlowRule(srcIp, dstIp), installFlowRule(dstIp, srcIp));
	}

	/**
	 * Installs a single flow rule denying the traffic from the source to the destination
	 * 
	 * @param srcIp the source ip address 
	 * @param dstIp the destination ip address
	 * @return the installed rule id
	 */
	public String installFlowRule(String srcIp, String dstIp) {

		String ruleId = UUID.randomUUID().toString();
		String json = String.format(FLOW_TEMPLATE, switchId, ruleId, srcIp, dstIp);
		System.out.println(json);
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(endpointURL).post(body).build();
		try {
			client.newCall(request).execute();
			return ruleId;
		} catch (IOException e) {
			LOGGER.error("Exception while installing flaw rules", e);
			throw new RuntimeException("Exception while installing flaw rules", e);
		}
	}

	/**
	 * Deletes the flow rule of a given id
	 * 
	 * @param ruleId the id of the flow rule to delete
	 */
	public void deleteFlowRule(String ruleId) {
		
		String json = String.format(DELETE_FLOW, switchId, ruleId);
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(endpointURL).delete(body).build();
		try {
			client.newCall(request).execute();
		} catch (IOException e) {
			LOGGER.error("Exception while deleting flaw rules", e);
			throw new RuntimeException("Exception while deleting flaw rules", e);
		}
	}
}
