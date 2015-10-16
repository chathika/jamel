/**
 * 
 */
package jamel.jamel.govern;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import jamel.basic.Circuit;
import jamel.basic.sector.AbstractPhase;
import jamel.basic.sector.Phase;
import jamel.basic.sector.Sector;
import jamel.basic.util.InitializationException;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.govern.tax.BasicTaxCollector;
import jamel.jamel.govern.tax.TaxCollector;
import jamel.jamel.incubators.AbstractIncubator;
import jamel.jamel.incubators.FundedIncubator;
import jamel.jamel.incubators.IncubatorSector;

/**
 * @author cgunaratne
 *
 */
public class TaxDrivenGovernment extends AbstractGovernment {
	
	/** Tax collector for this government **/
	protected BasicTaxCollector taxCollector;
	
	/** Tax amount */
	private static final String TAX_AMOUNT = "tax.amount";
	
	
	/** Key word for the "tax.collection" phase. */
	private static final String TAX_COLLECTION = "collect_taxes";
	
	/** Controls tax collection */
	boolean collectTaxes = false;
	/**
	 * @param name name of government
	 * @param circuit circuit for simulation
	 */
	public TaxDrivenGovernment(String name, Circuit circuit) {
		super(name, circuit);
		//taxCollector = new BasicTaxCollector(name, this);
	}

	public float getTaxAmount () {
		return Float.valueOf(getParam(TAX_AMOUNT));
	}

	public void collectTaxes() {
		if(collectTaxes) {
			if(true) {
				//taxes fill up the treasury
				taxCollector.collectTaxes();
			}
		}
	}
	
	public void fundPublicServices() {
		long fundAmount = 0;
		if (getIncubatorSector().getIncubators().getList().size() > 0 ) {
			fundAmount = taxCollector.getValueOfTreasury() / getIncubatorSector().getIncubators().getList().size();
		}
		if (fundAmount > 0) {
			for(Firm incubator: getIncubatorSector().getIncubators().getList()) {
				((FundedIncubator)incubator).fund(taxCollector.liquidateFunds(fundAmount));
			}
		}
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.govern.AbstractGovernment#getPhase(java.lang.String)
	 */
	@Override
	public Phase getPhase(String name) throws InitializationException {
		final Phase result;

		if (name.equals(TAX_COLLECTION)) {
			result = new AbstractPhase(name, this) {
				@Override
				public void run() {
					TaxDrivenGovernment.this.collectTaxes();
					TaxDrivenGovernment.this.fundPublicServices();
				}
			};
		}
		
		else {
			result=null;
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.govern.AbstractGovernment#init(org.w3c.dom.Element)
	 */
	@Override
	public void init(Element element) throws InitializationException {
		// Initialization of the agent type:
		super.init(element);
		final String agentAttribute = element.getAttribute("agent");
		this.agentType = agentAttribute;
		try {
			this.taxCollector = (BasicTaxCollector) Class.forName(
					 this.agentType, false,ClassLoader.getSystemClassLoader()).getConstructor(String.class, AbstractGovernment.class)
					.newInstance(name, this);
		} catch (Exception e) {
			throw new RuntimeException("Firm creation failure", e);
		}	
	}
	
	/* (non-Javadoc)
	 * @see jamel.jamel.govern.AbstractGovernment#doEvent(org.w3c.dom.Element)
	 */
	@Override
	public void doEvent(Element event) {
		//super.doEvent(event);
		if (event.getNodeName().equals("start_taxation")){
			collectTaxes = true;
		} else if (event.getNodeName().equals("stop_taxation")){
			collectTaxes = false;
		} else {
			throw new RuntimeException("Unknown event or not yet implemented: "
					+ event.getNodeName());
		}
	}
}
