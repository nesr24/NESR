package it.cnr.iasi.saks.kg.extension;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;
import it.cnr.iasi.saks.semrel.method.asrmp.ASRMP_a;
import it.cnr.iasi.saks.semrel.method.asrmp.aggregation.TNorm_H_Lambda;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class KGExtension {
	KnowledgeBase kb = null;
	String namespace = "";
	double threshold = 0.1d;
	String out_discarded = "target/test-classes/semrel/pizza/results/discarded_"+LocalDate.now().getYear()+LocalDate.now().getMonthValue()+LocalDate.now().getDayOfMonth()+".csv";
	ExtensionResults results = new ExtensionResults();
	Map<Node, Integer> candidateNewNodes_count = new HashMap<Node, Integer>();
	final public String KEY_SEPARATOR = "####";
	
	public KGExtension(KnowledgeBase kb, String namespace) {
		this.setKb(kb);
		this.setNamespace(namespace);
	}
	
	public KnowledgeBase getKb() {
		return kb;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String ns) {
		this.namespace = ns;
	}

	public String getOut_discarded() {
		return out_discarded;
	}

	public void setOut_discarded(String out_discarded) {
		this.out_discarded = out_discarded;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public ExtensionResults getResults() {
		return results;
	}

	public void setResults(ExtensionResults er) {
		this.results = er;
	}

	public Map<Node, Integer> getCandidateNewNodes_count() {
		return candidateNewNodes_count;
	}

	public void setCandidateNewNodes_count(Map<Node, Integer> candidateNewNodes_count) {
		this.candidateNewNodes_count = candidateNewNodes_count;
	}

	public ExtensionResults suggestNodes(Vector<Node> seeds, SemanticRelatednessStrategy method, Context contexts) {
		
    	for(int i=0; i<seeds.size(); i++) {
        	for(int j=i+1; j<seeds.size(); j++) {
        		Node n1 = seeds.get(i);
        		Node n2 = seeds.get(j);
        		if(!(n1.getURI().toString().equalsIgnoreCase(n2.getURI().toString()))) {					
        			this.suggestNodes(n1, n2, method);						
        		}
        	}
    	}
	
   	
    	for(Node cn:this.getCandidateNewNodes_count().keySet()) {
    		if(isInContext(cn, contexts));
    		else {
    	    	for(int i=0; i<seeds.size(); i++) {
    	        	for(int j=i+1; j<seeds.size(); j++) {
    	        		String key = seeds.get(i).getURI().toString()+this.KEY_SEPARATOR+seeds.get(j).getURI().toString();
    	        		if(this.results.getNewNode(key, cn)!=null) {
    	        			double value = this.results.getNewNode(key, cn);
//    	        			this.results.getNewNodes().get(key).remove(cn);
    	        			this.results.addNotInContextNode(key, cn, value);
    	        		}
    	        	}
    	    	}

    		}
    	}
		return this.getResults();
	}
	
	public Map<Node, Double> suggestNodes(Node n1, Node n2, SemanticRelatednessStrategy method) {
		Map<Node, Double> result = new HashMap<Node, Double>();

		String key = n1.getURI().toString()+this.KEY_SEPARATOR+n2.getURI().toString();
		
		Map<Node, Double> temp = weightNodesInPaths(n1, n2, method);
//		System.out.println("result"+result.size());
		for(Node n:temp.keySet()) {
			if(isDiscarded(n)) {
				this.results.addDiscardedNode(key, n, temp.get(n));
			}
			else {
				result.put(n, temp.get(n));
				this.results.addNewNode(key, n, temp.get(n));
				Integer count = this.candidateNewNodes_count.get(n);
				if(count!=null)
					this.candidateNewNodes_count.put(n, count.intValue()+1);
				else
					this.candidateNewNodes_count.put(n, 1);
			}
		}		
//		System.out.println("result"+result.size());
		
		return result;
	}
	
	public Map<Node, Double> weightNodesInPaths(Node n1, Node n2, SemanticRelatednessStrategy method) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		
		Vector<Path> paths = kb.paths(n1, n2, 2, Constants.UNDIRECTED_PATH, true);
//		System.out.println(paths);
		
		Set<Node> newNodes = new HashSet<Node>();
		for(Path p:paths) {
			newNodes.addAll(newNodesInPath(p, n1, n2));
		}
		
		for(Node n:newNodes) {
			double w = weightNode(n1, n2, n, method);
			if(w > this.getThreshold()) { 
				result.put(n, weightNode(n1, n2, n, method));
			}
		}
		
		return result;
	}
	

	public Double weightNode(Node n1, Node n2, Node n, SemanticRelatednessStrategy method) {
		Double result = 0.0d;
		result = (method.semrel(n1, n) + method.semrel(n2, n))/2;
		return result;
	}
	
	public Set<Node> newNodesInPath(Path p, Node n1, Node n2) {
		Set<Node> result = new HashSet<Node>();
		
		String s1 = n1.getURI().toString();
		String s2 = n2.getURI().toString();
		
		for(Triple t:p.getTriples()) {
			if(!(t.getSubject().getURI().toString().equalsIgnoreCase(s1) || t.getSubject().getURI().toString().equalsIgnoreCase(s2)))
				result.add(t.getSubject());
			else if(!(t.getObject().getURI().toString().equalsIgnoreCase(s1) || t.getObject().getURI().toString().equalsIgnoreCase(s2)))
					result.add(t.getObject());
		}
		
//		System.out.println(result);
		return result;
	}

	
	public boolean isCommonNoun(Node n1) {
		boolean result = false;
		
		boolean temp01 = false;
		PathPattern pattern = new PathPattern(new Vector<String>());
		Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Country");
		Triple t1 = new Triple(n1, p1, o1);
				
		pattern.getTriples().addElement(t1);

		temp01 = this.getKb().pathExistence(pattern);
		
//		System.out.println(n1+" "+temp01);
		
		if(!temp01) {
		
			pattern = new PathPattern(new Vector<String>());
			p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			o1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Person");
			t1 = new Triple(n1, p1, o1);
					
			pattern.getTriples().addElement(t1);

			temp01 = this.getKb().pathExistence(pattern);
			
//			System.out.println(n1+" "+temp01);
			
			if(!temp01) {
				
				boolean temp1 = false;
				pattern = new PathPattern(new Vector<String>());
				p1 = NodeFactory.createURI(Constants.RDF_TYPE);
				o1 = NodeFactory.createVariable("o");
				t1 = new Triple(n1, p1, o1);
				
				Node p2 = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
				Node o2 = NodeFactory.createURI(Constants.OWL_THING);
				Triple t2 = new Triple(o1, p2, o2);
				
				pattern.getTriples().addElement(t1);
				pattern.getTriples().addElement(t2);
		
				temp1 = this.getKb().pathExistence(pattern);
				
//				System.out.println(n1+" "+temp1);
				
				
				boolean temp2 = false;
				pattern = new PathPattern(new Vector<String>());
				p1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_TYPE);
				o1 = NodeFactory.createVariable("o");
				t1 = new Triple(n1, p1, o1);
						
				pattern.getTriples().addElement(t1);
		
				temp2 = this.getKb().pathExistence(pattern);
				
//				System.out.println(n1+" "+temp2);
				
				if(!(temp1 || temp2)) {
					result = true;
				}
				else {
				
					boolean temp3 = false;
					pattern = new PathPattern(new Vector<String>());
					p1 = NodeFactory.createURI(Constants.RDF_TYPE);
					o1 = NodeFactory.createVariable("o");
					t1 = new Triple(o1, p1, n1);
							
					pattern.getTriples().addElement(t1);
			
					temp3 = this.getKb().pathExistence(pattern);
					
//					System.out.println(n1+" "+temp3);
					
					if(temp3) 
						result = true;
					else {
						boolean temp4 = false;
						pattern = new PathPattern(new Vector<String>());
						p1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_TYPE);
						o1 = NodeFactory.createVariable("o");
						t1 = new Triple(o1, p1, n1);
								
						pattern.getTriples().addElement(t1);
				
						temp4 = this.getKb().pathExistence(pattern);
						
//						System.out.println(n1+" "+temp4);
						
						if(temp4) 
							result = true;
					}
				}

			}
		}
		return result;
	}
	
	public boolean isDiscarded(Node n) {
		boolean result = true;
		
		if(this.isCommonNoun(n))
			result = false;
	
//		System.out.println(n+": "+result);
		
		return result;
	}
	
	public boolean _isInContext(Node n1, Context contexts) {
		boolean result = false;
		
//		int threshold = context.getContext().size()/10;
		
		int threshold = 0;
		
		int count = 0;

//		for(Context context:contexts) {
			Vector<Node> cnt = new Vector<Node>();
			cnt.addAll(contexts.getContext().keySet());
			
	    	for(int i=0; i<cnt.size(); i++) {
	        	for(int j=i+1; j<cnt.size(); j++) {
	        		String key = cnt.get(i).getURI().toString()+this.KEY_SEPARATOR+cnt.get(j).getURI().toString();
	        		
	        		if(results.getNewNode(key, n1)!=null)
	        			count++;        		
	        	}
	    	}
				
			if(count>=(contexts.getContext().size()/10))
				result = true;
//		}
		
		return result;
	}
	
	
	public boolean isInContext(Node n1, Context contexts) {
		boolean result = false;

		int threshold = 3;
		int count = 0;
		
		for(Node seed:contexts.getContext().keySet()) {
			System.out.print(n1.getURI().toString()+" --- "+seed.getURI().toString()+": ");
			Vector<Path> paths = kb.paths(n1, seed, 1, Constants.UNDIRECTED_PATH, true);	
			if(paths.size()>0) {
				count++;
			}
			System.out.println(count);	
		}
		System.out.println(count);
		if(count>threshold)
			result = true;
		return result;
	}	

}
