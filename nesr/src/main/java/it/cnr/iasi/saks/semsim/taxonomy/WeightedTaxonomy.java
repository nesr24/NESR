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
package it.cnr.iasi.saks.semsim.taxonomy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.RDFGraph;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.likelihood.CorpusBasedLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.IntrinsicLikelihood;

/**
 * 
 * @author francesco
 *
 */
public abstract class WeightedTaxonomy extends RDFGraph {

	private String xmlnsPrefix = "";
	
	//private String ontoFile = "semsim/ACM/taxonomy/ACM_dag__all.owl"; 
	//private String ontoFile = "semsim/test/taxonomy/test.owl";
	//private String ontoFile = "semsim/tourism/taxonomy/CompInd_tourism.owl";
	//private String ontoFile = "semsimPlus/encyclopedia/taxonomy/taxonomy_splus.owl";
	//private String ontoFile = "semsimPlus/axed/taxonomy/taxonomy_v04.owl";
	//private String ontoFile = "clustering/ontology/dbpedia_2014.owl";
	//private String ontoFile = "semsim/ACM/dataFromAntonio/output2/acmtaxonomy.owl";
	private String ontoFile = "semrel/ftaf/dbpedia/dbpedia.owl";
	//private String ontoFile = "";
	
	private Map<Node, Double> weights = new HashMap<Node, Double>();
	private Map<Node, String> id_label = new HashMap<Node, String>();
	private Map<String, Node> label_id = new HashMap<String, Node>();
	private Map<Node, Integer> id_order = new HashMap<Node, Integer>(); 
	private double height = 0.0d;
	
	public WeightedTaxonomy() {}
		
	
	public void WeightedTaxonomy_init(String ontoFile) {
//		super();
		this.setOntoFile(ontoFile);
		this.loadOWL(ontoFile);
		this.setXmlnsPrefix(((OntModel)(this.getKnowledgeResourceRef())).getNsPrefixURI(Constants.XMLNS_PREFIX));
		init_idLabelMaps();
		init_ordering();
	}
	
	public String getOntoFile() {
		return ontoFile;
	}

	public void setOntoFile(String ontoFile) {
		this.ontoFile = ontoFile;
	}

	public Map<Node, Double> getWeights() {
		return weights;
	}

	public void setWeights(Map<Node, Double> weights) {
		this.weights = weights;
	}

	public Map<Node, String> getId_label() {
		return id_label;
	}

	public void setId_label(Map<Node, String> id_label) {
		this.id_label = id_label;
	}

	public Map<String, Node> getLabel_id() {
		return label_id;
	}

	public void setLabel_id(Map<String, Node> label_id) {
		this.label_id = label_id;
	}

	public Map<Node, Integer> getId_order() {
		return id_order;
	}

	public void setId_order(Map<Node, Integer> id_order) {
		this.id_order = id_order;
	}

	public String getXmlnsPrefix() {
		return xmlnsPrefix;
	}
	
	

	public double getHeight() {
		return height;
	}


	public void setHeight(double height) {
		this.height = height;
	}


	public void setXmlnsPrefix(String xmlnsPrefix) {
		this.xmlnsPrefix = xmlnsPrefix;
	}
		
	public void loadOWL(String ontoFile) {				
		System.out.println(ontoFile);
		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	    try {
	        InputStream in = FileManager.get().open(ontoFile);
	        try {
	            ontoModel.read(in, null);
	            this.setXmlnsPrefix(ontoModel.getNsPrefixURI("base"));
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        //LOGGER.info("Ontology " + ontoFile + " loaded.");
	    } 
	    catch (JenaException je) {
	        System.err.println("ERROR" + je.getMessage());
	        je.printStackTrace();
	        System.exit(0);
	    }
	
		this.setKnowledgeResourceRef(ontoModel);
		this.addOWLThingAsClass();
		this.addLinksFromRootsToOWLThing();

	}
	
	public void init_idLabelMaps() {
		Set<Node> classes = this.allClasses();
				
		for(Node n:classes) {
			PathPattern pattern = new PathPattern(null);
			Node s = n;
			Node p = NodeFactory.createURI(Constants.RDFS_LABEL);
			Node o = NodeFactory.createVariable("u1");
			Triple t = new Triple(s, p, o); 
			pattern.getTriples().add(t);
			pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
			pattern.getVarsToSelect().add("u1");
			
			Vector<Node> temp = this.nodesByPattern(pattern);
			
			String label = "";
			if(temp.size() == 0)
				label = n.getURI().toString().substring(n.getURI().toString().indexOf("#")+1);
			else 
				label = temp.get(0).getLiteral().toString();
			this.getId_label().put(n, label);
			this.getLabel_id().put(label, n);
		}
	}
	
	public void init_ordering() {
		Set<Node> classes = this.allClasses();
				
		for(Node n:classes) {
			PathPattern pattern = new PathPattern(null);
			Node s = n;
			Node p = NodeFactory.createURI(getXmlnsPrefix()+Constants.SEMSIM_ORDERING);
			Node o = NodeFactory.createVariable("u1");
			Triple t = new Triple(s, p, o); 
			pattern.getTriples().add(t);
			pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
			pattern.getVarsToSelect().add("u1");
			
			Vector<Node> temp = this.nodesByPattern(pattern);
			
			int ordering = 0;
			if(temp.size() > 0)
				ordering = new Integer(temp.get(0).getLiteral().toString());
			this.getId_order().put(n, ordering);
		}
	}
	
	public Set<Node> allClasses() {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_CLASS);
		Triple t = new Triple(s, p, o); 
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		Vector<Node> temp = this.nodesByPattern(pattern);
		result.addAll(temp);
		return result;
	}

	public int size() {
		int result = 0;
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_CLASS);
		Triple t = new Triple(s, p, o); 
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		result = this.countNodesByPattern(pattern);
		return result;
	}
	
	public Vector<Node> lub(Node n1, Node n2) {
		Vector<Node> result = new Vector<Node>(); 
		
		if(n1.getURI().toString().equals(n2.getURI().toString()))
			result.addElement(n1);
		else {
			Vector<Node> ancestors_first = this.ancestors(n1);
			//Vector<Node> ancestors_first = this.ancestorsWithinNs(pair.getFirst(), "http://dbpedia.org/ontology");
			ancestors_first.add(n1);
			Vector<Node> ancestors_second = this.ancestors(n2);
			//Vector<Node> ancestors_second = this.ancestorsWithinNs(pair.getSecond(), "http://dbpedia.org/ontology");
			ancestors_second.add(n2);
			ancestors_first.retainAll(ancestors_second);
			if(ancestors_first.size() == 0) {
				result.add(NodeFactory.createURI(Constants.OWL_THING));
			}
			else {
				result = this.mostSpecific(ancestors_first);
			}
		}
		return result;
	}
	
	public Vector<Node> ancestorsWithinNs(Node n1, String ns) {
		Vector<Node> result = new Vector<Node>();
		for(Node n:this.ancestors(n1)) 
			if(n.getURI().startsWith(ns) || n.getURI().equals(Constants.OWL_THING))
				result.add(n);
		return result;
	}
	
	public Vector<Node> ancestors(Node n1) {
		Vector<Node> result = new Vector<Node>();

		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node u = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Vector<String> ppa = new Vector<String>();
		ppa.add("+");
		Triple t = new Triple(n1, p, u);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setPropertyPathAdornments(ppa);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		Vector<Node> temp = this.nodesByPattern(pattern);
	
		for(Node x:temp) 
			result.add(x);
		
		return result;
	}
	
	public Vector<Node> descendants(Node n) {
		Vector<Node> result = new Vector<Node>();

		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node u = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Vector<String> ppa = new Vector<String>();
		ppa.add("+");
		Triple t = new Triple(u, p, n);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setPropertyPathAdornments(ppa);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		Vector<Node> temp = this.nodesByPattern(pattern);
	
		for(Node x:temp) 
			result.add(x);
		
		return result;
	}
	
	public Vector<Node> mostSpecific(Vector<Node> classes) {
		Vector<Node> result = new Vector<Node>();
		
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		for(Node class_ext:classes) {
			boolean candidate = true;
			for(Node class_int:classes) {
				if(!(class_ext.equals(class_int))) {
					PathPattern pattern = new PathPattern(null); 
					Triple t = new Triple(class_int, p, class_ext);
					pattern.getTriples().add(t);
					Vector<String> ppa = new Vector<String>();
					ppa.add("+");
					pattern.setPropertyPathAdornments(ppa);
	
					if(this.pathExistence(pattern)) {
						candidate = false;
						break;
					}
				}
			}	
			if(candidate) 
				result.add(class_ext);
		}
		return result;
	}
	
	public Vector<Node> children(Node n) {
		Vector<Node> result = new Vector<Node>();
		
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Triple t = new Triple(s, p, n);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		result = this.nodesByPattern(pattern);
		
		return result;

	}

	public boolean ifSibling(Node n1, Node n2) {
		boolean result = false;
		
		Vector<Node> parents_first = new Vector<Node>();
		Vector<Node> parents_second = new Vector<Node>();
		
		parents_first = this.parents(n1);
		parents_second = this.parents(n2);
		
		/*System.out.println(pair);
		System.out.println("\t"+parents_first);
		System.out.println("\t"+parents_second);
		*/
		int parent_first_count = parents_first.size();
		parents_first.removeAll(parents_second);
		int parent_first_count_after = parents_first.size(); 
		if((parent_first_count - parent_first_count_after) > 0)
			result = true;
		//System.out.println(result);
		return result;

	}
	
	public Vector<Node> parents(Node n) {
		Vector<Node> result = new Vector<Node>();
		
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Triple t = new Triple(n, p, s);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		result = this.nodesByPattern(pattern);
		
		return result;

	}
		
	public void initWeights(IntrinsicLikelihood likelihood) {
		this.setWeights(likelihood.likelihood(this));		
	}
	
	public void initWeights(CorpusBasedLikelihood likelihood, Map<String, Vector<OFVElem>> corpus) {
		this.setWeights(likelihood.likelihood(this, corpus));		
	}
	
	public void initWeights(String input_file, String mode) {
		this.loadWeights(input_file, mode);		
	}
	
	public int getOrderById(Node id) {
		int result = 0;
		result = new Integer(this.getId_order().get(id));
		return result;
	}
	
	public String getLabelById(Node n) {
		String result = "";
		result = this.getId_label().get(n);
		return result;
	}
	
	public Node getIdByName(String name) {
		Node result = null;
		result = NodeFactory.createURI(this.getXmlnsPrefix()+name);
		return result;
	}
	
	public String getNameById(Node n) {
		String result = "";
		result = n.getURI().toString().substring(n.getURI().toString().indexOf(Constants.URINAME_SEPARATOR)+1);
		return result;
	}
	
	public String getLabelByName(String name) {
		String result = "";
		result = this.getLabelById(NodeFactory.createURI(this.getXmlnsPrefix()+Constants.URINAME_SEPARATOR+name));
		return result;
	}
	
	public Node getIdByLabel(String label) {
		Node result = null;
		result = this.getLabel_id().get(label);
		return result;
	}
	
	public String getNameByLabel(String label) {
		String result = "";
		result = this.getNameById(this.getIdByLabel(label));
		return result;
	}
	
	public Set<Node> getIds() {
		Set<Node> result = new HashSet<Node>();
		result = this.getId_label().keySet();
		return result;
	}
	
	public Set<Node> roots() {
		Set<Node> result = new HashSet<Node>();
		
		Set<Node> classes = this.allClasses();
		for(Node c:classes) {
			PathPattern pattern = new PathPattern(null);
			Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			Node n = NodeFactory.createVariable("u1");
			Triple t = new Triple(c, p, n);
			pattern.getTriples().add(t);
			if(!(this.pathExistence(pattern)))
				result.add(c);
		}
		
		return result;
	}
	
	public Set<Node> leaves() {
		Set<Node> result = new HashSet<Node>();
		
		Set<Node> classes = this.allClasses();
		for(Node c:classes) {
			PathPattern pattern = new PathPattern(null);
			Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			Node n = NodeFactory.createVariable("u1");
			Triple t = new Triple(n, p, c);
			pattern.getTriples().add(t);
			if(!(this.pathExistence(pattern)))
				result.add(c);
		}
		
		return result;
	}

	private void addOWLThingAsClass() {
		Resource s = ResourceFactory.createResource(Constants.OWL_THING);
		Property p = ResourceFactory.createProperty(Constants.RDF_TYPE);
		Resource o = ResourceFactory.createResource(Constants.OWL_CLASS);
		((OntModel)(this.getKnowledgeResourceRef())).add(s, p, o);
	}
	
	private void addLinksFromRootsToOWLThing() {
		Set<Node> roots = this.roots();
		Property p = ResourceFactory.createProperty(Constants.RDFS_SUBCLASSOF);
		Resource o = ResourceFactory.createResource(Constants.OWL_THING);
		for(Node n:roots) {
			if(!(n.getURI().toString().equals(Constants.OWL_THING))) {
				Resource s = ResourceFactory.createResource(n.getURI().toString());
				((OntModel)(this.getKnowledgeResourceRef())).add(s, p, o);
			}
		}
	}
	
	private void loadWeights(String input_file, String mode) {
		System.out.println(input_file);
		this.getWeights().put(NodeFactory.createURI(Constants.OWL_THING), 1.0d);
        try {
//            BufferedReader b = new BufferedReader(new FileReader(this.getClass().getResource(input_file).getFile()));
            BufferedReader b = new BufferedReader(new FileReader(input_file));
        	String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				
				String class_ref = s.substring(0, s.indexOf(Constants.SEMSIM_EQUAL)).trim();
				Node id = null;
				if(mode.equals(Constants.SEMSIM_BY_LABEL))
					id = this.getIdByLabel(class_ref);
				else if(mode.equals(Constants.SEMSIM_BY_ID))
					id = NodeFactory.createURI(class_ref);
				else if(mode.equals(Constants.SEMSIM_BY_NAME))
					id = this.getIdByName(class_ref);

				s = s.substring(s.indexOf(Constants.SEMSIM_WEIGHT_DELIM_START)+1, s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_END)).trim();
				double weight = Double.valueOf(s);
				this.getWeights().put(id, weight);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public int depth(Node n) {
		int result = 0;
		PathPattern pattern = new PathPattern(null);
		Node n2 = NodeFactory.createURI(Constants.OWL_THING);
		result = this.distanceInTree(n, n2);
		return result;
	}
	
	public int distanceInTree(Node n1, Node n2) {
		int result = 0;

		Node lub = mostSpecific(lub(n1, n2)).get(0);
		
		PathPattern pattern = new PathPattern(null);
		Node u1 = NodeFactory.createVariable("u1");
		Node p1 = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Triple t1 = new Triple(n1, p1, u1);
		Triple t2 = new Triple(u1, p1, lub);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		Vector<String> ppa = new Vector<String>();
		ppa.add("*");
		ppa.add("+");
		pattern.setPropertyPathAdornments(ppa);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		result = this.countNodesByPattern(pattern);
		
		
		pattern = new PathPattern(null);
		t1 = new Triple(n2, p1, u1);
		t2 = new Triple(u1, p1, lub);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.setPropertyPathAdornments(ppa);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("u1");
		
		result = result + this.countNodesByPattern(pattern);

		return result;
	}
	
	
	public int max_depth() {
		int result = 0;
		for(Node c:this.leaves()) {
			int depth = this.depth(c);
			if(depth > result)
				result = depth;
		}
		return result;
	}
	
	public Node bestLubId(Map<Node, Double> classes) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		result = bestLub(classes);
		return result;
	}
	
	public Node bestLub(Map<Node, Double> classes) {
		Node result = null;
		double result_value = 0.0d;
		for(Node n:classes.keySet()) {
			double temp = classes.get(n);
			if(temp > result_value)
				result = n;
		}
		return result;
	}
	
	public Node getEquivalentClasses(Node c) {
		Node result = null;
		
		return result;
	}
	
	public Node getEquivalentClass(Node c) {
		Node result = null;
		PathPattern pattern = new PathPattern(null);
		Node s = NodeFactory.createVariable("s");
		Node p = NodeFactory.createURI(Constants.OWL_EQUIVALENT_CLASS);
		Triple t = new Triple(s, p, c);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("s");
		
		Vector<Node> temp = this.nodesByPattern(pattern);
		if(temp.size()>0) {
			if(temp.size()>1) {
				System.out.println("******************"+c.getURI().toString()+" has more than 1 equivalent class");
			}
			result = temp.get(0);
		}		
		
		return result;
	}
	
	public Set<Node> leaves(Node c) {
		Set<Node> result = new HashSet<Node>();
		Set<Node> leaves = this.leaves();
		for(Node hypo:this.descendants(c)) {
			if(leaves.contains(hypo)) {
				result.add(hypo);
			}
		}
		return result;
	}
	
	public double height() {
		double result = 0.0d;
		
		if(this.getHeight()>0) {
			result = this.getHeight();
		}
		else {
			double temp = 0.0d;
			Set<Node> leaves = this.leaves();
			for(Node n:leaves) {
				double depth = this.depth(n);
				if(depth>temp)
					temp = depth;
			}
			this.setHeight(temp);
			result = temp;
		}
		return result;
	}
	
	public Set<Node> siblings(Node n) {
		Set<Node> result = new HashSet<Node>();
		Set<Node> temp = new HashSet<Node>();
		Vector<Node> parents = this.parents(n);
		for(Node p:parents) {
			temp.addAll(this.children(p));
		}
		temp.remove(n);
		result = temp;
		return result;
	}
}
