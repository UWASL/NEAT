package netpart.clientwrapper;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Wrapper interface for the client operations
 * 
 * @author htakruri
 *
 */
public interface IClientWrapper extends Remote {

	/**
	 * Starts the client service
	 * 
	 * @return Implementation-specific result 
	 * @throws RemoteException
	 */
	public String start() throws RemoteException;

	/**
	 * Stops the client  service
	 * 
	 * @return Implementation-specific result
	 * @throws RemoteException
	 */
	public String stop() throws RemoteException;
}
