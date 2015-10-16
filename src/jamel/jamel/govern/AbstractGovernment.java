/**
 * 
 */
package jamel.jamel.govern;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import jamel.basic.Circuit;
import jamel.basic.data.SectorDataset;
import jamel.basic.sector.AbstractPhase;
import jamel.basic.sector.Phase;
import jamel.basic.sector.Sector;
import jamel.basic.util.BasicParameters;
import jamel.basic.util.InitializationException;
import jamel.basic.util.JamelParameters;
import jamel.basic.util.Timer;
import jamel.jamel.firms.BasicIndustrialSector;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.govern.tax.TaxCollector;
import jamel.jamel.incubators.IncubatorSector;
import jamel.jamel.sectors.BankingSector;
import jamel.jamel.sectors.CapitalistSector;
import jamel.jamel.sectors.GoverningSector;
import jamel.jamel.sectors.HouseholdSector;

/**
 * @author cgunaratne
 *
 */
public abstract class AbstractGovernment implements GoverningSector, Sector {

	

	/** The <code>dependencies</code> element. */
	private static final String ELEM_DEPENDENCIES = "dependencies";

	
	/** The macroeconomic circuit. */
	protected final Circuit circuit;
	
	/** The name of the governing body */
	protected final String name;
	
	/** The timer. */
	final private Timer timer;
	
	/** The parameters of this sector. */
	protected final JamelParameters parameters = new BasicParameters();
	
	protected BankingSector banks;
	protected HouseholdSector households;
	protected IndustrialSector industry;
	protected IncubatorSector incubators;
	
	

	
	protected String agentType;
	/**
	 * 
	 */
	public AbstractGovernment(String name, Circuit circuit) {
		this.name = name;
		this.circuit = circuit;
		this.timer = this.circuit.getTimer();
		//this.random = this.circuit.getRandom();
	}

	
	public float getParam(String key) {
		return this.parameters.get(key);
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.sectors.GoverningSector#govern()
	 */
	//@Override
	//public abstract void govern();

	public void init(Element element) throws InitializationException {
		if (element == null) {
			throw new IllegalArgumentException("Element is null");
		}

		// Initialization of the dependencies:
		final Element refElement = (Element) element.getElementsByTagName(
				ELEM_DEPENDENCIES).item(0);
		if (refElement == null) {
			throw new InitializationException("Element not found: "
					+ ELEM_DEPENDENCIES);
		}

		// Looking for the banking sector.
		final String key1 = "Banks";
		final Element banksElement = (Element) refElement.getElementsByTagName(
				key1).item(0);
		if (banksElement == null) {
			throw new InitializationException("Element not found: " + key1);
		}
		final String banksKey = banksElement.getAttribute("value");
		if (banksKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.banks = (BankingSector) circuit.getSector(banksKey);

		// Looking for the industrial sector.
		final String key2 = "Industry";
		final Element industryElement = (Element) refElement.getElementsByTagName(
				key2).item(0);
		if (industryElement == null) {
			throw new InitializationException("Element not found: " + key2);
		}
		final String industrykey = industryElement.getAttribute("value");
		if (industrykey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.industry = (IndustrialSector) circuit.getSector(industrykey);
		
		// Looking for the households sector.
		final String key3 = "CapitalistSector";
		final Element householdsElement = (Element) refElement.getElementsByTagName(
				key3).item(0);
		if (householdsElement == null) {
			throw new InitializationException("Element not found: " + key3);
		}
		final String householdsKey = householdsElement.getAttribute("value");
		if (householdsKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.households = (HouseholdSector) circuit.getSector(householdsKey);
		
		// Looking for the Incubators sector.
		final String key4 = "Incubators";
		final Element incubatorElement = (Element) refElement.getElementsByTagName(
				key4).item(0);
		if (incubatorElement == null) {
			throw new InitializationException("Element not found: " + key4);
		}
		final String incubatorServicesKey = incubatorElement.getAttribute("value");
		if (incubatorServicesKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.incubators = (IncubatorSector) circuit.getSector(incubatorServicesKey);
		
		// Initialization of the parameters:
		final Element settingsElement = (Element) element.getElementsByTagName("settings").item(0);
		final NamedNodeMap attributes = settingsElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			final Node node = attributes.item(i);
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				final Attr attr = (Attr) node;
				this.parameters.put(attr.getName(),
						Float.parseFloat(attr.getValue()));
			}
		}

	}
	
	
	/* (non-Javadoc)
	 * @see jamel.basic.sector.Sector#doEvent(org.w3c.dom.Element)
	 */
	@Override
	public void doEvent(Element event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jamel.basic.sector.Sector#getDataset()
	 */
	@Override
	public SectorDataset getDataset() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.basic.sector.Sector#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see jamel.basic.sector.Sector#getPhase(java.lang.String)
	 */
	@Override
	public Phase getPhase(String name) throws InitializationException {
		return null;
	}
	
	/**
	 * @return the banks
	 */
	public BankingSector getBanks() {
		return banks;
	}


	/**
	 * @return the households
	 */
	public HouseholdSector getHouseholds() {
		return households;
	}

	/**
	 * @return the industry
	 */
	public IndustrialSector getIndustry() {
		return industry;
	}
	
	/**
	 * @return the incubator sector
	 */
	public IncubatorSector getIncubatorSector() {
		return incubators;
	}

}
