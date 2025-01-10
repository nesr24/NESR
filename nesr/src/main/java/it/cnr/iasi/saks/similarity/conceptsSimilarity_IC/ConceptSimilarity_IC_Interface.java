package it.cnr.iasi.saks.similarity.conceptsSimilarity_IC;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.ic.IC_IntrinsicMethod;

public interface ConceptSimilarity_IC_Interface {
	double sim(Node n1, Node n2, IC_IntrinsicMethod icm);
}
