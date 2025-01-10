package it.cnr.iasi.saks.semrel.method;

import org.apache.jena.graph.Node;

public interface SemanticRelatednessStrategy {
	public double semrel(Node n1, Node n2);
}
