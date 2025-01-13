package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class TopDown implements IntrinsicLikelihood {
	Map<Node, Double> likelihood = new HashMap<Node, Double>();
	
	public Map<Node, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<Node, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	
	public Map<Node, Double> likelihood(WeightedTaxonomy wt) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		
		for(Node c:wt.allClasses()) {
			this.getLikelihood().put(c, 0d);
		}
		this.getLikelihood().put(NodeFactory.createURI(Constants.OWL_THING), 1.0d);
		
		Vector<Node> toCompute = new Vector<Node>();
		Node thing = NodeFactory.createURI(Constants.OWL_THING);
		toCompute.add(thing);
		assignWeightsInProbabilisticMode(toCompute, wt);
		
		result = this.getLikelihood();
		return result;
	}
	
	
	// Assign the weights in a probabilistic mode
	// w(c)=w(parent(c))/|children(parent(c))|
	public void assignWeightsInProbabilisticMode(Vector<Node> toCompute, WeightedTaxonomy wt) {
		Vector<Node> nextToCompute = new Vector<Node>(); 
		for(Node n:toCompute) {
			Vector<Node> children = wt.children(n);
			for(Node child:children) {
				double weight = 0.0d;
				if(n.getURI().toString().equalsIgnoreCase(Constants.OWL_THING))
					weight = 1.0d/children.size();
				else {
					weight = this.likelihood.get(n)/children.size();
				}
					//System.out.println(child.getURI().toString()+"\t"+weight);
				this.getLikelihood().put(child, weight);
				nextToCompute.add(child);
			}
		}
		if(nextToCompute.size()>0)
			assignWeightsInProbabilisticMode(nextToCompute, wt);
	}


	// Assign the weights in a probabilistic mode
	// w(c)=w(parent(c))/|children(parent(c))|
	public void _assignWeightsInProbabilisticMode(Vector<Node> toCompute, WeightedTaxonomy wt) {
		Vector<Node> nextToCompute = new Vector<Node>(); 
		for(Node n:toCompute) {
			Vector<Node> children = wt.children(n);
			for(Node child:children) {
				System.out.println(n);
				System.out.println(n.getURI().toString());
				System.out.println(children);
				System.out.println(likelihood.get("http://dbpedia.org/ontology/Name"));
				double weight = 0.0d;
				weight = likelihood.get(n.getURI().toString())/children.size();
				//System.out.println(child.getURI().toString()+"\t"+weight);
				this.getLikelihood().put(child, weight);
				nextToCompute.add(child);
			}
		}
		if(nextToCompute.size()>0)
			assignWeightsInProbabilisticMode(nextToCompute, wt);
	}
}
