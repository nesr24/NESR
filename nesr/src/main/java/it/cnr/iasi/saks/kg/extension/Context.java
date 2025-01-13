package it.cnr.iasi.saks.kg.extension;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Node;

public class Context {
	Map<Node, Double> context = new HashMap<Node, Double>();

	public Context() {
		super();
	}
	
	public Context(Map<Node, Double> context) {
		super();
		this.context = context;
	}

	public Map<Node, Double> getContext() {
		return context;
	}

	public void setContext(Map<Node, Double> context) {
		this.context = context;
	}
	
}
