package it.cnr.iasi.saks.semrel.method.gnldsd;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.method.gnldsd.GNLDSD_Abstract;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_direct_weighted;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_indirect_weighted;

public class GNLDSD_alpha extends GNLDSD_Abstract {

	public GNLDSD_alpha(KnowledgeBase kb) {
		this.setKb(kb);
		this.setLdsd_direct_weighted(new LDSD_direct_weighted(kb));
		this.setLdsd_indirect_weighted(new LDSD_indirect_weighted(kb));
	}
	
	/**
	 * The method implements the the C_prime_io(l1, ra, rb) function in the Piao and Breslin article.
	 * @param n1
	 * @param n2
	 * @return The number of resources nx linked to n1 and n2 via an incoming predicate p1.
	 * The corresponding pattern is <n1, p1, nx> . <n2, p1, nx>.
	 */
	public double Cio_prime_npn(Node n1, Node p, Node n2) {
		double result = 0.0d;
		
		result = this.Ci_prime_npn(n1, p, n2, Constants.METHOD_LDSD_OUTGOING);
		
		return result;
	}
	
	
	/**
	 * The method implements the the C_prime_ii(l1, ra, rb) function in the Piao and Breslin article.
	 * @param n1
	 * @param n2
	 * @return The number of resources nx linked to n1 and n2 via an incoming predicate p1.
	 * The corresponding pattern is <nx, p1, n1> . <nx, p1, n2>.
	 */	public double Cii_prime_npn(Node n1, Node p, Node n2) {
		double result = 0.0d;
		
		result = this.Ci_prime_npn(n1, p, n2, Constants.METHOD_LDSD_INCOMING);
		
		return result;
	}
	
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		if(n1.getURI().toString() == n2.getURI().toString())
			result = 1.0d;
		else {
			double denom = 1.0d;
			
			// for all the p1 such that <n1, p1, n2> exists	
			for(Node p:ldsd_direct_weighted.directLinks(n1, n2)) {
				double d = ldsd_direct_weighted.Cd_np(n1, p);
				denom = denom + 1/(1+Math.log(d));
			}
	
			// for all the p1 such that <n2, p1, n1> exists		
			for(Node p:ldsd_direct_weighted.directLinks(n2, n1)) {
				double d = ldsd_direct_weighted.Cd_np(n2, p);
				denom = denom + 1/(1+Math.log(d));
			}
	
			// for all the indirect incoming links
			for(Node p:ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_INCOMING))
				denom = denom + Cii_prime_npn(n1, p, n2)/(1+Math.log(ldsd_indirect_weighted.Cii_np(n1, p)));
			
			// for all the indirect outgoing links
			for(Node p:ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_OUTGOING))
				denom = denom + Cio_prime_npn(n1, p, n2)/(1+Math.log(ldsd_indirect_weighted.Cio_np(n1, p)));
	
			double dist = 1.0d/denom;
			
			result = 1.0d - dist;
		}
			
		return result;
	}
	
	public double Ci_prime_npn(Node n1, Node p, Node n2, String direction) {
		double result = 0.0d;
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p, nx);
			t2 = new Triple(n2, p, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx, p, n1);
			t2 = new Triple(nx, p, n2);
		}
		
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("nx");
		
		result = this.getKb().countNodesByPattern(pattern);
		
		return result;
	}
}
