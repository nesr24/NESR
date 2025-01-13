package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class TopDown_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
//RIVEDERE QUESTA FUNZIONE	
	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		result = Math.log(((double)wt.siblings(n).size()) + 1) - 1;
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt) {
		for(Node n: wt.getIds()) {
			double ic = ic(wt, n);
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}

	
}
