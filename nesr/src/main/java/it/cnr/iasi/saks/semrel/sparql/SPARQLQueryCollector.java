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
package it.cnr.iasi.saks.semrel.sparql;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class SPARQLQueryCollector {
	
	/**
	 * Get the nodes filling a given pattern, on a given knowledgeSourceRef and a given graph
	 * Only nodes for which isVariable() == true are returned. 
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern A PathPattern representing the search criteria.
	 * @return
	 */
	public static Vector<Node> nodesByPattern(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		Vector<Node> result = new Vector<Node>();
				
		Set<String> vars = new HashSet<String>();
		
		// these variables will form the WHERE clause
		String whereClause = "";
		
		// this variable say what to SELECT and return as result
		String varToSelect = "";  
				
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// Treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			// if the node is a variable of which the value is requested as result then ...
			if(s.isVariable()) { 				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+s.toString()+" ";
				vars.add(s.toString());
			}
			else if(s.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+s.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+s.toString()+"> ";

			// Treat the predicate
			Node p = pattern.getTriples().get(i).getPredicate();
			// if the node is a variable of which the value is requested as result then ...
			if(p.isVariable()) 
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+p.toString()+" ";
			else if(p.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+p.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+p.toString()+"> ";

			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = pattern.getTriples().get(i).getObject();
			// if the node is a variable of which the value is requested as result then ...
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			else if(o.isVariable()) {
				vars.add(o.toString());
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+o.toString()+" ";
			}
			else if(o.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+o.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+o.toString()+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
//			else whereClause = whereClause + " \n ";
		}
		
		// build a string representing all the filters defined for the pattern
		String filterConstraints = pattern.filtersToString();
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";
		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		

		varToSelect = "?"+pattern.getVarsToSelect().get(0);
		
		String queryString = "SELECT "+pattern.getDistinct()+" "+varToSelect+" "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause
					+	filterConstraints
					+ "}";				
//		System.out.println(queryString);
		
		Vector<QuerySolution> query_results = sc.execQuery(queryString, knowledgeSourceRef);
		
		for(int i=0; i<query_results.size(); i++) {
			RDFNode node = query_results.elementAt(i).get(varToSelect);
			result.add(node.asNode());
		}

//		System.out.println(result.size());
		return result;
	}
	
	
	/**
	 * Count the nodes filling to a given pattern, on a given knowledgeSourceRef and a given graph 
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern A PathPattern representing the search criteria.
	 * @param distinct {@link Constants.SPARQL_DISTINCT} or {@link Constants.SPARQL_NOT_DISTINCT} 
	 * @return
	 */
	public static int countNodesByPattern(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		int result = 0;
		
		Set<String> vars = new HashSet<String>();
		
		// these variables will form the WHERE clause
		String whereClause = "";
						
		// collect what to SELECT
		String varToSelect = "";
		
		// this for loop if for building the whereClause and to collect the variables to SELECT  
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// Treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			// if the node is a variable of which the value is requested as result then ...
			if(s.isVariable()) {				// ... is added as subject of a triple of the WHERE clause
				vars.add(s.toString());
				whereClause = whereClause + " "+s.toString()+" ";
			}
			else whereClause = whereClause + " <"+s.toString()+"> ";

			// Treat the predicate
			Node p = pattern.getTriples().get(i).getPredicate();
			// if the node is a variable of which the value is requested as result then ...
			if(p.isVariable()) 
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+p.toString()+" ";
			else whereClause = whereClause + " <"+p.toString()+"> ";

			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = pattern.getTriples().get(i).getObject();
			// if the node is a variable of which the value is requested as result then ...
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			else if(o.isVariable()) {				// ... is added as subject of a triple of the WHERE clause
				vars.add(o.toString());
				whereClause = whereClause + " "+o.toString()+" ";
			}
			else whereClause = whereClause + " <"+o.toString()+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
//			else whereClause = whereClause + " \n ";
		}
		
		String selectVars = " ?"+pattern.getVarsToSelect().get(0);

		// build a string representing all the filters
		String filterConstraints = pattern.filtersToString();
		
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";

		
		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		
		
		String queryString = "SELECT (COUNT( "+pattern.getDistinct()+" "+selectVars+" ) AS ?count) "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause
					+	filterConstraints
					+ "}";		
		
//		System.out.println(queryString);
		
		Vector<QuerySolution> query_results = sc.execQuery(queryString, knowledgeSourceRef);
		
		if(query_results.size()>0)
			result = query_results.elementAt(0).getLiteral("count").asLiteral().getInt();

		return result;
	}

	/**
	 * Get the number of paths matching a given pattern, on a given knowledgeSourceRef and graph.
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern a PathPattern representing the search criteria
	 * @return
	 */
	public static int countPathsByPattern(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		int result = 0;
		 
		Set<String> vars = new HashSet<String>();
		
		String whereClause = "";
		
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			if(s.isVariable()) {
				vars.add(s.toString());
				whereClause = whereClause + " "+s.toString()+" ";
			}
			else
				whereClause = whereClause + " <"+s.toString()+"> "; 

			// treat the predicate			
			Node p = pattern.getTriples().get(i).getPredicate();
			if(p.isVariable())
				whereClause = whereClause + " "+p+" ";
			else 
				whereClause = whereClause + " <"+p+"> ";
			
			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// treat the object
			Node o = pattern.getTriples().get(i).getObject();
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			else if(o.isVariable()) {
				vars.add(o.toString());
				whereClause = whereClause + " "+o+" ";
			}
			else 
				whereClause = whereClause + " <"+o+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
		}
							
		// build a string representing all the filters
		String filterConstraints = pattern.filtersToString();
		
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";

		
		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		
		
		String queryString = " SELECT (COUNT(*) AS ?count) "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause 
					+	filterConstraints
					+ "}";		
		
//		System.out.println(queryString);
		Vector<QuerySolution> query_results = sc.execQuery(queryString, knowledgeSourceRef);
		if(query_results.size()>0)
			result = query_results.elementAt(0).getLiteral("count").asLiteral().getInt();
		return result;
	}
	
	/**
	 * The method returns all the paths matching the given PathPattern.
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern a PathPattern
	 * @return
	 */
	public static Vector<Path> pathsByPathPattern(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		Vector<Path> result = new Vector<Path>();

		Set<String> vars = new HashSet<String>();
		
		// form the WHERE clause
		String whereClause = "";
		
		// collect what to SELECT
		String selectVars = "";

		// this for loop if for building the whereClause and to collect the variables to SELECT  
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// Treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			// if the node is a variable of which the value is requested as result then ...
			if(s.isVariable()) {
				vars.add(s.toString());
				// ... is added as subject of a triple in the WHERE clause
				whereClause = whereClause + " "+s.toString()+" ";
				// when searching for paths, all the variables MUST be in the select
				selectVars = selectVars +s.toString()+" ";
			}
			else whereClause = whereClause + " <"+s.toString()+"> ";

			// Treat the predicate
			Node p = pattern.getTriples().get(i).getPredicate();
			// if the node is a variable of which the value is requested as result then ...
			if(p.isVariable()) {
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+p.toString()+" ";
				// when searching for paths, all the variables MUST be in the select
				selectVars = selectVars + p.toString()+" ";
			}
			else whereClause = whereClause + " <"+p.toString()+"> ";

			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = pattern.getTriples().get(i).getObject();
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			// if the node is a variable of which the value is requested as result then ...
			else if(o.isVariable()) {
				vars.add(o.toString());
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+o.toString()+" ";
				// when searching for paths, all the variables MUST be in the select
				selectVars = selectVars +o.toString()+" ";
			}
			else whereClause = whereClause + " <"+o.toString()+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
//			else whereClause = whereClause + " \n ";
		}
				
		// build a string representing all the filters
		String filterConstraints = pattern.filtersToString();
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";

		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		

		
		String queryString = "SELECT "+selectVars+" "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause
					+	filterConstraints
					+ "}";		
			
//		System.out.println(queryString);

		Vector<QuerySolution> query_results = sc.execQuery(queryString, knowledgeSourceRef); 
		
		for(int i=0; i<query_results.size(); i++) {
			Path path_result = new Path(); 
			for(Triple t:pattern.getTriples()) {
				Node s_result;
				Node o_result;
				Node p_result;

				if(t.getSubject().isVariable())
					s_result = query_results.elementAt(i).get(t.getSubject().toString()).asNode();
				else s_result = t.getSubject();
				
				if(t.getObject().isVariable())
					o_result = query_results.elementAt(i).get(t.getObject().toString()).asNode();
				else o_result = t.getObject();
					
				if(t.getPredicate().isVariable())
					p_result = query_results.elementAt(i).get(t.getPredicate().toString()).asNode();
				else p_result = t.getPredicate();
				
				Triple t_result = new Triple(s_result, p_result, o_result);
				
				path_result.getTriples().add(t_result);
			}
				
			result.add(path_result);
		}
		
		return result;
	}
	
	/**
	 * Check if a given PathPattern has an instance in the graph.
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern a PathPattern
	 * @return
	 */
	public static boolean pathsExistence(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		boolean result = false;

		Set<String> vars = new HashSet<String>();
		
		// form the WHERE clause
		String whereClause = "";
		
		// this for loop if for building the whereClause and to collect the variables to SELECT  
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// Treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			// if the node is a variable of which the value is requested as result then ...
			if(s.isVariable()) {
				vars.add(s.toString());
				// ... is added as subject of a triple in the WHERE clause
				whereClause = whereClause + " "+s.toString()+" ";
			}
			else whereClause = whereClause + " <"+s.toString()+"> ";

			// Treat the predicate
			Node p = pattern.getTriples().get(i).getPredicate();
			// if the node is a variable of which the value is requested as result then ...
			if(p.isVariable())
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+p.toString()+" ";
			else whereClause = whereClause + " <"+p.toString()+"> ";

			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = pattern.getTriples().get(i).getObject();
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			// if the node is a variable of which the value is requested as result then ...
			else if(o.isVariable()) {
				vars.add(o.toString());
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+o.toString()+" ";
			}
			else whereClause = whereClause + " <"+o.toString()+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
		}
				
		// build a string representing all the filters
		String filterConstraints = pattern.filtersToString();
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";

		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		

		
		String queryString = "ASK "+graphFilter+" WHERE "
				+ "{"
				+ 	whereClause
				+	filterConstraints
				+ "}";		
		
		result = sc.execAsk(queryString, knowledgeSourceRef); 
				
		return result;
	}
	
	/**
	 * The method returns all the paths matching the given PathPattern.
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern a PathPattern
	 * @return
	 */
	public static boolean pathExistence(SPARQLConnector sc, Object knowledgeSourceRef, String graph, Path path) {
		boolean result = false;

		Set<String> vars = new HashSet<String>();
		
		// form the WHERE clause
		String whereClause = "";
		
		// this for loop if for building the whereClause  
		for(int i=0; i<path.getTriples().size(); i++) {
			// Treat the subject
			Node s = path.getTriples().get(i).getSubject();
			whereClause = whereClause + " <"+s.toString()+"> ";
			if(s.isVariable())
				vars.add(s.toString());
			// Treat the predicate
			Node p = path.getTriples().get(i).getPredicate();
			whereClause = whereClause + " <"+p.toString()+"> ";
			whereClause = whereClause + path.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = path.getTriples().get(i).getObject();
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			else whereClause = whereClause + " <"+o.toString()+"> ";
			if(o.isVariable())
				vars.add(o.toString());
			
			if(i<path.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
		}
		
		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		
		
		String filterConstraints = "";
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";

		
		String queryString = "ASK "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause
					+ 	filterConstraints
					+ "}";		
		
		result = sc.execAsk(queryString, knowledgeSourceRef); 
				
		return result;
	}

	/**
	 * Search and retrieve paths connecting n1 and n2.
	 * If length is equal to 1 it is allowed to constrain variable p1.
	 * If length is equal to 2 it is allowed to constrain variables p1, p2, u1.
	 * If length is equal to 3 it is allowed to constrain variables p1, p2, p3, u1, u2.
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param n1 {@link Node} a node at the extremity of the searched paths
	 * @param n2 {@link Node} a node at the extremity of the searched paths
	 * @param length The length of the searched paths that must be 1, 2 or 3.
	 * @param mode {@link Constants.DIRECTED_PATH} or {@link Constants.NO_DIRECTED_PATH} for assuming the graph as directed or not, respectively.
	 * @param filters {@link Set} of {@link Filter} an additional search criteria 
	 * @return {@link Vector} of {@link Path} representing the paths connecting he two nodes.
	 */
/*	public static Vector<Path> getPaths(SPARQLConnector sc, Object knowledgeSourceRef, String graph, Node n1, Node n2, int length, String mode, Set<Filter> filters) {
		Vector<Path> result = new Vector<Path>();
		// If the searched paths must be straight from n1 to n2, 
		if(mode==Constants.DIRECTED_PATH) {
			// If the length of the searched paths is equal to 1  
			if(length==1) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getVarsToSelect().add("p1");
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
			}
			// If the length of the searched paths is equal to 2
			else if(length==2) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = NodeFactory.createVariable("u1");
				Node s2 = NodeFactory.createVariable("u1");
				Node p2 = NodeFactory.createVariable("p2");
				Node o2 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
			}
			// If the length of the searched paths is equal to 3			
			else if(length==3) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = NodeFactory.createVariable("u1");
				Node s2 = NodeFactory.createVariable("u1");
				Node p2 = NodeFactory.createVariable("p2");
				Node o2 = NodeFactory.createVariable("u2");
				Node s3 = NodeFactory.createVariable("u2");
				Node p3 = NodeFactory.createVariable("p3");
				Node o3 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,o3);
				PathPattern pattern = new PathPattern(null);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
			}

		}

		// if the edges (predicates) are considered not directed (they can be traversed in any direction)
		if(mode==Constants.NOT_DIRECTED_PATH) {
			// If the length of the searched paths is equal to 1  
			if(length==1) {
//				1.1. path pattern = n1--(p1)->n2 (0)
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
//				1.2. path pattern = u1<-(p1)--u2 (1)
				s1 = n2;
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				t1 = new Triple(s1,p1,o1);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
			}

			// If the length of the searched paths is equal to 2
			else if(length==2) {
				// 2.1. path pattern = n1--(p1)->u1--(p2)->n2 (0-0)
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = NodeFactory.createVariable("u1");
				Node s2 = NodeFactory.createVariable("u1"); 
				Node p2 = NodeFactory.createVariable("p2");
				Node o2 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
				// 2.2. path pattern = n1--(p1)->u1<-(p2)--n2 (0-1)
				s1 = n1;
				p1 = NodeFactory.createVariable("p1"); 
				o1 = NodeFactory.createVariable("u1");
				s2 = n2;
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 2.3. path pattern = n1<-(p1)--u1--(p2)->n2 (1-0)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = NodeFactory.createVariable("u1");
				p2 = NodeFactory.createVariable("p2");
				o2 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 2.4. path pattern = n1<-(p1)--u1<-(p2)--n2 (1-1)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = n2;
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
			}

			// If the length of the searched paths is equal to 3			
			if(length>=3) {
				// 3.1. path pattern = n1--(p1)->u1--(p2)->u2--(p3)->n2 (0-0-0)
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				Node o1 = NodeFactory.createVariable("u1");
				Node s2 = NodeFactory.createVariable("u1");
				Node p2 = NodeFactory.createVariable("p2");
				Node o2 = NodeFactory.createVariable("u2");
				Node s3 = NodeFactory.createVariable("u2");
				Node p3 = NodeFactory.createVariable("p3");
				Node o3 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,o3);
				PathPattern pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result = pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern);
				// 3.2. path pattern = n1--(p1)->u1--(p2)->u2<-(p3)--n2 (0-0-1)
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				o1 = NodeFactory.createVariable("u1");
				s2 = NodeFactory.createVariable("u1");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u2");
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				o3 = NodeFactory.createVariable("u2");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.3 path pattern = n1--(p1)->u1<-(p2)--u2--(p3)->n2 (0-1-0)
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				o1 = NodeFactory.createVariable("u1");
				s2 = NodeFactory.createVariable("u2");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				s3 = NodeFactory.createVariable("u2");
				p3 = NodeFactory.createVariable("p3");
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.4 path pattern = n1--(p1)->u1<-(p2)--u2<-(p3)--n2 (0-1-1)
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				o1 = NodeFactory.createVariable("u1");
				s2 = NodeFactory.createVariable("u2");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				o3 = NodeFactory.createVariable("u2");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.5 path pattern = n1<-(p1)--u1--(p2)->u2--(p3)->n2 (1-0-0)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = NodeFactory.createVariable("u1");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u2");
				s3 = NodeFactory.createVariable("u2");
				p3 = NodeFactory.createVariable("p3");
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.6 path pattern = n1<-(p1)--u1--(p2)->u2<-(p3)--n2 (1-0-1)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = NodeFactory.createVariable("u1");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u2");
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				o3 = NodeFactory.createVariable("u2");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.7 path pattern = n1<-(p1)--u1<-(p2)--u2--(p3)->n2 (1-1-0)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = NodeFactory.createVariable("u2");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				s3 = NodeFactory.createVariable("u2");
				p3 = NodeFactory.createVariable("p3");
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
				// 3.8 path pattern = n1<-(p1)--u1<-(p2)--u2<-(p3)--n2 (1-1-1)
				s1 = NodeFactory.createVariable("u1");
				p1 = NodeFactory.createVariable("p1");
				o1 = n1;
				s2 = NodeFactory.createVariable("u2");
				p2 = NodeFactory.createVariable("p2");
				o2 = NodeFactory.createVariable("u1");
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				o3 = NodeFactory.createVariable("u2");
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern(null);
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				pattern.getVarsToSelect().add("p1");
				pattern.getVarsToSelect().add("u1");
				pattern.getVarsToSelect().add("p2");
				pattern.getVarsToSelect().add("u2");
				pattern.getVarsToSelect().add("p3");
				result.addAll(pathsByPathPattern(sc, knowledgeSourceRef, graph, pattern));
			}
		}
		return result;
	}
*/	
	
	/**
	 * The method returns true if the graph already exists.
	 * @param knowledgeSourceRef
	 * @param graph the graph to be checked
	 * @return
	 */
	public static boolean graphExistence(SPARQLConnector sc, Object knowledgeSourceRef, String graph) {
		boolean result = false;
	
		String queryString = "ASK WHERE { GRAPH <"+graph+"> { ?s ?p ?o } }";		
		
		result = sc.execAsk(queryString, knowledgeSourceRef); 
				
		return result;
	}
	
	public static Vector<QuerySolution> execQuery(SPARQLConnector sc, Object knowledgeSourceRef, String graph, String queryString) {
		return sc.execQuery(queryString, knowledgeSourceRef);
	}
	
	/**
	 * Get the nodes filling a given pattern, on a given knowledgeSourceRef and a given graph
	 * Only nodes for which isVariable() == true are returned. 
	 * @param knowledgeSourceRef
	 * @param graph
	 * @param pattern A PathPattern representing the search criteria.
	 * @return
	 */
	public static Vector<Vector<Node>> predicatesAndNodesByPattern(SPARQLConnector sc, Object knowledgeSourceRef, String graph, PathPattern pattern) {
		Vector<Vector<Node>> result = new Vector<Vector<Node>>();
				
		Set<String> vars = new HashSet<String>();
		
		// these variables will form the WHERE clause
		String whereClause = "";
		
		// this variable say what to SELECT and return as result
		String varsToSelect_concatenated = "";  
				
		for(int i=0; i<pattern.getTriples().size(); i++) {
			// Treat the subject
			Node s = pattern.getTriples().get(i).getSubject();
			// if the node is a variable of which the value is requested as result then ...
			if(s.isVariable()) { 				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+s.toString()+" ";
				vars.add(s.toString());
			}
			else if(s.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+s.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+s.toString()+"> ";

			// Treat the predicate
			Node p = pattern.getTriples().get(i).getPredicate();
			// if the node is a variable of which the value is requested as result then ...
			if(p.isVariable()) 
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+p.toString()+" ";
			else if(p.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+p.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+p.toString()+"> ";

			whereClause = whereClause + pattern.getPropertyPathAdornments().get(i) + " ";
			
			// Treat the object
			Node o = pattern.getTriples().get(i).getObject();
			// if the node is a variable of which the value is requested as result then ...
			if(o.isLiteral())
				whereClause = whereClause + " "+o.toString()+" ";
			else if(o.isVariable()) {
				vars.add(o.toString());
				// ... is added as subject of a triple of the WHERE clause
				whereClause = whereClause + " "+o.toString()+" ";
			}
			else if(o.getURI().startsWith("*")) {
				whereClause = whereClause + " ?"+o.getURI().substring(1)+" ";
			}
			else whereClause = whereClause + " <"+o.toString()+"> ";
			
			if(i<pattern.getTriples().size()-1)
				whereClause = whereClause + " \n . ";
//			else whereClause = whereClause + " \n ";
		}
		
		// build a string representing all the filters defined for the pattern
		String filterConstraints = pattern.filtersToString();
		for(String v:vars)
			filterConstraints = filterConstraints + "\n . FILTER("+v+" != <" +Constants.OWL_THING+">)";
		String graphFilter = ((graph!=null) && (graph.length()!=0)) ? " FROM <"+graph+"> " : " ";  		

		for(String s:pattern.getVarsToSelect()) {
			varsToSelect_concatenated = varsToSelect_concatenated + "?"+s+" ";
		}
		
		String queryString = "SELECT "+pattern.getDistinct()+" "+varsToSelect_concatenated+" "+graphFilter+" WHERE "
					+ "{"
					+ 	whereClause
					+	filterConstraints
					+ "}";		
		
//		System.out.println(queryString);
		
		Vector<QuerySolution> query_results = sc.execQuery(queryString, knowledgeSourceRef);
		
		for(int i=0; i<query_results.size(); i++) {
			Vector<Node> single_result = new Vector<Node>();
			for(String v:pattern.getVarsToSelect()) {
				RDFNode node = query_results.elementAt(i).get(v);
				single_result.add(node.asNode());
			}
			result.add(single_result);
		}

//		System.out.println(result.size());
		return result;
	}

}
