package it.cnr.iasi.saks.semrel.method.ldsd;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class LDSD_indirect_weighted extends LDSD_indirect {

	public LDSD_indirect_weighted(KnowledgeBase kb) {
		super(kb);
	}
		
	//If @direction equals to {@link Constants.METHOD_LDSD_OUTGOING} it search for the predicates p1 such that it exists nx and <n1, p1, nx> . <n2, p1, nx>
	//If @direction equals to {@link Constants.METHOD_LDSD_INCOMING} it search for the predicates p1 such that it exists nx and <nx, p1, n1> . <nx, p1, n1>
	public Vector<Node> indirectLinks(Node n1, Node n2, String direction) {
		Vector<Node> result = new Vector<Node>();
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Node p1 = NodeFactory.createVariable("p1");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p1, nx);
			t2 = new Triple(n2, p1, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx, p1, n1);
			t2 = new Triple(nx, p1, n2);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("p1");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);

		result = this.kb.nodesByPattern(pattern);
/*		
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING))
			System.out.println("Cio(l1, "+n1.getURI()+", "+n2.getURI()+")="+result);
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING))
			System.out.println("Cii(l1, "+n1.getURI()+", "+n2.getURI()+")="+result);
*/
		return result;
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		result = 1.0d / (1.0d + denom(n1, n2));
		
		return result;
	}

	public double denom(Node n1, Node n2) {
		double result = 0d;
		// for all the indirect incoming links
		for(Node p:indirectLinks(n1, n2, Constants.METHOD_LDSD_INCOMING))
			result = result + 1/(1+Math.log(Cii_np(n1, p)));
		
		// for all the indirect outgoing links
		for(Node p:indirectLinks(n1, n2, Constants.METHOD_LDSD_OUTGOING))
			result = result + 1/(1+Math.log(Cio_np(n1, p)));
		
		return result;
	}
	
	
	public double ldsd_(Node n1, Node n2) {
		double result = 0.0d;
		result = 1.0d / (1.0d+denom(n1, n2));
		return result;
	}

	public double denom_(Node n1, Node n2) {
		double result = 0.0d;
		String queryString = "SELECT ?p (COUNT (DISTINCT ?d) AS ?directNodes) FROM $$$graph$$$ WHERE {\n" + 
				"<"+n1+"> ?p <"+n2+"> .\n" + 
				"<"+n1+"> ?p ?d \n" + 
				"} GROUP BY ?p";
		Vector<QuerySolution> v1 = this.getKb().execQuery(queryString);
		for(int i=0; i<v1.size(); i++) {
			result = result + 1.0d / v1.elementAt(i).get("?directNodes").asLiteral().getDouble();
		}

		queryString = "SELECT ?p (COUNT (DISTINCT ?d) AS ?directNodes) FROM <http://dbpedia.org> WHERE {\n" + 
				"<"+n2+"> ?p <"+n1+"> .\n" + 
				"<"+n2+"> ?p ?d \n" + 
				"} GROUP BY ?p";
		Vector<QuerySolution> v2 = this.getKb().execQuery(queryString);
		for(int i=0; i<v2.size(); i++) {
			result = result + 1.0d / v1.elementAt(i).get("?directNodes").asLiteral().getDouble();
		}

		return result;
	}
}
