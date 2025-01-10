package it.cnr.iasi.saks.similarity.setSimilarity.taxonomyBasedMethods;

import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.similarity.conceptsSimilarity.Li_Bandar_McLean;

public class Haase_Siebes_vanHarmelen {
	Li_Bandar_McLean csm = null;
	
	public Haase_Siebes_vanHarmelen(Li_Bandar_McLean csm) {
		this.csm = csm;
	}
	
	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		
		for(Node n1:s1) {
			double temp = 0.0d;
			for(Node n2:s2) {
				if(csm.sim(n1, n2)>temp)
					temp = csm.sim(n1, n2);
			}
			result = result + temp;
		}
				
		result = result / ((double)s1.size());
		return result;
	}
}
