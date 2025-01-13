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
package it.cnr.iasi.saks.semrel.method.loddo;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

/**
 *  
 * @author ftaglino
 *
 */
public abstract class Loddo_Abstract implements SemanticRelatednessStrategy {
	private KnowledgeBase kb = null;
	
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
 	
	public int min(Set<String> d1, Set<String> d2) {
		int result = 0;
		result = (d1.size() < d2.size()) ?
					d1.size() :
					d2.size();
		return result;
	}
	
	public Set<String> description(Node n1) {
		Set<String> result = new HashSet<String>();
				
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>(); 
		Node u1 = NodeFactory.createVariable("u1");
		filters.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
		Node p1 = NodeFactory.createVariable("p1");
		//ignore the rdf:type predicate
		Filter f1 = new Filter();
		f1.mustBeDifferent(p1, NodeFactory.createURI(Constants.RDF_TYPE));
		filters.add(f1);
		filters.addAll(this.getKb().instantiateFilters("p1", Constants.PREDICATE));
		Triple t = new Triple(u1,p1,n1);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("u1");
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		
		for(Node n:this.getKb().nodesByPattern(pattern)) {
			result.add(n.getLocalName());
//			System.out.println(n.getURI().toString());
		}
				
		pattern = new PathPattern(null);
		filters = new HashSet<Filter>(); 
		u1 = NodeFactory.createVariable("u1");
		filters.addAll(this.getKb().instantiateFilters("u1", Constants.OBJECT));

		//ignore the rdf:type predicate
		f1 = new Filter();
		f1.mustBeDifferent(p1, NodeFactory.createURI(Constants.RDF_TYPE));
		filters.add(f1);
		filters.addAll(this.getKb().instantiateFilters("p1", Constants.PREDICATE));
		t = new Triple(n1,p1,u1);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("u1");
		pattern.setDistinct(Constants.SPARQL_NOT_DISTINCT);
		
		for(Node n:this.getKb().nodesByPattern(pattern)) {
			result.add(n.getLocalName());
//			System.out.println(n.getURI().toString());
		}

				
		result.add(n1.getLocalName());
		//System.out.println("description("+n1.getURI()+")="+result);
		
		System.out.println(n1.getLocalName()+": "+result.size());
		
		return result;
	}
	
	public Set<String> commonDescription(Set<String> d1, Set<String> d2) {
		Set<String> result = new HashSet<String>();
		
		result = d1;
		result.retainAll(d2);
				
		return result;
	}
		
}
