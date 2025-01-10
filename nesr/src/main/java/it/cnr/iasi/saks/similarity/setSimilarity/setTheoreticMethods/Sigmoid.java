package it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods;

import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods.utilities.SetUtilities;

public class Sigmoid {
	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		Set<Node> inters = SetUtilities.intersection(s1, s2);
		
		result = (Math.pow(Math.E, inters.size()) - 1) /
				((Math.pow(Math.E, inters.size()) + 1) *
					((double)(SetUtilities.difference(s1, s2).size() +
					 SetUtilities.difference(s2, s1).size() + 1
					))
				);
					
		return result;
	}
}
