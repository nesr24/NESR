package it.cnr.iasi.saks.semrel.method.ldsd;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

/**
 * The implementation of this semantic relatedness method refers to the following publication
 * Passant A. 
 * Measuring Semantic Distance on Linking Data and Using it for Resources Recommendations
 * AAAI Spring Symposium: Linked Data Meets Artificial Intelligence, AAAI, (2010)
 *  
 * @author francesco
 *
 */

public interface LDSD_Interface extends SemanticRelatednessStrategy {
	public double semrel(Node n1, Node n2);
}
