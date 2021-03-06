package jamel.basic.agent;

import jamel.basic.data.AgentDataset;


/**
 * Represents an agent.
 */
public interface Agent {
	
	/**
	 * Returns the data of the agent.
	 * @return the data of the agent.
	 */
	AgentDataset getData();

	/**
	 * Returns the name of the agent.
	 * @return the name of the agent.
	 */
	String getName();

}

//***
