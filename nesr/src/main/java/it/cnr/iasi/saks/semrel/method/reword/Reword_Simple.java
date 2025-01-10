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
package it.cnr.iasi.saks.semrel.method.reword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class Reword_Simple extends Reword_Abstract {

	public Reword_Simple(KnowledgeBase kb) {
		this.setKb(kb);
	}
	
	protected Map<Node, Double> relatednessSpace(Node n, String in_out) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		PathPattern pattern = new PathPattern(null);
		Set<Filter> filters = new HashSet<Filter>();
		Node nx = NodeFactory.createVariable("nx");
		filters.addAll(this.getKb().instantiateFilters("nx", Constants.SUBJECT));
		Node px = NodeFactory.createVariable("px");
		filters.addAll(this.getKb().instantiateFilters("px", Constants.PREDICATE));
		Triple t = null;
		if(in_out.equals(Constants.IN) || in_out.equals(Constants.IN_OUT))
			t = new Triple(nx, px, n);
		else if(in_out.equals(Constants.OUT) || in_out.equals(Constants.IN_OUT)) 
			t = new Triple(n, px, nx);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		pattern.getVarsToSelect().add("px");
		
		for(Node p:kb.nodesByPattern(pattern)) {
			result.put(p, pfitf(n, p, in_out));
		}		
		return result;
	}
	
	public Map<Node, Double> relatednessSpace_in(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = relatednessSpace(n, Constants.IN);
		return result;
	}
	
	public Map<Node, Double> relatednessSpace_out(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = relatednessSpace(n, Constants.OUT);
		return result;
	}	
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		if(this.getDirection().equals(Constants.IN)) {
			double cosine = Reword_Abstract.cosine(this.relatednessSpace_in(n1), this.relatednessSpace_in(n2));
			result = cosine;
		}
		else if(this.getDirection().equals(Constants.OUT)) {
			double cosine = Reword_Abstract.cosine(this.relatednessSpace_out(n1), this.relatednessSpace_out(n2));
			result = cosine;
		}
		else if(this.getDirection().equals(Constants.IN_OUT)) {
			double cosine_in = Reword_Abstract.cosine(this.relatednessSpace_in(n1), this.relatednessSpace_in(n2));
			double cosine_out = Reword_Abstract.cosine(this.relatednessSpace_out(n1), this.relatednessSpace_out(n2));
			result = (cosine_in + cosine_out)/2;
		}
		return result;
	}
}
