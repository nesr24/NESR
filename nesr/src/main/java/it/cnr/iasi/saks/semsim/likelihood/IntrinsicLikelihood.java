package it.cnr.iasi.saks.semsim.likelihood;

import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface IntrinsicLikelihood {
	public Map<Node, Double> likelihood(WeightedTaxonomy wt);
}
