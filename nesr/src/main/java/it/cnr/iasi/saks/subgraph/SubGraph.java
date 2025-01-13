package it.cnr.iasi.saks.subgraph;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;

public class SubGraph {
	
	KnowledgeBase kb = null;
	
	public SubGraph(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	
	public Set<Triple> subgraph(Vector<Node> seeds, int minLength, int maxLength, String mode, boolean acyclic) {
		Set<Triple> result = new HashSet<Triple>();
		for(Node n1:seeds) {
			for(Node n2:seeds) {
				Vector<Path> paths = this.kb.paths(n1, n2, minLength, maxLength, mode, acyclic);
				for(Path p:paths) {
					result.addAll(p.getTriples());
				}
			}	
		}
		return result;
	}
	
}
