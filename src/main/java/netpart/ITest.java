package netpart;

import java.rmi.RemoteException;

/**
 * This interface needs to be implemented by every system test class
 * 
 * @author htakruri
 *
 */
public interface ITest {

	/**
	 * This is the first method to be triggered by the {@link TestRunner}
	 * 
	 * @param args optional arguments to pass form command line to the test class 
	 * @throws RemoteException
	 */
	void test(String[] args) throws RemoteException;
}
