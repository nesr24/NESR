package it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods;

import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods.utilities.SetUtilities;

public class Tversky {
	public double sim(Set<Node> s1, Set<Node> s2, double alpha, double beta, double gamma) {
		double result = 0.0d;
		double inters = ((double)SetUtilities.intersection(s1, s2).size());
		double s1_diff_s2 = ((double)SetUtilities.difference(s1, s2).size());
		double s2_diff_s1 = ((double)SetUtilities.difference(s2, s1).size());
		
		result = alpha * inters /
				(beta * s1_diff_s2 + gamma * s2_diff_s1 + alpha * inters);
					
		return result;
	}
}
