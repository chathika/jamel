/**
 * 
 */
package jamel.jamel.incubators;

import jamel.basic.data.AgentDataset;
import jamel.basic.util.Timer;
import jamel.jamel.firms.AbstractFirm;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IncubatableFirm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.govern.PublicService;
import jamel.jamel.widgets.Cheque;
import jamel.jamel.widgets.Supply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author cgunaratne
 *An abstract incubator
 */
public abstract class AbstractIncubator extends AbstractFirm implements Incubator {
	
	protected enum selectionTypes {YOUNGEST, OLDEST, BEST, WORST, RANDOM};
	protected enum assistanceTypes {RESOURCES, MONEY, NETWORKING, MIXED};
	protected enum graduationRequirementTypes {TIME};
	
	private int incubatorSize;
	private int incubatorSearchScope;
	
	private selectionTypes selectionType;
	private assistanceTypes assistanceType;
	

	private int maxIncubationPeriod;
	
	protected HashSet<IncubatableFirm> incubatees;
	
	/**
	 * Creates a new Incubator
	 * @param name 
	 * 		name of incubator
	 * @param sector
	 * 		sector incubator belongs to (assuming incubators are part of the industrial sector for now)
	 * 
	 */
	public AbstractIncubator(String name, IncubatorSector sector) {
		super(name, sector);
		incubatees = new HashSet<IncubatableFirm>();
	}

	/* (non-Javadoc)
	 * @see jamel.basic.agent.Agent#getData()
	 */
	@Override
	public AgentDataset getData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.basic.agent.Agent#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Incubator";
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#goBankrupt()
	 */
	@Override
	public void goBankrupt() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#isBankrupted()
	 */
	@Override
	public boolean isBankrupted() {
		return this.bankrupted;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#isSolvent()
	 */
	@Override
	public boolean isSolvent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.Corporation#getBookValue()
	 */
	@Override
	public Long getBookValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.widgets.Asset#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.Supplier#getSupply()
	 */
	@Override
	public Supply getSupply() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.incubators.Incubator#selectForIncubation()
	 */
	@Override
	public void selectForIncubation() {
		//this.sector.
		IndustrialSector incubatableFirms = ((IncubatorSector)sector).getIncubatableFirms();
		List <Firm> nominees = incubatableFirms.getSimpleRandomSample(incubatorSearchScope);
		try {
			double pAccept = 0;
			List<IncubatableFirm>incubatableNominees = new ArrayList<IncubatableFirm>();
			for(Firm firm:nominees){
				IncubatableFirm incubatableNominee = (IncubatableFirm) firm;
				incubatableNominees.add(incubatableNominee);
			}
			switch(selectionType) {
			case YOUNGEST:
				Collections.sort(incubatableNominees, IncubatableFirm.getComparator(IncubatableFirm.compareBy.YOUNGEST));
				break;
			case OLDEST:
				Collections.sort(incubatableNominees, IncubatableFirm.getComparator(IncubatableFirm.compareBy.OLDEST));
				break;
			case BEST:
				Collections.sort(incubatableNominees, IncubatableFirm.getComparator(IncubatableFirm.compareBy.BEST));
				break;
			case WORST:
				Collections.sort(incubatableNominees, IncubatableFirm.getComparator(IncubatableFirm.compareBy.WORST));
				break;
			default:
				return;
			}
			for(Firm firm:incubatableNominees){
				IncubatableFirm incubatableNominee = (IncubatableFirm) firm;
				System.out.println(incubatableNominee.getName() + " " + (incubatableNominee.getBookValue() +incubatableNominee.getValueOfAssets() - incubatableNominee.getValueOfLiabilities()));
			}
			//TODO: complete selection process simply returning the first members for now 
			for (int i=0; i<incubatorSize-1; i++){
				if(!incubatableNominees.get(i).getName().equals("Firm101")) {
				registerFirm(incubatableNominees.get(i));
				//System.out.println(incubatableNominees.get(i).getName() + " " + (incubatableNominees.get(i).getBookValue() +incubatableNominees.get(i).getValueOfAssets() - incubatableNominees.get(i).getValueOfLiabilities()));
				}
			}
			registerFirm(((IncubatorSector)sector).getIncubatableFirmByName("Firm101"));
		} catch (Exception e) {
			return;
		}
		
			
	}
	
	/* (non-Javadoc)
	 * @see jamel.jamel.incubators.Incubator#performIncubation()
	 * performs incubation according to defined assistance type
	 */
	@Override
	public void performIncubation() {
		switch(assistanceType) {
		case RESOURCES:
			break;
		case MONEY:
			for (IncubatableFirm incubatee: incubatees) {
				incubatee.incubate(provideMonetaryAssistance(incubatee));
			}
			break;
		case NETWORKING:
			break;
		case MIXED:
			break;
		default:
			break;
		}
		
	}

	/** 
	 * Defines how much is provided as monetary assistance and where
	 * the money comes from.
	 * Must be defined by incubator type
	 * e.g.: venture capitalist would determine incubation value 
	 * through firm performance
	 * e.g.: Basic incubation may use a fixed amount for all firms
	 */
	protected abstract Cheque provideMonetaryAssistance (IncubatableFirm incubatee);
	/* (non-Javadoc)
	 * @see jamel.jamel.incubators.Incubator#graduateFirms()
	 */
	@Override
	public void graduateFirms() {
		for (IncubatableFirm incubatee: incubatees) {
			if (incubatee.getAge() > this.maxIncubationPeriod ) {
				//TODO : incomplete may have to handle more dependencies
				try {
					unregisterFirm(incubatee);
				} catch (Exception e) {
					System.err.println("incubatee " + incubatee.getName() + " cannot be unregistered! " + e.getMessage());
				}
			}
		}	
	}
	
	/**
	 * Ensures safe registration of firm to incubator
	 * @param firm
	 * 		entering firm
	 */
	protected void registerFirm(IncubatableFirm firm) throws Exception {
		if (incubatees.contains(firm)) {
			throw new Exception("Firm already exists in incubator!");
		} else {
			incubatees.add(firm);			
		}
	}
	
	/**
	 * Ensures safe exit of firm from incubator
	 * @param firm
	 * 		exiting firm
	 */
	protected void unregisterFirm(IncubatableFirm firm) throws Exception {
		if (!incubatees.contains(firm)) {
			throw new Exception("Unknown firm attempting to exit incubator!");
		} else {
			incubatees.remove(firm);
		}
	}

	/**
	 * @return the incubatorSize
	 */
	public int getIncubatorSize() {
		return incubatorSize;
	}

	/**
	 * @param incubatorSize the incubatorSize to set
	 */
	public void setIncubatorSize(int incubatorSize) {
		this.incubatorSize = incubatorSize;
	}

	/**
	 * @return the incubatorSearchScope
	 */
	public int getIncubatorSearchScope() {
		return incubatorSearchScope;
	}

	/**
	 * @param incubatorSearchScope the incubatorSearchScope to set
	 */
	public void setIncubatorSearchScope(int incubatorSearchScope) {
		this.incubatorSearchScope = incubatorSearchScope;
	}

	/**
	 * @return the selectionType
	 */
	public selectionTypes getSelectionType() {
		return selectionType;
	}

	/**
	 * @param selectionType the selectionType to set
	 */
	public void setSelectionType(float selectionType) {
		switch((int)selectionType) {
		case 0:
			this.selectionType = selectionTypes.YOUNGEST;
			break;
		case 1:
			this.selectionType = selectionTypes.OLDEST;
			break;
		case 2:
			this.selectionType = selectionTypes.BEST;
			break;
		case 3:
			this.selectionType = selectionTypes.WORST;
			break;
		case 4:
			this.selectionType = selectionTypes.RANDOM;
			break;
		default:
			this.selectionType = selectionTypes.RANDOM;
		}
	}
	
	/**
	 * @return the assistanceType
	 */
	public assistanceTypes getAssistanceType() {
		return assistanceType;
	}

	/**
	 * @param assistanceType the assistanceType to set
	 */
	public void setAssistanceType(float assistanceType) {
		switch((int)assistanceType) {
		case 0:
			this.assistanceType = assistanceTypes.RESOURCES;
			break;
		case 1:
			this.assistanceType = assistanceTypes.MONEY;
			break;
		case 2:
			this.assistanceType = assistanceTypes.NETWORKING;
			break;
		case 3:
			this.assistanceType = assistanceTypes.MIXED;
			break;
		default:
			this.assistanceType = assistanceTypes.MIXED;
		}
	}
	
	/**
	 * @return the maxIncubationPeriod
	 */
	public int getMaxIncubationPeriod() {
		return maxIncubationPeriod;
	}

	/**
	 * @param maxIncubationPeriod the maxIncubationPeriod to set
	 */
	public void setMaxIncubationPeriod(int maxIncubationPeriod) {
		this.maxIncubationPeriod = maxIncubationPeriod;
	}
	/**
	 * @return the incubatees
	 */
	public HashSet<IncubatableFirm> getIncubatees() {
		return incubatees;
	}
}
