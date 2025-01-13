package it.cnr.iasi.saks.kg.extension;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

public class ExtensionResults {
	Map<String, Map<Node, Double>> newNodes = new HashMap<String, Map<Node, Double>>();
	Map<String, Map<Node, Double>> discardedNodes = new HashMap<String, Map<Node, Double>>();
	Map<String, Map<Node, Double>> notInContextNodes = new HashMap<String, Map<Node, Double>>();
	
	public ExtensionResults() {
		super();
	}

	public Map<String, Map<Node, Double>> getNewNodes() {
		return newNodes;
	}

	public void setNewNodes(Map<String, Map<Node, Double>> newNodes) {
		this.newNodes = newNodes;
	}

	public Map<String, Map<Node, Double>> getDiscardedNodes() {
		return discardedNodes;
	}

	public void setDiscardedNodes(Map<String, Map<Node, Double>> discardedNodes) {
		this.discardedNodes = discardedNodes;
	}

	public Map<String, Map<Node, Double>> getNotInContextNodes() {
		return notInContextNodes;
	}

	public void setNotInContextNodes(Map<String, Map<Node, Double>> notInContextNodes) {
		this.notInContextNodes = notInContextNodes;
	}

	public void addNewNode(String n, Node n_in, double d) {
		if(this.getNewNodes().get(n)==null) {
			Map<Node, Double> m1 = new HashMap<Node, Double>();
			this.getNewNodes().put(n, m1);
		}
		this.getNewNodes().get(n).put(n_in, d);
	}
	
	public Double getNewNode(String n, Node n_out) {
		if(this.getNewNodes().get(n)==null) {
			return null;
		}
		return this.getNewNodes().get(n).get(n_out);
	}
	
	public void addDiscardedNode(String n, Node n_in, double d) {
		if(this.getDiscardedNodes().get(n)==null) {
			Map<Node, Double> m1 = new HashMap<Node, Double>();
			this.getDiscardedNodes().put(n, m1);
		}
		this.getDiscardedNodes().get(n).put(n_in, d);
	}
	
	public Double getDiscardedNode(String n, Node n_out) {
		if(this.getDiscardedNodes().get(n)==null) {
			return null;
		}
		return this.getDiscardedNodes().get(n).get(n_out);
	}
	
	public void addNotInContextNode(String n, Node n_in, double d) {
		if(this.getNotInContextNodes().get(n)==null) {
			Map<Node, Double> m1 = new HashMap<Node, Double>();
			this.getNotInContextNodes().put(n, m1);
		}
		this.getNotInContextNodes().get(n).put(n_in, d);
	}
	
	public Double getNotInContextNode(String n, Node n_out) {
		if(this.getNotInContextNodes().get(n)==null) {
			return null;
		}
		return this.getNotInContextNodes().get(n).get(n_out);
	}
}
