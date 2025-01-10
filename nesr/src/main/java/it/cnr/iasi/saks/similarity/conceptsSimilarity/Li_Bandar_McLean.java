package it.cnr.iasi.saks.similarity.conceptsSimilarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Li_Bandar_McLean implements ConceptSimilarity_Interface {
	Map<String, Double> sim_cache = new HashMap<String, Double>();
	WeightedTaxonomy wt = null;
	double alpha = 0.0d;
	double beta = 0.0d;
	
	public Li_Bandar_McLean(WeightedTaxonomy wt, double alpha, double beta) {
		this.wt = wt;
		this.alpha = alpha;
		this.beta = beta;
	}
	
	public double sim(Node n1, Node n2) {
		double result = 0.0d;
		
		if(n1.getURI().toString().equals(n2.getURI().toString())) 
			result = 1.0d;
		else {
			String key = n1.getURI().toString()+"-"+n2.getURI().toString();
			if(sim_cache.get(key) == null) {				
				int distance = this.wt.distanceInTree(n1, n2);
				Vector<Node> lubs = this.wt.lub(n1, n2);
				Map<Node, Double> lubs_values = new HashMap<Node, Double>();
				for(Node n:lubs)
					lubs_values.put(n, ((double)this.wt.depth(n)));
				Node lub = this.wt.bestLub(lubs_values);
				double lub_depth = ((double)lubs_values.get(lub));
				
				double numer = Math.exp(this.beta * lub_depth) - Math.exp(-this.beta * lub_depth);
				double denom = Math.exp(this.beta * lub_depth) + Math.exp(-this.beta * lub_depth);
				double exp = numer / denom;
				result = Math.exp(-this.alpha*((double)distance)) * exp;
				sim_cache.put(key, result);
				if(sim_cache.get(key) > 1) {
					System.out.println(lub+": "+lub_depth);
					System.out.println(n1);
					System.out.println(n2);
					System.out.println(distance);
					System.out.println(result);
					System.exit(0);
				}

			}
			else {
				result = sim_cache.get(key);
			}
		}
		return result;
	}
}
