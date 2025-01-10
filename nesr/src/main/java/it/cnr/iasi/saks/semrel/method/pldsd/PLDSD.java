package it.cnr.iasi.saks.semrel.method.pldsd;

import java.util.HashSet;
import java.util.Set;
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
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_combined_weighted;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_direct_weighted;

public class PLDSD  implements SemanticRelatednessStrategy {
	KnowledgeBase kb = null;
	int minLength = 1;
	int maxLength = 2;
	String mode = Constants.UNDIRECTED_PATH;
	boolean acyclic = true; 
	LDSD_combined_weighted ldsd = new LDSD_combined_weighted(kb);
	
	public double finalResult = 0.0d;
	public int terminated = 0;
	int i=0;
	
	public PLDSD(KnowledgeBase kb) {
		this.setKb(kb);
		this.setLdsd(new LDSD_combined_weighted(kb));
	}
	
	public PLDSD(KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic) {
		this.setKb(kb);
		this.setMinLength(minLength);
		this.setMaxLength(maxLength);
		this.setMode(mode);
		this.setAcyclic(acyclic);
		this.setLdsd(new LDSD_combined_weighted(kb));
		finalResult = 0.0d;
	}

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	public LDSD_combined_weighted getLdsd() {
		return ldsd;
	}

	public void setLdsd(LDSD_combined_weighted ldsd) {
		this.ldsd = ldsd;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isAcyclic() {
		return acyclic;
	}

	public void setAcyclic(boolean acyclic) {
		this.acyclic = acyclic;
	}

/*	
	public Vector<Node> intermediateNodes(Node n1, Node n2) {
		Vector<Node> result = new Vector<Node>();
		
		Set<Node> intermediateNodesBecauseOf_n1 = new HashSet<Node>();
		intermediateNodesBecauseOf_n1.addAll(intermediateNodesBecauseOfNode(n1, Constants.METHOD_LDSD_INCOMING));
		intermediateNodesBecauseOf_n1.addAll(intermediateNodesBecauseOfNode(n1, Constants.METHOD_LDSD_OUTGOING));
		
		Set<Node> intermediateNodesBecauseOf_n2 = new HashSet<Node>();
		intermediateNodesBecauseOf_n2.addAll(intermediateNodesBecauseOfNode(n2, Constants.METHOD_LDSD_INCOMING));
		intermediateNodesBecauseOf_n2.addAll(intermediateNodesBecauseOfNode(n2, Constants.METHOD_LDSD_OUTGOING));
		
		result = intersection(intermediateNodesBecauseOf_n1, intermediateNodesBecauseOf_n2);
		
		return result;
	}
	
	public Set<Node> intermediateNodesBecauseOfNode(Node n1, String direction) {
		Set<Node> result = new HashSet<Node>();
		
		PathPattern pattern = new PathPattern(null);
		Node nx1 = NodeFactory.createVariable("nx1");
		Node nx2 = NodeFactory.createVariable("nx2");
		Node px1 = NodeFactory.createVariable("px1");
		Triple t1 = null;
		Triple t2 = null;
		
		if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_INCOMING)) {
			t1 = new Triple(nx2, px1, n1);
			t2 = new Triple(nx2, px1, nx1);
		}
		else if(direction.equalsIgnoreCase(Constants.METHOD_LDSD_OUTGOING)) {
			t1 = new Triple(n1, px1, nx2);
			t2 = new Triple(nx1, px1, nx2);
		}
		
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		Filter f = new Filter();
		f.mustBeDifferent(n1, nx1);
		Filter f1 = new Filter();
		f1.mustBeDifferent(nx2, NodeFactory.createURI(Constants.OWL_THING));
		Filter f2 = new Filter();
		f2.mustBeDifferent(nx1, NodeFactory.createURI(Constants.OWL_THING));
		Filter f3 = new Filter();
		f3.mustBeDifferent(px1, NodeFactory.createURI(Constants.RDF_TYPE));
		pattern.getFilters().add(f);
		pattern.getFilters().add(f1);
		pattern.getFilters().add(f2);
		pattern.getFilters().add(f3);
		pattern.getVarsToSelect().add("nx1");
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		
		result.addAll(this.getKb().nodesByPattern(pattern));
		return result;
	}
	
	public static Vector<Node> intersection(Set<Node> s1, Set<Node> s2) {
		Vector<Node> result = new Vector<Node>();
		for(Node n:s1)
			result.add(n);
		result.retainAll(s2);
		return result;
	}
*/
	
	public double pldsd_(Node n1, Node n2) {
		double result = 0.0d;
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());
		boolean pathLengthOneToDo = true;
		double temp = 0.0d;
		for(Path p:paths) {
			if((p.size() == 1) && pathLengthOneToDo) { 
				temp = 1 - this.getLdsd().semrel(n1, n2);
				pathLengthOneToDo = false;
			}
			else if(p.size() == 2) {
				temp = 1 - ((1- this.getLdsd().semrel(p.getTriples().get(0).getSubject(), p.getTriples().get(0).getObject())) * (1 - this.getLdsd().semrel(p.getTriples().get(1).getSubject(), p.getTriples().get(1).getObject())));
			}
			if(temp > result)
				result = temp;
		}
		return result;
	}
	
	// This is the parallelized version of the method.
	public double pldsd(Node n1, Node n2) {
		double result = 0.0d;
		
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());
		boolean pathLengthOneToDo = true;
		ExecutorService es=Executors.newFixedThreadPool(7);
		
		this.terminated = paths.size();
		for(Path p:paths) {
//			System.out.println(i++ + " - "+ p);
			if((p.size() == 1) && pathLengthOneToDo) { 
				Runnable task1 = () -> {
//					int x = read_i();
					double temp1 = 1 - this.getLdsd().semrel(n1, n2);
					checkAndUpdate(temp1);
					updateOrReadTerminated("UPDATE");
//					System.out.println("x="+x);
				};
				es.submit(task1);
				pathLengthOneToDo = false;
			}
			else if((p.size() == 1) && !pathLengthOneToDo)
				updateOrReadTerminated("UPDATE");
			else if(p.size() == 2) {
				Runnable task2 = () -> {
//					int x = read_i();
					double temp2 = 1 - ((1- this.getLdsd().semrel(p.getTriples().get(0).getSubject(), p.getTriples().get(0).getObject())) * (1 - this.getLdsd().semrel(p.getTriples().get(1).getSubject(), p.getTriples().get(1).getObject())));
					checkAndUpdate(temp2);
					updateOrReadTerminated("UPDATE");
//					System.out.println("x="+x);
				};
				es.submit(task2);
			}
		}
		while (updateOrReadTerminated("")>0);
		
		es.shutdown();
		
		
		result = finalResult;
		return result;
	}
	
		
	synchronized void checkAndUpdate(double temp) {
		if(temp > this.finalResult)
			this.finalResult = temp;
	}
	
	synchronized int updateOrReadTerminated (String option) {
		if(option.equalsIgnoreCase("UPDATE")) {
			this.terminated--;
		}
		return this.terminated;
	}
	
	
		
	synchronized int read_i () {
		i++;
		return this.i;
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		double temp1 = this.pldsd(n1, n2);
		double temp2 = this.pldsd(n2, n1);
		result = Math.max(temp1, temp2);
		
		return result;
	}
}
