package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class M3 extends AF {
	
	public Set<OFVElem> annotationClosure(WeightedTaxonomy wt, Vector<OFVElem> av) {
		Set<OFVElem> result = new HashSet<OFVElem>();
		result.addAll(av);
		for(OFVElem ofv_elem:av) {
			Node id = ofv_elem.getConc_id();
			Vector<Node> ancs = wt.ancestors(id);
			for(Node anc:ancs) {
				OFVElem new_ofv_elem = new OFVElem(anc, ofv_elem.getCoeff());
				result.add(new_ofv_elem);
			}
		}
		
		return result;
	}
	
	public Map<Node, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		this.setAvs(avs);
		this.classId_avs(wt, avs);
		Set<Node> classes = wt.allClasses();
		for(Node c:classes) {
			double value = 0d;
			for(String id_av:avs.keySet()) {
				double temp = 0;
				if(annotationClosure(wt, avs.get(id_av)).contains(c)) {
					Vector<Node> descs = wt.descendants(c);
					Set<String> ids_descs = new HashSet<String>();
					
					for(Node n:descs) 
						ids_descs.add(n.getURI().toString());
					Set<String> ids_descs_inAnnotation = new HashSet<String>();
					ids_descs_inAnnotation.addAll(ids_descs);
					ids_descs_inAnnotation.retainAll(avs.get(id_av));
					
					double num = (ids_descs_inAnnotation.size() + 1)/(double)(avs.size());
					temp = (num)*1d/((double)avs.size());
				}
				value = value + temp;
			}
			result.put(c, value);
		}
		
		double x = result.get(Constants.OWL_THING);
		for(Node c:classes) {
			result.put(c, result.get(c)/x);
		}
		return result;
	}
}
