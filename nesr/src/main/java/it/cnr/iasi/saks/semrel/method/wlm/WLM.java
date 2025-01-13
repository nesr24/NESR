package it.cnr.iasi.saks.semrel.method.wlm;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

public class WLM implements SemanticRelatednessStrategy {
	protected KnowledgeBase kb = null;
	
	public WLM(KnowledgeBase kb) {
		super();
		this.kb = kb;
	}

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}	
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		PathPattern pattern = new PathPattern(null);
		Node u1 = NodeFactory.createVariable("u1");
		Node p1 = NodeFactory.createVariable("p1");
		Triple t = new Triple(u1,p1,n1);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> entities_linking_to_n1 = this.getKb().nodesByPattern(pattern);
		
		pattern = new PathPattern(null);
		t = new Triple(u1,p1,n2);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> entities_linking_to_n2 = this.getKb().nodesByPattern(pattern);

		double numer_add1 = Math.max(entities_linking_to_n1.size(), entities_linking_to_n2.size());
		numer_add1 = Math.log(numer_add1);

		double numer_add2 = intersection(entities_linking_to_n1, entities_linking_to_n2).size();
		numer_add2 = Math.log(numer_add2);
		
		double numer =  numer_add1 - numer_add2 ;

		double denom_add1 = this.getKb().countAllNodes();
//		double denom_add1 = 10.0d;
		denom_add1 = Math.log(denom_add1);
		
		double denom_add2 = Math.min(entities_linking_to_n1.size(), entities_linking_to_n2.size());
		denom_add2 = Math.log(denom_add2);

		double denom = denom_add1 - denom_add2;
		
		if(numer_add2 == Double.NEGATIVE_INFINITY)
			result = Double.MAX_VALUE;
		else if(denom_add2 == Double.NEGATIVE_INFINITY)
			result = 0.0d;
		else if(numer_add2 == Double.NEGATIVE_INFINITY && denom_add2 == Double.NEGATIVE_INFINITY) 
			result = 1.0d;
		else result = numer/denom;
		
		result = 1 /(1+result);
		
		return result;
	}
	
	public static Vector<Node> intersection(Vector<Node> s1, Vector<Node> s2) {
		Vector<Node> result = new Vector<Node>();
		for(Node n:s1)
			result.add(n);
		result.retainAll(s2);
		return result;
	}
}
