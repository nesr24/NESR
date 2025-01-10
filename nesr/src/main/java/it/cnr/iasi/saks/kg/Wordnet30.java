package it.cnr.iasi.saks.kg;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class Wordnet30 {
	KnowledgeBase kb = null;
	
	public Wordnet30(KnowledgeBase kb) {
		this.setKb(kb);
	}
	
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	public Vector<Node> wordsensesByLabel(String label) {
		Vector<Node> result = new Vector<Node>();

		PathPattern pattern = new PathPattern(null);
		Node x1 = NodeFactory.createVariable("label");
		Node p = NodeFactory.createURI(Constants.RDFS_LABEL);
		Node x2 = NodeFactory.createLiteral(label);	
		Triple t = new Triple(x1, p, x2);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("label");
		result = this.getKb().nodesByPattern(pattern);
		
    	return result;
	}
	
	public Vector<Node> synsetsByWordsense(Node wordsense) {
		Vector<Node> result = new Vector<Node>();

		PathPattern pattern = new PathPattern(null);
		Node x1 = NodeFactory.createVariable("synset");
		Node p = NodeFactory.createURI(Constants.WORDNET__CONTAINS_WORD_SENSE);
		Triple t = new Triple(x1, p, wordsense);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("synset");
		result = this.getKb().nodesByPattern(pattern);
		
    	return result;
	}
	
	public Vector<Node> synsetsByLabel(String label) {
		Vector<Node> result = new Vector<Node>();

		for(Node n:wordsensesByLabel(label)) {
			result.addAll(synsetsByWordsense(n));
		}
			
    	return result;
	}
	
	public String labelByWordsense(Node wordsense) {
		String result = "";
		
		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.RDFS_LABEL);
		Node x1 = NodeFactory.createVariable("label");
		Triple t = new Triple(wordsense, p, x1);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("label");
		Vector<Node> nodes = this.getKb().nodesByPattern(pattern);
		
		if(nodes.size()>0) {
			result = nodes.get(0).toString();
		}
		return result;
	}
	
	
	public Vector<String> labelsBySynset(Node synset) {
		Vector<String> result = new Vector<String>();
		
		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.WORDNET__CONTAINS_WORD_SENSE);
		Node x1 = NodeFactory.createVariable("wordsense");
		Triple t = new Triple(synset, p, x1);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("wordsense");
		Vector<Node> nodes = this.getKb().nodesByPattern(pattern);
		
		for(Node n:nodes)
			result.add(this.labelByWordsense(n));
		
		return result;
	}
	
	public Vector<Node> hypernymOf(Node n) {
		Vector<Node> result = new Vector<Node>();
		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.WORDNET__HYPERNYM_OF);
		Node x1 = NodeFactory.createVariable("synset");
		Triple t = new Triple(n, p, x1);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("synset");
		
		result = this.getKb().nodesByPattern(pattern);

		return result;
	}
	
	public Vector<Node> hyponymOf(Node n) {
		Vector<Node> result = new Vector<Node>();
		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createURI(Constants.WORDNET__HYPONYM_OF);
		Node x1 = NodeFactory.createVariable("synset");
		Triple t = new Triple(n, p, x1);
		pattern.getTriples().add(t);
		pattern.getVarsToSelect().add("synset");
		
		result = this.getKb().nodesByPattern(pattern);

		return result;
	}
}
