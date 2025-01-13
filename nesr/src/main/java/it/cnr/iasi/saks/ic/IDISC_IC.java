package it.cnr.iasi.saks.ic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class IDISC_IC implements IC_IntrinsicMethod {
	
	Map<Node, Double> ics = new HashMap<Node, Double>();
	Map<Node, Map<Node, Double>> distances = new HashMap<Node, Map<Node, Double>>();
		
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}
	
	public Map<Node, Map<Node, Double>> getDistances() {
		return distances;
	}

	public void setDistances(Map<Node, Map<Node, Double>> distances) {
		this.distances = distances;
	}

	public double ic(WeightedTaxonomy wt, Node n) {
		double result = 0.0d;
		result = -Math.log(g(wt, n));
		return result;
	}

	
	public Map<Node, Double> computeIcs(WeightedTaxonomy wt) {
		System.out.println("Nr. Nodes = "+wt.getIds().size());
		
		this.setDistances(this.weighting(wt)); 
		
		int count = 1;
		for(Node n: wt.getIds()) {
			double ic = ic(wt, n);
//			System.out.println("\t IC of("+n.getURI().toString()+") = "+ic+" --- "+count);
			count++;
			this.getIcs().put(n, ic);
		}
		return this.getIcs();
	}
	
	
	private double g(WeightedTaxonomy wt, Node x) {
		double result = 0.0d;
		double num = 0.0d;
		double denom = 0.0d;
		for(Node y: wt.getIds()) {
			double temp = distances.get(x).get(y);
			num = num + temp;
			denom = denom + distances.get(y).get(x) + temp;
		}
		result = num / denom;
		return result;
	}
	
	private Map<Node, Map<Node, Double>> weighting(WeightedTaxonomy wt) {
		Map<Node, Map<Node, Double>> result = new HashMap<Node, Map<Node, Double>>();
		
		double p_w = 0.5d;
		double epsilon_k = 1;
		
		Set<Triple> triples = new HashSet<Triple>();
		Set<Triple> allTriples = wt.allTriples();
		for(Triple t: allTriples) {
			if(t.getPredicate().getURI().toString().equalsIgnoreCase(Constants.RDFS_SUBCLASSOF))
				triples.add(t);
		}
		
		
		Map<Triple, Double> delta = new HashMap<Triple, Double>();
		Map<Triple, Double> delta_segnato = new HashMap<Triple, Double>();
		
		for(Triple t:triples) {
			delta.put(t, 0.0d);
			delta_segnato.put(t, 0.0d);
		}
		
		
		Set<Node> nodes = wt.allClasses();
		Map<Node, Double> i = new HashMap<Node, Double>();
		Map<Node, Double> o = new HashMap<Node, Double>();
	
		Map<Node, Double> omega_i = new HashMap<Node, Double>();
		Map<Node, Double> omega_o = new HashMap<Node, Double>();
		Map<Node, Double> g = new HashMap<Node, Double>();
		Map<Node, Double> g_new = new HashMap<Node, Double>();
		
		for(Node n:nodes) {
			double i_temp = (double)wt.countTriplesWithObject(n); 
			i.put(n, i_temp);
			double o_temp = (double)wt.countTriplesWithSubject(n); 
			o.put(n, o_temp);

			omega_i.put(n, (1.0d - i_temp/(i_temp + o_temp)));
			omega_o.put(n, (1.0d - o_temp/(i_temp + o_temp)));
						
			g.put(n, 1.0d);
		}
				
		int iter = 1;
		double value = 0.0d;
		
/*		for(Node ne:nodes) {
			Map<Node, Double> ttt = new HashMap<Node, Double>();
			for(Node ni:nodes) {
				ttt.put(ni, 1.0d);
			}
			result.put(ne, ttt);
		}
*/		
		do {
			Map<Node, Map<Node, Double>> A_gamma = new HashMap<Node, Map<Node, Double>>();
			for(Node ne:nodes) {
				Map<Node, Double> m = new HashMap<Node, Double>();
				for(Node ni:nodes) {
					if(ne == ni) 
						m.put(ni, 0.0d);
					else
						m.put(ni, -1.0d);
				}
				A_gamma.put(ne, m);
			}
			
			for(Triple t: triples) {
				double omega_ab = p_w * (g.get(t.getSubject())*omega_o.get(t.getSubject()) + 
									g.get(t.getObject())*omega_i.get(t.getObject())) +
							  		(1.0d - p_w) * delta.get(t);
				
				double omega_ba = p_w * (g.get(t.getObject())*omega_o.get(t.getObject()) + 
						 			g.get(t.getSubject())*omega_i.get(t.getSubject())) +
									(1.0d - p_w) * delta_segnato.get(t);
				
				A_gamma.get(t.getSubject()).put(t.getObject(), omega_ab);
				A_gamma.get(t.getObject()).put(t.getSubject(), omega_ba);
				
				result = A_gamma;
				
			}

			int esterno = 0;

			for(Node ne:nodes) {
				for(Node ni:nodes) {
					if(A_gamma.get(ne).get(ni)<0) {
						Vector<Node> path_ne_ni = this.shortestPath(wt, ne, ni);
						double pathWeight = this.pathWeight(A_gamma, path_ne_ni);
						A_gamma.get(ne).put(ni, pathWeight);
					}
				}
				esterno++;
				if(esterno%100 == 0)
					System.out.println(esterno);
			}

			for(Node ne: nodes) {
				double num = 0.0d;
				double denom = 0.0d;
				for(Node ni: nodes) {
					num = num + A_gamma.get(ne).get(ni);
					denom = denom + A_gamma.get(ni).get(ne);
				}
				g_new.put(ne, num/denom);
			}
			
			for(Triple t:triples) {
				double sum = 0.0d;
				sum = sum + A_gamma.get(t.getSubject()).get(t.getObject());
				delta.put(t, sum);
				
				sum = 0.0d;
				sum = sum + A_gamma.get(t.getObject()).get(t.getSubject());
				delta_segnato.put(t, sum);
			}
			
			value = 0.0d;

			for(Node n:nodes) 
				value = value + Math.pow((g_new.get(n) - g.get(n)), 2);
			value = value / nodes.size();
		
			System.out.println(iter+" --- "+value);
			iter++;
		} while(value > epsilon_k);
		
		return result;
	}

	
	public Vector<Node> shortestPath(WeightedTaxonomy wt, Node n1, Node n2) {
		Vector<Node> result = new Vector<Node>();
				
		Node lub = wt.lub(n1, n2).elementAt(0);
		
		result.add(n1);
		
		Node father = n1;
		while(!(father.getURI().toString().equalsIgnoreCase(lub.getURI().toString()))) {
			father = wt.parents(father).get(0);
			result.add(father);
		}
				
		Vector<Node> temp = new Vector<Node>();
		temp.add(n2);
		father = n2;
		while(!(father.getURI().toString().equalsIgnoreCase(lub.getURI().toString()))) {
			father = wt.parents(father).get(0);
			temp.add(father);
		}
		
		if(temp.size() > 1) {
			for(int i= (temp.size()-2); i >=0; i--) {
				result.add(temp.get(i));
			}
		}
		
		return result;
	}
	
	
	public double pathWeight(Map<Node, Map<Node, Double>> A_gamma, Vector<Node> path) {
		double result = 0.0d;
		
		for(int i=0; i<path.size()-1; i++) {
			result = result + A_gamma.get(path.get(i)).get(path.get(i+1));
		}
		
		return result;
	}
	
}
