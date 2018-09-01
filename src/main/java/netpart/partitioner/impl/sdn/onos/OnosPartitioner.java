package netpart.partitioner.impl.sdn.onos;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netpart.node.Node;
import netpart.partitioner.PartitionType;
import netpart.partitioner.Partitioner;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An ONOS partitioner implementation
 * 
 * @author htakruri
 *
 */
public class OnosPartitioner implements Partitioner<OnosPartition> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnosPartitioner.class);
	
	// FIXME To fetch from config tier
	private String endpointURL = "http://127.0.0.1:8181/onos/v1";
	private String username = "onos";
	private String password = "rocks";
	private String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	
	private OkHttpClient client = new OkHttpClient();
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static final String FLOW_TEMPLATE = "{\n" + 
			"  \"priority\": 40001,\n" + 
			"  \"appId\": \"Mycontroller\",\n" +
			"  \"isPermanent\": true,\n" + 
			"  \"deviceId\": \"of:0000000000000001\",\n" + 
			"  \"treatment\": {\n" + 
			"  \"instructions\": [\n" +
			"    ]\n" + 
			"  },\n" + 
			"  \"selector\": {\n" + 
			"    \"criteria\": [      \n" + 
			"		{\n" + 
			"        \"type\": \"IPV4_SRC\",\n" + 
			"        \"ip\": \"%s/32\"\n" + 
			"      }," +
			"	   {\n" + 
			"        \"type\": \"ETH_TYPE\",\n" + 
			"        \"ethType\": \"0x0800\"\n" + 
			"      }," +
			"      {\n" + 
			"        \"type\": \"IPV4_DST\",\n" + 
			"        \"ip\": \"%s/32\"\n" + 
			"      }\n" + 
			"    ]\n" + 
			"  }\n" + 
			"}";

	
	/**
	 * Default constructor
	 */
	public OnosPartitioner() {
		
	}
	
	/**
	 * @param endpointURL the REST endpoint url of ONOS controller
	 * @param username ONOS username
	 * @param password ONOS password
	 */
	public OnosPartitioner(String endpointURL, String username, String password) {
		
		this.endpointURL = endpointURL;
		this.username = username;
		this.password = password;
		this.basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OnosPartition fullPartition(List<Node>... groups) {

		List<String> flowRules = new LinkedList<>();

		for (int i = 0; i < groups.length - 1; i++) {
			for (int j = i + 1; j < groups.length; j++) {
				flowRules.addAll(partialPartition(groups[i], groups[j]).getRuleIds());
			}
		}

		return new OnosPartition(PartitionType.FULL, Arrays.asList(groups), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OnosPartition partialPartition(List<Node> groupA, List<Node> groupB) {

		List<String> flowRules = new LinkedList<>();

		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				flowRules.addAll(installDuplexFlowRule(nodeA.getHost(), nodeB.getHost()));
			}
		}

		return new OnosPartition(PartitionType.PARTIAL, Arrays.asList(groupA, groupB), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OnosPartition simplexPartition(List<Node> groupA, List<Node> groupB) {

		List<String> flowRules = new LinkedList<>();
		for (Node nodeA : groupA) {
			for (Node nodeB : groupB) {
				LOGGER.info("installing simplex flow rule between {} and {}", nodeA.getHost(), nodeB.getHost());
				flowRules.add(installFlowRule(nodeA.getHost(), nodeB.getHost()));
			}
		}
		return new OnosPartition(PartitionType.SIMPLEX, Arrays.asList(groupA, groupB), flowRules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void heal(OnosPartition partition) {

		for (String ruleId : partition.getRuleIds()) {
			deleteFlowRule(ruleId);
		}
	}

	/**
	 * Installs flaw rules denying the traffic from both hosts
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
	private String installFlowRule(String srcIp, String dstIp) {

		String json = String.format(FLOW_TEMPLATE, srcIp, dstIp);
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(endpointURL + "/flows/of:0000000000000001")
				.header("Authorization", basicAuth).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.header("Location").split("/")[7];
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
	private void deleteFlowRule(String ruleId) {
		
		Request request = new Request.Builder().url(endpointURL + "/flows/of:0000000000000001/" + ruleId)
				.header("Authorization", basicAuth).delete().build();
		try {
			client.newCall(request).execute();
		} catch (IOException e) {
			LOGGER.error("Exception while deleting flaw rules", e);
			throw new RuntimeException("Exception while deleting flaw rules", e);
		}
	}
}
