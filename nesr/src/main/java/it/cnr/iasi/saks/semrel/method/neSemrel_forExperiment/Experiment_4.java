package it.cnr.iasi.saks.semrel.method.neSemrel_forExperiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;
import it.cnr.iasi.saks.semrel.Utils;

public class Experiment_4 {
	String f0 = "KORE/1_KORE_modified_ITCompanies_updated";
	String f1 = "KORE/2_KORE_modified_Hollywood_updated";
	String f2 = "KORE/3_KORE_modified_VideoGames_updated";
	String f3 = "KORE/4_KORE_modified_TelevisionSeries_updated";
	String f4 = "KORE/5_KORE_modified_ChuckNorris_updated";	
	
	public void run() {		
		String graph = Constants.SPARQL_DBPEDIA_GRAPH;
//		String graph = Constants.SPARQL_YAGO_GRAPH;//yago-knowledge.org/resource/
		
		String ns = graph+"/";

		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);
		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
		
		BufferedReader br = null;

		LocalDate localDate = LocalDate.now();
		String date = ""+localDate.getYear()+localDate.getMonthValue()+localDate.getDayOfMonth();
		
		int maxLength = 2;
				
		String in_file = f4;
    	String in = "/semrel/datasets/"+in_file+".txt";
//    	System.out.println(in);
    	String out = "target/test-classes/semrel/neSemRel/results/"+in_file+"_"+date+"_"+maxLength+"_original.csv";

    	int row = 0;
		int startFrom = 0;
		
    	if(startFrom == 0) {
    		Utils.createFile(out);
    		Utils.writeOnFile(out, "n1;n2;;alpha=0;n=1;n=2;n=3;n=4;n=5;n=6;n=7;n=8;n=9;n=10;\n", true);
    	}
    	    	
    	try {   
    		System.out.println("in="+in);
    		System.out.println("kb="+kb.getClass().getResource(in));
        	br = new BufferedReader(new FileReader(kb.getClass().getResource(in).getFile()));
            String line;
            while ((line = br.readLine()) != null) {
            	if(line.equalsIgnoreCase("")) {
            		Utils.writeOnFile(out, "\n", true);
            		
            	}
            	else {
	            	StringTokenizer st = new StringTokenizer(line, ";");
	            	String n1_label = st.nextToken();
	            	String n2_label = st.nextToken();
	
	//	            	double hj = Double.valueOf(st.nextToken());
	            	Node n1, n2;
	            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+n1_label);
	            	n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+n2_label);
//	            	n1 = NodeFactory.createURI(Constants.YAGO_RES_NS+n1_label);
//	            	n2 = NodeFactory.createURI(Constants.YAGO_RES_NS+n2_label);
	            	
	        		if(row >= startFrom) {
	        			NESemRel nesr = new NESemRel(kb, Constants.WIKIDATA_SPARQL_SERVER);
	        			Map<String, Double> result = nesr.semrel_forExperiment(n1, n2);
	        			        			
	        			String content = n1.getURI().toString()+";"+n2.getURI().toString()+";"+";"+
	        					result.get("a0") + ";" +
	        					result.get("n1") + ";" +
	        					result.get("n2") + ";" +
	        					result.get("n3") + ";" +
	        					result.get("n4") + ";" +
	        					result.get("n5") + ";" +
	        					result.get("n6") + ";" +
	        					result.get("n7") + ";" +
	        					result.get("n8") + ";" +
	        					result.get("n9") + ";" +
	        					result.get("n10");
		        			
	        			Utils.writeOnFile(out, content+"\n", true);
	        		}
            	}
            	row++;
            	System.out.println("row="+row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}

}
