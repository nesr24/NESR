package it.cnr.iasi.saks.similarity.setSimilarity.taxonomyBasedMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;
import it.cnr.iasi.saks.similarity.conceptsSimilarity.Leacock_Chodorow;

public class WNSim {
	private Leacock_Chodorow lc = null;
	private Map<String,Vector<OFVElem>> ofvs = new HashMap<String,Vector<OFVElem>>();
		
	public WNSim(WeightedTaxonomy wt, Map<String,Vector<OFVElem>> ofvs) {
		lc = Leacock_Chodorow.getInstance(wt);
		this.setOfvs(ofvs);
	}
	
	public Leacock_Chodorow getLc() {
		return lc;
	}

	public void setLc(Leacock_Chodorow lc) {
		this.lc = lc;
	}

	public Map<String,Vector<OFVElem>> getOfvs() {
		return ofvs;
	}

	public void setOfvs(Map<String,Vector<OFVElem>> ofvs) {
		this.ofvs = ofvs;
	}

	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		
//		int d = lc.distance(NodeFactory.createURI("http://acm_test/1017"), NodeFactory.createURI("http://acm_test/1824"));
		double numer = 0.0d;
		double denom = 0.0d;
		
		for(Node s_1:s1) {
			double max = 0.0d;
			for(Node s_2:s2) {
				double sim_lc = ((double)lc.sim(s_1, s_2));
				if(sim_lc > max) {
					max = sim_lc;
				}			}
			double idf = idf(s_1);
			numer = numer + max*idf;
			denom = denom + idf;

		}
		
		result = numer / denom;
		
		return result;
	}
	
	public double idf(Node n) {
		double result = 0.0d;
		int freq = 0;
		for(Vector<OFVElem> ofv:this.getOfvs().values()) {
			for(OFVElem elem:ofv) 
				if(elem.getConc_id() == n)
				freq ++;
		}
		result = Math.log(
				((double)this.getOfvs().size()) / 
				((double)freq));		
		return result;
	}
}
