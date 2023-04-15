package ca.pfv.spmf._group7.rpgrowthstreaming;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;

public class ItemsetDouble extends Itemset {

	double doubleSupport;
	
	ItemsetDouble(int[] items, double sup) {
		super(items);
		this.doubleSupport = sup;
	}
	
	@Override
	public String getSupportString() {
		return "" + doubleSupport;
	}
}
