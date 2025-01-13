package it.cnr.iasi.saks.semrel.method.neSemrel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.RDFGraph;
import it.cnr.iasi.saks.wikidata.Wikidata;

public abstract class NESemRel_Abstract {
	
	RDFGraph kb = null;
	String wikidataServer = "";
	
	public RDFGraph getKb() {
		return kb;
	}

	public void setKb(RDFGraph kb) {
		this.kb = kb;
	}
	
	public String getWikidataServer() {
		return wikidataServer;
	}

	public void setWikidataServer(String wikidataServer) {
		this.wikidataServer = wikidataServer;
	}
	
	public Vector<Vector<Node>> cleanPredicatesWithObject_uri(Vector<Vector<Node>> p_o, Node n) {
		Vector<Vector<Node>> result = new Vector<Vector<Node>>();
		for(Vector<Node> r:p_o) {
			if(r.get(1).getURI().toString().equalsIgnoreCase(n.getURI().toString())) {
				result.add(r);
			}
		}
		return result;
	}
	
	public Vector<Vector<Node>> cleanPredicatesWithObject_literal(Vector<Vector<Node>> p_o, Vector<String> labels) {
		Vector<Vector<Node>> result = new Vector<Vector<Node>>();
//		System.out.println("labels ="+labels);
		if(labels.size()>0)
			for(Vector<Node> r:p_o) {
				for(String s:labels) {
					if(r.get(1).getLiteral().toString().contains(s)) {
						result.add(r);	
					}
				}
			}	
		return result;
	}
	
	public Vector<String> nodeLabels(Node n) {
		Vector<String> result = new Vector<String>();
		Wikidata wd = new Wikidata(this.getWikidataServer(), Constants.WIKIDATA_PARAM);	
		if(n!=null) {
//			System.out.println("wikidata node = "+n);
			result = wd.altLabels(n.getURI().toString());
			String label = wd.label(n.getURI().toString());
			result.add(label);
		}
		return result;
	}
	
	public Node wikidataId(Vector<Vector<Node>> p_o) {
		Node result = null;
		for(Vector<Node> r:p_o) {
			if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.OWL_SAMEAS) &&
					r.get(1).getURI().toString().startsWith(Constants.WIKIDATA_NS))
				result = r.get(1);
		}
		return result;
	}
	
	public int countSubStringOccurrences(String s, String sub) {
		int result = 0;
		s = s.replace("_", "%%%");
		s = s.replace("(", "%%%");
		s = s.replace(")", "%%%");
		s = s.replace(" ", "%%%");
		s = s.replace(",", "%%%");
		s = s.replace(".", "%%%");
		s = s.replace(";", "%%%");
		s = s.replace(":", "%%%");
		s = "%%%"+s+"%%%";
		sub = sub.replace("_", "%%%");
		sub = sub.replace("(", "%%%");
		sub = sub.replace(")", "%%%");
		sub = sub.replace(" ", "%%%");
		sub = sub.replace(",", "%%%");
		sub = sub.replace(".", "%%%");
		sub = sub.replace(";", "%%%");
		sub = sub.replace(":", "%%%");
		sub = "%%%"+sub+"%%%";
//		System.out.println("s="+s);
//		System.out.println("sub="+sub);
		result = s.split(sub, -1).length;
		return (result-1);
	}
	
	public int _countSubStringOccurrences(String s, String sub) {
		int result = 0;
		result = s.split(sub, -1).length;
		return (result-1);
	}
 
	public int _countSubStringsOccurrences(String s, Vector<String> subs) {
		int result = 0;
		for(String sub:subs) 
			result = result + this.countSubStringOccurrences(s, sub);	
		return result;
	}
	
	public int countSubStringsOccurrences(String s, Vector<String> subs) {
		int result = 0;
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for(String sub:subs) {
			if(sub.length()>0)
				counts.put(sub, this.countSubStringOccurrences(s, sub));
		}
		
		int toRemove = 0;
		for(String k:counts.keySet()) {
			result = result + counts.get(k);
			if(counts.get(k)>0) {
				for(String sub:subs) {
					if(sub.length()>0)	
						if(!(k.toString().equalsIgnoreCase(sub))&&k.toString().contains(s))
							toRemove = toRemove + counts.get(k);
				}
			}
		}		
		result = result - toRemove;
		return result;
	}
	
	public boolean containSubStrings(String s, Vector<String> subs) {
		s = s.replace("_", "***");
		s = s.replace("(", "***");
		s = s.replace(")", "***");
		s = s.replace(" ", "***");
		s = s.replace(",", "***");
		s = s.replace(".", "***");
		s = s.replace(";", "***");
		s = s.replace(":", "***");
		s = "***"+s+"***";
		for(String sub:subs)
			if(sub.length()>0) {
				sub = sub.replace("_", "***");
				sub = sub.replace("(", "***");
				sub = sub.replace(")", "***");
				sub = sub.replace(" ", "***");
				sub = sub.replace(",", "***");
				sub = sub.replace(".", "***");
				sub = sub.replace(";", "***");
				sub = sub.replace(":", "***");
				sub = "***"+sub+"***";
				if(s.contains(sub))
					return true;
			}
		return false;
	}
	
	public String family_name(Node n) {
		String result = "";
		if(n!=null) {
			Wikidata wd = new Wikidata(this.getWikidataServer(), Constants.WIKIDATA_PARAM);	
			result = wd.family_name(n.getURI().toString());
			if(result == null) {
				StringTokenizer st = new StringTokenizer(n.getURI().toString(), " ");
				while(st.hasMoreTokens())
					result = st.nextToken();
			}
		}
		return result;
	}
}
	
