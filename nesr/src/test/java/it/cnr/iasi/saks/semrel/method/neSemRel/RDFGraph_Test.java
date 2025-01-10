/*
 * 	 This file is part of SemRel, originally promoted and
 *	 developed at CNR-IASI. For more information visit:
 *	 http://saks.iasi.cnr.it/tools/semrel
 *	     
 *	 This is free software: you can redistribute it and/or modify
 *	 it under the terms of the GNU General Public License as 
 *	 published by the Free Software Foundation, either version 3 of the 
 *	 License, or (at your option) any later version.
 *	 
 *	 This software is distributed in the hope that it will be useful,
 *	 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	 GNU General Public License for more details.
 * 
 *	 You should have received a copy of the GNU General Public License
 *	 along with this source.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.cnr.iasi.saks.semrel.method.neSemRel;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.RDFGraph_Endpoint;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class RDFGraph_Test {

//	@Ignore
	@Test
	public void nodesByPattern() {
		System.out.println("nodesByPattern");
		
		String graph = "http://dbpedia.org";
		String ns = graph+"/";

		String distinct = Constants.SPARQL_NOT_DISTINCT;
		
		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);

		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
		System.out.println("KnowledgeResourceRef="+kb.getKnowledgeResourceRef());
		
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Silicon_Valley");
		Node o = NodeFactory.createVariable("o1");
		// Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node p = NodeFactory.createURI(Constants.RDFS_LABEL);
		Triple t = new Triple(s,p,o);
		PathPattern pattern = new PathPattern(null);
		pattern.getTriples().add(t);
		pattern.setDistinct(distinct);
		pattern.getVarsToSelect().add("o1");
		Filter f = new Filter();
		f.setValue("FILTER (lang(?o1) = \"en\")");
		pattern.getFilters().add(f);
		Vector<Node> nodes = kb.nodesByPattern(pattern);
//		for(Node n:nodes) {
//			System.out.println(n);
//		}

		if(nodes.size()>0) {
			String label = nodes.get(0).getLiteral().toString();
			int index = label.indexOf('@');
			label = label.substring(0, index);
			System.out.println(label);
		}

		
		Vector<Node> expectedResult = new Vector<Node>();
		//NOT DISTINCT
		if(distinct.equalsIgnoreCase(Constants.SPARQL_NOT_DISTINCT)) {
			expectedResult.add(NodeFactory.createURI(ns+"a2"));
			expectedResult.add(NodeFactory.createURI(ns+"a2"));
			expectedResult.add(NodeFactory.createURI(ns+"a4"));
			expectedResult.add(NodeFactory.createURI(ns+"a5"));
		}
		//DISTINCT
		else if(distinct.equalsIgnoreCase(Constants.SPARQL_DISTINCT)) {
			expectedResult.add(NodeFactory.createURI(ns+"a2"));
			expectedResult.add(NodeFactory.createURI(ns+"a4"));
			expectedResult.add(NodeFactory.createURI(ns+"a5"));
		}
				
		if(nodes.size()==expectedResult.size()) {
			nodes.removeAll(expectedResult);
			System.out.println(nodes);
			System.out.println(nodes.size());
			if(nodes.size()==0)
				Assert.assertTrue(true);
			else Assert.assertTrue(false);
		}
		else
			Assert.assertTrue(true);
	}
	
	@Ignore
	@Test
	public void literalsByPattern() {
		System.out.println("literalsByPattern");
		
		Vector<String> result = new Vector<String>();
		
		String graph = "http://dbpedia.org";
		String ns = graph+"/";

		String distinct = Constants.SPARQL_NOT_DISTINCT;
		
		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);

		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
		
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		Node o = NodeFactory.createVariable("o1");
		// Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node p = NodeFactory.createVariable("p1");
		Triple t = new Triple(s,p,o);
		PathPattern pattern = new PathPattern(null);
		pattern.getTriples().add(t);
		pattern.setDistinct(distinct);
		pattern.getVarsToSelect().add("o1");
		Filter f = new Filter();
		f.setValue("FILTER (lang(?o1) = \"en\")");
		pattern.getFilters().add(f);
		Vector<Node> nodes = kb.nodesByPattern(pattern);

		for(Node n:nodes) {
			String label = n.getLiteral().toString();
			int index = label.indexOf('@');
			label = label.substring(0, index);
			result.add(label);
			System.out.println(label);
		}
		Assert.assertTrue(true);
	}
	
	@Ignore
	@Test
	public void predicatesAndNodesByPattern() {
		System.out.println("predicatesAndNodesByPattern");
		
		Vector<String> result = new Vector<String>();
		
		String graph = "http://dbpedia.org";
		String ns = graph+"/";

		String distinct = Constants.SPARQL_NOT_DISTINCT;
		
		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);

		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
		
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		Node o = NodeFactory.createVariable("o1");
		// Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node p = NodeFactory.createVariable("p1");
		Triple t = new Triple(s,p,o);
		PathPattern pattern = new PathPattern(null);
		pattern.getTriples().add(t);
		pattern.setDistinct(distinct);
		pattern.getVarsToSelect().add("p1");
		pattern.getVarsToSelect().add("o1");
		Vector<Vector<Node>> p_n = kb.predicatesAndNodesByPattern(pattern);

		Vector<Vector<Node>> objects_uri = new Vector<Vector<Node>>();
		Vector<Vector<Node>> objects_literal = new Vector<Vector<Node>>();
		
		for(Vector<Node> r:p_n) {
			System.out.println(r);
			if(r.get(1).isLiteral()) {
				if(r.get(1).getLiteral().toString().endsWith("@en"))
					objects_literal.add(r);
			}
			else
				objects_uri.add(r);
		}

		
		Assert.assertTrue(true);	
		
	}
	
	@Ignore
	@Test
	public void predicatesAndNodesBySubject() {
		System.out.println("predicatesAndNodesBySubject");
		
		Vector<String> result = new Vector<String>();
		
		String graph = "http://dbpedia.org";
		String ns = graph+"/";

		String distinct = Constants.SPARQL_NOT_DISTINCT;
		
		RDFGraph_Endpoint kb = RDFGraph_Endpoint.getInstance(graph, null, null);

		kb.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT_PUBLIC);
		
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		Vector<Vector<Node>> p_n = kb.predicatesAndNodesBySubject(s);

		Vector<Vector<Node>> objects_uri = new Vector<Vector<Node>>();
		Vector<Vector<Node>> objects_literal = new Vector<Vector<Node>>();
		
		for(Vector<Node> r:p_n) {
			System.out.println(r);
			if(r.get(1).isLiteral()) {
				if(r.get(1).getLiteral().toString().endsWith("@en"))
					objects_literal.add(r);
			}
			else
				objects_uri.add(r);
		}

		
		Assert.assertTrue(true);	
		
	}	
}
