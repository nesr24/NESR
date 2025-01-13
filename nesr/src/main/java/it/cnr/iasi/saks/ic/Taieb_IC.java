package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Taieb_IC implements IC_IntrinsicMethod {
	
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
		Vector<Node> hyper = wt.ancestors(n);
		for(Node a:hyper) {
			temp = temp + this.score(wt, a);
		}
		result = temp * this.avgDepth(wt, n);
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt) {
		for(Node n: wt.getIds()) {
			double ic = ic(wt, n);
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}

	private double score(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		double temp = 0.0d;
		Vector<Node> parents = wt.parents(n);
		for(Node p:parents) {
			temp = temp + (((double)wt.depth(p)) / ((double)wt.descendants(p).size()));
		}
		result = temp * ((double)wt.descendants(n).size());
		return result;
	}
	
	private double avgDepth(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		double temp = 0.0d;
		Vector<Node> hyper = wt.ancestors(n);
		for(Node a:hyper) {
			temp = temp + ((double)wt.depth(a));
		}
		result = temp / ((double)wt.ancestors(n).size());
		return result;
	}
}
