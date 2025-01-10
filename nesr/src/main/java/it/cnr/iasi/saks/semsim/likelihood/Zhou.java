package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Zhou implements IntrinsicLikelihood {
	Map<Node, Double> likelihood = new HashMap<Node, Double>();
	
	public Map<Node, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<Node, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	
	public Map<Node, Double> likelihood(WeightedTaxonomy wt) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		return result;
	}
}
