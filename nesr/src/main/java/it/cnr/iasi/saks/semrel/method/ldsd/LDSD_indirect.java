package it.cnr.iasi.saks.semrel.method.ldsd;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class LDSD_indirect extends LDSD_Abstract {

	public LDSD_indirect(KnowledgeBase kb) {
		this.setKb(kb);
	}

	/**
	 * This method implements the C_io(l1, ra, rb) function in the Passant's article.
	 * @param n1
	 * @param p
	 * @param n2
	 * @return 1 if there is a resource nx that satisfies both <n1, p, nx> and <n2, p, nx>, 0 otherwise
	 */
	public double Cio_npn(Node n1, Node p, Node n2) {
		double result = 0.0d;
		
		result = this.Cix_npn(n1, p, n2, Constants.METHOD_LDSD_OUTGOING);
		
		return result;
	}
	
	
	/**
	 * This method implements the C_ii(l1, ra, rb) function in the Passant's article.
	 * @param n1
	 * @param p
	 * @param n2
	 * @return 1 if there is a resource nx that satisfies both <nx, p, n1> and <nx, p, n2>, 0 otherwise
	 */
	public double Cii_npn(Node n1, Node p, Node n2) {
		double result = 0.0d;
		
		result = this.Cix_npn(n1, p, n2, Constants.METHOD_LDSD_INCOMING);
		
		return result;
	}
	
	/**
	 * The method implements the the C_ii(n, ra, rb) function in the Passant's article.
	 * @param n1
	 * @param n2
	 * @return The total number of outgoing indirect and distinct links between n1 and n2.
	 * That is the number of indirect paths between n1 and n2 of the form <n1, p1, nx> . <n2, p1, nx>
	 */
	public double Cio_nn(Node n1, Node n2) {
		double result = 0.0d;
		result = Cix_nn(n1, n2, Constants.METHOD_LDSD_OUTGOING);
		return result;
	}
	
	
	/**
	 * The method implements the the C_io(n, ra, rb) function in the Passant's article.
	 * @param n1
	 * @param n2
	 * @return The total number of incoming indirect and distinct links between n1 and n2.
	 * That is the number of indirect paths between n1 and n2 of the form <nx, p1, n1> . <nx, p1, n2>
	 */
	public double Cii_nn(Node n1, Node n2) {
		double result = 0.0d;
		result = Cix_nn(n1, n2, Constants.METHOD_LDSD_INCOMING);
		return result;
	}
	
	/**
	 * This method implements the Cio(li, ra, n) method in the Passant's article.
	 * @param n1
	 * @param p1
	 * @return The total number of resources nx linked indirectly to n1 via p1.
	 * That is the number of resources n2 such that a node nx exists and <n1, p1, nx> . <n2, p1, nx>  
	 */
	public double Cio_np(Node n1, Node p1) {
		double result = 0.0d;
		result = Cix_np(n1, p1, Constants.METHOD_LDSD_OUTGOING);
//		System.out.println("Cio("+p1.getURI()+", "+n1.getURI()+", n)="+result);
		return result;
	}
	
	/**
	 * This method implements the Cii(li, ra, n) method in the Passant's article.
	 * @param n1
	 * @param p1
	 * @return The total number of resources nx linked indirectly to n1 via p1.
	 * That is the number of resources n2 such that a node nx exists and <n1, p1, nx> . <n2, p1, nx>  
	 */
	public double Cii_np(Node n1, Node p1) {
		double result = 0.0d;
		result = Cix_np(n1, p1, Constants.METHOD_LDSD_INCOMING);
//		System.out.println("Cii("+p1.getURI()+", "+n1.getURI()+", n)="+result);
		return result;
	}
	
	public double Cix_npn(Node n1, Node p, Node n2, String direction) {
		double result = 0.0d;
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p, nx);
			t2 = new Triple(n2, p, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx, p, n1);
			t2 = new Triple(nx, p, n2);
		}
		
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("nx");
		
		double d = this.kb.countNodesByPattern(pattern);
		
		if(d>0)
			result = 1.0d;
		
		return result;
	}
	
	public double Cix_nn(Node n1, Node n2, String direction) {
		double result = 0.0d;
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Node p1 = NodeFactory.createVariable("p1");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p1, nx);
			t2 = new Triple(n2, p1, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx, p1, n1);
			t2 = new Triple(nx, p1, n2);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("p1");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		
		result = this.kb.countNodesByPattern(pattern);

		return result;
	}
	
	public double Cix_np(Node n1, Node p1, String direction) {
		double result = 0.0d;

		PathPattern pattern = new PathPattern(null);
		Node n2 = NodeFactory.createVariable("n2");
		Node nx = NodeFactory.createVariable("nx");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p1, nx);
			t2 = new Triple(n2, p1, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx, p1, n1);
			t2 = new Triple(nx, p1, n2);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("n2");
		Filter f = new Filter();
		f.mustBeDifferent(n1, n2);
		pattern.getFilters().add(f);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);

		result = this.kb.countNodesByPattern(pattern);
//		System.out.println(result);

		
//		Vector<Node> temp = this.getKb().nodesByPattern(pattern);
//		System.out.println(temp);
/*		
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING))
			System.out.println("Cio("+n1.getURI()+", "+p1.getURI()+", n)="+temp);
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING))
			System.out.println("Cii("+n1.getURI()+", "+p1.getURI()+", n)="+temp);
*/
		
		return result;
	}
	
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		result = 1.0d/(1.0d + this.Cio_nn(n1, n2) + this.Cii_nn(n1, n2));
		return result;
	}
}
