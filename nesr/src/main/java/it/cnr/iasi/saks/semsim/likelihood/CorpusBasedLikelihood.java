package it.cnr.iasi.saks.semsim.likelihood;

import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface CorpusBasedLikelihood {
	public Map<Node, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs);
}
