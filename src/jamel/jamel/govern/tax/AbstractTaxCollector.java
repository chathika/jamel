/**
 * 
 */
package jamel.jamel.govern.tax;

import jamel.basic.Circuit;
import jamel.basic.data.AgentDataset;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.IndustrialSector;
import jamel.jamel.govern.AbstractGovernment;
import jamel.jamel.incubators.FundedIncubator;
import jamel.jamel.roles.AccountHolder;
import jamel.jamel.sectors.BankingSector;
import jamel.jamel.sectors.GoverningSector;
import jamel.jamel.widgets.BankAccount;
import jamel.jamel.widgets.Cheque;

/**
 * @author cgunaratne
 *
 */
public abstract class AbstractTaxCollector implements TaxCollector, AccountHolder {

	

	/** Government to which this tax collector belongs **/
	protected AbstractGovernment sector;

	/**Tax collector's name **/
	protected final String name;
	
	/** tax paying industry */
	IndustrialSector industrialTaxpayers;
	
	/**Governing body's treasury*/
	protected BankAccount treasury;
	
	/**
	 * @param name 
	 * @param sector government to which tax collector belongs
	 */
	public AbstractTaxCollector(String name, AbstractGovernment sector) {
		this.name = name;
		this.sector = sector;

	}

	/* (non-Javadoc)
	 * @see jamel.jamel.tax.TaxCollector#setTaxPayers(jamel.jamel.firms.IndustrialSector)
	 */
	@Override
	public void setTaxPayers(IndustrialSector taxpayingSector) {
		this.industrialTaxpayers = taxpayingSector;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.tax.TaxCollector#collectTaxes()
	 */
	@Override
	abstract public void collectTaxes();

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
		return this.name;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#goBankrupt()
	 */
	@Override
	public void goBankrupt() {
		throw new RuntimeException("The Government has fallen!");
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#isBankrupted()
	 */
	@Override
	public boolean isBankrupted() {
		boolean bankrupted = treasury.getAmount() < 0;
		if (bankrupted) 
			goBankrupt();
		return treasury.getAmount() < 0;
	}

	/* (non-Javadoc)
	 * @see jamel.jamel.roles.AccountHolder#isSolvent()
	 */
	@Override
	public boolean isSolvent() {
		long target = 0;
		for(Firm fundedIncubator: sector.getIncubatorSector().getIncubators().getList()) {
			target += ((FundedIncubator)fundedIncubator).getPublicServicesCost();
		}
		long value = treasury.getAmount();
		return value > target;
	}
	
	/**
	 * returns funds from treasury
	 * @param amount value of required funds
	 * @return 
	 */
	public Cheque liquidateFunds(long amount) {
		isBankrupted();
		return treasury.newCheque(amount);
	}
	
	/**
	 * 
	 * @return value of treasury
	 */
	public long getValueOfTreasury() {
		return treasury.getAmount();
	}
}
