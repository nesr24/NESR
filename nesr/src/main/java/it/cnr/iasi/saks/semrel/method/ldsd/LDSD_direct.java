package it.cnr.iasi.saks.semrel.method.ldsd;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class LDSD_direct extends LDSD_Abstract {

	public LDSD_direct(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * This method implements the C_d(li, ra, rb) function in the Passant's article.
	 * The suffix npn in the method's name says that the subject (n), the predicate (p), and object (n) of the searched triple are given as input
	 * @param n1 
	 * @param n2 
	 * @return 1 if the triple <n1, p1, n2> exists, 0 otherwise. p1 must be a link from n1 to n2. the direction of the link is relevant.
	 */
	public double Cd_npn(Node n1, Node n2, Node p1) {
		double result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Triple t = new Triple(n1, p1, n2);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("p1");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);

		if(this.kb.pathExistence(pattern))
			result = 1;
		
		return result;
	}
	
	/**
	 * This method implements the C_d(n, ra, rb) function in the Passant's article.
	 * The suffix nn in the method's name says that the subject (n) and the object (n) of the searched triples are given as input, whereas the predicate is not given.
	 * @param n1
	 * @param n2
	 * @return The number of direct and distinct links from n1 to n2. The direction of the link is relevant.
	 */
	public double Cd_nn(Node n1, Node n2) {
		double result = 0;
		PathPattern pattern = new PathPattern(null);
		Node p1 = NodeFactory.createVariable("p1");
		Triple t = new Triple(n1, p1, n2);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("p1");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		
		result = this.kb.countNodesByPattern(pattern);
		
		return result;
	}
	
	/**
	 * This method implements the C_d(li, ra, n) function in the Passant's article.
	 * The suffix np in the method's name says that the subject (n) and the predicate (p) of the searched triples are given as input. 
	 * @param n1
	 * @param p1
	 * @return The total number of nodes nx such that <n1, p1, nx> is a triple in the graph.
	 */
	public double Cd_np(Node n1, Node p1) {
		double result = 0;
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Triple t = new Triple(n1, p1, nx);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("nx");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);

		result = this.kb.countNodesByPattern(pattern);
		
		return result;
	}
	
	/**
	 * This method implements the LDSD_d function in the Passant's article
	 */
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		result = 1.0d/(1.0d+Cd_nn(n1, n2)+Cd_nn(n2, n1));
		
		return result;
	}
	
}
