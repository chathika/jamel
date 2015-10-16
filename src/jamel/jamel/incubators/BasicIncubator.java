/**
 * 
 */
package jamel.jamel.incubators;

import jamel.jamel.firms.IncubatableFirm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.firms.factory.Factory;
import jamel.jamel.firms.managers.CapitalManager;
import jamel.jamel.firms.managers.PricingManager;
import jamel.jamel.firms.managers.ProductionManager;
import jamel.jamel.firms.managers.SalesManager;
import jamel.jamel.firms.managers.WorkforceManager;
import jamel.jamel.incubators.AbstractIncubator.selectionTypes;
import jamel.jamel.widgets.Cheque;

/**
 * @author cgunaratne
 * Defines a basic incubator for a JAMEL
 */
public class BasicIncubator extends AbstractIncubator {
	@SuppressWarnings("javadoc")
	public final static String INCUBATION_SELECTION_TYPE = "incubation.selection.type";
	public final static String INCUBATION_ASSISTANCE_TYPE = "incubation.assistance.type";
	public final static String INCUBATION_SEARCH_SCOPE = "incubation.search.scope";
	public final static String INCUBATION_INCUBATOR_SIZE = "incubation.incubator.size";
	public final static String INCUBATION_MAX_INCUBATION_PERIOD = "incubation.max.incubation.period";
	/**
	 * @param name
	 * @param sector
	 */
	public BasicIncubator(String name, IncubatorSector sector) {
		super(name, sector);
		//setSelectionType(sector.getParam());
		setSelectionType(sector.getParam(INCUBATION_SELECTION_TYPE));
		setAssistanceType(sector.getParam(INCUBATION_ASSISTANCE_TYPE));
		setIncubatorSearchScope((int)sector.getParam(INCUBATION_SEARCH_SCOPE));
		setIncubatorSize((int)sector.getParam(INCUBATION_INCUBATOR_SIZE));
		setMaxIncubationPeriod((int)sector.getParam(INCUBATION_MAX_INCUBATION_PERIOD));
	}

	
	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewCapitalManager()
	 */
	@Override
	protected CapitalManager getNewCapitalManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewFactory()
	 */
	@Override
	protected Factory getNewFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewPricingManager()
	 */
	@Override
	protected PricingManager getNewPricingManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewProductionManager()
	 */
	@Override
	protected ProductionManager getNewProductionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewSalesManager()
	 */
	@Override
	protected SalesManager getNewSalesManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.firms.AbstractFirm#getNewWorkforceManager()
	 */
	@Override
	protected WorkforceManager getNewWorkforceManager() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	protected Cheque provideMonetaryAssistance(IncubatableFirm incubatee) {
		//temporary value for testing
		int incubationAmount = 100000;
		Cheque incubatorDeposit = new Cheque() {
			private boolean paid = false;

			@Override
			public long getAmount() {
				return incubationAmount;
			}

			@Override
			public boolean payment() {
				boolean result = true;
				if (!paid) {
					this.paid = true;
				} else {
					result = false;
				}
				return result;
			}
		};
		return incubatorDeposit;
	}
}
