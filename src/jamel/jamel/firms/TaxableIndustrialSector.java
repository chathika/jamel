/**
 * 
 */
package jamel.jamel.firms;

import java.util.ArrayList;
import java.util.List;

import jamel.basic.Circuit;
import jamel.basic.sector.AgentSet;
import jamel.jamel.widgets.Cheque;

/**
 * @author cgunaratne
 * This class provides the transparency (access to perfect information) 
 * required by "super" agents such as the Government.
 */
public class TaxableIndustrialSector extends BasicIndustrialSector {

	/**
	 * @param name
	 * @param circuit
	 */
	public TaxableIndustrialSector(String name, Circuit circuit) {
		super(name, circuit);
		
	}
	
	/**
	 * Collects taxes from taxable firms in the form of Cheques
	 * @param taxableAmount the amount to be taxed 
	 * @return List of cheques to be returned as tax
	 */
	public List<Cheque> payTaxes(float taxRate) {
		//Taxes will be fixed for now. But percentages etc should be considered
		List<Cheque> taxCheques = new ArrayList<Cheque>();
		for (Firm taxpayer: firms.getList()) {
			long taxableAmount = Math.round(((BasicFirm)taxpayer).account.getAmount() * taxRate);
			if (taxableAmount > 0) {
				Cheque taxCheque = ((BasicFirm)taxpayer).account.newCheque(taxableAmount);
				taxCheques.add(taxCheque);
			}
		}
		return taxCheques;
	}
}
