package it.cnr.iasi.saks.similarity.conceptsSimilarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Wu_Palmer implements ConceptSimilarity_Interface {
	WeightedTaxonomy wt = null;
	
	public Wu_Palmer(WeightedTaxonomy wt) {
		this.setWt(wt);
	}
	
	public WeightedTaxonomy getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}

	public double sim(Node n1, Node n2) {
		double result = 0.0d;
		Vector<Node> lubs = this.getWt().lub(n1, n2);
		Map<Node, Double> lubs_depth = new HashMap<Node, Double>();
		for(Node n:lubs) {
			lubs_depth.put(n, ((double)this.getWt().depth(n)));
		}
		Node lub = this.getWt().bestLubId(lubs_depth);
		
		double lub_depth = lubs_depth.get(lub);
		int n1_depth = this.getWt().depth(n1);
		int n2_depth = this.getWt().depth(n2);
		
//		System.out.println("lub="+lub+"::"+lub_depth);
//		System.out.println("n1="+n1+"::"+n1_depth);
//		System.out.println("n2="+n2+"::"+n2_depth);

		
		result = 2*lubs_depth.get(lub)/
				(n1_depth + n2_depth);
		
//		System.out.println("sim("+n1+", "+n2+")="+result);
		
		return result;
	}

	public double sim_(Node n1, Node n2) {
		double result = 2.0d;

		if(n1.equals(NodeFactory.createURI("c1")) && n2.equals(NodeFactory.createURI("c1")))
			result = 1.0d;
		else if(n1.equals(NodeFactory.createURI("c1")) && n2.equals(NodeFactory.createURI("c2")))
			result = 0.5d;
		else if(n1.equals(NodeFactory.createURI("c1")) && n2.equals(NodeFactory.createURI("c3")))
			result = 0.3d;
		else if(n1.equals(NodeFactory.createURI("c1")) && n2.equals(NodeFactory.createURI("c4")))
			result = 0.7d;
		else if(n1.equals(NodeFactory.createURI("c2")) && n2.equals(NodeFactory.createURI("c1")))
			result = 0.5d;
		else if(n1.equals(NodeFactory.createURI("c2")) && n2.equals(NodeFactory.createURI("c2")))
			result = 1.0d;
		else if(n1.equals(NodeFactory.createURI("c2")) && n2.equals(NodeFactory.createURI("c3")))
			result = 0.4d;
		else if(n1.equals(NodeFactory.createURI("c2")) && n2.equals(NodeFactory.createURI("c4")))
			result = 0.9d;
		else if(n1.equals(NodeFactory.createURI("c3")) && n2.equals(NodeFactory.createURI("c1")))
			result = 0.3d;
		else if(n1.equals(NodeFactory.createURI("c3")) && n2.equals(NodeFactory.createURI("c2")))
			result = 0.4d;
		else if(n1.equals(NodeFactory.createURI("c3")) && n2.equals(NodeFactory.createURI("c3")))
			result = 1.0d;
		else if(n1.equals(NodeFactory.createURI("c3")) && n2.equals(NodeFactory.createURI("c4")))
			result = 0.3d;
		else if(n1.equals(NodeFactory.createURI("c4")) && n2.equals(NodeFactory.createURI("c1")))
			result = 0.7d;
		else if(n1.equals(NodeFactory.createURI("c4")) && n2.equals(NodeFactory.createURI("c2")))
			result = 0.9d;
		else if(n1.equals(NodeFactory.createURI("c4")) && n2.equals(NodeFactory.createURI("c3")))
			result = 0.3d;
		else if(n1.equals(NodeFactory.createURI("c4")) && n2.equals(NodeFactory.createURI("c4")))
			result = 1.0d;
		
		return result;

	}

}
