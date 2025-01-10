package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.likelihood.AF;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class AnnotationFrequency_IC implements IC_ExtrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(double likelihood) {
		double result = 0.0d;
		result = -Math.log(likelihood);;
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt, OFVMgmt ofvmgmt) {
		AF af = new AF();
		Map<Node, Double> weights = af.likelihood(wt, ofvmgmt.getAvs());
		for(Node n: weights.keySet()) {
			double ic = ic(weights.get(n));
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}

	
}
