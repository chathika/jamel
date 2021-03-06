package jamel.jamel.households;

import jamel.Jamel;
import jamel.basic.Circuit;
import jamel.basic.data.SectorDataset;
import jamel.basic.sector.AbstractPhase;
import jamel.basic.sector.AgentSet;
import jamel.basic.sector.BasicAgentSet;
import jamel.basic.sector.Phase;
import jamel.basic.util.InitializationException;
import jamel.basic.util.Timer;
import jamel.jamel.aggregates.Banks;
import jamel.jamel.aggregates.Capitalists;
import jamel.jamel.aggregates.Employers;
import jamel.jamel.aggregates.Suppliers;
import jamel.jamel.capital.StockCertificate;
import jamel.jamel.roles.Corporation;
import jamel.jamel.roles.Shareholder;
import jamel.jamel.widgets.BankAccount;
import jamel.jamel.widgets.Cheque;
import jamel.jamel.widgets.JobOffer;
import jamel.jamel.widgets.Supply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A basic household sector.
 */
public class BasicHouseholdSector implements HouseholdSector, Capitalists {

	/** The key word for the "closure" phase. */
	private static final String PHASE_CLOSURE = "closure";

	/** The key word for the "consumption" phase. */
	private static final String PHASE_CONSUMPTION = "consumption";

	/** The key word for the "job search" phase. */
	private static final String PHASE_JOB_SEARCH = "job_search";

	/** The key word for the "opening" phase. */
	private static final String PHASE_OPENING = "opening";

	/** The key word for the "take dividends" phase. */
	private static final String PHASE_TAKE_DIVIDENDS = "take_dividends";

	/** The <code>dependencies</code> element. */
	protected static final String DEPENDENCIES = "dependencies";

	/** The type of the agents. */
	protected String agentType;

	/**
	 * The banking sector. The banking sector provides new accounts to
	 * households.
	 */
	protected Banks banks = null;

	/** The circuit. */
	protected final Circuit circuit;

	/** The agent counter. */
	protected int countAgents;

	/** The employers. */
	protected Employers employers;

	/** The collection of agents. */
	protected final AgentSet<Household> households;

	/** The sector name. */
	protected final String name;

	/** The parameters of the household sector. */
	protected final Map<String, Float> parameters = new HashMap<String, Float>();

	/** The random. */
	protected final Random random;

	/**
	 * The supplier sector. The supplier sector supplies commodities to
	 * households.
	 */
	protected Suppliers suppliers = null;

	/** The timer. */
	protected Timer timer;

	/**
	 * Creates a new sector for households.
	 * 
	 * @param name
	 *            the name of the sector.
	 * @param circuit
	 *            the circuit.
	 */
	public BasicHouseholdSector(String name, Circuit circuit) {
		this.circuit = circuit;
		this.random = this.circuit.getRandom();
		this.timer = this.circuit.getTimer();
		this.name = name;
		this.households = new BasicAgentSet<Household>(this.random);
	}

	/**
	 * Closes the sector at the end of the period.
	 */
	protected void close() {
		for (final Household household : this.households.getList()) {
			household.close();
		}
	}

	/**
	 * Creates households.
	 * 
	 * @param type
	 *            the type of households to create.
	 * @param lim
	 *            the number of households to create.
	 * @return a list containing the new households.
	 */
	protected List<Household> createHouseholds(String type, int lim) {
		final List<Household> list = new ArrayList<Household>(lim);
		for (int index = 0; index < lim; index++) {
			this.countAgents++;
			final String householdName = this.name + "-" + this.countAgents;
			try {
				list.add((Household) Class.forName(type, false, ClassLoader.getSystemClassLoader())
						.getConstructor(String.class, HouseholdSector.class).newInstance(householdName, this));
			} catch (Exception e) {
				throw new RuntimeException("Something goes wrong while creating households.", e);
			}
		}
		return list;
	}

	@Override
	public void doEvent(Element event) {
		if (event.getNodeName().equals("new")) {
			final int size = Integer.parseInt(event.getAttribute("size"));
			this.households.putAll(this.createHouseholds(this.agentType, size));
		} else if (event.getNodeName().equals("shock")) {
			final NodeList nodes = event.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element elem = (Element) nodes.item(i);
					if (elem.getNodeName().equals("param")) {
						final String key = elem.getAttribute("key");
						final float val = Float.parseFloat(elem.getAttribute("value"));
						this.parameters.put(key, val);
					}
				}
			}
		} else {
			throw new RuntimeException("Unknown event or not yet implemented: " + event.getNodeName());
		}
	}

	@Override
	public SectorDataset getDataset() {
		return this.households.collectData();
	}

	@Override
	public JobOffer[] getJobOffers(int i) {
		return this.employers.getJobOffers(i);
	}

	/**
	 * Returns the sector name.
	 * 
	 * @return the sector name.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public BankAccount getNewAccount(Household household) {
		return this.banks.getNewAccount(household);
	}

	@Override
	public Float getParam(String key) {
		return this.parameters.get(key);
	}

	@Override
	public Phase getPhase(String phaseName) throws InitializationException {
		final Phase result;

		if (phaseName.equals(PHASE_OPENING)) {
			result = new AbstractPhase(phaseName, this) {
				@Override
				public void run() {
					for (final Household household : households.getList()) {
						household.open();
					}
				}
			};
		}

		else if (phaseName.equals(PHASE_TAKE_DIVIDENDS)) {
			result = new AbstractPhase(phaseName, this) {
				@Override
				public void run() {
					for (final Household household : households.getList()) {
						household.takeDividends();
					}
				}
			};
		}

		else if (phaseName.equals(PHASE_JOB_SEARCH)) {
			result = new AbstractPhase(phaseName, this) {
				@Override
				public void run() {
					for (final Household household : households.getShuffledList()) {
						household.jobSearch();
					}
				}
			};
		}

		else if (phaseName.equals(PHASE_CONSUMPTION)) {
			result = new AbstractPhase(phaseName, this) {
				@Override
				public void run() {
					for (final Household household : households.getShuffledList()) {
						household.consumption();
					}
				}
			};
		}

		else if (phaseName.equals(PHASE_CLOSURE)) {
			result = new AbstractPhase(phaseName, this) {
				@Override
				public void run() {
					BasicHouseholdSector.this.close();
				}
			};
		}

		else {
			result = null;
			throw new InitializationException("Unknown phase: \"" + phaseName + "\".");
		}

		return result;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public Supply[] getSupplies(int i) {
		return this.suppliers.getSupplies(i);
	}

	@Override
	public Timer getTimer() {
		return this.timer;
	}

	@Override
	public void init(Element element) throws InitializationException {

		// Initialization of the dependencies:
		if (element == null) {
			throw new IllegalArgumentException("Element is null");
		}

		// Initialization of the agent type:
		final String agentAttribute = element.getAttribute("agent");
		if ("".equals(agentAttribute)) {
			throw new InitializationException("Attribute not found: agent");
		}
		this.agentType = agentAttribute;

		final Element refElement = (Element) element.getElementsByTagName(DEPENDENCIES).item(0);
		if (refElement == null) {
			throw new InitializationException("Element not found: " + DEPENDENCIES);
		}

		// Looking for the supplier sector.
		final String key1 = "Suppliers";
		final Element suppliersElement = (Element) refElement.getElementsByTagName(key1).item(0);
		if (suppliersElement == null) {
			throw new InitializationException("Element not found: " + key1);
		}
		final String suppliersKey = suppliersElement.getAttribute("value");
		if (suppliersKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.suppliers = (Suppliers) circuit.getSector(suppliersKey);

		// Looking for the employer sector.
		final String key2 = "Employers";
		final Element employersElement = (Element) refElement.getElementsByTagName(key2).item(0);
		if (employersElement == null) {
			throw new InitializationException("Element not found: " + key2);
		}
		final String employersKey = employersElement.getAttribute("value");
		if (employersKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.employers = (Employers) circuit.getSector(employersKey);

		// Looking for the banking sector.
		final String key3 = "Banks";
		final Element banksElement = (Element) refElement.getElementsByTagName(key3).item(0);
		if (banksElement == null) {
			throw new InitializationException("Element not found: " + key3);
		}
		final String banksKey = banksElement.getAttribute("value");
		if (banksKey == "") {
			throw new InitializationException("Missing attribute: value");
		}
		this.banks = (Banks) circuit.getSector(banksKey);

		// Initialization of the parameters:
		final Element settingsElement = (Element) element.getElementsByTagName("settings").item(0);
		final NamedNodeMap attributes = settingsElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			final Node node = attributes.item(i);
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				final Attr attr = (Attr) node;
				this.parameters.put(attr.getName(), Float.parseFloat(attr.getValue()));
			}
		}
	}

	@Override
	public List<Shareholder> selectCapitalOwners() {
		return new ArrayList<Shareholder>(households.getShuffledList());
	}

	@Override
	public Shareholder selectRandomCapitalOwner() {
		return households.getRandomAgent();
	}

	@Override
	public List<Shareholder> selectRandomCapitalOwners(int n) {
		return new ArrayList<Shareholder>(households.getSimpleRandomSample(n));
	}

	@Override
	public Cheque[] buyCorporation(Corporation firm) {
		final long firmValue = firm.getBookValue();
		final List<Shareholder> all = new LinkedList<Shareholder>(this.households.getShuffledList());
		final List<Shareholder> buyers = new ArrayList<Shareholder>(10);
		final List<Long> prices = new ArrayList<Long>(10);
		final List<Integer> shares = new ArrayList<Integer>(10);
		final long priceOfOneShare = firmValue / 100;
		if (priceOfOneShare < 2) {
			throw new RuntimeException("priceOfOneShare: " + priceOfOneShare);
			// FIXME
		}

		class Auctioneer {
			// TODO: It is not really an auctioneer. To be renamed.
			int auction(long minimalFinancialCapacity) {
				int nonIssuedShares = 100;
				for (Shareholder shareholder : all) {
					final long shareholderFinancialCapacity = shareholder.getFinancialCapacity();
					if (shareholderFinancialCapacity > minimalFinancialCapacity) {

						final int nShares0 = (int) (shareholderFinancialCapacity / priceOfOneShare);
						final int nShares = Math.min(nShares0, nonIssuedShares);
						final long priceOfTheShares = priceOfOneShare * nShares;
						buyers.add(shareholder);
						prices.add(priceOfTheShares);
						shares.add(nShares);
						nonIssuedShares -= nShares;
					}
					if (nonIssuedShares == 0) {
						break;
					}
				}
				return nonIssuedShares;
			}
		}

		final Auctioneer auctioneer = new Auctioneer();

		int nonIssuedShares = auctioneer.auction(priceOfOneShare * 10);
		if (nonIssuedShares > 0) {
			buyers.clear();
			prices.clear();
			shares.clear();
			nonIssuedShares = auctioneer.auction(priceOfOneShare * 5);
			if (nonIssuedShares > 0) {
				buyers.clear();
				prices.clear();
				shares.clear();
				nonIssuedShares = auctioneer.auction(priceOfOneShare * 2);
				if (nonIssuedShares > 0) {
					buyers.clear();
					prices.clear();
					shares.clear();
					nonIssuedShares = auctioneer.auction(priceOfOneShare);
					if (nonIssuedShares != 0) {
						Jamel.println("minimalPrice: " + priceOfOneShare);
						throw new RuntimeException("Non issued shares: " + nonIssuedShares);
					}
				}
			}
		}

		if (buyers.size() == 0) {
			throw new RuntimeException(
					"Buyers list is empty: " + firm.getName() + ", period " + timer.getPeriod().intValue());
		}

		final StockCertificate[] newShares = firm.getNewShares(shares);
		// Tout le capital est maintenant partagé proportionnellement aux
		// contributions de chacun.

		final Cheque[] cheques = new Cheque[buyers.size()];
		for (int i = 0; i < buyers.size(); i++) {
			cheques[i] = buyers.get(i).buy(newShares[i], prices.get(i));
		}

		return cheques;
	}

}

// ***
