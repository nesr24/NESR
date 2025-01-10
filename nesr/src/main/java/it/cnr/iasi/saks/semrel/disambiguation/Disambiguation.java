package it.cnr.iasi.saks.semrel.disambiguation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods.utilities.SetUtilities;

public class Disambiguation {
	
	public void disambiguate(String filename, String graph) {
		BufferedReader br = null;

    	String in = "/semrel/datasets/"+filename+".csv";
    	String out = "target/test-classes/semrel/datasets/"+filename+"_disambiguated.csv";
    	
    	System.out.println(in);
		KnowledgeBase kb = RDFGraph_Endpoint.getInstance(graph, null, null);

		Utils.createFile(out);
		
		int row = 1;
		
    	try {     
        	br = new BufferedReader(new FileReader(kb.getClass().getResource(in).getFile()));
        	
            String line;
            while ((line = br.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, ";");
            	String term1 = st.nextToken();
            	String term2 = st.nextToken();

            	String mod_1 = "";
            	String mod_2 = "";
            	
//            	String mod_1 = st.nextToken();
//            	String mod_2 = st.nextToken();
            	
            	double hj = Double.valueOf(st.nextToken());
            	
            	String term1_changed = term1.substring(0, 1).toUpperCase() + term1.substring(1);
            	term1_changed = term1_changed.replace(" ", "_");
            	
            	String term2_changed = term2.substring(0, 1).toUpperCase() + term2.substring(1);
            	term2_changed = term2_changed.replace(" ", "_");

            	
            	Node n1, n2;
            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+term1_changed);
            	n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+term2_changed);
            	
            	System.out.println(row+" --- "+term1+" --- "+term2+" --- "+n1.getURI()+" --- "+n2.getURI());
            	
           		n1 = getRedirect(kb, n1);
           		n2 = getRedirect(kb, n2);
            	
           		Vector<Node> nodes = getDisambiguation(kb, n1, n2);

           		System.out.println(row+" --- "+term1+" --- "+term2+" --- "+nodes.get(0).getURI()+" --- "+nodes.get(1).getURI());
           		String data = term1+";"+term2+";"+mod_1+";"+mod_2+";"+nodes.get(0).getURI().toString().replace("http://dbpedia.org/resource/", "")+";"+nodes.get(1).getURI().toString().replace("http://dbpedia.org/resource/", "")+";"+hj+"\n";
        		Utils.writeOnFile(out, data, true);
            	
            	row++;
            }
    	}
    	catch(Exception ex){ex.printStackTrace();}
	}
	
	public Node getRedirect(KnowledgeBase kb, Node n) {
		Node result = n;

		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_WIKIPAGEREDIRECTS);
		Node nx = NodeFactory.createVariable("nx");
		Triple t = new Triple(n, p, nx);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("nx");
		Vector<Node> nodes = kb.nodesByPattern(pattern);
		
		if(nodes.size()>0)
			result = nodes.elementAt(0);
		return result;
	}
	
	public Vector<Node> getDisambiguation(KnowledgeBase kb, Node n1, Node n2) {
		Vector<Node> result = new Vector<Node>();
		result.add(n1);
		result.add(n2);
			
		PathPattern pattern_1 = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_WIKIPAGEDISAMBIGUATES);
		Node nx = NodeFactory.createVariable("nx");
		Triple t = new Triple(n1, p, nx);
		pattern_1.getTriples().add(t);
		pattern_1.getVarsToSelect().add("nx");
		Vector<Node> nodes_1 = kb.nodesByPattern(pattern_1);

		Set<Node> nodes_1_temp = new HashSet<Node>();
		nodes_1_temp.addAll(kb.nodesByPattern(pattern_1));
		
		System.out.print(nodes_1.size() + " --- ");
		
		for(Node nc:nodes_1_temp) {
			if(nc.getURI().toString().endsWith("_(disambiguation)"))
				nodes_1.remove(nc);
		}
		System.out.print(nodes_1.size() + " --- ");

		
		if(nodes_1.size() == 0)
			nodes_1.add(n1);

		System.out.println(nodes_1.size());

		
		PathPattern pattern_2 = new PathPattern(null);
		t = new Triple(n2, p, nx);
		pattern_2.getTriples().add(t);
		pattern_2.getVarsToSelect().add("nx");
		Vector<Node> nodes_2 = kb.nodesByPattern(pattern_2);

		System.out.print(nodes_2.size() + " --- ");

		
		Set<Node> nodes_2_temp = new HashSet<Node>();
		nodes_2_temp.addAll(kb.nodesByPattern(pattern_2));
		
		for(Node nc:nodes_2_temp) {
			if(nc.getURI().toString().endsWith("_(disambiguation)"))
				nodes_2.remove(nc);
		}
		
		System.out.print(nodes_2.size() + " --- ");

		
		if(nodes_2.size() == 0)
			nodes_2.add(n2);
		
		System.out.println(nodes_2.size());

		int maxCount = 0;
		for(Node n_1:nodes_1) {
			Set<Node> set_1 = new HashSet<Node>();
			PathPattern pattern_in = new PathPattern(null);
			Node px = NodeFactory.createVariable("px");
			Triple t_in = new Triple(nx, px, n_1);
			pattern_in.getTriples().add(t_in);
			pattern_in.getVarsToSelect().add("nx");
			
			PathPattern pattern_out = new PathPattern(null);
			Triple t_out = new Triple(n_1, px, nx);
			pattern_out.getTriples().add(t_out);
			pattern_out.getVarsToSelect().add("nx");
			
			
			set_1.addAll(kb.nodesByPattern(pattern_in));
			set_1.addAll(kb.nodesByPattern(pattern_out));

			for(Node n_2:nodes_2) {
				Set<Node> set_2 = new HashSet<Node>();
				pattern_in = new PathPattern(null);
				t_in = new Triple(nx, px, n_2);
				pattern_in.getTriples().add(t_in);
				pattern_in.getVarsToSelect().add("nx");
				
				pattern_out = new PathPattern(null);
				t_out = new Triple(n_2, px, nx);
				pattern_out.getTriples().add(t_out);
				pattern_out.getVarsToSelect().add("nx");
				
				set_2.addAll(kb.nodesByPattern(pattern_in));
				set_2.addAll(kb.nodesByPattern(pattern_out));
				
				Set<Node> intersection = SetUtilities.intersection(set_1, set_2);
				if(intersection.size()>maxCount) {
					maxCount = intersection.size();
					result.insertElementAt(n_1, 0);
					result.insertElementAt(n_2, 1);
				}
			}
		}
		System.out.println(maxCount);		
//		System.out.println(result.get(0) + " --- "+ result.get(1));
		
		return result;
	}
	
}
