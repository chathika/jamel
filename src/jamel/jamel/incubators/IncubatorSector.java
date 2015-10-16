/**
 * 
 */
package jamel.jamel.incubators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameList;

import jamel.basic.Circuit;
import jamel.basic.sector.AbstractPhase;
import jamel.basic.sector.AgentSet;
import jamel.basic.sector.Phase;
import jamel.basic.util.InitializationException;
import jamel.jamel.firms.BasicIndustrialSector;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IncubatableFirm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.govern.PublicService;
import jamel.jamel.sectors.BankingSector;

/**
 * @author cgunaratne
 *
 */
public class IncubatorSector extends BasicIndustrialSector implements IndustrialSector {

	/** The <code>dependencies</code> element. */
	private static final String ELEM_DEPENDENCIES = "dependencies";
	
	/** Key word for the "incubation" phase. */
	private static final String PHASE_INCUBATION = "incubation";
	
	/** The Incubatable Firms sector. */
	private BasicIndustrialSector incubatableFirms;
	
	private float incubationStopPeriod;
	
	boolean incubating = false;
	/**
	 * @param name
	 * @param circuit
	 */
	public IncubatorSector(String name, Circuit circuit) {
		super(name, circuit);
		// TODO Auto-generated constructor stub
	}
	
	public AgentSet<Firm> getIncubators () {
		return firms;
	}
	@Override
	public void init(Element element) throws InitializationException {
		
		super.init(element);
		
		// Initialization of the dependencies:
		final Element refElement = (Element) element.getElementsByTagName(ELEM_DEPENDENCIES).item(0);
		if (refElement == null) {
			throw new InitializationException("Element not found: " + ELEM_DEPENDENCIES);
		}
		
		// Looking for the Industry sector.
		final String key = "Industry";
		final Element incubatableFirmsElement = (Element) refElement.getElementsByTagName(key).item(0);
		if (incubatableFirmsElement == null) {
			throw new InitializationException("Element not found: " + key);
		} 
		final String incubatableFirmsKey = incubatableFirmsElement.getAttribute("value");
		if (incubatableFirmsKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.incubatableFirms = (BasicIndustrialSector) circuit.getSector(incubatableFirmsKey);
	}
	/**
	 * @return the incubatableFirms
	 */
	public BasicIndustrialSector getIncubatableFirms() {
		return incubatableFirms;
	}
	
	/**
	 * Creates firms.
	 * 
	 * @param type
	 *            the type of firms to create.
	 * @param lim
	 *            the number of firms to create.
	 * @return a list containing the new firms.
	 */
	protected List<Firm> createFirms(String type, int lim) {
		final List<Firm> result = new ArrayList<Firm>(lim);
		try {
			for (int index = 0; index < lim; index++) {
				this.countFirms++;
				final String name = "Firm" + this.countFirms;
				final Firm firm = (Firm) Class
						.forName(type, false,
								ClassLoader.getSystemClassLoader())
						.getConstructor(String.class, IncubatorSector.class)
						.newInstance(name, this);
				result.add(firm);
			}
		} catch (Exception e) {
			throw new RuntimeException("Firm creation failure", e);
		}
		return result;
	}
	
	@Override
	public void doEvent(Element event) {
		if (event.getNodeName().equals("new")) {
			final int size = Integer.parseInt(event.getAttribute("size"));
			this.firms.putAll(this.createFirms(this.agentType, size));
		} else if (event.getNodeName().equals("shock")) {
			final NodeList nodes = event.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element elem = (Element) nodes.item(i);
					if (elem.getNodeName().equals("param")) {
						final String key = elem.getAttribute("key");
						final float val = Float.parseFloat(elem
								.getAttribute("value"));
						this.parameters.put(key, val);
					}
				}
			}
		} else if (event.getNodeName().equals("incubate")){
			System.out.println("incubation time" + event.getAttribute("period") + ((jamel.jamel.incubators.AbstractIncubator)firms.getRandomAgent()).getSelectionType());
			incubationStopPeriod = getTimer().getPeriod().intValue() + Float.parseFloat(event.getAttribute("duration"));
			for (Firm incubatorFirm: this.firms.getList()){
				AbstractIncubator incubator = (AbstractIncubator)(incubatorFirm);
				incubator.selectForIncubation();
			}
			incubating = true;
		} else {
			throw new RuntimeException("Unknown event or not yet implemented: "
					+ event.getNodeName());
		}
		
	}
	@Override
	public Phase getPhase(String name) {
		super.getPhase(name);
		final Phase result;

		if (name.equals(PHASE_INCUBATION)) {
			result = new AbstractPhase(name, this) {
				@Override
				public void run() {
					IncubatorSector.this.incubate();
				}
			};
		}
		else {
			result=null;
		}
		return result;
	}
	
	/**
	 * Incubates the sector at the end of the period.
	 */
	private void incubate() {
		if (incubating) {
			System.out.println("performing incubation at period: " + getTimer().getPeriod());
			if(getTimer().getPeriod().isAfter((int)incubationStopPeriod)) {
				incubating = false;				
			} else {
				for (Firm incubatorFirm: this.firms.getList()){
					AbstractIncubator incubator = (AbstractIncubator)(incubatorFirm);
					incubator.performIncubation();
				}
				for(Firm incubator: firms.getList())
					for(IncubatableFirm incubatee: ((BasicIncubator)incubator).getIncubatees())
						System.out.print(incubatee.getName());
				System.out.println();
			}			
		}
	}
	
	public IncubatableFirm getIncubatableFirmByName (String name) {
		IncubatableFirm result = null;
		for(Firm firm: incubatableFirms.getSimpleRandomSample(105)) {
			if (firm.getName().equals(name)) {
				result = (IncubatableFirm)firm;
				break;
			}			
		}
		return result;
	}
	
}
