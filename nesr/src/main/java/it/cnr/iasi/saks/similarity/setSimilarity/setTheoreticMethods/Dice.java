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

public class Dice {
	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		
		Set<Node> intersection = new HashSet<Node>();
		intersection.addAll(s1);
		intersection = SetUtilities.intersection(intersection, s2);
		
		result = 2*(double)intersection.size() / (double)(s1.size() + s2.size()); 
		
		return result;
	}
}
