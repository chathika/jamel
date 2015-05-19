package jamelV3.basic;

import jamelV3.Simulator;
import jamelV3.basic.data.BasicDataManager;
import jamelV3.basic.gui.GUI;
import jamelV3.basic.sector.Phase;
import jamelV3.basic.sector.Sector;
import jamelV3.basic.util.BasicTimer;
import jamelV3.basic.util.InitializationException;
import jamelV3.basic.util.Period;
import jamelV3.basic.util.Timer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A basic class of the Circuit.
 */
public class BasicCircuit implements Circuit {

	/**
	 * The control panel.
	 */
	public class ControlPanel extends JPanel {

		/** The pause button. */
		private final JButton pauseButton = new JButton("Pause") {{
			this.setToolTipText("Pause Simulation") ;
			this.setEnabled(false);			
		}};

		/** The play button. */
		private final JButton playButton = new JButton("Run") {{
			this.setToolTipText("Pause Simulation") ;
			this.setEnabled(false);
		}};

		{
			this.pauseButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					BasicCircuit.this.pause(true);
					repaint();
				} 
			}) ;
			this.playButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					BasicCircuit.this.pause(false);
					repaint();
				} 
			}) ;
			this.playButton.setToolTipText("Run Simulation") ;
			this.playButton.setEnabled(false);
			this.add(pauseButton);
			this.add(playButton);
			this.add(timer.getCounter());
		}

		/**
		 * Updates the pause/run buttons.
		 */
		@Override
		public void repaint() {
			final boolean b = BasicCircuit.this.isPaused();
			if (pauseButton!=null) {
				pauseButton.setEnabled(!b) ;
				pauseButton.setSelected(b) ;
				playButton.setEnabled(b) ;
				playButton.setSelected(!b) ;
			}
			super.repaint();
		}

	}

	/**
	 * @param settings a XML element with the settings.
	 * @param timer the timer.
	 * @param path the path of scenario file.
	 * @param name the name of the scenario file.
	 * @return a new basic data manager.
	 * @throws InitializationException If something goes wrong.
	 */
	private static BasicDataManager getNewDataManager(Element settings, Timer timer, String path, String name) throws InitializationException {
		return new BasicDataManager(settings, timer, path, name);
	}

	/**
	 * Returns the events.
	 * @param params a XML doc with the description of the events.
	 * @return a map that contains the events.
	 * @throws InitializationException If something goes wrong.
	 */
	private static Map<Integer,List<Element>> getNewEvents(Document params) throws InitializationException {
		final HashMap<Integer,List<Element>> map = new HashMap<Integer,List<Element>>();
		final Element circuitNode = (Element) params.getDocumentElement().getElementsByTagName("circuit").item(0);
		final Element eventsNode = (Element) circuitNode.getElementsByTagName("events").item(0);
		if (eventsNode!=null) {
			final NodeList eventsList = eventsNode.getChildNodes();
			for (int i = 0; i<eventsList.getLength(); i++) {
				final Node item = eventsList.item(i);  
				if (item.getNodeType()==Node.ELEMENT_NODE) {
					final Element element = (Element) item;
					final String periodKey = element.getAttribute("period");	
					if ("".equals(periodKey)) {
						throw new InitializationException("Malformed event: Missing attribute: period");					
					}
					final Integer period = Integer.parseInt(periodKey);
					if (map.containsKey(period)) {
						final List<Element> list = map.get(period);
						list.add(element);
					}
					else {			
						final List<Element> list = new LinkedList<Element>();
						list.add(element);
						map.put(period, list);
					}
				}
			}			
		}
		return map;
	}

	/**
	 * Initializes and returns a new GUI.
	 * @param name the name of the new GUI.
	 * @param controlPanel the control panel.
	 * @return the new GUI.
	 */
	private static GUI getNewGUI(String name, Component controlPanel) {
		return new GUI(name,controlPanel);
	}

	/**
	 * Initializes and returns a new info panel.
	 * @return a new info panel.
	 */
	private static Component getNewInfoPanel() {
		final Component jEditorPane = new JEditorPane() {
			{
				String infoString = readResourceFile("info.html");
				this.setContentType("text/html");
				this.setText("<center><h3>Jamel3 ("+Simulator.version+")</h3>"+infoString+"</center>");
				this.setEditable(false);
				this.addHyperlinkListener(new HyperlinkListener() {
					@Override
					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
							try {
								java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
							} catch (Exception ex) {
								ex.printStackTrace();
							}	        
					}
				});
			}
		};
		final JScrollPane pane = new JScrollPane(jEditorPane);
		pane.setName("Info");
		return pane;
	}

	/**
	 * Initializes and returns the list of phases of the circuit period.
	 * @param param a XML doc with the phases description.
	 * @param sectors a collection (a Map<name,sector>)of sectors.
	 * @return a list of phases.
	 * @throws InitializationException If an <code>InitializationException</code> occurs.
	 */
	private static LinkedList<Phase> getNewPhases(Map<String, Sector> sectors, Document param) throws InitializationException {
		final LinkedList<Phase> result = new LinkedList<Phase>();
		final Element circuitNode = (Element) param.getDocumentElement().getElementsByTagName("circuit").item(0);
		final Element phasesNode = (Element) circuitNode.getElementsByTagName("phases").item(0);
		final NodeList phases = phasesNode.getChildNodes();
		for (int i = 0; i<phases.getLength(); i++) {
			final Node node = phases.item(i); 
			if (node.getNodeType()==Node.ELEMENT_NODE) {
				final Element element = (Element) node;
				final String sectorName = element.getNodeName();
				final String phaseName = ((Element)phases.item(i)).getAttribute("action");
				final Sector sector = sectors.get(sectorName);
				if (sector==null) {
					throw new InitializationException("Error while parsing phases: sector: "+sectorName+" (sector not found)." );				
				}
				final Phase newPhase = sector.getPhase(phaseName);
				if (newPhase==null) {
					throw new InitializationException("Error while parsing phases: phase: \""+phaseName+"\" (sector: \""+sectorName+"\").");				
				}
				result.add(newPhase);

			}
		}
		return result;
	}

	/**
	 * Returns a new Random.
	 * @param settings a XML element that contains the settings.
	 * @return a new Random.
	 * @throws InitializationException If something goes wrong.
	 */
	private static Random getNewRandom(Element settings) throws InitializationException {
		Random result = null;
		final String randomSeed = settings.getAttribute("randomSeed");
		if ("".equals(randomSeed)) {
			throw new InitializationException("Circuit settings: Missing attribute: \"randomSeed\"");
		}
		try {
			final int seed = Integer.parseInt(randomSeed);
			result = new Random() {{this.setSeed(seed);}};
		} catch (NumberFormatException e) {
			throw new InitializationException("Something went wrong while parsing the tag \"randomSeed\".",e);			
		}
		return result;
	}

	/**
	 * Initializes and returns the sectors.
	 * @param circuit the circuit.
	 * @param params the parameters.
	 * @return a map <name of the sector, sector>.
	 * @throws InitializationException If something goes wrong.
	 */
	private static LinkedHashMap<String,Sector> getNewSectors(Circuit circuit, Document params) throws InitializationException {
		final LinkedHashMap<String,Sector> result = new LinkedHashMap<String,Sector>();
		final Element circuitNode = (Element) params.getDocumentElement().getElementsByTagName("circuit").item(0);
		final Element sectorsNode = (Element) circuitNode.getElementsByTagName("sectors").item(0);
		final NodeList sectorsList = sectorsNode.getChildNodes();
		for (int i = 0; i<sectorsList.getLength(); i++) {
			final Node item = sectorsList.item(i);  
			if (item.getNodeType()==Node.ELEMENT_NODE) {
				final Element element = (Element) item;
				final String sectorName = element.getNodeName();
				final String sectorQualifiedName = element.getAttribute("type");	
				if (sectorQualifiedName==null) {
					throw new InitializationException(sectorName+".type not found");					
				}
				Sector sector = null;
				try {
					sector = (Sector) Class.forName(sectorQualifiedName,false,ClassLoader.getSystemClassLoader()).getConstructor(String.class,Circuit.class).newInstance(sectorName,circuit);
					result.put(sectorName, sector);
				} catch (Exception e) {
					throw new InitializationException("Error while creating \""+sectorName+"\" as \""+sectorQualifiedName+"\"", e);					
				}
			}
		}
		return result;
	}

	/**
	 * Returns a XML element that contains the settings of the circuit.
	 * @param params a XML document that contains the settings.
	 * @return a XML element that contains the settings of the circuit.
	 */
	private static Element getSettings(Document params) {
		final Element circuitNode = (Element) params.getDocumentElement().getElementsByTagName("circuit").item(0);
		final Element settings = (Element) circuitNode.getElementsByTagName("settings").item(0);
		return settings;
	}

	/**
	 * Initializes the sectors.
	 * Must be called only after creating each sector.
	 * @param sectors the list of the sectors to be initialized.
	 * @param params a XML document that contains the parameters of each sector.
	 * @throws InitializationException If something goes wrong.
	 */
	private static void initSectors(LinkedHashMap<String, Sector> sectors, Document params) throws InitializationException {
		final Element circuitElement = (Element) params.getDocumentElement().getElementsByTagName("circuit").item(0);
		final NodeList nodeList = circuitElement.getElementsByTagName("sectors").item(0).getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE) {
				final Element sectorNode = (Element) node;
				final Sector sector = sectors.get(sectorNode.getNodeName());
				sector.init(sectorNode);
			}
		}
	}

	/**
	 * Reads the specified file in resource and returns its contain as a String.
	 * @param filename  the name of the resource file.
	 * @return a String.
	 */
	private static String readResourceFile(String filename) {
		BufferedReader br=new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/"+filename)));
		String line;		
		String string="";
		final String rc = System.getProperty("line.separator");
		try {
			while ((line=br.readLine())!=null){
				string+=line+rc;
			}
			br.close();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return string;
	}

	/** controlPanel */
	private final ControlPanel controlPanel;

	/** The macroeconomic data manager. */
	private final BasicDataManager dataManager;

	/** The events. */
	private final Map<Integer, List<Element>> events;

	/** The GUI. */
	private final GUI gui;

	/** A flag that indicates if the simulation is paused or not. */
	private boolean pause;

	/** The phases of the circuit. */
	private final LinkedList<Phase> phases;

	/** The random. */
	private final Random random;

	/** A flag that indicates if the simulation is running or not. */
	private boolean run = true;

	/** The sectors of the circuit. */
	private final LinkedHashMap<String,Sector> sectors;

	/** The timer. */
	private final BasicTimer timer;

	/**
	 * Creates a new basic circuit.
	 * @param params a document with the parameters for the new circuit.
	 * @param path the path to the scenario file.
	 * @param name the name of the scenario file.
	 * @throws InitializationException If something goes wrong.
	 */
	public BasicCircuit(final Document params, String path, String name) throws InitializationException {
		this.timer = new BasicTimer(-1);
		final Element settings = getSettings(params);
		this.random = getNewRandom(settings);
		this.sectors = getNewSectors(this, params);
		initSectors(this.sectors,params);
		this.phases = getNewPhases(this.sectors, params);
		this.events = getNewEvents(params);
		this.controlPanel = getNewControlPanel();
		this.gui = getNewGUI(name,this.controlPanel);
		this.dataManager = getNewDataManager(settings, timer, path, name);
		this.gui.addPanel(this.dataManager.getPanelList());
		this.gui.addPanel(getNewInfoPanel());
	}

	/**
	 * Executes the events of the simulation.
	 */
	private void doEvents() {
		final List<Element> eventList = this.events.get(this.timer.getPeriod().intValue());
		if (eventList!=null) {
			for(Element event:eventList) {
				final String sectorName = event.getAttribute("sector");
				if ("".equals(sectorName)) {
					this.doEvents(event);
				}
				else {
					final Sector sector = this.sectors.get(sectorName);
					sector.doEvent(event);
				}
			}
		}
		/*final String event = this.jamelParameters.get("Circuit.events."+timer.getPeriod().intValue());
		if (event!=null) {
			final String[] events = JamelParameters.split(event,",");
			for(final String string:events) {

				if (string.equals("pause")) {
					System.out.println("Duration: "+((new Date()).getTime()-start.getTime())/1000+" s.");
					this.pause=true;
					for(Sector sector:sectors.values()) {
						sector.pause();
					}
				}

				else if (string.startsWith("change.")) {
					final String[] truc1 = JamelParameters.split(string.substring(7),"=");
					final String[] truc2 = truc1[0].split("\\.", 2);
					this.jamelParameters.put(truc1[0], truc1[1]);
					final Sector sector = this.sectors.get(truc2[0]);
					if (sector!=null) {
						sector.forward("change in parameters");					
					}
					else {
						throw new RuntimeException("Sector not found: "+truc2[0]);
					}
				}

				else {
					final String[] truc1 = JamelParameters.split(string,"=");
					final String[] truc2 = truc1[0].split("\\.", 2);
					final Sector sector = this.sectors.get(truc2[0]);
					if (sector!=null) {
						sector.forward(truc2[1],truc1[1]);
					}
					else {
						throw new RuntimeException("Error while parsing the event <"+event+">: Sector not found <"+truc2[0]+">.");
					}
				}
			}
		}
		 */
	}

	/**
	 * Executes the specified event.
	 * @param event a XML element that describes the event to be executed.
	 */
	private void doEvents(Element event) {
		if (event.getNodeName().equals("pause")) {
			this.pause=true;
			this.controlPanel.repaint();
		}		
	}

	/**
	 * Executes a period of the circuit.
	 */
	private void doPeriod() {
		timer.next();
		this.doEvents();
		for(Phase phase:phases) {
			phase.run();
		}
		for(Sector sector:this.sectors.values()) {
			this.dataManager.putData(sector.getName(), sector.getDataset());
		}
		this.dataManager.updatesSeries();
	}

	/**
	 * Creates and returns the control panel.
	 * @return the control panel.
	 */
	private ControlPanel getNewControlPanel() {
		return new ControlPanel();
	}

	/**
	 * Sets the simulation paused or not.
	 * @param b a boolean.
	 */
	private void pause(boolean b) {
		this.pause=b;
	}

	@Override
	public Period getCurrentPeriod() {
		return this.timer.getPeriod();
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	@Override
	public Sector getSector(String name) {
		return this.sectors.get(name);
	}

	@Override
	public long getSimulationID() {
		return Simulator.getSimulationID();
	}

	@Override
	public Timer getTimer() {
		return this.timer;
	}

	@Override
	public boolean isPaused() {
		return pause;
	}

	/**
	 * Runs the simulation.
	 */
	@Override
	public void run() {
		while (this.run) {
			if (!this.pause){
				this.doPeriod();
			}
			else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}
		// Ciao bye bye.
	}

}

// ***