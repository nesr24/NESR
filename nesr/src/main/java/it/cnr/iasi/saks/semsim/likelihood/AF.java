/**
 * This Class implements the Annotation Frequency method
 */

package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class AF implements CorpusBasedLikelihood {
	Map<String, Vector<OFVElem>> avs = new HashMap<String, Vector<OFVElem>>();
	Map<String, Set<String>> classId_avs = new HashMap<String, Set<String>>();
	
	public Map<String, Vector<OFVElem>> getAvs() {
		return avs;
	}

	public void setAvs(Map<String, Vector<OFVElem>> avs) {
		this.avs = avs;
	}

	public Map<String, Set<String>> getClassId_avs() {
		return classId_avs;
	}

	public void setClassId_avs(Map<String, Set<String>> classId_avs) {
		this.classId_avs = classId_avs;
	}

	public Map<Node, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs) {
		Map<Node, Double> result = new HashMap<Node, Double>();

		this.classId_avs(wt, avs);
		
		Set<Node> classes = wt.allClasses();
		for(Node n:classes) {
			result.put(n, ((double)(this.getClassId_avs().get(n.getURI().toString())).size())/avs.size());
		}
		return result;
	}	
	
	public Map<String, Set<String>> classId_avs(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		Set<Node> classes = wt.allClasses();
		for(Node n:classes) {
			this.getClassId_avs().put(n.getURI().toString(), new HashSet<String>());
		}
		this.getClassId_avs().put(Constants.OWL_THING, new HashSet<String>());
		
		
		for(String av_id:avs.keySet()) {
			for(OFVElem ofv_elem:avs.get(av_id)) {
				Node c = ofv_elem.getConc_id();
				
				// add explicit annotation
//				String pref = "http://terin-sen-apic.org/SemSim-AI-TF-IDF#";
				
				System.out.println("c="+c);
				System.out.println(this.getClassId_avs());
				this.getClassId_avs().get(c.toString()).add(av_id);

				// add implicit annotation
				Vector<Node> ancestors = wt.ancestors(c);
				for(Node n:ancestors) {
					this.getClassId_avs().get(n.getURI().toString()).add(av_id);
				}
			}
		}
		
		return result;
	}

}
