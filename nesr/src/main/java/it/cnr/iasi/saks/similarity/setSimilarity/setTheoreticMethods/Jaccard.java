package it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods.utilities.SetUtilities;

public class Jaccard {
	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		Set<Node> union = SetUtilities.union(s1, s2);
		
		Set<Node> intersection = SetUtilities.intersection(s1, s2);
		
		result = (double)intersection.size() / (double)union.size(); 
		
		return result;
	}
}
