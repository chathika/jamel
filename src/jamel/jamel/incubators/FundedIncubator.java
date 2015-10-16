/**
 * 
 */
package jamel.jamel.incubators;

import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IncubatableFirm;
import jamel.jamel.govern.PublicService;
import jamel.jamel.widgets.Cheque;

/**
 * @author cgunaratne
 * The Funded Incubator will use it's account to incubate
 * to invest in start-ups. This incubator is a non-profit organization
 */
public class FundedIncubator extends BasicIncubator implements PublicService {

	private long incubationCost;
	private long incubationAmount;
	/**
	 * @param name
	 * @param sector
	 */
	public FundedIncubator(String name, IncubatorSector sector) {
		super(name, sector);
	}
	
	/** 
	 * Incubation amount is withdrawn from account
	 * account is updated by Government's Tax collection
	 * This incubator will utilize funding possibly from taxes for incubation
	 * with no expectation on return on investment.
	 */
	@Override
	protected Cheque provideMonetaryAssistance(IncubatableFirm incubatee) {
		incubationAmount = account.getAmount() / getIncubatorSize();
		return account.newCheque(incubationAmount);
	}
	
	/**
	 * 
	 * Cost will be the total used for incubation 
	 */
	@Override
	public long getPublicServicesCost() {
		return incubationAmount * getIncubatorSize();
	}
	
	public void fund (Cheque funding) {
		account.deposit(funding);
	}
}
