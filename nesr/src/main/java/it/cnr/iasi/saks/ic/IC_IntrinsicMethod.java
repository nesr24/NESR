package it.cnr.iasi.saks.ic;

import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface IC_IntrinsicMethod extends IC_Method {
	double ic(WeightedTaxonomy wt, Node n);
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt);

}
