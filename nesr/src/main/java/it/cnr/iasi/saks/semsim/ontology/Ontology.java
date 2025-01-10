package it.cnr.iasi.saks.semsim.ontology;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy_OntModel;

public abstract class Ontology {
	
	WeightedTaxonomy_OntModel wt = null;
	
	public WeightedTaxonomy_OntModel getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy_OntModel wt) {
		this.wt = wt;
	}

	public Set<Node> allObjectProperties() {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
		Triple t = new Triple(s, p, o); 
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> temp = this.wt.nodesByPattern(pattern);
		result.addAll(temp); 
		
		return result;
	}
	
	public Node rangeOf(Node n) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_RANGE);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.wt.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> temp = this.wt.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}

	public Node domainOf(Node n) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_DOMAIN);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.wt.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> temp = this.wt.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}
	
	public Set<Node> equivalentClass(Node n) {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.OWL_EQUIVALENT_CLASS);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.wt.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> temp = this.wt.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result.addAll(temp);
		
		return result;
	}	
}
