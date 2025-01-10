package it.cnr.iasi.saks.similarity.conceptsSimilarity;

import org.apache.jena.graph.Node;

public interface ConceptSimilarity_Interface {
	double sim(Node n1, Node n2);
}
