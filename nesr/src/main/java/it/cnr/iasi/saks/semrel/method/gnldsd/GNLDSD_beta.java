package it.cnr.iasi.saks.semrel.method.gnldsd;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_direct_weighted;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_indirect_weighted;

public class GNLDSD_beta extends GNLDSD_alpha {
	
	public GNLDSD_beta(KnowledgeBase kb) {
		super(kb);
		this.setLdsd_direct_weighted(new LDSD_direct_weighted(kb));
		this.setLdsd_indirect_weighted(new LDSD_indirect_weighted(kb));
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
				denom = denom + Cii_prime_npn(n1, p, n2)/(1+(Math.log(ldsd_indirect_weighted.Cii_np(n1, p))+Math.log(ldsd_indirect_weighted.Cii_np(n2, p)))/2);
			
			// for all the indirect outgoing links
			for(Node p:ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_OUTGOING))
				denom = denom + Cio_prime_npn(n1, p, n2)/(1+(Math.log(ldsd_indirect_weighted.Cio_np(n1, p))+Math.log(ldsd_indirect_weighted.Cio_np(n2, p)))/2);
	
			double dist  = 1.0d/denom;
			
			result = 1.0d - dist;
		}
			
		return result;
	}
}
