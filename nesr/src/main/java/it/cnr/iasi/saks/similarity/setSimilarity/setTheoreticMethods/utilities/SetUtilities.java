package it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods.utilities;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;

public class SetUtilities {
	public static Set<Node> intersection(Set<Node> s1, Set<Node> s2) {
		Set<Node> result = new HashSet<Node>();
		result.addAll(s1);
		result.retainAll(s2);
		return result;
	}
	
	public static Set<Node> difference(Set<Node> s1, Set<Node> s2) {
		Set<Node> result = new HashSet<Node>();
		result.addAll(s1);
		result.removeAll(s2);
		return result;
	}

	public static Set<Node> union(Set<Node> s1, Set<Node> s2) {
		Set<Node> result = new HashSet<Node>();
		result.addAll(s1);
		result.addAll(s2);
		return result;
	}
}
