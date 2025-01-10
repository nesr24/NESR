package it.cnr.iasi.saks.semrel.method.ldsd;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;

public class LDSD_combined_weighted extends LDSD_Abstract {
	
	LDSD_direct_weighted ldsd_direct_weighted = null;
	LDSD_indirect_weighted ldsd_indirect_weighted = null;
	
	public LDSD_combined_weighted(KnowledgeBase kb) {
		this.setKb(kb);
		this.setLdsd_direct_weighted(new LDSD_direct_weighted(kb));
		this.setLdsd_indirect_weighted(new LDSD_indirect_weighted(kb));
	}
	public LDSD_direct_weighted getLdsd_direct_weighted() {
		return ldsd_direct_weighted;
	}

	public void setLdsd_direct_weighted(LDSD_direct_weighted ldsd_direct_weighted) {
		this.ldsd_direct_weighted = ldsd_direct_weighted;
	}

	public LDSD_indirect_weighted getLdsd_indirect_weighted() {
		return ldsd_indirect_weighted;
	}

	public void setLdsd_indirect_weighted(LDSD_indirect_weighted ldsd_indirect_weighted) {
		this.ldsd_indirect_weighted = ldsd_indirect_weighted;
	}

	public double ldsd_(Node n1, Node n2) {
//		System.out.println("\t"+ n1 + " --- "+ n2);
		double result = 0.0d;
		
		double denom = 1.0d;
		
//		System.out.println("\t\t directLinks("+n1+","+n2+")="+ ldsd_direct_weighted.directLinks(n1, n2));
		// for all the p1 such that <n1, p1, n2> exists	
		for(Node p:ldsd_direct_weighted.directLinks(n1, n2)) {
			double d = ldsd_direct_weighted.Cd_np(n1, p);
			denom = denom + 1/(1+Math.log(d));
		}

		
//		System.out.println("\t\t directLinks("+n2+","+n1+")="+ ldsd_direct_weighted.directLinks(n2, n1));		
		// for all the p1 such that <n2, p1, n1> exists		
		for(Node p:ldsd_direct_weighted.directLinks(n2, n1)) {
			double d = ldsd_direct_weighted.Cd_np(n2, p);
			denom = denom + 1/(1+Math.log(d));
		} 

//		System.out.println("\t\t indirectLinks_incoming("+n1+","+n2+")="+ ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_INCOMING));
		// for all the indirect incoming links
		for(Node p:ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_INCOMING))
			denom = denom + 1/(1+Math.log(ldsd_indirect_weighted.Cii_np(n1, p)));

		
//		System.out.println("\t\t indirectLinks_outgoing("+n1+","+n2+")="+ ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_OUTGOING));
		// for all the indirect outgoing links
		for(Node p:ldsd_indirect_weighted.indirectLinks(n1, n2, Constants.METHOD_LDSD_OUTGOING))
			denom = denom + 1/(1+Math.log(ldsd_indirect_weighted.Cio_np(n1, p)));

		
		result = 1.0d/denom;
		
		return result;
	}
	
	public double semrel(Node n1, Node n2) {
//		System.out.println("\t"+ n1 + " --- "+ n2);
		double result = 0.0d;
		
		result = 1.0d / (1.0d + ldsd_direct_weighted.denom(n1, n2) + ldsd_indirect_weighted.denom(n1, n2));
		
		
		return result;
	}
}
