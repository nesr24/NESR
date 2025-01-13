package it.cnr.iasi.saks.semrel.ftaf;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.likelihood.TopDown;
import it.cnr.iasi.saks.semsim.likelihood.IntrinsicLikelihood;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy_OntModel;

public class FTAF {
	
	public double dbr_dbo_dbr__ft(KnowledgeBase kb, Node s, Node p, Node o) {
		double result = 0.0d;
		
		Node type_s = mostSpecificTypes(kb, typesOf(kb, s)).iterator().next();
		Node type_o = mostSpecificTypes(kb, typesOf(kb, o)).iterator().next();
		
		int num_instancesOf_type_s = countClassExtension(kb, type_s);
		int num_instancesOf_type_o = countClassExtension(kb, type_o);

		int countPredicateBetweenClassesInstances = countPredicateBetweenClassesInstances(kb, type_s, type_o, p);

		PathPattern pattern = new PathPattern(null);
		Node x = NodeFactory.createVariable("x");
		Triple t = new Triple(s, p, x);
		pattern.getTriples().add(t);
		int numOf_s_p_x = kb.countPathsByPattern(pattern);
		
		System.out.println("Math.log(numOf_s_p_x)="+Math.log(numOf_s_p_x));
		System.out.println(("(double)countPredicateBetweenClassesInstances)="+((double)countPredicateBetweenClassesInstances)));
		System.out.println("type_s="+type_s.getURI().toString());
		System.out.println("((double)num_instancesOf_type_s)="+((double)num_instancesOf_type_s));
		System.out.println("type_o="+type_o.getURI().toString());
		System.out.println("((double)num_instancesOf_type_o)="+((double)num_instancesOf_type_o));
		
//		result = (1/(1+Math.log(numOf_s_p_x)))*((double)countPredicateBetweenClassesInstances)/((double)num_instancesOf_type_s * num_instancesOf_type_o);
			
		result = Math.pow(Math.E, -numOf_s_p_x)*Math.pow(Math.E, -((double)countPredicateBetweenClassesInstances)/((double)num_instancesOf_type_s * num_instancesOf_type_o));
		
		
		return result;
	}
	
	public double semsim_types(KnowledgeBase kb, Node s, Node o, WeightedTaxonomy_OntModel wt) {
		double result = 0.0d;
		
//		System.out.println(s.getURI().toString());
//		System.out.println(o.getURI().toString());
		
		Set<Node> types_s = typesOf(kb, s);
		Set<Node> types_o = typesOf(kb, o);
		
//		System.out.println(types_s);
//		System.out.println(types_o);
		
		result = typesSimilarity(types_s, types_o, wt);
		
		return result;
	}
	
	public double dbr_dbo_dbr__af(KnowledgeBase kb, Node s, Node p, Node o, WeightedTaxonomy_OntModel wt) {
		double result = 0.0d;
				
		result = semsim_types(kb, s, o, wt);
		
		return result;
	}
	
	public int countClassExtension(KnowledgeBase kb, Node c) {
		int result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("s");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t = new Triple(s, p, c);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("s");
		
		result = kb.countNodesByPattern(pattern);
		
		return result;
	}
	
	
	public int countPredicateBetweenClassesInstances(KnowledgeBase kb, Node c1, Node c2, Node p) {
		int result = 0;

		PathPattern pattern = new PathPattern(null);
		Node s1 = NodeFactory.createVariable("s1");
		Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t1 = new Triple(s1, p1, c1);
		pattern.getTriples().add(t1);
		
		Node s2 = NodeFactory.createVariable("s2");
		Triple t2 = new Triple(s2, p1, c2);
		pattern.getTriples().add(t2);
		
		Triple t3 = new Triple(s1, p, s2);
		pattern.getTriples().add(t3);
		pattern.getVarsToSelect().add("s");
		
		result = kb.countPathsByPattern(pattern);

		return result;
	}
	
	
	public static Set<Node> typesOf(KnowledgeBase kb, Node n) {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		Vector<Node> temp = kb.nodesByPattern(pattern);
				
		for(Node r:temp) {
			if(r.getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS))
				result.add(r);
		}

		
		
		
		return result;
	}
	
	public static Set<Node> mostSpecificTypes(KnowledgeBase kb, Set<Node> types) {
		Set<Node> result = new HashSet<Node>();
		System.out.println("type.size()="+types.size());
		System.out.println(types);
		
		Vector<String> ppa = new Vector<String>();
		ppa.add("*");
		for(Node t1:types) {
			Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			boolean most = true;
			for(Node t2:types) {
				if(t1!=t2) {
					Path path = new Path();
					Triple t = new Triple(t2, p, t1);
					path.getTriples().add(t);
					path.setPropertyPathAdornments(ppa);
					if(kb.pathExistence(path))
						most = false;
				}
			}
			if(most==true)
				result.add(t1);
		}

		System.out.println("most="+result);
		if(result.size()==0)
			result.add(NodeFactory.createURI(Constants.OWL_THING));

		return result;
	}
	
	/*
	 * Similarity between sets of classes representing types of nodes in dbr
	 */
	public double typesSimilarity(Set<Node> types1, Set<Node> types2, WeightedTaxonomy_OntModel wt) {
		double result = 0.0d;
		
		Vector<OFVElem> ofv1 = new Vector<OFVElem>();
		for(Node n:types1) {
			ofv1.add(new OFVElem(n, ""));
		}
	
		Vector<OFVElem> ofv2 = new Vector<OFVElem>();
		for(Node n:types2) {
			ofv2.add(new OFVElem(n, ""));
		}
		
		String annotationMode = Constants.SEMSIM_BY_ID;
		boolean coeff_option = false;
		
//		String ontoFile = "semrel/dbpedia/dbpedia.owl";
		
//		WeightedTaxonomy_OntModel wt = WeightedTaxonomy_OntModel.getInstance(ontoFile);
		IntrinsicLikelihood likelihood = new TopDown();
		SemsimEngine se = new SemsimEngine(wt, coeff_option);
		wt.initWeights(likelihood);

//		System.out.println(ofv1);
//		System.out.println(ofv2);
		result = se.semsim(ofv1, ofv2, coeff_option);
		
		return result;
	}
	
}
