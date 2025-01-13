package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class M4 implements IntrinsicLikelihood {
	Map<Node, Double> likelihood = new HashMap<Node, Double>();
	
	public Map<Node, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<Node, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	private void likelihood(WeightedTaxonomy wt, Node root) {
		Vector<Node> nextToWeight = new Vector<Node>(wt.children(root));
		if(nextToWeight.size() > 0) {
			double value = this.getLikelihood().get(root.getURI().toString()) / ((double)nextToWeight.size());
		
			for(Node n:nextToWeight) {
				if(((this.getLikelihood().containsKey(n)) 
						&& (this.getLikelihood().get(n) > value)) 
					|| !(this.getLikelihood().containsKey(n))) {
						this.getLikelihood().put(n, value);
						this.likelihood(wt, n);
				}
			}
		}
	}

	public Map<Node, Double> likelihood(WeightedTaxonomy wt) {
		Map<Node, Double> result = new HashMap<Node, Double>();

		Node root = NodeFactory.createURI(Constants.OWL_THING);
		this.getLikelihood().put(NodeFactory.createURI(Constants.OWL_THING), 1.0d);
		this.likelihood(wt, root);
				
		result = this.getLikelihood();
		return result;
	}
	
}
