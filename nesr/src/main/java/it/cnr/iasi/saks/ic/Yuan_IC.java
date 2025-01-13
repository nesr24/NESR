package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Yuan_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		result = Math.log(((double)wt.depth(n))) / Math.log(((double)wt.height())) *
				(1 - Math.log(((double)wt.leaves(n).size()) + 1) / Math.log(((double)wt.leaves().size() + 1))) * 
				(1 - (Math.log(((double)wt.ancestors(n).size()) + 1) / Math.log(((double)wt.getIds().size()))));
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
