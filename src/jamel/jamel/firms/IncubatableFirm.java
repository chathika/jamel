package jamel.jamel.firms;

import java.util.Comparator;

import jamel.basic.data.AgentDataset;
import jamel.jamel.widgets.Cheque;

public class IncubatableFirm extends BasicFirm {

	public static enum compareBy {YOUNGEST, OLDEST, BEST, WORST};
	public IncubatableFirm(String name, IndustrialSector sector) {
		super(name, sector);
	}
	
	@Override
	public int getAge() {
		return super.getAge();
	}

	public void incubate(Cheque incubatorDeposit) {
		if(incubatorDeposit.getAmount() > 0) {
			account.deposit(incubatorDeposit);
		}
	}

//	@Override
//	public int compareTo(Object arg0) {
//		return ((IncubatableFirm)arg0).getAge() - this.getAge();
//	}
	/**
	 * returns a comparator as s
	 * @return
	 */
	public static Comparator<IncubatableFirm> getComparator(compareBy type) {
		Comparator<IncubatableFirm> comparator = null;
		switch(type) {
		case YOUNGEST:
			comparator = new Comparator<IncubatableFirm>() {
				public int compare(IncubatableFirm firm1, IncubatableFirm firm2) {
					int result = 0;
					if (firm1.getAge()>firm2.getAge()) {
						result = 1;
					} else if (firm1.getAge()<firm2.getAge()) {
						result = -1;
					}
					return result;
				}
			};
			break;
		case OLDEST:
			comparator = new Comparator<IncubatableFirm>() {
				public int compare(IncubatableFirm firm1, IncubatableFirm firm2) {
					int result = 0;
					if (firm1.getAge()<firm2.getAge()) {
						result = 1;
					} else if (firm1.getAge()>firm2.getAge()) {
						result = -1;
					}
					return result;
				}
			};
			break;
		case BEST:
			comparator = new Comparator<IncubatableFirm>() {
				public int compare(IncubatableFirm firm1, IncubatableFirm firm2) {
					int result = 0;
					long firm1Value = firm1.getBookValue() + firm1.getValueOfAssets() - firm1.getValueOfLiabilities();
					long firm2Value = firm2.getBookValue() + firm2.getValueOfAssets() - firm2.getValueOfLiabilities();
					if (firm1Value>firm2Value) {
						result = 1;
					} else if (firm1Value<firm2Value) {
						result = -1;
					}
					return result;
				}
			};
			break;
		case WORST:
			comparator = new Comparator<IncubatableFirm>() {
				public int compare(IncubatableFirm firm1, IncubatableFirm firm2) {
					int result = 0;
					long firm1Value = firm1.getBookValue() + firm1.getValueOfAssets() - firm1.getValueOfLiabilities();
					long firm2Value = firm2.getBookValue() + firm2.getValueOfAssets() - firm2.getValueOfLiabilities();
					if (firm1Value<firm2Value) {
						result = 1;
					} else if (firm1Value>firm2Value) {
						result = -1;
					}
					return result;
				}
			};
			break;
		default:
			break;
		}
		return comparator;
	}
}
