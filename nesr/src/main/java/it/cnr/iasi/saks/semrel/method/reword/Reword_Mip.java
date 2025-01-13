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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.Utils;

public class Reword_Mip extends Reword_Abstract {
	int minLength = 0;
	int maxLength = 0;
	String mode = "";
	boolean acyclic = true;
	Mip mip = null;
	
	public Reword_Mip(KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic) {
		this.setKb(kb);
		this.setMinLength(minLength);
		this.setMaxLength(maxLength);
		this.setMode(mode);
		this.setAcyclic(acyclic);
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isAcyclic() {
		return acyclic;
	}

	public void setAcyclic(boolean acyclic) {
		this.acyclic = acyclic;
	}

	public Mip getMip() {
		return mip;
	}

	public void setMip(Mip mip) {
		this.mip = mip;
	}

	public double path_informativeness(Path path) {
		double result = 0;
		Map<Triple, Double> triple_informativeness = path_triple_informativeness(path);
		double temp = 0.0d;
		for(Triple t:triple_informativeness.keySet()) {
			temp = temp + triple_informativeness.get(t);
		}
		result = temp / ((double)path.size());
		return result;
	}
	
	public Map<Triple, Double> path_triple_informativeness(Path path) {
		Map<Triple, Double> result = new HashMap<Triple, Double>();
		double triple_informativeness = 0;
		for(Triple t:path.getTriples()) {
			double add1 = this.pfitf_out(t.getSubject(), t.getPredicate());
			double add2 = this.pfitf_in(t.getObject(), t.getPredicate());
			
			triple_informativeness = (add1 + add2)/2;
			
			result.put(t, triple_informativeness);
			
		}
		return result;
	}
	
	/**
	 * Find the most informative path (MIP) connecting nodes n1 and n2, and according to the specified constraints.
	 * @param n1
	 * @param n2
	 * @param minLength
	 * @param maxLength
	 * @param mode
	 * @param acyclic
	 * @return
	 */
	public Mip mip(Node n1, Node n2) {
		Mip result = null;
		
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());
		Map<Path,Double> paths_informativeness = new HashMap<Path, Double>();
		int i=1;
		for(Path p:paths) {
			paths_informativeness.put(p, path_informativeness(p));
//			System.out.println(i++ + " --- "+p.toString()+"["+path_informativeness(p)+"]");
//			System.out.println("path informativeness("+p.getTriples()+")="+path_informativeness(p));
		}

/*		
		Vector<Path> paths_2 = this.getKb().paths(n2, n1, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());		
		for(Path p:paths_2) {
			paths_informativeness.put(p, path_informativeness(p));
		}
*/		
		if(paths_informativeness.size() > 0) {		
			List<Entry<Path, Double>> temp = Utils.findGreatest(paths_informativeness);
			result = new Mip(temp.get(0).getKey(), temp.get(0).getValue());
			this.setMip(result);
		}
		
//		System.out.println(result.getPath().toString());
		return result;
	}
	
	public double mip_informativeness(Node n1, Node n2) {
		double result = 0;
		Mip temp = this.mip(n1, n2);
		if(temp == null)
			;
		else
			result = temp.getValue();
		return result;
	}
	

	public Map<Node, Double> relatednessSpace(String in_out) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		Mip temp = this.getMip();
		if(!(temp==null)) {
//			if(temp.getPath().getTriples().size() > 0) {
				for(Triple t:this.getMip().getPath().getTriples()) {
					Node s =  t.getSubject();
					Node p =  t.getPredicate();
					Node o =  t.getObject();
					if(in_out.equals(Constants.METHOD_REWORD_SIMPLE_IN)) {
						double temp_in = this.pfitf_in(o, p);
						if(result.containsKey(p))
							result.put(p, result.get(p) + temp_in);
						else result.put(p, temp_in);
					}
					
					if(in_out.equals(Constants.METHOD_REWORD_SIMPLE_OUT)) {
						double temp_out = this.pfitf_out(s, p);
						if(result.containsKey(p))
							result.put(p, result.get(p) + temp_out);
						else result.put(p, temp_out);
						
					}
				}
//			}
		}
		return result;
	}
	
	public Map<Node, Double> relatednessSpace_in() {
		return relatednessSpace(Constants.METHOD_REWORD_SIMPLE_IN);
	}

	
	public Map<Node, Double> relatednessSpace_out() {
		return relatednessSpace(Constants.METHOD_REWORD_SIMPLE_OUT);
	}

	// This method exists for respecting the reword_Interface.
	// But it is never used
	public Map<Node, Double> relatednessSpace_in(Node n) {
		return null;
	}

	// This method exists for respecting the reword_Interface.
	// But it is never used
	public Map<Node, Double> relatednessSpace_out(Node n) {
		return null;
	}
	
	public class Mip {
		Path path = new Path();
		double value = 0.0d;
		
		public Mip(Path path, double value) {
			this.setPath(path);
			this.setValue(value);
		}
		
		public Path getPath() {
			return path;
		}
		public void setPath(Path path) {
			this.path = path;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		result = this.mip_informativeness(n1, n2);
		return result;
	}
}
