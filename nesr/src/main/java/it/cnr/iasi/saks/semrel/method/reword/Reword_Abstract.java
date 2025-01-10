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

public abstract class Reword_Abstract implements Reword_Interface{
	protected KnowledgeBase kb;
	protected String direction = Constants.IN_OUT; 

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * This method implements the Predicate Frequency function as in the Pirrò's article
	 * If in_out equals to Constants.IN (Constants.OUT) it computes the number of times the predicate p 
	 * is used in a triple with n as object (subject) compared to the number of triples in which n appears
	 * @param n
	 * @param p
	 * @param in_out
	 * @return
	 */
	private double pf(Node n, Node p, String in_out) {
		double result = 0;
		
		// if in_out is IN, computes the number of triples of the form <nx, p, n>;
		// if in_out is OUT, computes the number of triples of the form <n, p, nx>
		int numerator = 0;
		{
			PathPattern pattern1 = new PathPattern(null);
			Set<Filter> filters1 = new HashSet<Filter>();
			Node nx = NodeFactory.createVariable("nx");
			filters1.addAll(this.getKb().instantiateFilters("nx", Constants.SUBJECT));
			Triple t1 = null;
			if(in_out.equals(Constants.IN))
				t1 = new Triple(nx, p, n);
			else if(in_out.equals(Constants.OUT)) 
				t1 = new Triple(n, p, nx);
			
			pattern1.getTriples().add(t1);
			pattern1.setFilters(filters1);
			numerator = this.getKb().countPathsByPattern(pattern1);
		}
				
		// computes the number of triples of the form <nx, px, n>
		int numTriples_nx_px_n = 0;
		{
			PathPattern pattern2 = new PathPattern(null);
			Set<Filter> filters2 = new HashSet<Filter>();
			Node nx = NodeFactory.createVariable("nx");
			filters2.addAll(this.getKb().instantiateFilters("nx", Constants.SUBJECT));
			Node px = NodeFactory.createVariable("px");
			filters2.addAll(this.getKb().instantiateFilters("px", Constants.PREDICATE));
			Triple t2 = new Triple(nx, px, n);
			pattern2.getTriples().add(t2);
			pattern2.setFilters(filters2);
			numTriples_nx_px_n = this.getKb().countPathsByPattern(pattern2);			
		}

		// computes the number of triples of the form <n, px, nx>
		int numTriples_n_px_nx = 0;
		{
			PathPattern pattern3 = new PathPattern(null);
			Set<Filter> filters3 = new HashSet<Filter>();
			Node nx = NodeFactory.createVariable("nx");
			filters3.addAll(this.getKb().instantiateFilters("nx", Constants.OBJECT));
			Node px = NodeFactory.createVariable("px");
			filters3.addAll(this.getKb().instantiateFilters("px", Constants.PREDICATE));
			Triple t3 = new Triple(n, px, nx);
			pattern3.getTriples().add(t3);
			pattern3.setFilters(filters3);
			numTriples_n_px_nx = this.getKb().countPathsByPattern(pattern3);
		}
		
		result = 	((double)numerator) /
					(((double)numTriples_nx_px_n) + ((double)numTriples_n_px_nx));
		return result;
	}
	
	public double pf_in(Node n, Node p) {
		double result = 0;
		result = pf(n, p, Constants.IN);
		return result;
	}
	
	public double pf_out(Node n, Node p) {
		double result = 0;
		result = pf(n, p, Constants.OUT);		
		return result;
	}	
		
	
	/**
	 * This method computes the Inverse Triple Frequency as in the Pirrò's article
	 * It computes how many times the predicate p is used in some triples wrt the total number of triples.
	 * @param p
	 * @return
	 */
	public double itf(Node p) {
		double result = 0;
		
		int numTriples_nx1_p_nx2 = 0;
		{
			PathPattern pattern = new PathPattern(null);
			Set<Filter> filters = new HashSet<Filter>();
			Node nx1 = NodeFactory.createVariable("nx1");
			filters.addAll(this.getKb().instantiateFilters("nx1", Constants.SUBJECT));
			Node nx2 = NodeFactory.createVariable("nx2");
			filters.addAll(this.getKb().instantiateFilters("nx2", Constants.OBJECT));
			Triple t = new Triple(nx1, p, nx2);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
			numTriples_nx1_p_nx2 = this.getKb().countPathsByPattern(pattern);
		}
		
		result = Math.log(((double) this.getKb().countAllTriples()) / ((double) numTriples_nx1_p_nx2));
		
		return result;
	}
	
	/**
	 * This method implements the PFITF function as in the Pirro's article.
	 * @param n
	 * @param p
	 * @param in_out
	 * @return
	 */
	protected double pfitf(Node n, Node p, String in_out) {
		double result = 0;
		double pf = 0.0;
		if(in_out.equals(Constants.IN)) {
			pf = pf_in(n, p);
		}
		else if(in_out.equals(Constants.OUT)) {
			pf = pf_out(n, p);
		}
		double itf = itf(p);
		result = pf*itf;
		return result;
	}
	
	public double pfitf_in(Node n, Node p) {
		double result = 0;
		result = pfitf(n, p, Constants.IN);
		return result;
	}
	
	public double pfitf_out(Node n, Node p) {
		double result = 0;
		result = pfitf(n, p, Constants.OUT);
		return result;
	}	

	public static double cosine(Map<Node, Double> m1, Map<Node, Double> m2) {
		double result = 0;		
		
		if((m1.size() > 0) && (m2.size() > 0)) {
			Set<Node> keys = new HashSet<Node>();
			keys.addAll(m1.keySet());
			keys.addAll(m2.keySet());
			double dot_product = 0;		
			for(Node n:keys) {
				if((m1.get(n)!=null) && (m2.get(n)!=null))
					dot_product = dot_product + m1.get(n).doubleValue() * m2.get(n).doubleValue();
			}
			double n1_norm = norm(m1);
			double n2_norm = norm(m2);		
			result =	dot_product / (n1_norm*n2_norm);
		}
		return result;
	}
	
	/**
	 * Calculate the norm of one feature vector
	 * 
	 * @param feature of one cluster
	 * @return
	 */
	public static double norm(Map<Node, Double> features) {
		double result = 0.0;
		Set<Node> keys = features.keySet();
		double temp = 0.0;
		for(Node n:keys) {
			temp = temp + Math.pow(features.get(n).doubleValue(), 2);
		}
		result = Math.sqrt(temp);
		
		return result;
	}
}
