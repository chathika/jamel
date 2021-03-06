package jamel.basic.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A basic dataset implementing the {@link AgentDataset} interface.
 */
public class BasicAgentDataset implements AgentDataset {
		
	/** The name of the agent. */
	final private String name;
	
	/**
	 * A map containing the numerical data.
	 */
	final private Map<String,Double> data = new HashMap<String,Double>();
	
	/**
	 * A map containing the string data.
	 */
	final private Map<String,String> infos = new HashMap<String,String>();
	
	/**
	 * Creates a new BasicAgentDataset.
	 * @param name the name of the agent.
	 */
	public BasicAgentDataset(String name) {
		super();
		this.name = name;
	}

	@Override
	public void exportHeadersTo(File outputFile) throws IOException {
		final FileWriter writer = new FileWriter(outputFile,true);
		for (String key:this.keySet()) {
			writer.write(key+",");
		}	
		writer.write(System.getProperty("line.separator"));
		writer.close();
	}
	
	@Override
	public void exportTo(File outputFile) throws IOException {
		final FileWriter writer = new FileWriter(outputFile,true);
		for (String key:this.keySet()) {
			writer.write(this.get(key)+",");
		}	
		writer.write(System.getProperty("line.separator"));
		writer.close();
	}

	@Override
	public Double get(String key) {
		return this.data.get(key);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void putAll(AgentDataset agentDataset) {
		for(String key:agentDataset.keySet()) {
			this.put(key, agentDataset.get(key));
		}
		for(String key:infos.keySet()) {
			this.putInfo(key, infos.get(key));
		}
	}

	@Override
	public Double put(String key, Number value) {
		final Double d;
		if (value==null) {
			d=null;
		} else {
			d=value.doubleValue();
		}
		return this.data.put(key, d);
	}

	@Override
	public void putInfo(String key, String message) {
		this.infos.put(key, message);
	}

	@Override
	public Set<String> keySet() {
		return this.data.keySet();
	}

	@Override
	public boolean containsKey(String key) {
		return this.data.containsKey(key);
	}

	@Override
	public String getInfo(String key) {
		return this.infos.get(key);
	}

}

// ***
