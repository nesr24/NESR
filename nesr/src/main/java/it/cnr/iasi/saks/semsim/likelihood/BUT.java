package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class BUT implements IntrinsicLikelihood {
	Map<Node, Double> likelihood = new HashMap<Node, Double>();
	
	public Map<Node, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<Node, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	
	public Map<Node, Double> likelihood(WeightedTaxonomy wt) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		for(Node c:wt.allClasses()) {
			this.getLikelihood().put(c, 0d);
		}
		double leaf_weight = 1.0d / ((double)wt.leaves().size());
		for(Node leaf:wt.leaves()) {
			this.getLikelihood().put(leaf, leaf_weight);
			for(Node anc:wt.ancestors(leaf)) {
				double old_value = this.getLikelihood().get(anc);
				this.getLikelihood().put(anc, old_value + leaf_weight);
			}
		}
		result = this.getLikelihood();
		return result;
	}
	
}
