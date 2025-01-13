package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Sanchez_Batet_2_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(WeightedTaxonomy wt, Node c) {
		double result = 0.0d;
		double n_leaves = ((double)wt.leaves().size());
		result = - Math.log(
				((double)wt.leaves(c).size()/((double)wt.ancestors(c).size()) + 1) /
				(n_leaves+1)
				);
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
