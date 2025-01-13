package it.cnr.iasi.saks.similarity.setSimilarity.taxonomyBasedMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.similarity.conceptsSimilarity.Wu_Palmer;

public class RezaeiFranti {
	Map<String, Double> c_sim_temp = new HashMap<String, Double>();
	Vector<Node> greatest_set = new Vector<Node>();
	Vector<Node> smallest_set = new Vector<Node>();
	Wu_Palmer conceptSimilarity = null;
	double result = 0.0d;
	
	public RezaeiFranti(Wu_Palmer csm) {
		this.conceptSimilarity = csm;
	}
	
	public double sim(Set<Node> s1, Set<Node> s2) {
		double result = 0.0d;
		
		this.greatest_set = new Vector<Node>();
		this.smallest_set = new Vector<Node>();
		this.result = 0;
		int n = 0;
		
		Vector<Node> v1 = new Vector<Node>();
		Vector<Node> v2 = new Vector<Node>();		
		v1.addAll(s1);
		v2.addAll(s2);
		
		if(s1.size()>s2.size()) {
			this.greatest_set.addAll(v1);
			this.smallest_set.addAll(v2);
		}
		else {
			this.greatest_set.addAll(v2);
			this.smallest_set.addAll(v1);			
		}

		init(greatest_set, smallest_set, conceptSimilarity);

		
		n = greatest_set.size();

		int max_times = smallest_set.size();
		
		for(int times=0; times<max_times; times++) {
			addMaxPairingValue();
		}
		
		max_times = greatest_set.size();
		
		
		for(int times=0; times<max_times; times++) {		
			if(s1.size()>s2.size()) {
				addRemainingMaxPairingValue(v2); 
			}
			else {
				addRemainingMaxPairingValue(v1); 
			}
		}
		result = this.result/((double)n);
		
		if(result>1) {
			System.out.println("ERROR:"+n+"---"+this.result);
			System.out.println(s1);
			System.out.println(s2);
		}
			
		
		return result;
	}
	
	public void addMaxPairingValue() {
		double result_partial = 0.0d;

		int r_i = 0;
		int r_j = 0;
		
		double[][] c_sim = new double[greatest_set.size()][smallest_set.size()];
		for(int i=0; i<greatest_set.size(); i++) {
			for(int j=0; j<smallest_set.size(); j++) {
//				c_sim[i][j] = conceptSimilarity.sim(greatest_set.get(i), smallest_set.get(j)); 
				String key = greatest_set.get(i).getURI().toString()+"-"+smallest_set.get(j).getURI().toString();
				c_sim[i][j] = c_sim_temp.get(key);
				if(c_sim[i][j]>result_partial) {
					r_i = i;
					r_j = j;
					result_partial = c_sim[i][j];
				}
			}
		}
		
		greatest_set.remove(r_i);
		smallest_set.remove(r_j);
		this.result = this.result + result_partial;
	}
	
	public void addRemainingMaxPairingValue(Vector<Node> v) {
		double result_partial = 0.0d;

		int r_i = 0;
		
		double[][] c_sim = new double[greatest_set.size()][v.size()];
		for(int i=0; i<greatest_set.size(); i++) {
			for(int j=0; j<v.size(); j++) {
				String key = greatest_set.get(i).getURI().toString()+"-"+v.get(j).getURI().toString();
				c_sim[i][j] = c_sim_temp.get(key);
				if(c_sim[i][j]>result_partial) {
					result_partial = c_sim[i][j];
					r_i = i;
				}
			}
		}
		greatest_set.remove(r_i);
		this.result = this.result + result_partial;
	}
	
	public void init(Vector<Node> s1, Vector<Node> s2, Wu_Palmer conceptSimilarity) {
		for(Node s_1:s1) {
			for(Node s_2:s2) {
				String key = s_1.getURI().toString()+"-"+s_2.getURI().toString();
				if(c_sim_temp.get(key) == null)
					c_sim_temp.put(key, conceptSimilarity.sim(s_1, s_2)); 
			}
		}
	}
}
