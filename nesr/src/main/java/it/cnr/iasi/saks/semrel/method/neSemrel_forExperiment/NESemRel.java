package it.cnr.iasi.saks.semrel.method.neSemrel_forExperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.RDFGraph;

public class NESemRel extends NESemRel_Abstract {

	private RDFGraph kb = null; 
	private String wikidata_server = "";
		
	public RDFGraph getKb() {
		return kb;
	}

	public void setKb(RDFGraph kb) {
		this.kb = kb;
	}

	public String getWikidata_server() {
		return wikidata_server;
	}

	public void setWikidata_server(String wikidata_server) {
		this.wikidata_server = wikidata_server;
	}

	public NESemRel(RDFGraph kb, String wikidata_server) {
		this.setKb(kb);
		this.setWikidata_server(wikidata_server);
	}
	
	public Vector<Double> semrel(Node n1, Node n2) {
		Vector<Double> result = new Vector<Double>();

		Vector<Path> paths = kb.paths(n1, n2, 2, Constants.DIRECTED_PATH, true);
		System.out.println(n1.getURI().toString()+" --- "+n2.getURI().toString()+" --- paths="+paths.size());
		
		Vector<String> innerNodes = new Vector<String>();
		int count_paths = 0;
		double paths_result = 0.0d; 
		for(Path p:paths) {
			System.out.println("path nr. = "+count_paths+"/"+paths.size());
			Node candidate = null;
			if(p.size()==2) {
				Triple t = p.getTriples().get(0);
				if(t.getSubject().getURI().toString().equalsIgnoreCase(n1.getURI().toString()) ||
						t.getSubject().getURI().toString().equalsIgnoreCase(n2.getURI().toString())) {
					candidate = t.getObject();
				}
				else 			
					candidate = t.getSubject();
				if(candidate.isURI()) {
					if(innerNodes.contains(candidate.getURI().toString())) 
						candidate = null;
					else innerNodes.add(candidate.getURI().toString());
					
					if(candidate!=null) {
						if(isCandidateOK(candidate)) {
							NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
							double temp1 = nesr.adjacentNodesRelatedness(n1, candidate);
							double temp2 = nesr.adjacentNodesRelatedness(n2, candidate);
							paths_result = paths_result + temp1 + temp2;
						}
					}	
				}
			}
			count_paths++;
				
		}
		NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
		result.add(nesr.adjacentNodesRelatedness(n1, n2));
		result.add(paths_result);
		result.add((double)count_paths);
		return result;
	}

	private boolean isCandidateOK(Node c) {
		if(!(c.getURI().toString().equalsIgnoreCase(Constants.OWL_THING)))
			if(c.getURI().toString().startsWith(Constants.DBPEDIA_NS))
//			if(c.getURI().toString().startsWith(Constants.YAGO_NS))
				return true;
		return true;
	}

	/**
	 * This method prunes the set of nodes involved in paths connecting n1 and n2
	 * @param n1
	 * @param n2
	 * @return
	 */
	public Map<String, Double> semrel_forExperiment(Node n1, Node n2) {
		Map<String, Double> result = new HashMap<String, Double>();
		
		if(n1.getURI().toString().equalsIgnoreCase(n2.getURI().toString())) {
			result.put("a0", 1.0d);
			result.put("n1", 3.0d);result.put("n2", 5.0d);
			result.put("n3", 7.0d);result.put("n4", 9.0d);
			result.put("n5", 11.0d);result.put("n6", 13.0d);
			result.put("n7", 15.0d);result.put("n8", 17.0d);
			result.put("n9", 19.0d);result.put("n10", 21.0d);
		}
		else {
			Vector<Path> paths = kb.paths(n1, n2, 2, Constants.UNDIRECTED_PATH, true);
			System.out.println(n1.getURI().toString()+" --- "+n2.getURI().toString()+" --- paths="+paths.size());
			
			Map<String, Integer> innerNodes = new HashMap<String, Integer>();
	
			double count_paths_1 = 0.0d;
			Vector<Double> paths_result = new Vector<Double>(); 
			for(Path p:paths) {
	//			System.out.println("path nr. = "+count_paths+"/"+paths.size());
				Node candidate = null;
				Triple t = p.getTriples().get(0);
				if(t.getSubject().getURI().toString().equalsIgnoreCase(n1.getURI().toString()) ||
						t.getSubject().getURI().toString().equalsIgnoreCase(n2.getURI().toString())) {
					candidate = t.getObject();
				}
				else 			
					candidate = t.getSubject();
				if(candidate.isURI()) {
					if(this.isCandidateOK(candidate)) {
						String c = candidate.getURI().toString();
						if(innerNodes.keySet().contains(c)) {
							innerNodes.put(c, innerNodes.get(c)+1);
							candidate = null;
						}
						else 
							if(isCandidateOK(candidate))
								innerNodes.put(c, 1);
					}
				}
			}
			
			LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
	        ArrayList<Integer> list = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : innerNodes.entrySet()) {
	            list.add(entry.getValue());
	        }
	        Collections.sort(list, Collections.reverseOrder()); 
	        for (int num : list) {
	            for (Entry<String, Integer> entry : innerNodes.entrySet()) {
	                if (entry.getValue().equals(num)) {
	                    sortedMap.put(entry.getKey(), num);
	                }
	            }
	        }
	        
	
	        int count = 0;
			double count_paths_2 = 0.0d;
			result.put("a0", 0.0d);
			result.put("n1", 0.0d);result.put("n2", 0.0d);
			result.put("n3", 0.0d);result.put("n4", 0.0d);
			result.put("n5", 0.0d);result.put("n6", 0.0d);
			result.put("n7", 0.0d);result.put("n8", 0.0d);
			result.put("n9", 0.0d);result.put("n10", 0.0d);
			for(String s:sortedMap.keySet()) {
				NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
//				NESemRelWithWikidataAndNormalization nesr = new NESemRelWithWikidataAndNormalization(this.getKb(), this.getWikidata_server());
				double temp1 = nesr.adjacentNodesRelatedness(n1, NodeFactory.createURI(s));
				double temp2 = nesr.adjacentNodesRelatedness(n2, NodeFactory.createURI(s));
				
				
				count_paths_2 = count_paths_2+sortedMap.get(s);
				double temp = (temp1 + temp2)/2;
	            if(count < 1)
	            	result.put("n1", temp);
	            if(count < 2)
	            	result.put("n2", result.get("n2") + temp);
	            if(count < 3)
	            	result.put("n3", result.get("n3") + temp);
	            if(count < 4)
	            	result.put("n4", result.get("n4") + temp);
	            if(count < 5)
	            	result.put("n5", result.get("n5") + temp);
	            if(count < 6)
	            	result.put("n6", result.get("n6") + temp);
	            if(count < 7)
	            	result.put("n7", result.get("n7") + temp);
	            if(count < 8)
	            	result.put("n8", result.get("n8") + temp);
	            if(count < 9)
	            	result.put("n9", result.get("n9") + temp);
	            if(count < 10)
	            	result.put("n10", result.get("n10") + temp);
	            count++;
	
	            if(count == 10) break;
	        }
	                
			
			NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
			result.put("a0",nesr.adjacentNodesRelatedness(n1, n2));
			
	//		result.add((double)(kb.paths(n1, n2, 1, Constants.UNDIRECTED_PATH, true).size()));
	//		result.add(count_paths_2);
		}
		return result;
	}
	
}
