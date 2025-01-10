package it.cnr.iasi.saks.semrel.method.neSemRel;

import java.io.BufferedReader;
import java.time.LocalDate;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;
import it.cnr.iasi.saks.semrel.method.neSemrel.NESemRel;

public class NESemRel_Test {
//	@Ignore
	@Test
	public void tripleSemRel() {
		String graph = Constants.SPARQL_DBPEDIA_GRAPH;
		
		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);
		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
				
		double alpha = 0.2;
		int n = 7;
				
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");

		NESemRel nesr = new NESemRel(kb, Constants.WIKIDATA_SPARQL_SERVER, alpha, n);
		
		double result = nesr.semrel(n1, n2);
		
		System.out.println("semrel("+n1.getURI().toString()+", "+n2.getURI().toString()+") = "+result);
	}

}
