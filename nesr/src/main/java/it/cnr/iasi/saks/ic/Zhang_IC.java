package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Zhang_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		Vector<Node> hyper = wt.ancestors(n); 
		double n_hypo = (double)wt.descendants(n).size();
		double k = n_hypo/(((double)hyper.size())+n_hypo);
		
		double fact1 = 1.0d;

		
		for(Node c:hyper) {
			fact1 = fact1 * 1.0d/((double)wt.siblings(c).size()+1);
		}
		
		
		double fact2 = k*(1 - Math.log(n_hypo + 1)/Math.log(((double)wt.getIds().size())));
		
		
		result = - (1-k)* 1/((double)wt.parents(n).size()) * Math.log(fact1 + 1) + fact2;
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
