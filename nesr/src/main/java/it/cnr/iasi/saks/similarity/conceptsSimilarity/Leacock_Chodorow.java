package it.cnr.iasi.saks.similarity.conceptsSimilarity;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Leacock_Chodorow implements ConceptSimilarity_Interface {
	private static Leacock_Chodorow instance = null;
	private WeightedTaxonomy wt = null;
	private int maxDepth = 0;
	
	public synchronized static Leacock_Chodorow getInstance(WeightedTaxonomy onto){
    	if (instance == null){
    		instance = new Leacock_Chodorow(onto);
    	}
    	return instance;
    }
	
	private Leacock_Chodorow(WeightedTaxonomy onto) {
		this.setWt(onto);
		this.setMaxDepth(this.getWt().max_depth());
	}
	

	public WeightedTaxonomy getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}

	public int distance(Node n1, Node n2) {
		int result = 0;
		result = this.getWt().distanceInTree(n1, n2);
		return result;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public double sim(Node n1, Node n2) {
		double result = 0.0d;
		if(n1.getURI().equals(n2.getURI())) 
			result = 1.0d;
		else
			result = (- Math.log(
					((double)this.distance(n1, n2)) / 
					(2.0d*((double)this.getMaxDepth()))
					))/Math.log(Double.MAX_VALUE);
		return result;
	}
	

}
