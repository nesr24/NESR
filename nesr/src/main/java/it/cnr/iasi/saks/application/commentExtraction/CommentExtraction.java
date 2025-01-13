package it.cnr.iasi.saks.application.commentExtraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;
import it.cnr.iasi.saks.semrel.Utils;

public class CommentExtraction {

	public void extract() {
		String f0 = "B/B0_modified_disambiguated";	
		String f1 = "B/B1_modified_disambiguated";
		String f2 = "RG-65/RG-65_modified_disambiguated";
		String f3 = "MC-30/MC-30_modified_disambiguated";
		String f4 = "MG30/MG30_modified_disambiguated";
		String f5 = "R122/R122_MODIFIED_disambiguated";
		String f6 = "KORE/1_KORE_modified_ITCompanies_disambiguated";
		String f7 = "KORE/2_KORE_modified_Hollywood_disambiguated";
		String f8 = "KORE/3_KORE_modified_VideoGames_disambiguated";
		String f9 = "KORE/4_KORE_modified_TelevisionSeries_disambiguated";
		String f10 = "KORE/5_KORE_modified_ChuckNorris_disambiguated";	
		String f11 = "WS252-Rel/wordsim_relatedness_goldstandard_MODIFIED_disambiguated";
		String f12 = "MTURK-287/MTURK-287_modified_disambiguated";
		String f13 = "Atlasify240/Atlasify240_2021532_modified_disambiguated";	

		
		Vector<String> in_file = new Vector<String>();
		
		in_file.add(f0);
		in_file.add(f1);
		in_file.add(f2);
		in_file.add(f3);
		in_file.add(f4);
		in_file.add(f5);
		in_file.add(f6);
		in_file.add(f7);		
		in_file.add(f8);		
		in_file.add(f9);
		in_file.add(f10);		
		in_file.add(f11);
		in_file.add(f12);
		in_file.add(f13);
		
		String graph = Constants.SPARQL_DBPEDIA_GRAPH;
		KnowledgeBase kb = RDFGraph_Endpoint.getInstance(graph, null, null);
		
		BufferedReader br = null;
		

		
		for(int i=0; i<14; i++) {
	    	String in = "/semrel/datasets/"+in_file.elementAt(i)+".csv";
	    	String out_disambiguated = "target/test-classes/semrel/datasets/"+in_file.elementAt(i)+"_comments.csv";
	    	System.out.println(out_disambiguated);
//	    	Vector<String> processed = new Vector<String>();
	    	
	    	System.out.println("START DATASET n "+i);
	    	
	    	Utils.writeOnFile(out_disambiguated, "Term \t URI \t Definition"+"\n", true);		
	    	
        	try {     
	        	br = new BufferedReader(new FileReader(kb.getClass().getResource(in).getFile()));
	        	
	            String line;
	            while ((line = br.readLine()) != null) {
	            	StringTokenizer st = new StringTokenizer(line, ";");
	            	String word1_orig = st.nextToken();
	            	String word2_orig = st.nextToken();
	            	st.nextToken(); st.nextToken();
	            	String word1_disamb = st.nextToken();
//	            	if(!(processed.contains(word1_disamb))) 
	            	{
	            		Node n = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+word1_disamb);
	            		String comment = extractComment(kb, n);
	        	        Utils.writeOnFile(out_disambiguated, word1_orig+"\t dbr:"+word1_disamb+"\t"+comment+"\n", true);
//	        	        processed.add(word1_disamb);
	            	}
	            	String word2_disamb = st.nextToken();
//	            	if(!(processed.contains(word2_disamb))) 
	            	{
	            		Node n = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+word2_disamb);
	            		String comment = extractComment(kb, n);
	        	        Utils.writeOnFile(out_disambiguated, word2_orig+"\t dbr:"+word2_disamb+"\t"+comment+"\n", true);
//	        	        processed.add(word2_disamb);
	            	}

	            }
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        	}
        	

		}
	}
	
	public String extractComment(KnowledgeBase kb, Node n) {
		String result = "";
		
		PathPattern pattern = new PathPattern(null);
		Node o = NodeFactory.createVariable("comment");
		Node p = NodeFactory.createURI(Constants.RDFS_COMMENT);
		Triple t = new Triple(n, p, o);
		pattern.getVarsToSelect().add("comment");
		pattern.getTriples().add(t);
		
		Filter f = new Filter();
		f.setValue("FILTER(LANG(?comment) = \"\" || LANGMATCHES(LANG(?comment), \"en\"))");
		
		pattern.getFilters().add(f);
		
		Vector<Node> r = kb.nodesByPattern(pattern);
		if(r.size()>0) {
			result = r.get(0).getLiteral().toString().replace("\n", " ");
			result = result.replace(";", ".");
		}
		return result;
	}
	
}
