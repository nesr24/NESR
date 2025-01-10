package it.cnr.iasi.saks.semrel.method.neSemrel;

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
import it.cnr.iasi.saks.semrel.method.neSemrel_forExperiment.NESemRelWithWikidata;
import it.cnr.iasi.saks.semrel.method.neSemrel_forExperiment.NESemRelWithWikidataAndNormalization;

public class NESemRel extends NESemRel_Abstract {

	private RDFGraph kb = null; 
	private String wikidata_server = "";
	private double alpha = 0.0d;
	private int n = 1;
	
		
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
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}
	
	public NESemRel(RDFGraph kb, String wikidata_server, double alpha, int n) {
		super();
		this.kb = kb;
		this.wikidata_server = wikidata_server;
		this.alpha = alpha;
		this.n = n;
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
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		

		Vector<Path> paths = kb.paths(n1, n2, 2, Constants.UNDIRECTED_PATH, true);
//		System.out.println(n1.getURI().toString()+" --- "+n2.getURI().toString()+" --- paths="+paths.size());
		
		Map<String, Integer> innerNodes = new HashMap<String, Integer>();

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
		if(sortedMap.size() > 0) {
			for(String s:sortedMap.keySet()) {
				NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
//				NESemRelWithWikidataAndNormalization nesr = new NESemRelWithWikidataAndNormalization(this.getKb(), this.getWikidata_server());
				double temp1 = nesr.adjacentNodesRelatedness(n1, NodeFactory.createURI(s));
				double temp2 = nesr.adjacentNodesRelatedness(n2, NodeFactory.createURI(s));
				
				count_paths_2 = count_paths_2+sortedMap.get(s);
				result = result + (temp1 + temp2)/2;
	            count++;
	
	            if(count == this.getN()) break;
	        }
			
			result = result * this.getAlpha() / (double)count;
		}
		
		NESemRelWithWikidata nesr = new NESemRelWithWikidata(this.getKb(), this.getWikidata_server());
		result = result + nesr.adjacentNodesRelatedness(n1, n2);
		
//		result.add((double)(kb.paths(n1, n2, 1, Constants.UNDIRECTED_PATH, true).size()));
//		result.add(count_paths_2);
		return result;
	}
	
}
