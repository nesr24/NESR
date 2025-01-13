package it.cnr.iasi.saks.other;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;

public class Other {
	
	public void collectEntityComment(String file_in, String file_out) {
		String graph = Constants.SPARQL_DBPEDIA_GRAPH;
		KnowledgeBase kb = RDFGraph_Endpoint.getInstance(graph, null, null);
		
		BufferedReader br = null;
		Set<String> termsToProcess = new HashSet<String>();
		
    	try {     
        	br = new BufferedReader(new FileReader(kb.getClass().getResource(file_in).getFile()));
        	
            String line;
            while ((line = br.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, ";");
            	st.nextToken(); st.nextToken();
            	st.nextToken(); st.nextToken();
            	termsToProcess.add(st.nextToken());
            	termsToProcess.add(st.nextToken());
            }
            
            for(String s:termsToProcess) {
            	String s1 = s.substring(0, 1).toUpperCase() + s.substring(1);            	
            	s1 = s1.replace(" ", "_");	
            	Node n;
            	n = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+s1);
            	String comment = entityComment(n);
            }
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
	}
	
	public String entityComment(Node n) {
		String result = "";
		
		PathPattern p = new PathPattern(null);
		
		return result;
	}
}
