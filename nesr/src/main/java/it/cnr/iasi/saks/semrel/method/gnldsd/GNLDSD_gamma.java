package it.cnr.iasi.saks.semrel.method.gnldsd;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_direct_weighted;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_indirect_weighted;

public class GNLDSD_gamma extends GNLDSD_Abstract {
	
	int terminated = 0;
	double denom = 1.0d;
	double denom_1 = 0.0d;
	double denom_2 = 0.0d;
	int i=0;
	
	public GNLDSD_gamma(KnowledgeBase kb) {
		this.setKb(kb);
		this.setLdsd_direct_weighted(new LDSD_direct_weighted(kb));
		this.setLdsd_indirect_weighted(new LDSD_indirect_weighted(kb));
	}
	
	/**
	 * This method implements the C_dp(li) function in the Piao and Breslin article
	 * @param p1
	 * @return the number of triples with p1 as the predicate
	 */
	public double Cdp_p(Node p1) {
		double result = 0.0d;
		
		PathPattern pattern = new PathPattern(null);
		Node nx1 = NodeFactory.createVariable("nx1");
		Node nx2 = NodeFactory.createVariable("nx2");
		Triple t = new Triple(nx1, p1, nx2);
		pattern.getTriples().add(t);
		
		result = this.getKb().countPathsByPattern(pattern);
		
		return result;
	}
	
	/**
	 * This method implements the C_iip(li, rj) function in the Piao and Breslin article
	 * @param p1
	 * @param nj
	 * @return The number of paths <nj, p1, nx1> . <nj, p1, nx2>, where nx1 and nx2 can be any node 
	 */
	public double Ciip_pn(Node p1, Node nj) {
		double result = 0.0d;
		result = this.Cixp_pn(p1, nj, Constants.METHOD_LDSD_INCOMING);
		return result;
	}
	
	/**
	 * This method implements the C_iop(li, rj) function in the Piao and Breslin article
	 * @param p1
	 * @param nj
	 * @return The number of paths <nx1, p1, nj> . <nx2, p1, nj>, where nx1 and nx2 can be any node 
	 */	
	public double Ciop_pn(Node p1, Node nj) {
		double result = 0.0d;
		result = this.Cixp_pn(p1, nj, Constants.METHOD_LDSD_OUTGOING);
		return result;
	}

		
	public double Cixp_pn(Node p1, Node n1, String direction) {
		double result = 0.0d;
		
		PathPattern pattern = new PathPattern(null);
		Node nx = NodeFactory.createVariable("nx");
		Triple t1 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(n1, p1, nx);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(nx, p1, n1);
		}
		pattern.getTriples().add(t1);
		pattern.getVarsToSelect().add("nx");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		
		int temp = this.getKb().countNodesByPattern(pattern);
		for(int i=0; i<temp; i++)
			result = result + i;

		return result;
	}

	/**
	 *If @direction equals to {@link Constants.METHOD_LDSD_OUTGOING} it search for the paths of the form <n1, p1, nx> . <n2, p1, nx>
	 *If @direction equals to {@link Constants.METHOD_LDSD_INCOMING} it search for the paths of the form <nx, p1, n1> . <nx, p1, n1>
	 * @param n1
	 * @param n2
	 * @param direction
	 * @return
	 */
	public Vector<Path> indirectPaths(Node n1, Node n2, String direction) {
		Vector<Path> result = new Vector<Path>();
		
		PathPattern pattern = new PathPattern(null);
		Node nj = NodeFactory.createVariable("nj");
		Node p1 = NodeFactory.createVariable("p1");
		Triple t1 = null;
		Triple t2 = null;
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nj, p1, n1);
			t2 = new Triple(nj, p1, n2);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, p1, nj);
			t2 = new Triple(n2, p1, nj);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getVarsToSelect().add("nj");
		pattern.getVarsToSelect().add("p1");
		
		result = this.getKb().pathsByPattern(pattern, true);
		
		return result;
	}

	
	public double semrel_(Node n1, Node n2) {
		double result = 0.0d;
		
		if(n1.getURI().toString() == n2.getURI().toString())
			result = 1.0d;
		else {
			double denom = 1.0d;
			Vector<Node> ps = this.ldsd_direct_weighted.directLinks(n1, n2);
			for(Node p:ps)
				denom = denom + 1/(1+Math.log(this.Cdp_p(p)));
			
			ps = this.ldsd_direct_weighted.directLinks(n2, n1);
			for(Node p:ps)
				denom = denom + 1/(1+Math.log(this.Cdp_p(p)));
			
	
			double t = 0.0d;
			Vector<Path> paths = this.indirectPaths(n1, n2, Constants.METHOD_LDSD_OUTGOING);
			for(Path path:paths) {
				Node p = path.getTriples().get(0).getPredicate();
				Node nj = path.getTriples().get(0).getObject();
				
				t = t + 1/(1 + Math.log(this.Ciop_pn(p, nj)));
				
				denom = denom + 1/(1 + Math.log(this.Ciop_pn(p, nj)));
			}
	
			t = 0.0d;
			paths = this.indirectPaths(n1, n2, Constants.METHOD_LDSD_INCOMING);
			for(Path path:paths) {
				Node p = path.getTriples().get(0).getPredicate();
				Node nj = path.getTriples().get(0).getSubject();
				
				t = t + 1/(1 + Math.log(this.Ciip_pn(p, nj)));
				
				denom = denom + 1/(1 + Math.log(this.Ciip_pn(p, nj)));
			}
	
			double dist = 1.0d/denom;
			
			result = 1.0d - dist;
		}
		
		return result;
	}
	
	public double semrel(Node n1, Node n2) {	
		double result = 0.0d;
		
		Vector<Node> ps = this.ldsd_direct_weighted.directLinks(n1, n2);
		for(Node p:ps)
			this.updateDenom(1/(1+Math.log(this.Cdp_p(p))));
		
		ps = this.ldsd_direct_weighted.directLinks(n2, n1);
		for(Node p:ps)
			this.updateDenom(1/(1+Math.log(this.Cdp_p(p))));
		
		result = 1.0d/updateDenom(denom_2(n1, n2));
		
		return result;
	}
	
	public double denom_2(Node n1, Node n2) {
		double result = 0.0d;
		ExecutorService es=Executors.newFixedThreadPool(7);

		Vector<Path> paths_out = this.indirectPaths(n1, n2, Constants.METHOD_LDSD_OUTGOING);
		updateTerminated(paths_out.size());
		for(Path path:paths_out) {
			Node p = path.getTriples().get(0).getPredicate();
			Node nj = path.getTriples().get(0).getObject();

			Runnable task1 = () -> {
				double temp1 = 1/(1 + Math.log(this.Ciop_pn(p, nj)));
				this.updateDenom_1(temp1);
				this.updateTerminated(-1);
			};
			es.submit(task1);			

		}

		while (updateTerminated(0)>0);
		
		Vector<Path> paths_in = this.indirectPaths(n1, n2, Constants.METHOD_LDSD_INCOMING);
		updateTerminated(paths_in.size());
		for(Path path:paths_in) {
			Node p = path.getTriples().get(0).getPredicate();
			Node nj = path.getTriples().get(0).getSubject();
			
			Runnable task1 = () -> {
				double temp1 = 1/(1 + Math.log(this.Ciip_pn(p, nj)));
				this.updateDenom_2(temp1);
				this.updateTerminated(-1);
			};
			es.submit(task1);			
		}

		while (updateTerminated(0)>0);
		
		es.shutdown();

		double d1 = updateDenom_1(0);
		double d2 = updateDenom_2(0);
		
		result = d1 + d2;
		return result;
	}
	
	private synchronized double updateDenom(double v) {
		this.denom = this.denom + v;
		return this.denom;
	}
	
	private synchronized double updateDenom_1(double v) {
		this.denom_1 = this.denom_1 + v;
		return this.denom_1;
	}
	
	private synchronized double updateDenom_2(double v) {
		this.denom_2 = this.denom_2 + v;
		return this.denom_2;
	}
	
	private synchronized int updateTerminated(int v) {
		this.terminated = this.terminated + v;
		return terminated;
	}
	
	synchronized int read_i () {
		return this.i++;
	}
}
