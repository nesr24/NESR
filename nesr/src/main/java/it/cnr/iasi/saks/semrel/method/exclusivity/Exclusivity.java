package it.cnr.iasi.saks.semrel.method.exclusivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

/**
 * The implementation of this method refers to the following publication
 * Hulpuus I., Prangnawarat N., Hayes C. 
 * Path-based Semantic Relatedness on Linked Data and its use to Word and Entity Disambiguation.
 * Springer International Publishing, Cham, 442–457. DOI:hp://dx.doi.org/10.1007/978-3-319-25007-6 26 
 *  
 * @author ftaglino
 *
 */

public class Exclusivity implements SemanticRelatednessStrategy {
	protected KnowledgeBase kb = null;
	protected int minLength = 0;
	protected int maxLength = 0;
	protected String mode = "";
	protected int k = 0;
	protected double alpha = 0.0d;
	protected boolean acyclic = true;

	public Exclusivity(KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic, int k, double alpha) {
		super();
		this.kb = kb;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.mode = mode;
		this.k = k;
		this.alpha = alpha;
		this.acyclic = acyclic;
	}

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
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

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public boolean isAcyclic() {
		return acyclic;
	}

	public void setAcyclic(boolean acyclic) {
		this.acyclic = acyclic;
	}
	
	public double exclusivity(Triple t) {
		double result = 0;
		
		double np1 = 0;
		{
			PathPattern p = new PathPattern(null);
			Set<Filter> filters = new HashSet<Filter>();
			Node o1 = NodeFactory.createVariable("u1");
			filters.addAll(this.getKb().instantiateFilters("u1", Constants.OBJECT));
			Triple t1 = new Triple(t.getSubject(), t.getPredicate(), o1);
			p.getTriples().add(t1);
			p.setFilters(filters);
			np1 = this.getKb().countPathsByPattern(p);
		}
		
		double np2 = 0;
		{
			PathPattern p = new PathPattern(null);
			Set<Filter> filters = new HashSet<Filter>();
			filters = new HashSet<Filter>();
			Node s1 = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			Triple t1 = new Triple(s1, t.getPredicate(), t.getObject());
			p.getTriples().add(t1);
			p.setFilters(filters);
			np2 = kb.countPathsByPattern(p);
		}
		
		result = 1 / (np1 + np2 - 1);
		
//		System.out.println("\t exclusivity("+t+")="+result+" ["+np1+", "+np2+"]");
		return result; 
	}
	
	public double path_weight(Path path) {
		double result = 0;
		double temp = 0;
		for(Triple t:path.getTriples())
			temp = temp + (1 / this.exclusivity(t));
		result = 1 / temp;
//		System.out.println("weight("+path+")="+result);
		return result;
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0;
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());

		System.out.println(this.minLength);
		System.out.println(this.maxLength);
		
		Map<Path,Double> weightedPaths = new HashMap<Path, Double>();
		for(Path p:paths) {
			weightedPaths.put(p, this.path_weight(p));
		}

		int k = 0;
		System.out.println(this.getK());
		if(this.getK() == Constants.MEAN)
			k = (paths.size() / 2) +  (paths.size() % 2);
		else k = this.getK();
		
		if(k>weightedPaths.size())
			k = weightedPaths.size();
//		System.out.println("weightedPaths="+weightedPaths.size());
		Vector<Path> orderedPaths = orderedPaths(weightedPaths);
		Vector<Path> k_topPaths = k_topPaths(orderedPaths, k);
		
//		List<Entry<Path, Double>> k_greatestPaths = Utils.findGreatest_n(weightedPaths, k);
		 
		for (Path path: k_topPaths) 
			result = result + Math.pow(this.getAlpha(), path.size())*weightedPaths.get(path);

		return result;
	}

	public static Vector<Path> k_topPaths(Vector<Path> orderedPaths, int k) {
		Vector<Path> result = new Vector<Path>();
		for(int i=0; i<k; i++)
	    	result.add(orderedPaths.get(i));

	    return result;
	}

	
	public static Vector<Path> orderedPaths(Map<Path,Double> weightedPaths) {
		Vector<Path> result = new Vector<Path>();

		result.addAll(weightedPaths.keySet());
		
	    boolean sorted = false;
	    Path temp;
	    while(!sorted) {
	        sorted = true;
	        for (int i = 0; i < result.size() - 1; i++) {
	        	if(((weightedPaths.get(result.get(i+1)) == weightedPaths.get(result.get(i))) && (result.get(i+1).size() < result.get(i).size())) 
	        		||	
	            (weightedPaths.get(result.get(i+1)) > weightedPaths.get(result.get(i)))) {
		            	temp = result.get(i);
		                result.set(i, result.get(i+1));
		                result.set(i+1, temp);
		                sorted = false;
	            }
	        }
	    }
	    return result;

	}

	public static Map<String, Double> semrel_experiment(Node n1, Node n2, Exclusivity exclusivity) {
		Map<String, Double> result = new HashMap<String, Double>();
		Vector<Path> paths = exclusivity.getKb().paths(n1, n2, exclusivity.getMinLength(), exclusivity.getMaxLength(), exclusivity.getMode(), exclusivity.isAcyclic());

		Map<Path,Double> weightedPaths = new HashMap<Path, Double>();
		for(Path p:paths) {
			weightedPaths.put(p, exclusivity.path_weight(p));
		}

		Vector<Path> orderedPaths = orderedPaths(weightedPaths);
		
		int k = 1;
		if(k>weightedPaths.size())
			k = weightedPaths.size();

		Vector<Path> k_topPaths = k_topPaths(orderedPaths, k);
		
		result.put(Constants.METHOD_EXCLUSIVITY_1_025, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_1_050, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_1_075, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_1_100, 0.0);
		for (Path path: k_topPaths) {
			double pathWeight = weightedPaths.get(path);
			double temp = result.get(Constants.METHOD_EXCLUSIVITY_1_025);
			result.put(Constants.METHOD_EXCLUSIVITY_1_025, temp + Math.pow(0.25d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_1_050);
			result.put(Constants.METHOD_EXCLUSIVITY_1_050, temp + Math.pow(0.50d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_1_075);
			result.put(Constants.METHOD_EXCLUSIVITY_1_075, temp + Math.pow(0.75d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_1_100);
			result.put(Constants.METHOD_EXCLUSIVITY_1_100, temp + Math.pow(1.00d, path.size())*pathWeight);

			//= result + Math.pow(exclusivity.getAlpha(), path.size())*weightedPaths.get(path);
		}
		
		k = 5;
		if(k>weightedPaths.size())
			k = weightedPaths.size();

		k_topPaths = k_topPaths(orderedPaths, k);
		
		result.put(Constants.METHOD_EXCLUSIVITY_5_025, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_5_050, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_5_075, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_5_100, 0.0);
		for (Path path: k_topPaths) {
			double pathWeight = weightedPaths.get(path);
			double temp = result.get(Constants.METHOD_EXCLUSIVITY_5_025);
			result.put(Constants.METHOD_EXCLUSIVITY_5_025, temp + Math.pow(0.25d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_5_050);
			result.put(Constants.METHOD_EXCLUSIVITY_5_050, temp + Math.pow(0.50d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_5_075);
			result.put(Constants.METHOD_EXCLUSIVITY_5_075, temp + Math.pow(0.75d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_5_100);
			result.put(Constants.METHOD_EXCLUSIVITY_5_100, temp + Math.pow(1.00d, path.size())*pathWeight);

			//= result + Math.pow(exclusivity.getAlpha(), path.size())*weightedPaths.get(path);
		}
		
		k = 10;
		if(k>weightedPaths.size())
			k = weightedPaths.size();

		k_topPaths = k_topPaths(orderedPaths, k);
		
		result.put(Constants.METHOD_EXCLUSIVITY_10_025, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_10_050, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_10_075, 0.0);
		result.put(Constants.METHOD_EXCLUSIVITY_10_100, 0.0);
		for (Path path: k_topPaths) {
			double pathWeight = weightedPaths.get(path);
			double temp = result.get(Constants.METHOD_EXCLUSIVITY_10_025);
			result.put(Constants.METHOD_EXCLUSIVITY_10_025, temp + Math.pow(0.25d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_10_050);
			result.put(Constants.METHOD_EXCLUSIVITY_10_050, temp + Math.pow(0.50d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_10_075);
			result.put(Constants.METHOD_EXCLUSIVITY_10_075, temp + Math.pow(0.75d, path.size())*pathWeight);
			
			temp = result.get(Constants.METHOD_EXCLUSIVITY_10_100);
			result.put(Constants.METHOD_EXCLUSIVITY_10_100, temp + Math.pow(1.00d, path.size())*pathWeight);

			//= result + Math.pow(exclusivity.getAlpha(), path.size())*weightedPaths.get(path);
		}
		
		return result;
	}
}
