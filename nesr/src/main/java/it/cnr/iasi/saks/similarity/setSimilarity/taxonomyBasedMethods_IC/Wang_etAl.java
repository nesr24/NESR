package it.cnr.iasi.saks.similarity.setSimilarity.taxonomyBasedMethods_IC;

import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.ic.Resnik_IC;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Wang_etAl {
	
	private WeightedTaxonomy wt = null; 
	private OFVMgmt ofvmgmt = null;
	private Resnik_IC resnik_ic = null; 
	
	public Wang_etAl(WeightedTaxonomy wt, OFVMgmt o, Resnik_IC r) {
		this.setWt(wt);
		this.setOfvmgmt(o);
		this.setResnik_ic(r);
	}
		
	public WeightedTaxonomy getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}

	public OFVMgmt getOfvmgmt() {
		return ofvmgmt;
	}

	public void setOfvmgmt(OFVMgmt ofvmgmt) {
		this.ofvmgmt = ofvmgmt;
	}
	
	public Resnik_IC getResnik_ic() {
		return resnik_ic;
	}

	public void setResnik_ic(Resnik_IC resnik_ic) {
		this.resnik_ic = resnik_ic;
	}

	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		
		for(Node n1:s1) {
			for(Node n2:s2) {
				Vector<Node> lubs = this.getWt().lub(n1, n2);
				for(Node l:lubs) {
					result = result + this.getResnik_ic().getIcs().get(l); 
				}
			}
		}
				
		result = result / ((double)s1.size()*(double)s2.size());
		return result;
	}

}
