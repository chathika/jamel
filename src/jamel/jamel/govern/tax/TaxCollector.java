/**
 * 
 */
package jamel.jamel.govern.tax;

import jamel.jamel.firms.IndustrialSector;

/**
 * @author cgunaratne
 *	Implements the basic tax collector interface
 */
public interface TaxCollector {
	
	/**
	 * sets the tax paying firms
	 * 
	 * @param taxpayerSector set of firms that are tax payers
	 * 
	 */
	public void setTaxPayers(IndustrialSector taxpayingSector);
	
	/**
	 * performs tax collection
	 */
	public void collectTaxes();
}
