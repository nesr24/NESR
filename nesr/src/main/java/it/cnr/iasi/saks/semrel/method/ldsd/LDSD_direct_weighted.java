package it.cnr.iasi.saks.semrel.method.ldsd;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

import java.util.Vector;

public class LDSD_direct_weighted extends LDSD_direct {
	
	public LDSD_direct_weighted(KnowledgeBase kb) {
		super(kb);
	}

	/**
	 * Searches for all the p1 such that <n1, p1, n2> exists
	 * @param n1
	 * @param n2
	 * @param direction
	 * @return
	 */
	public Vector<Node> directLinks(Node n1, Node n2) {
		Vector<Node> result = new Vector<Node>();

		PathPattern pattern = new PathPattern(null);
		Node p1 = NodeFactory.createVariable("p1");
		Triple t = new Triple(n1, p1, n2);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("p1");

		result = this.kb.nodesByPattern(pattern);
		return result;
	}

	
	/**
	 * This method implements the LDSD_dw function in the Passant's article.
	 */
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;

		double denom = 1.0d + denom(n1, n2);
		result = 1.0d/denom;
		
		return result;
	}

	
	public double denom(Node n1, Node n2) {
		double result = 0.0d;
		// for all the p1 such that <n1, p1, n2> exists	
		for(Node p:this.directLinks(n1, n2)) {
			double d = this.Cd_np(n1, p);
			result = result + 1/(1+Math.log(d));
		}
		
		// for all the p1 such that <n2, p1, n1> exists		
		for(Node p:this.directLinks(n2, n1)) {
			double d = this.Cd_np(n2, p);
			result = result + 1/(1+Math.log(d));
		}		
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

		queryString = "SELECT ?p (COUNT (DISTINCT ?d) AS ?directNodes) FROM $$$graph$$$ WHERE {\n" + 
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
