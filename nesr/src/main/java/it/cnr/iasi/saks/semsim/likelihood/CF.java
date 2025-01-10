/**
 * This Class implements the Concept Frequency method
 */

package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class CF implements CorpusBasedLikelihood {
	Map<Node, Integer> classId_times = new HashMap<Node, Integer>();
		
	public Map<Node, Integer> getClassId_times() {
		return classId_times;
	}

	public void setClassId_times(Map<Node, Integer> classId_times) {
		this.classId_times = classId_times;
	}
	
	public Map<Node, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs) {
		Map<Node, Double> result = new HashMap<Node, Double>();

		Set<Node> classes = wt.allClasses();
		for(Node n:classes)
			this.getClassId_times().put(n, 0);
		
		double total_used_times = 0;
		for(String av_id:avs.keySet()) {
			total_used_times = total_used_times + (double)(avs.get(av_id).size());
			for(OFVElem ofv_elem:avs.get(av_id)) {
				Node c = ofv_elem.getConc_id(); 
				// add explicit annotation
				this.getClassId_times().put(c, this.getClassId_times().get(c)+1);
				// add implicit annotation
				Vector<Node> ancestors = wt.ancestors(c);
				for(Node n:ancestors) {
					this.getClassId_times().put(n, this.getClassId_times().get(n)+1);
				}
	
			}
		}
		
		for(Node n:classes) {
			result.put(n, ((double)(this.getClassId_times().get(n)))/total_used_times);
		}
		
		return result;
	}	
}
