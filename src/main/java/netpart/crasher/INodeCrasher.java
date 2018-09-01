package netpart.crasher;

import netpart.node.Node;

/**
 * The INodeCrasher is responsible for crashing and fixing system nodes <br>
 * Possible implementations and limitations of this class includes:
 * <ul>
 * 	<li>Reboot the machine: rebooting a system will automatically fix the node if the service type is demon</li>
 * 	<li>Kill system-wide processes: needs an external event to reboot the machine</li>
 * 	<li>Kill all service processes: it would be hard to track the different service processes for different deployments</li>
 * </ul>
 * 
 * @author htakruri
 *
 */
public interface INodeCrasher {

	/**
	 * Crashes a node
	 * 
	 * @param node the node to crash
	 */
	void crash(Node node);
	
	/**
	 * Fixes a crashed node
	 * 
	 * @param node the node to fix
	 */
	void fix(Node node);
}
