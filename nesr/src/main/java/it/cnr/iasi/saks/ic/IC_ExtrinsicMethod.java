package it.cnr.iasi.saks.ic;

import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface IC_ExtrinsicMethod extends IC_Method {
	double ic(double likelihood);
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt, OFVMgmt ofvmgmt);

}
