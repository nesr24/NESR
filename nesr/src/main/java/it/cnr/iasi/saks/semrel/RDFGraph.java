package it.cnr.iasi.saks.semrel;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;

import it.cnr.iasi.saks.semrel.sparql.SPARQLConnector;
import it.cnr.iasi.saks.semrel.sparql.SPARQLQueryCollector;
/**
 * 
 * @author francesco
 *
 */
public abstract class RDFGraph implements KnowledgeBase {
	
	private KBCache cache = new KBCache();
	private SPARQLConnector sc;
	private Object knowledgeSourceRef;
	private String graph;
//	private int triplesCount = 320033856;
	private int triplesCount = -1;
	private int resourcesCount = -1;
		
	private Set<Filter> p_filters = new HashSet<Filter>();
	private Set<Filter> so_filters = new HashSet<Filter>();


	public KBCache getCache() {
		return cache;
	}

	public void setCache(KBCache cache) {
		this.cache = cache;
	}
	
	public Set<Filter> getP_filters() {
		return p_filters;
	}

	public void setP_filters(Set<Filter> p_filters) {
		this.p_filters = p_filters;
	}

	public Set<Filter> getSo_filters() {
		return so_filters;
	}

	public void setSo_filters(Set<Filter> so_filters) {
		this.so_filters = so_filters;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public Object getKnowledgeResourceRef() {
		return knowledgeSourceRef;
	}

	public void setKnowledgeResourceRef(Object knowledgeSourceRef) {
		this.knowledgeSourceRef = knowledgeSourceRef;
	}

	public SPARQLConnector getSc() {
		return sc;
	}

	public void setSc(SPARQLConnector sc) {
		this.sc = sc;
	}

	public int getTriplesCount() {
		return triplesCount;
	}

	public void setTriplesCount(int triplesCount) {
		this.triplesCount = triplesCount;
	}

	public int getResourcesCount() {
		return resourcesCount;
	}

	public void setResourcesCount(int resourcesCount) {
		this.resourcesCount = resourcesCount;
	}

	/**
	 * Count the nodes filling a given pattern. 
	 * @param pattern A PathPattern representing the search criteria.
	 * @param distinct {@link Constants.SPARQL_DISTINCT} or {@link Constants.SPARQL_NOT_DISTINCT} 
	 * @return
	 */
	public int countNodesByPattern(PathPattern pattern) {
//		System.out.println("countNodesByPattern");
		int result = 0;
		result = this.getCache().getNumNodesByPattern(pattern);
		// if the answer is not in the cache then ... 
		if(result == -1) {
			// ... get it from the SPARQL endpoint ...
			
			result = SPARQLQueryCollector.countNodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			this.getCache().getNumNodesByPattern().put(pattern, result);
		}
		return result; 
	}
	
	/**
	 * Get the nodes corresponding to a given pattern.
	 * For the allowed values in the pattern, see the method countTriplesByPattern. 
	 * If the required information is not in the cache, it is asked to the SPARQL endpoint. 
	 * @param pattern
	 * @return
	 */
	public Vector<Node> nodesByPattern(PathPattern pattern) {
		Vector<Node> result = new Vector<Node>();
		//result = this.getCache().getNodesByPattern().get(pattern);
		// if the answer is not in the cache then ...
		//if(result == null) {
			// ... get it from the SPARQL endpoint ...
			result = SPARQLQueryCollector.nodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			//this.getCache().getNodesByPattern().put(pattern, result);
		//}
		return result;
	}
	

	public Vector<Vector<Node>> predicatesAndNodesByPattern(PathPattern pattern) {
		Vector<Vector<Node>> result = new Vector<Vector<Node>>();
		//result = this.getCache().getPredicatesAndNodesByPattern().get(pattern);
		// if the answer is not in the cache then ...
		//if(result == null) {
			// ... get it from the SPARQL endpoint ...
			result = SPARQLQueryCollector.predicatesAndNodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			//this.getCache().getPredicatesAndNodesByPattern().put(pattern, result);
		//}
		return result;
	}
	
	public Vector<Vector<Node>> predicatesAndNodesBySubject(Node s) {
		Vector<Vector<Node>> result = new Vector<Vector<Node>>();		
		Node o = NodeFactory.createVariable("o1");
		// Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node p = NodeFactory.createVariable("p1");
		Triple t = new Triple(s,p,o);
		PathPattern pattern = new PathPattern(null);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("p1");
		pattern.getVarsToSelect().add("o1");
		result = SPARQLQueryCollector.predicatesAndNodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
		return result;
	}
	
	
	/**
	 * Get the number of paths matching a given pattern.
	 * This is a special case of the generic one implemented by the countGraphExpressionsByPattern method
	 * A pattern is a Triple. Each node in the triple can be:
	 * (1) a non empty String URI, meaning that the triples' element is fixed. 
	 * (2) an empty String URI, meaning that the triple's element is not fixed but is not the requested info. 
	 * (3) a variable, meaning that the triple's element is the requested info.
	 * If the required information is not in the cache, it is asked to the SPARQL endpoint.
	 * @param pattern
	 * @return
	 */
	public int countPathsByPattern(PathPattern pattern) {
		int result = 0;
		result = this.getCache().getNumPathsByPattern(pattern);
		// if the answer is not in the cache then ... 
		if(result == -1) {
			// ... get it from the SPARQL endpoint ...
			result = SPARQLQueryCollector.countPathsByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			//this.getCache().getNumPathsByPattern().put(pattern, result);
			this.getCache().update(pattern, result);
		}
		return result; 
	}
	
	public int countTriplesWithPredicateRDF_TYPE() {
		int result = 0;
		PathPattern p0 = new PathPattern(new Vector<String>()) ;
		Node s = NodeFactory.createVariable("u1");
		Set<Filter> s_filters = this.instantiateFilters("u1", Constants.SUBJECT);
		Node o = NodeFactory.createVariable("u2");
		Set<Filter> o_filters = this.instantiateFilters("u2", Constants.OBJECT);
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t0 = new Triple(s, p, o);
		p0.getTriples().add(t0);
		p0.getFilters().addAll(s_filters);
		p0.getFilters().addAll(o_filters);
		result = this.getCache().getNumPathsByPattern(p0);
		if(!(result>0)) {
			Node s1, o1, p1;
			Node s2, o2, p2; 
			
			// 1 count all triples with predicate rdf:type and object as an OWL Class:
			//		<u1, rdf:type, u2> .
			//		<u2, rdf:type, owl:Class>
			s1 = NodeFactory.createVariable("u1");
			p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			o1 = NodeFactory.createVariable("u2");
			
			s2 = NodeFactory.createVariable("u2");
			p2 = NodeFactory.createURI(Constants.RDF_TYPE);
			o2 = NodeFactory.createURI(Constants.OWL_CLASS);
	
			PathPattern pattern = new PathPattern(new Vector<String>()); 
			Triple t = new Triple(s1, p1, o1);
			pattern.getTriples().addElement(t);
			t = new Triple(s2, p2, o2);
			pattern.getTriples().addElement(t);
			
			int countAllTriplesWithPredicate_RDFType_and_ObjectInDBO = 
					this.countPathsByPattern(pattern);
	
			// 2 count all triples with predicate rdf:type and object owl:ObjectProperty: 
			//     <u1, rdf:type, owl:ObjectProperty> .
			//		filters on u1
			Set<Filter> filters = new HashSet<Filter>();
			s1 = NodeFactory.createVariable("u1");
			filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
			p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			o1 = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
			pattern = new PathPattern(new Vector<String>());
			t = new Triple(s1, p1, o1);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
	
			int countAllTriplesWithPredicate_RDFType_and_ObjectEqualsToObjectProperty = 
					this.countPathsByPattern(pattern);
			
			result = countAllTriplesWithPredicate_RDFType_and_ObjectInDBO +
					countAllTriplesWithPredicate_RDFType_and_ObjectEqualsToObjectProperty;
			//this.getCache().getNumPathsByPattern().put(p0, result);
			this.getCache().update(p0, result);
		}
		return result;
	}

	public int countAllTriples() {
//		System.out.println("countAllTriples");
		int result = this.getTriplesCount();
		
		if((result==-1)) {
			result = this.getCache().getAllTriplesNum();
			if(result==-1) {
				Node s1, o1, p1;
				PathPattern pattern = new PathPattern(new Vector<String>());
							
				s1 = NodeFactory.createVariable("u1");
				o1 = NodeFactory.createVariable("u2");
				p1 = NodeFactory.createVariable("p1");
				
				Triple t = new Triple(s1, p1, o1);
				pattern.getTriples().addElement(t);
				
				result = this.countPathsByPattern(pattern);
				System.out.println("all triples = "+result);		
			}
			this.setTriplesCount(result);
			this.getCache().setAllTriplesNum(result);
		}
		return result;

	}
	
	public int countAllNodes() {
//		System.out.println("countAllNodes");
		int result = this.getResourcesCount();

		if(result==-1) {
			result = this.getCache().getAllNodesNum();
			if(result==-1) {

				Node s1, o1, p1;
				PathPattern pattern = new PathPattern(null);
							
				s1 = NodeFactory.createVariable("u1");
				o1 = NodeFactory.createVariable("u2");
				p1 = NodeFactory.createVariable("p1");
				
				Triple t = new Triple(s1, p1, o1);
				pattern.getTriples().add(t);
				pattern.setDistinct(Constants.SPARQL_DISTINCT);
				//pattern.getFilters().addAll(this.getSo_filters());
				Set<Filter> filters = new HashSet<Filter>();
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("u1");

				result = this.countNodesByPattern(pattern);

			}
			this.getCache().setAllNodesNum(result);
			this.setResourcesCount(result);
		}
			System.out.println(result);
		return result;

	}

	
	public int countAllTriplesWithConstraints() {
		int result = 0;
		result = this.getCache().getAllTriplesNum();
		if(!(result>0)) {
			Node s1, o1, p1;
			Node s2, o2, p2; 
			PathPattern pattern = new PathPattern(null);
			
			int countAllTriplesWithPredicate_RDFType = this.countTriplesWithPredicateRDF_TYPE(); 
			
			// 1. count all triples with predicate as an Object Property in DBO
			//		<u1, p1, o1> . 
			//		<p1, rdf:type, owl:ObjectProperty>
			//		NO FILTERS ARE APPLIED
			s1 = NodeFactory.createVariable("u1");
			o1 = NodeFactory.createVariable("u2");
			p1 = NodeFactory.createVariable("p1");
			
			s2 = NodeFactory.createVariable("p1");
			o2 = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
			p2 = NodeFactory.createURI(Constants.RDF_TYPE);
						
			pattern = new PathPattern(null); 
			Triple t = new Triple(s1, p1, o1);
			pattern.getTriples().addElement(t);
			t = new Triple(s2, p2, o2);
			pattern.getTriples().addElement(t);
			
			// 1.1 count all triples with predicate in out/dbo.txt
			String fileName = getClass().getResource("/predicates/out/dbo.txt").getFile();
			int countAllTriplesWithPredicateIn_DBO_OUT = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.NOT_FILTERING);

			int countAllTriplesWithPredicateAsAnObjectPropertyInDBO = 	
					this.countPathsByPattern(pattern) - 
					countAllTriplesWithPredicateIn_DBO_OUT;

			// 2. count all triples with predicate in in/rdfs.txt
			fileName = getClass().getResource("/predicates/in/rdfs.txt").getFile();
			int countAllTriplesWithPredicateIn_RDFS_IN = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.FILTERING);
			
			// 3. count all triples with predicate in in/owl.txt
			fileName = getClass().getResource("/predicates/in/owl.txt").getFile();
			int countAllTriplesWithPredicateIn_OWL_IN = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.FILTERING);
			
			result = countAllTriplesWithPredicate_RDFType +
					countAllTriplesWithPredicateAsAnObjectPropertyInDBO +
					countAllTriplesWithPredicateIn_RDFS_IN +
					countAllTriplesWithPredicateIn_OWL_IN;
		}
		return result;
	}
	
	private int countAllTriplesWithPredicateInFile(String file, boolean filtering) {
		int result = 0;
		BufferedReader br = null;
        try {     
        	br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
            	if(!(line.trim().startsWith("#"))) {
	            	Node s1, o1, p1;
	            	Set<Filter> filters = new HashSet<Filter>();
	        		s1 = NodeFactory.createVariable("u1");
	        		o1 = NodeFactory.createVariable("u2");
	        		p1 = NodeFactory.createURI(line.trim());
	        		PathPattern pattern = new PathPattern(null);
	            	Triple t = new Triple(s1,p1,o1);
	            	pattern.getTriples().add(t);
	            	if(filtering) {
		            	filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
		            	filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
		            	pattern.setFilters(filters);
	            	}
	            	int partial = this.countPathsByPattern(pattern);
	            	result = result + partial;
            	}
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
        
        return result;		
	}
	
	public Vector<Path> pathsByPattern(PathPattern pattern, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		Vector<Path> temp = SPARQLQueryCollector.pathsByPathPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
		if(acyclic) 
			for(Path p:temp)
				if(p.isAcyclic())
					result.add(p);
				else;
		else result = temp;
		return result;
	}
	
	public boolean pathExistence(PathPattern pattern) {
		boolean result = false;
		result = SPARQLQueryCollector.pathsExistence(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
		return result;
	}
	
	public boolean pathExistence(Path path) {
		boolean result = false;
		result = SPARQLQueryCollector.pathExistence(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), path);
		return result;
	}

	public boolean graphExistence(String graph) {
		boolean result = false;
		result = SPARQLQueryCollector.graphExistence(this.getSc(), this.getKnowledgeResourceRef(), graph);
		return result;
	}
		
	
	public Vector<Path> paths(Node n1, Node n2, int minLength, int maxLength, String mode, boolean acyclic) {
//		System.out.println("paths");
		Vector<Path> result = new Vector<Path>();
		for(int l=minLength; l<=maxLength; l++)
			result.addAll(this.paths_3_new(n1, n2, l, mode, acyclic));
		return result;
	}

	public int clearNodesByPattern() {
		int result = 0;
		result = this.getCache().clearNodesByPattern();
		return result;
	}
	
	public int clearNumNodesByPattern() {
		int result = 0;
		result = this.getCache().clearNumNodesByPattern();
		return result;
	}
	
	public int clearNumPathsByPattern() {
		int result = 0;
		result = this.getCache().clearNumPathsByPattern();
		return result;
	}

	public int clearPaths() {
		int result = 0;
		result = this.getCache().clearPaths();
		return result;
	}
	
	public void clearCache() {
		int x = this.getCache().getAllNodesNum();
		int y = this.getCache().getAllTriplesNum();
		
		this.setCache(new KBCache());
		
		if(x>-1) {
			this.getCache().setAllNodesNum(x);
			this.getCache().setAllTriplesNum(y);
		}
	}
	
	/**
	 * Search and retrieve paths connecting n1 and n2.
	 * @param n1 {@link Node} a node at the extremity of the searched paths
	 * @param n2 {@link Node} a node at the extremity of the searched paths
	 * @param length The length of the searched paths that must be 1, 2 or 3.
	 * @param mode {@link Constants.DIRECTED_PATH} or {@link Constants.NO_DIRECTED_PATH} for assuming the graph as directed or not, respectively.
	 * @param acyclic if the retrieved p aths must be acyclic {@link Constraints.ACYCLIC} or not {@link Constraints.ACYCLIC} 
	 * @return {@link Vector} of {@link Path} representing the paths connecting he two nodes.
	 */
	public Vector<Path> paths(Node n1, Node n2, int length, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		String pathKey = n1.getURI()+n2.getURI()+length+mode+acyclic;
		result = this.getCache().getPaths(pathKey);
		if(result.size() == 0) {
			Vector<Path> temp = new Vector<Path>(); 
			Set<Filter> filters = new HashSet<Filter>();
			// If the searched paths must be straight from n1 to n2, 
			if(mode.equals(Constants.DIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 3			
				else if(length==3) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					Node s3 = o2;
					Node p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					Node o3 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					Triple t3 = new Triple(s3,p3,o3);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
			}
	
			// if the edges (predicates) are considered not directed (they can be traversed in any direction)
			if(mode.equals(Constants.UNDIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
	//				1.1. path pattern = n1--(p1)->n2 (0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 1.2. path pattern = u1<-(p1)--u2 (1)
					s1 = n2;
					o1 = n1;
					t1 = new Triple(s1,p1,o1);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					// 2.1. path pattern = n1--(p1)->u1--(p2)->n2 (0-0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 2.2. path pattern = n1--(p1)->u1<-(p2)--n2 (0-1)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = o1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);	
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.3. path pattern = n1<-(p1)--u1--(p2)->n2 (1-0)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = s1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.4. path pattern = n1<-(p1)--u1<-(p2)--n2 (1-1)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = s1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
				}
				// If the length of the searched paths is equal to 3			
				if(length>=3) {
					// 3.1. path pattern = n1--(p1)->u1--(p2)->u2--(p3)->n2 (0-0-0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					Node s3 = o2;
					Node p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					Node o3 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					Triple t3 = new Triple(s3,p3,o3);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.2. path pattern = n1--(p1)->u1--(p2)->u2<-(p3)--n2 (0-0-1)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = o1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					s3 = n2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = o2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.3 path pattern = n1--(p1)->u1<-(p2)--u2--(p3)->n2 (0-1-0)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = o1;
					s3 = s2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.4 path pattern = n1--(p1)->u1<-(p2)--u2<-(p3)--n2 (0-1-1)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = o1;
					s3 = n2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = s2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.5 path pattern = n1<-(p1)--u1--(p2)->u2--(p3)->n2 (1-0-0)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = s1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					s3 = o2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.6 path pattern = n1<-(p1)--u1--(p2)->u2<-(p3)--n2 (1-0-1)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = s1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					s3 = n2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = o2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.7 path pattern = n1<-(p1)--u1<-(p2)--u2--(p3)->n2 (1-1-0)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = s1;
					s3 = s2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.8 path pattern = n1<-(p1)--u1<-(p2)--u2<-(p3)--n2 (1-1-1)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = s1;
					s3 = n2;
					p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = s2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					t3 = new Triple(s3,p3,o3);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				
			}
			if(acyclic)
				for(Path p:temp)
					if(p.isAcyclic())
						result.add(p);
					else;
			else result = temp;
			
			this.getCache().getPaths().put(pathKey, result);
		}
		return result;
	}
		
	public Set<Filter> instantiateFilters(String varName, String nodePosition) {
		Set<Filter> result = new HashSet<Filter>();
		
		if(nodePosition.equals(Constants.SUBJECT) || nodePosition.equals(Constants.OBJECT)) {
			if(this.getSo_filters() != null) {
				for(Filter f:this.getSo_filters()) {
					Filter instance_f = new Filter(f.getValue().replaceAll("%%var%%", varName));
					result.add(instance_f);
				}
			}
		}
		else if(nodePosition.equals(Constants.PREDICATE)) {
			if(this.getP_filters() != null) {
				for(Filter f:this.getP_filters()) {
					Filter instance_f = new Filter(f.getValue().replaceAll("%%var%%", varName));
					result.add(instance_f);
				}
			}
		}
		
		return result;
	}
	
	public Vector<Node> predicateTypes() {
		Vector<Node> result = new Vector<Node>();
		
		return result;
	}
	
	/**
	 * Search for neighborhoods Nodes at distance.
	 * Nodes are searched via direct paths
	 */
	public Vector<Node> neighbours(Node n1, int distance, String direction) {
		Vector<Node> result = new Vector<Node>();
		Set<Node> temp = new HashSet<Node>();
		Set<Filter> filters = new HashSet<Filter>();
		if(direction.equalsIgnoreCase(Constants.OUT) || direction.equalsIgnoreCase(Constants.IN_OUT)) {
			// If the distance is equal to 1  
			if(distance==1) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(s1,p1,o1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
			// If the distance is equal to 2
			else if(distance==2) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
			// If the distance is equal to 3			
			else if(distance==3) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				Node s3 = o2;
				Node p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				Node o3 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,o3);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
		}
		System.out.println(temp.size());
		if(direction.equalsIgnoreCase(Constants.IN) || direction.equalsIgnoreCase(Constants.IN_OUT)) {
			// If the distance is equal to 1  
			if(distance==1) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(o1,p1,s1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
			// If the distance is equal to 2
			else if(distance==2) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(o2,p1,o1);
				Triple t2 = new Triple(s2,p2,s1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
			// If the distance is equal to 3			
			else if(distance==3) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				Node s3 = o2;
				Node p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				Node o3 = NodeFactory.createVariable("n2");
				filters.addAll(this.instantiateFilters("n2", Constants.OBJECT));
				Triple t1 = new Triple(o3,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,s1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("n2");
				temp.addAll(this.nodesByPattern(pattern));
			}
		}
		result.addAll(temp);
		return result;	
	}
	
	
	public Vector<Path> paths_2(Node n1, Node n2, int minLength, int maxLength, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		
		int neigh_1 = neighbours(n1, 1, Constants.IN_OUT).size();
		int neigh_2 = neighbours(n2, 1, Constants.IN_OUT).size();
		
		System.out.println(neigh_1+" -- "+neigh_2);
		
		Node n_start = null;
		Node n_end = null;
		
		if(neigh_1 < neigh_2) {
			n_start = n1;
			n_end = n2;
		}
		else {
			n_start = n2;
			n_end = n1;
		}
			
		Vector<Path> paths1_1 = new Vector<Path>();
		Vector<Path> paths2_1 = new Vector<Path>();
		
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();		
		Node p1 = NodeFactory.createVariable("p1");
		filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
		Node nx = NodeFactory.createVariable("nx1");

		int i = 0;
		
//		1.1. path pattern = n--(p1)->nx (0)
		filters.addAll(this.instantiateFilters("nx", Constants.OBJECT));
		Triple t1 = new Triple(n_start,p1,nx);
		pattern.getTriples().add(t1);
		pattern.setFilters(filters);
		Filter f = new Filter();
		f.mustBeDifferent(nx, NodeFactory.createURI(Constants.OWL_THING));
		pattern.getFilters().add(f);
		paths1_1.addAll(this.pathsByPattern(pattern, acyclic));

		Vector<Path> paths1_2 = new Vector<Path>();
		for(Path path:paths1_1) {
			System.out.println(i++);
			paths1_2 = paths(path.getTriples().get(0).getObject(), n_end, 1, maxLength-1, mode, acyclic);
	
			for(Path p:paths1_2) {
				Path resultingPath = new Path();
				resultingPath.setTriples(path.getTriples());
				resultingPath.setPropertyPathAdornments(path.getPropertyPathAdornments());
				resultingPath.getTriples().addAll(p.getTriples());
				resultingPath.getPropertyPathAdornments().addAll(p.getPropertyPathAdornments());
				
				result.add(resultingPath);
			}
		}
		
		// 1.2. path pattern = n<-(p1)--nx (1)
		filters.addAll(this.instantiateFilters("p1", Constants.SUBJECT));
		t1 = new Triple(nx,p1,n_start);
		pattern = new PathPattern(null);
		pattern.getTriples().add(t1);
		pattern.setFilters(filters);
		pattern.getFilters().add(f);
		paths2_1.addAll(this.pathsByPattern(pattern, acyclic));

		Vector<Path> paths2_2 = new Vector<Path>();
		for(Path path:paths2_1) {
			System.out.println(i++);
			paths2_2 = paths(path.getTriples().get(0).getSubject(), n_end, 1, maxLength-1, mode, acyclic);
	
			for(Path p:paths2_2) {
				Path resultingPath = new Path();
				resultingPath.setTriples(path.getTriples());
				resultingPath.getTriples().addAll(p.getTriples());
				result.add(resultingPath);
			}
		}		
		return result;
	}
	
	public int countTriplesWithPredicate(Node p) {
		int result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Node nx1 = NodeFactory.createVariable("nx1");
		Node nx2 = NodeFactory.createVariable("nx2");
		Triple t = new Triple(nx1, p, nx2);
		
		pattern.getTriples().add(t);

		result = this.countPathsByPattern(pattern);
		
		return result;
	}
	
	public int countTriplesWithSubject(Node s) {
		int result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Node px = NodeFactory.createVariable("px");
		Triple t = new Triple(s, px, nx);
		
		pattern.getTriples().add(t);

		result = this.countPathsByPattern(pattern);
		
		return result;
	}
	
	public int countTriplesWithObject(Node o) {
		int result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Node px = NodeFactory.createVariable("px");
		Triple t = new Triple(nx, px, o);
		
		pattern.getTriples().add(t);

		result = this.countPathsByPattern(pattern);
		
		return result;
	}
	
	public int countTriplesWithSubject_Object(Node s, Node o) {
		int result = 0;
		
		PathPattern pattern = new PathPattern(null);
		Node px = NodeFactory.createVariable("px");
		Triple t = new Triple(s, px, o);
		
		pattern.getTriples().add(t);

		result = this.countPathsByPattern(pattern);
		
		return result;
	}
	
	public Vector<QuerySolution> execQuery(String queryString) {
		if(this.getGraph().equalsIgnoreCase("") || this.getGraph() == null)
			queryString = queryString.replace("$$$graph$$$", graph);
		else
			queryString = queryString.replace("$$$graph$$$", "<"+graph+">");		
		return SPARQLQueryCollector.execQuery(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), queryString);
	}
	
	
	/**
	 * Search and retrieve paths connecting n1 and n2.
	 * @param n1 {@link Node} a node at the extremity of the searched paths
	 * @param n2 {@link Node} a node at the extremity of the searched paths
	 * @param length The length of the searched paths that must be 1, 2 or 3.
	 * @param mode {@link Constants.DIRECTED_PATH} or {@link Constants.NO_DIRECTED_PATH} for assuming the graph as directed or not, respectively.
	 * @param acyclic if the retrieved p aths must be acyclic {@link Constraints.ACYCLIC} or not {@link Constraints.ACYCLIC} 
	 * @return {@link Vector} of {@link Path} representing the paths connecting he two nodes.
	 */
	public Vector<Path> paths_3(Node n1, Node n2, int length, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		String pathKey = n1.getURI()+n2.getURI()+length+mode+acyclic;
		result = this.getCache().getPaths(pathKey);
		if(result.size() == 0) {
			Vector<Path> temp = new Vector<Path>(); 
			Set<Filter> filters = new HashSet<Filter>();
			// If the searched paths must be straight from n1 to n2, 
			if(mode.equals(Constants.DIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 3			
				else if(length==3) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					Node s3 = o2;
					Node p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					Node o3 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					Triple t3 = new Triple(s3,p3,o3);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
			}
	
			// if the edges (predicates) are considered not directed (they can be traversed in any direction)
			if(mode.equals(Constants.UNDIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
	//				1.1. path pattern = n1--(p1)->n2 (0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 1.2. path pattern = u1<-(p1)--u2 (1)
					s1 = n2;
					o1 = n1;
					t1 = new Triple(s1,p1,o1);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					// 2.1. path pattern = n1--(p1)->u1--(p2)->n2 (0-0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 2.2. path pattern = n1--(p1)->u1<-(p2)--n2 (0-1)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = o1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);	
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.3. path pattern = n1<-(p1)--u1--(p2)->n2 (1-0)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = s1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.4. path pattern = n1<-(p1)--u1<-(p2)--n2 (1-1)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = s1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
				}
				// If the length of the searched paths is equal to 3			
				if(length>=3) {
					System.out.println("3.1");
					// 3.1. path pattern = n1--(p1)->u1--(p2)->u2--(p3)->n2 (0-0-0)
					Set<Filter> filters1 = new HashSet<Filter>();
					Set<Filter> filters2 = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					Node s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					Node p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					Node o3 = n2;
					
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					Vector<Path> paths1 = this.pathsByPattern(pattern1, acyclic);
					
					Triple t2 = new Triple(s3,p3,o3);
					PathPattern pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					Vector<Path> paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getObject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getSubject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt1_1,pxx,pt2_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}
					
//					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 3.2. path pattern = n1--(p1)->u1--(p2)->u2<-(p3)--n2 (0-0-1)
					System.out.println("3.2");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(s1,p1,o1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(o3,p3,s3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getObject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getObject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt1_1,pxx,pt2_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}
					
					// 3.3 path pattern = n1--(p1)->u1<-(p2)--u2--(p3)->n2 (0-1-0)
					System.out.println("3.3");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(s1,p1,o1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(s3,p3,o3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getObject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getSubject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt2_1,pxx,pt1_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}
					
					// 3.4 path pattern = n1--(p1)->u1<-(p2)--u2<-(p3)--n2 (0-1-1)
					System.out.println("3.4");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(s1,p1,o1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(o3,p3,s3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getObject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getObject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt2_1,pxx,pt1_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}					
					// 3.5 path pattern = n1<-(p1)--u1--(p2)->u2--(p3)->n2 (1-0-0)
					System.out.println("3.5");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(o1,p1,s1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(s3,p3,o3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getSubject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getSubject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt1_1,pxx,pt2_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}					
					// 3.6 path pattern = n1<-(p1)--u1--(p2)->u2<-(p3)--n2 (1-0-1)
					System.out.println("3.6");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(o1,p1,s1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(o3,p3,s3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getSubject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getObject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt1_1,pxx,pt2_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}					
					// 3.7 path pattern = n1<-(p1)--u1<-(p2)--u2--(p3)->n2 (1-1-0)
					System.out.println("3.7");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(o1,p1,s1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(s3,p3,o3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getSubject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getSubject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt2_1,pxx,pt1_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}					
					// 3.8 path pattern = n1<-(p1)--u1<-(p2)--u2<-(p3)--n2 (1-1-1)
					System.out.println("3.8");
					filters1 = new HashSet<Filter>();
					filters2 = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters1.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters1.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					
					s3 = NodeFactory.createVariable("u2");
					filters2.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					p3 = NodeFactory.createVariable("p3");
					filters2.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					o3 = n2;
					
					t1 = new Triple(o1,p1,s1);
					pattern1 = new PathPattern(null);
					pattern1.getTriples().add(t1);
					pattern1.setFilters(filters1);
					paths1 = this.pathsByPattern(pattern1, acyclic);
					
					t2 = new Triple(o3,p3,s3);
					pattern2 = new PathPattern(null);
					pattern2.getTriples().add(t2);
					pattern2.setFilters(filters2);
					paths2 = this.pathsByPattern(pattern2, acyclic);
					System.out.println(paths1.size() + " --- "+paths2.size());
					
					for(Path pt1:paths1) {
						Node pt1_1 = pt1.getTriples().get(0).getSubject();
						for(Path pt2:paths2) {
							Node pt2_1 = pt2.getTriples().get(0).getObject();
							if(!(pt1_1.getURI().toString().equalsIgnoreCase(pt2_1.getURI().toString()))) {
								Node pxx = NodeFactory.createVariable("pxx");
								PathPattern patternxx = new PathPattern(null);
								Triple txx = new Triple(pt2_1,pxx,pt1_1);
								Set<Filter> filtersxx = new HashSet<Filter>();
								filtersxx.addAll(this.instantiateFilters("pxx", Constants.PREDICATE));
								patternxx.setFilters(filtersxx);
								patternxx.getTriples().add(txx);
								Vector<Path> pathsxx = this.pathsByPattern(patternxx, acyclic);

								for(Path ppp:pathsxx) {
									Path path_in = new Path();
									path_in.getTriples().add(pt1.getTriples().get(0));
									path_in.getTriples().add(ppp.getTriples().get(0));
									path_in.getTriples().add(pt2.getTriples().get(0));
									temp.add(path_in);
								}
							}
						}
					}				}
				
			}
			if(acyclic)
				for(Path p:temp)
					if(p.isAcyclic())
						result.add(p);
					else;
			else result = temp;
			
			this.getCache().getPaths().put(pathKey, result);
		}
		return result;
	}
	
	
	
	
	/**
	 * Search and retrieve paths connecting n1 and n2.
	 * @param n1 {@link Node} a node at the extremity of the searched paths
	 * @param n2 {@link Node} a node at the extremity of the searched paths
	 * @param length The length of the searched paths that must be 1, 2 or 3.
	 * @param mode {@link Constants.DIRECTED_PATH} or {@link Constants.NO_DIRECTED_PATH} for assuming the graph as directed or not, respectively.
	 * @param acyclic if the retrieved p aths must be acyclic {@link Constraints.ACYCLIC} or not {@link Constraints.ACYCLIC} 
	 * @return {@link Vector} of {@link Path} representing the paths connecting he two nodes.
	 */
	public Vector<Path> paths_3_new(Node n1, Node n2, int length, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		String pathKey = n1.getURI()+n2.getURI()+length+mode+acyclic;
		result = this.getCache().getPaths(pathKey);
		if(result.size() == 0) {
			Vector<Path> temp = new Vector<Path>(); 
			Set<Filter> filters = new HashSet<Filter>();
			// If the searched paths must be straight from n1 to n2, 
			if(mode.equals(Constants.DIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 3			
				else if(length==3) {
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = NodeFactory.createVariable("u2");
					filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
					Node s3 = o2;
					Node p3 = NodeFactory.createVariable("p3");
					filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
					Node o3 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					Triple t3 = new Triple(s3,p3,o3);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.getTriples().add(t3);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
			}
	
			// if the edges (predicates) are considered not directed (they can be traversed in any direction)
			if(mode.equals(Constants.UNDIRECTED_PATH)) {
				// If the length of the searched paths is equal to 1  
				if(length==1) {
	//				1.1. path pattern = n1--(p1)->n2 (0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 1.2. path pattern = u1<-(p1)--u2 (1)
					s1 = n2;
					o1 = n1;
					t1 = new Triple(s1,p1,o1);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
				}
				// If the length of the searched paths is equal to 2
				else if(length==2) {
					// 2.1. path pattern = n1--(p1)->u1--(p2)->n2 (0-0)
					filters = new HashSet<Filter>();
					Node s1 = n1;
					Node p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					Node o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					Node s2 = o1;
					Node p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					Node o2 = n2;
					Triple t1 = new Triple(s1,p1,o1);
					Triple t2 = new Triple(s2,p2,o2);
					PathPattern pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
					// 2.2. path pattern = n1--(p1)->u1<-(p2)--n2 (0-1)
					filters = new HashSet<Filter>();
					s1 = n1;
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = o1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);	
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.3. path pattern = n1<-(p1)--u1--(p2)->n2 (1-0)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = s1;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = n2;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
					
					// 2.4. path pattern = n1<-(p1)--u1<-(p2)--n2 (1-1)
					filters = new HashSet<Filter>();
					s1 = NodeFactory.createVariable("u1");
					filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
					p1 = NodeFactory.createVariable("p1");
					filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
					o1 = n1;
					s2 = n2;
					p2 = NodeFactory.createVariable("p2");
					filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
					o2 = s1;
					t1 = new Triple(s1,p1,o1);
					t2 = new Triple(s2,p2,o2);
					pattern = new PathPattern(null);
					pattern.getTriples().add(t1);
					pattern.getTriples().add(t2);
					pattern.setFilters(filters);
					temp.addAll(this.pathsByPattern(pattern, acyclic));
	
				}
				// If the length of the searched paths is equal to 3			
				if(length>=3) {
					Vector<Path> v11 = new Vector<Path>();
					Vector<Path> v12 = new Vector<Path>();
					Vector<Path> v21 = new Vector<Path>();
					Vector<Path> v22 = new Vector<Path>();
				
					Set<Filter> filters1 = new HashSet<Filter>();
					Set<Filter> filters2 = new HashSet<Filter>();
					Node nv1 = NodeFactory.createVariable("nv1");
					Node nv2 = NodeFactory.createVariable("nv2");
					Node pv1 = NodeFactory.createVariable("np1");
					Node pv2 = NodeFactory.createVariable("np2");
					filters1.addAll(this.instantiateFilters("nv1", Constants.OBJECT));
					filters1.addAll(this.instantiateFilters("nv2", Constants.OBJECT));
					filters1.addAll(this.instantiateFilters("pv1", Constants.PREDICATE));
					filters1.addAll(this.instantiateFilters("pv2", Constants.PREDICATE));
					
					Triple t1 = new Triple(n1, pv1, nv1);
					Triple t2 = new Triple(nv1, pv2, nv2);
					PathPattern pp = new PathPattern(null);
					pp.setFilters(filters1);
					pp.getTriples().add(t1);
					pp.getTriples().add(t2);
					System.out.println("Loading v11");
					v11 = this.pathsByPattern(pp, acyclic);
					System.out.println(v11.size());
					
					t1 = new Triple(nv1, pv1, n1);
					t2 = new Triple(nv1, pv2, nv2);
					pp = new PathPattern(null);
					pp.setFilters(filters1);
					pp.getTriples().add(t1);
					pp.getTriples().add(t2);
					System.out.println("Loading v12");
					v12 = this.pathsByPattern(pp, acyclic);
					System.out.println(v12.size());
					
					t1 = new Triple(nv1, pv1, nv2);
					t2 = new Triple(nv2, pv2, n2);
					pp = new PathPattern(null);
					pp.setFilters(filters1);
					pp.getTriples().add(t1);
					pp.getTriples().add(t2);
					System.out.println("Loading v21");
					v21 = this.pathsByPattern(pp, acyclic);
					System.out.println(v21.size());					
					t1 = new Triple(nv1, pv1, nv2);
					t2 = new Triple(n2, pv2, nv2);
					pp = new PathPattern(null);
					pp.setFilters(filters1);
					pp.getTriples().add(t1);
					pp.getTriples().add(t2);
					System.out.println("Loading v22");
					v22 = this.pathsByPattern(pp, acyclic);
					System.out.println(v22.size());					
					System.out.println("1");
					
					for(Path ppp1:v11) {
						for(Path ppp2:v21) {
							if(ppp1.getTriples().get(1).getSubject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getSubject().getURI().toString()) && 
									ppp1.getTriples().get(1).getObject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getObject().getURI().toString())) {
								Path p_new = new Path();
								p_new.getTriples().add(ppp1.getTriples().get(0));
								p_new.getTriples().add(ppp1.getTriples().get(1));
								p_new.getTriples().add(ppp2.getTriples().get(1));
								temp.add(p_new);
							}
						}
					}
					
					System.out.println("2");
					for(Path ppp1:v11) {
						for(Path ppp2:v22) {
							if(ppp1.getTriples().get(1).getSubject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getSubject().getURI().toString()) && 
									ppp1.getTriples().get(1).getObject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getObject().getURI().toString())) {
								Path p_new = new Path();
								p_new.getTriples().add(ppp1.getTriples().get(0));
								p_new.getTriples().add(ppp1.getTriples().get(1));
								p_new.getTriples().add(ppp2.getTriples().get(1));
								temp.add(p_new);
							}
						}
					}
							
					System.out.println("3");
					for(Path ppp1:v12) {
						for(Path ppp2:v21) {
							if(ppp1.getTriples().get(1).getSubject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getSubject().getURI().toString()) && 
									ppp1.getTriples().get(1).getObject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getObject().getURI().toString())) {
								Path p_new = new Path();
								p_new.getTriples().add(ppp1.getTriples().get(0));
								p_new.getTriples().add(ppp1.getTriples().get(1));
								p_new.getTriples().add(ppp2.getTriples().get(1));
								temp.add(p_new);
							}
						}
					}
					
					System.out.println("4");
					for(Path ppp1:v12) {
						for(Path ppp2:v22) {
							if(ppp1.getTriples().get(1).getSubject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getSubject().getURI().toString()) && 
									ppp1.getTriples().get(1).getObject().getURI().toString().equalsIgnoreCase(ppp2.getTriples().get(0).getObject().getURI().toString())) {
								Path p_new = new Path();
								p_new.getTriples().add(ppp1.getTriples().get(0));
								p_new.getTriples().add(ppp1.getTriples().get(1));
								p_new.getTriples().add(ppp2.getTriples().get(1));
								temp.add(p_new);
							}
						}
					}
				}
									
			}
			if(acyclic)
				for(Path p:temp)
					if(p.isAcyclic())
						result.add(p);
					else;
			else result = temp;
			
			this.getCache().getPaths().put(pathKey, result);
		}
		return result;
	}	
	
	public Set<Triple> allTriples() {
//		System.out.println("countAllTriples");
		Set<Triple> result = new HashSet<Triple>();
		
		Node s1, o1, p1;
		PathPattern pattern = new PathPattern(new Vector<String>());
					
		s1 = NodeFactory.createVariable("u1");
		o1 = NodeFactory.createVariable("u2");
		p1 = NodeFactory.createVariable("p1");
		
		Triple t = new Triple(s1, p1, o1);
		pattern.getTriples().addElement(t);
		
		Vector<Path> temp = this.pathsByPattern(pattern, true);
		
		for(Path p:temp)
			result.add(p.getTriples().elementAt(0));
		return result;

	}
}
