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



public class Meng_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		
		double temp = 0.0d;
		for(Node c:wt.descendants(n)) {
			temp = temp + (1 / wt.depth(c));
		}

		result = (Math.log(wt.depth(n)) / Math.log(wt.height())) * 
				((1 - (Math.log(temp+1)) / Math.log(wt.getIds().size())));
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt) {
		for(Node n: wt.getIds()) {
			double ic = ic(wt, n);
			System.out.println("ic="+ic);
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}
}
