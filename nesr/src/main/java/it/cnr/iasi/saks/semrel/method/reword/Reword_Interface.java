package it.cnr.iasi.saks.semrel.method.reword;

import java.util.Map;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

public interface Reword_Interface extends SemanticRelatednessStrategy {
	public Map<Node, Double> relatednessSpace_in(Node n);
	public Map<Node, Double> relatednessSpace_out(Node n);
}
