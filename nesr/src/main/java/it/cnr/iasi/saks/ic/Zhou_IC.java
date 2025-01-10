package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Zhou_IC implements IC_IntrinsicMethod {
	double k = 0.0d;
	
	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}


	Map<Node, Double> ics = new HashMap<Node, Double>();
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
//		System.out.println(n.getURI().toString());
//		System.out.println("\t depth: "+ Math.log(wt.depth(n)));
//		System.out.println("\t descendants: "+ Math.log(wt.descendants(n).size()+1));
//		System.out.println("\t ids: "+ Math.log(wt.getIds().size()));
//		System.out.println("\t height: "+ Math.log(wt.height()));

		result = this.getK() * (1 - (((double)Math.log(wt.descendants(n).size()+1)))/((double)wt.getIds().size())) + 
				(1 - this.getK()) * (((double)Math.log(wt.depth(n))) / ((double)Math.log(wt.height())));
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
