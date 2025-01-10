package it.cnr.iasi.saks.semrel.ftaf.asrmp;

import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.ftaf.asrmp.aggregation.AggregationStrategy_Interface;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy_OntModel;

public interface ASRMP_Interface {
	public double semrel(Node n1, Node n2) ;
	public double pathAggregation(Path path, KnowledgeBase kb, AggregationStrategy_Interface aggregationStrategy, WeightedTaxonomy_OntModel wt);
	public double pathsAggregation(Vector<Path> paths, KnowledgeBase kb, AggregationStrategy_Interface aggregationStrategy);
	
}
