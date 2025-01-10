package it.cnr.iasi.saks.semrel.relatedness;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

public class SemanticRelatedness {
	public  double semrel(Node n1, Node n2, SemanticRelatednessStrategy method) {
		double result = 0.0d;
				
		result = method.semrel(n1, n2);

		return result;
	}
}
