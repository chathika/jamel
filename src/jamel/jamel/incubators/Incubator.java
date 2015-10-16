/**
 * 
 */
package jamel.jamel.incubators;

import jamel.basic.agent.Agent;
import jamel.jamel.roles.AccountHolder;
import jamel.jamel.roles.Corporation;
import jamel.jamel.roles.Supplier;

/**
 * @author cgunaratne
 * Represents an Incubator
 *
 */
public interface Incubator extends Agent, AccountHolder, Corporation, Supplier {
	/**
	 * Decides which firms are to be incubated out of the economy
	 */
	void selectForIncubation();
	
	/**
	 * Performs incubation on selected firms
	 */
	void performIncubation();
	
	/**
	 * Ends the incubation process for select firms
	 */
	void graduateFirms();
}
