/**
 * 
 */
package jamel.jamel.govern.tax;

import java.util.List;

import jamel.basic.sector.AgentSet;
import jamel.jamel.firms.BasicFirm;
import jamel.jamel.firms.Firm;
import jamel.jamel.firms.TaxableIndustrialSector;
import jamel.jamel.govern.AbstractGovernment;
import jamel.jamel.govern.TaxDrivenGovernment;
import jamel.jamel.widgets.BankAccount;
import jamel.jamel.widgets.Cheque;

/**
 * @author cgunaratne
 *
 */
public class BasicTaxCollector extends AbstractTaxCollector {

	/**
	 * @param name
	 * @param sector
	 */
	public BasicTaxCollector(String name, AbstractGovernment sector) {
		super(name, sector);
		treasury = sector.getBanks().getNewAccount(this);
	}

	@Override
	public void collectTaxes() {
		try {
			//Taxes will be fixed for now. But percentages etc should be considered
			TaxableIndustrialSector taxableIndustry = ((TaxableIndustrialSector)((TaxDrivenGovernment)sector).getIndustry());
			float taxRate =((TaxDrivenGovernment)sector).getTaxAmount();
			List<Cheque> collectedTax = taxableIndustry.payTaxes(taxRate);
			
			//deposit cheques in bank
			for(Cheque cheque: collectedTax) {
				this.treasury.deposit(cheque);	
			}			
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!treasury :" + treasury.getAmount());
		} catch (ClassCastException e) {
			System.err.println("Tax Collector needs Taxable Firms");
		}
	}
	
	
}
