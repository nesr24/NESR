package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;



public class Sanchez_Batet_1_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	double commonnesRoot = 0.0d;
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	
	public double getCommonnesRoot() {
		return commonnesRoot;
	}

	public void setCommonnesRoot(double commonnesRoot) {
		this.commonnesRoot = commonnesRoot;
	}

	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		double commonness_n = commonness(wt, n);
		double commonness_root = 0.0d;
		if(this.getCommonnesRoot() > 0)
			commonness_root = this.getCommonnesRoot();
		else {
			commonness_root = commonness(wt, NodeFactory.createURI("http://acm_test/0"));		
			this.setCommonnesRoot(commonness_root);
		}
		result = commonness_n / commonness_root;
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt) {
		for(Node n: wt.getIds()) {
			double ic = ic(wt, n);
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}
	
	private double commonness(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		double temp = 0.0d;
		if(wt.leaves().contains(n)) {
			double size = wt.ancestors(n).size();
			temp = 1 / size;
		}
		else {
			for(Node leaf:wt.leaves(n)) {
				double size = wt.ancestors(leaf).size();
				temp = temp + 1/size;
			}			
		}		
		result = temp;
//		System.out.println("commonness("+n.getURI().toString()+")="+result);
		return result;
	}

	
}
