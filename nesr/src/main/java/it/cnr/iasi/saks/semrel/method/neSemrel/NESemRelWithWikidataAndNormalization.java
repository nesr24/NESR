package it.cnr.iasi.saks.semrel.method.neSemrel;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraph;

public class NESemRelWithWikidataAndNormalization extends NESemRel_Abstract {

	private int n1_count_abstract = 0;
	private int n1_count_comment = 0;
	private int n1_count_literal_other = 0;
	private int n1_count_uri = 0;
	private int n1_count_uri_dbo = 0;
	private int n1_count_uri_other = 0;

	private int n2_count_abstract = 0;
	private int n2_count_comment = 0;
	private int n2_count_literal_other = 0;
	private int n2_count_uri = 0;
	private int n2_count_uri_dbo = 0;
	private int n2_count_uri_other = 0;
	
	final static private String COUNT_ABSTRACT = "ca";
	final static private String COUNT_COMMENT = "cc";
	final static private String COUNT_LITERAL_OTHER = "co";
	final static private String PRED_OBJ = "po";
	
	
	final static public String N1_COUNT_ABSTRACT = "ab_1";
	final static public String N1_COUNT_COMMENT = "cm_1";
	final static public String N1_COUNT_LITERAL_OTHER = "lo_1";
	final static public String N1_COUNT_WIKIPAGEWIKILINK = "wp_1";
	final static public String N1_COUNT_URI_DBO = "db_1";
	final static public String N1_COUNT_SEEALSO = "sa_1";
	final static public String N1_COUNT_URI = "ur_1";	
	final static public String N1_COUNT_URI_OTHER = "uo_1";	
	
	final static public String N2_COUNT_ABSTRACT = "ab_2";
	final static public String N2_COUNT_COMMENT = "cm_2";
	final static public String N2_COUNT_LITERAL_OTHER = "lo_2";
	final static public String N2_COUNT_WIKIPAGEWIKILINK = "wp_2";
	final static public String N2_COUNT_URI_DBO = "db_2";
	final static public String N2_COUNT_SEEALSO = "sa_2";
	final static public String N2_COUNT_URI = "ur_2";	
	final static public String N2_COUNT_URI_OTHER = "uo_2";	

	
	public NESemRelWithWikidataAndNormalization(RDFGraph kb, String wikidata_server) {
		this.setKb(kb);
		this.setWikidataServer(wikidata_server);
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
		
		return result;
	}
	
	public double adjacentNodesRelatedness(Node n1, Node n2) {
//		System.out.println("adjacentNodesRelatedness on "+n1.getURI().toString()+" --- "+n2.getURI().toString());
//		double result = 0.0d;
		double result = 0.0d;
		Vector<Vector<Node>> n1_p_o = this.getKb().predicatesAndNodesBySubject(n1);
		Vector<Vector<Node>> n1_predicatesWithObjects_uri = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n1_predicatesWithObjects_uri_dbo = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n1_predicatesWithObjects_literal = new Vector<Vector<Node>>();
		int n1_wikiPageWikiLink = 0;
		int n1_seeAlso = 0;
				
		
		Vector<Node> n1_types = new Vector<Node>();
		Vector<Node> n2_types = new Vector<Node>();
				
		//Get all the pairs (predicate, object) of triples having n1 as the subject
		//About triples with a literal as the object, only the one in English language are taken. 
		for(Vector<Node> r:n1_p_o) {
			Vector<Node> temp = new Vector<Node>();
			temp.add(r.get(0));
			temp.add(r.get(1));
			if(r.get(1).isLiteral()) {
				if(r.get(1).getLiteral().toString().endsWith("@en"))
					n1_predicatesWithObjects_literal.add(temp);
			}
			else if(r.get(1).isURI()) {
				if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.DBPEDIA_DBO_WIKIPAGE)&&r.get(1).getURI().toString().equalsIgnoreCase(n2.getURI().toString()))
					n1_wikiPageWikiLink = 1;
				else
					if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.RDFS_SEEALSO)&&r.get(1).getURI().toString().equalsIgnoreCase(n2.getURI().toString())) {
						n1_seeAlso = 1;
					}
				else
					n1_predicatesWithObjects_uri.add(temp);
				if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.RDF_TYPE))
					if(r.get(1).getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS))
						n1_types.add(r.get(1));
			}
		}
		
		
/*		if(n1_types.contains(Constants.DBPEDIA_DBO_PERSON))
			System.out.println(n1_predicatesWithObjects_uri);
*/
		
		// Get the wikidata reference to n1
		Node n1_wikidata_id = this.wikidataId(n1_predicatesWithObjects_uri);
		
		Vector<Vector<Node>> n1_predicatesWithObjects_uri_valuable = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n1_predicatesWithObjects_uri_other = new Vector<Vector<Node>>();
		// Remove all the pairs (predicate, object) where object is an URI but is not n2
		n1_predicatesWithObjects_uri_valuable = this.cleanPredicatesWithObject_uri(n1_predicatesWithObjects_uri, n2);
		
		n1_predicatesWithObjects_uri.removeAll(n1_predicatesWithObjects_uri_valuable);
		
		for(Vector<Node> r:n1_predicatesWithObjects_uri_valuable) {
			if(r.get(0).getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS)) 
				n1_predicatesWithObjects_uri_dbo.add(r);
		}
		
		n1_predicatesWithObjects_uri_valuable.removeAll(n1_predicatesWithObjects_uri_dbo);
		
		
		Vector<Vector<Node>> n2_p_o = kb.predicatesAndNodesBySubject(n2);
		Vector<Vector<Node>> n2_predicatesWithObjects_uri = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n2_predicatesWithObjects_uri_dbo = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n2_predicatesWithObjects_literal = new Vector<Vector<Node>>();
		int n2_wikiPageWikiLink = 0;
		int n2_seeAlso = 0;
		
		//Get all the pairs (predicate, object) of triples having n2 as the subject
		for(Vector<Node> r:n2_p_o) {
			Vector<Node> temp = new Vector<Node>();
			temp.add(r.get(0));
			temp.add(r.get(1));
			if(r.get(1).isLiteral()) {
				if(r.get(1).getLiteral().toString().endsWith("@en"))
					n2_predicatesWithObjects_literal.add(temp);
			}
			else if(r.get(1).isURI()) {
				if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.DBPEDIA_DBO_WIKIPAGE)&&r.get(1).getURI().toString().equalsIgnoreCase(n1.getURI().toString()))
					n2_wikiPageWikiLink = 1;
				else
					if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.RDFS_SEEALSO)&&r.get(1).getURI().toString().equalsIgnoreCase(n1.getURI().toString())) {
						n2_seeAlso = 1;
					}
				else
					n2_predicatesWithObjects_uri.add(temp);
				if(r.get(0).getURI().toString().equalsIgnoreCase(Constants.RDF_TYPE))
					if(r.get(1).getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS))
						n2_types.add(r.get(1));
			}
		}
		
		
		// Get the wikidata reference to n2
		Node n2_wikidata_id = this.wikidataId(n2_predicatesWithObjects_uri);
		
		Vector<Vector<Node>> n2_predicatesWithObjects_uri_valuable = new Vector<Vector<Node>>();
		Vector<Vector<Node>> n2_predicatesWithObjects_uri_other = new Vector<Vector<Node>>();
		// Remove all the pairs (predicate, object) where object is an URI but is not n1
		n2_predicatesWithObjects_uri_valuable = this.cleanPredicatesWithObject_uri(n2_predicatesWithObjects_uri, n1);

		n2_predicatesWithObjects_uri.removeAll(n2_predicatesWithObjects_uri_valuable);

		
//		System.out.println("n2_predicatesWithObjects_uri="+n2_predicatesWithObjects_uri.size());
		
		for(Vector<Node> r:n2_predicatesWithObjects_uri_valuable) {
			if(r.get(0).getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS)) 
				n2_predicatesWithObjects_uri_dbo.add(r);
		}
		
		n2_predicatesWithObjects_uri_valuable.removeAll(n2_predicatesWithObjects_uri_dbo);

//		System.out.println("n2_predicatesWithObjects_uri="+n2_predicatesWithObjects_uri.size());
//		System.out.println("n2_predicatesWithObjects_uri_dbo="+n2_predicatesWithObjects_uri_dbo.size());
		
		
		// Get the labels representing linguistic expression for referring to n1 by accessing to wikidata
		Vector<String> n1_labels = this.nodeLabels(n1_wikidata_id);

		// Get the labels representing linguistic expression for referring to n2 by accessing to wikidata
		Vector<String> n2_labels = this.nodeLabels(n2_wikidata_id);

		// Add to n1_predicatesWithObjects_uri_other all the additional (pred, uri) in which uri contains at least linguistic expression for referring to n2		for(Vector<Node> r:n1_predicatesWithObjects_uri) {
		Vector<String> n2_labels_with_underscore = new Vector<String>();
		for(String l:n2_labels) {
			l = l.replace(" ", "_");
			n2_labels_with_underscore.add(l);
		}

		
		for(Vector<Node> r:n1_predicatesWithObjects_uri) {
			if(this.containSubStrings(r.get(1).getURI().toString(), n2_labels_with_underscore))
				n1_predicatesWithObjects_uri_other.add(r);
		}
		
		// Add to n2_predicatesWithObjects_uri_other all the additional (pred, uri) in which uri contains at least linguistic expression for referring to n1
		Vector<String> n1_labels_with_underscore = new Vector<String>();
		for(String l:n1_labels) {
			l = l.replace(" ", "_");
			n1_labels_with_underscore.add(l);
		}
		
//		System.out.println("n1_labels_with_underscore="+n1_labels_with_underscore);
		for(Vector<Node> r:n2_predicatesWithObjects_uri) {
			if(this.containSubStrings(r.get(1).getURI().toString(), n1_labels_with_underscore)) {
				n2_predicatesWithObjects_uri_other.add(r);
			}
		}

		
		// Remove alle the pairs (predicate, object) in which the object does not contain a linguistic expression for referring to n2
		n1_predicatesWithObjects_literal = this.cleanPredicatesWithObject_literal(n1_predicatesWithObjects_literal, n2_labels);

			
		// Remove alle the pairs (predicate, object) in which the object does not contain a linguistic expression for referring to n1
		n2_predicatesWithObjects_literal = this.cleanPredicatesWithObject_literal(n2_predicatesWithObjects_literal, n1_labels);
		
//		System.out.println("\t"+n1.getURI().toString()+" --- URIS");
		
/*		for(Vector<Node> p_o:n1_predicatesWithObjects_uri) {
			System.out.println(p_o);
		}
*/
//		System.out.println("\t"+n2.getURI().toString()+" --- URIS");

		
/*		for(Vector<Node> p_o:n2_predicatesWithObjects_uri) {
			System.out.println(p_o);
		}
*/		
//		System.out.println("\t"+n1.getURI().toString()+" --- LITERALS");

//		System.out.println("n1_count_abstract = "+n1_count_abstract);
		Vector<Vector<Node>> n1_predicatesWithObjects_literal_valuable = new Vector<Vector<Node>>();
		for(Vector<Node> p_o:n1_predicatesWithObjects_literal) {
			if(p_o.get(0).getURI().toString().equalsIgnoreCase(Constants.DBPEDIA_DBO_ABSTRACT)) {
				if(n2_labels.size()>0) {
					n1_count_abstract = this.countSubStringsOccurrences(p_o.get(1).getLiteral().toString(), n2_labels);
					n1_predicatesWithObjects_literal_valuable.add(p_o);
				}
			}	
			else if(p_o.get(0).getURI().toString().equalsIgnoreCase(Constants.RDFS_COMMENT)) {
				if(n2_labels.size()>0) {
					n1_count_comment = this.countSubStringsOccurrences(p_o.get(1).getLiteral().toString(), n2_labels);
					n1_predicatesWithObjects_literal_valuable.add(p_o);
				}
			}
			else 
				if(n2_labels.size()>0) {
					if(this.containSubStrings(p_o.get(1).getLiteral().toString(), n2_labels)) {				
						n1_count_literal_other ++;
						n1_predicatesWithObjects_literal_valuable.add(p_o);
					}
			}
//			System.out.println(p_o.get(0).getURI().toString() + ", "+p_o.get(1).getLiteral().toString() + ", " + n1_count_literal_other);
		}
		
		
/*		
		System.out.println("n1_count_abstract = "+n1_count_abstract);
		System.out.println("n1_count_comment = "+n1_count_comment);
		System.out.println("n1_count_other = "+n1_count_literal_other);	
		System.out.println("count = "+(n1_count_abstract+n1_count_comment+n1_count_literal_other));
*/
		
		Map<String, Object> n1_tsf_literal = typeDependingFeatures_literal(n1_predicatesWithObjects_literal, n1_predicatesWithObjects_literal_valuable, n1_types, n2_types, n2_wikidata_id);
		
/*		System.out.println("n1_count_abstract_additional = "+((int)n1_tsf_literal.get(COUNT_ABSTRACT)));
		System.out.println("n1_count_comment_additional = "+((int)n1_tsf_literal.get(COUNT_COMMENT)));
		System.out.println("n1_count_other_additional = "+((int)n1_tsf_literal.get(COUNT_LITERAL_OTHER)));	
		System.out.println("n1_count_additional = "+(((int)(n1_tsf_literal.get(COUNT_ABSTRACT)))+((int)n1_tsf_literal.get(COUNT_COMMENT))+((int)n1_tsf_literal.get(COUNT_LITERAL_OTHER))));
*/		
		n1_count_abstract = Math.max(n1_count_abstract, (int)n1_tsf_literal.get(COUNT_ABSTRACT));
		n1_count_comment = Math.max(n1_count_comment, (int)n1_tsf_literal.get(COUNT_COMMENT));
		n1_count_literal_other = Math.max(n1_count_literal_other, (int)n1_tsf_literal.get(COUNT_LITERAL_OTHER));
		n1_count_uri = n1_predicatesWithObjects_uri_valuable.size();
		n1_count_uri_dbo = n1_predicatesWithObjects_uri_dbo.size();
		n1_count_uri_other = n1_predicatesWithObjects_uri_other.size();
		
		int n1_count = n1_count_abstract+n1_count_comment+n1_count_literal_other+n1_count_uri_dbo+n1_count_uri+n1_count_uri_other+n1_wikiPageWikiLink+n1_seeAlso;
		
//		System.out.println(n2.getURI().toString()+" --- LITERALS");

		
		Vector<Vector<Node>> n2_predicatesWithObjects_literal_valuable = new Vector<Vector<Node>>();
		for(Vector<Node> p_o:n2_predicatesWithObjects_literal) {
			if(p_o.get(0).getURI().toString().equalsIgnoreCase(Constants.DBPEDIA_DBO_ABSTRACT)) {
				if(n1_labels.size()>0) {
					n2_count_abstract = this.countSubStringsOccurrences(p_o.get(1).getLiteral().toString(), n1_labels);
					n2_predicatesWithObjects_literal_valuable.add(p_o);
				}
			}	
			else if(p_o.get(0).getURI().toString().equalsIgnoreCase(Constants.RDFS_COMMENT)) {
				if(n1_labels.size()>0) {
					n2_count_comment = this.countSubStringsOccurrences(p_o.get(1).getLiteral().toString(), n1_labels);
					n2_predicatesWithObjects_literal_valuable.add(p_o);
				}
			}
			else 
				if(n1_labels.size()>0) {
					if(this.containSubStrings(p_o.get(1).getLiteral().toString(), n1_labels)) {				
						n2_count_literal_other ++;
						n2_predicatesWithObjects_literal_valuable.add(p_o);
					}
			}		
			
		}
//		System.out.println("n2_count_comment="+n2_count_comment);
		
/*		System.out.println("n2_count_abstract = "+n2_count_abstract);
		System.out.println("n2_count_comment = "+n2_count_comment);
		System.out.println("n2_count_other = "+n2_count_literal_other);		
		System.out.println("count = "+(n2_count_abstract+n2_count_comment+n2_count_literal_other));
*/
		Map<String, Object> n2_tsf_literal = typeDependingFeatures_literal(n2_predicatesWithObjects_literal, n2_predicatesWithObjects_literal_valuable, n2_types, n1_types, n1_wikidata_id);
		
/*		System.out.println("n2_count_abstract_additional = "+((int)n2_tsf_literal.get(COUNT_ABSTRACT)));
		System.out.println("n2_count_comment_additional = "+((int)n2_tsf_literal.get(COUNT_COMMENT)));
		System.out.println("n2_count_other_additional = "+((int)n2_tsf_literal.get(COUNT_LITERAL_OTHER)));	
		System.out.println("n2_count_additional = "+(((int)(n2_tsf_literal.get(COUNT_ABSTRACT)))+((int)n2_tsf_literal.get(COUNT_COMMENT))+((int)n2_tsf_literal.get(COUNT_LITERAL_OTHER))));
*/
		n2_count_abstract = Math.max(n2_count_abstract, (int)n2_tsf_literal.get(COUNT_ABSTRACT));
		n2_count_comment = Math.max(n2_count_comment, (int)n2_tsf_literal.get(COUNT_COMMENT));
//		System.out.println("n2_count_comment="+n2_count_comment);
		n2_count_literal_other = Math.max(n2_count_literal_other, (int)n2_tsf_literal.get(COUNT_LITERAL_OTHER));
		n2_count_uri = n2_predicatesWithObjects_uri_valuable.size();
		n2_count_uri_dbo = n2_predicatesWithObjects_uri_dbo.size();
		n2_count_uri_other = n2_predicatesWithObjects_uri_other.size();
		
		
		int n2_count = n2_count_abstract+n2_count_comment+n2_count_literal_other+n2_count_uri_dbo+n2_count_uri+n2_count_uri_other+n2_wikiPageWikiLink+n2_seeAlso;
		
/*		System.out.println("\tn1_count_abstract="+n1_count_abstract);
		System.out.println("\tn1_count_comment="+n1_count_comment);
		System.out.println("\tn1_count_literal_other="+n1_count_literal_other);
		System.out.println("\tn1_count_uri_dbo="+n1_count_uri_dbo);
		System.out.println("\tn1_count_uri="+n1_count_uri);
		System.out.println("\tn1_count_uri_other="+n1_count_uri_other);
		System.out.println("\tn1_wikipage="+n1_wikiPageWikiLink);
		System.out.println("\tn1_seeAlso="+n1_seeAlso);
		System.out.println("\tn1_count="+n1_count);
		
		System.out.println("\tn2_count_abstract="+n2_count_abstract);
		System.out.println("\tn2_count_comment="+n2_count_comment);
		System.out.println("\tn2_count_literal_other="+n2_count_literal_other);
		System.out.println("\tn2_count_uri_dbo="+n2_count_uri_dbo);
		System.out.println("\tn2_count_uri="+n2_count_uri);
		System.out.println("\tn2_count_uri_other="+n2_count_uri_other);
		System.out.println("\tn2_wikipage="+n2_wikiPageWikiLink);
		System.out.println("\tn2_seeAlso="+n2_seeAlso);		
		System.out.println("\tn2_count="+n2_count);
*/		int count = n1_count+n2_count;
		
//		System.out.println(n1.getURI().toString()+";"+n2.getURI().toString()+";"+count);
		
		result = ((double)n1_count_comment + (double)n2_count_comment) * 1.0d +
					((double)n1_count_abstract + (double)n2_count_abstract) *0.8d +
					((double)n1_count_literal_other + (double)n2_count_literal_other) * 0.8 +
					((double)n1_wikiPageWikiLink + (double)n2_wikiPageWikiLink) * 0.6 +
					((double)n1_count_uri_dbo + (double)n2_count_uri_dbo) * 0.6 +
					((double)n1_seeAlso + (double)n2_seeAlso) * 1.0d +
					((double)n1_count_uri + (double)n2_count_uri) * 0.6 +
					((double)n1_count_uri_other + (double)n2_count_uri_other) * 0.1;

		result = result /(((double)n1_count_comment + (double)n2_count_comment) +
				((double)n1_count_abstract + (double)n2_count_abstract) +
				((double)n1_count_literal_other + (double)n2_count_literal_other) +
				((double)n1_wikiPageWikiLink + (double)n2_wikiPageWikiLink) +
				((double)n1_count_uri_dbo + (double)n2_count_uri_dbo) +
				((double)n1_seeAlso + (double)n2_seeAlso) +
				((double)n1_count_uri + (double)n2_count_uri) +
				((double)n1_count_uri_other + (double)n2_count_uri_other));
		
		System.out.println(n1.getURI().toString()+" - "+n2.getURI().toString()+": "+result);
		
/*		result = ((double)n1_count_comment + (double)n2_count_comment) * 1.0d +
				((double)n1_count_abstract + (double)n2_count_abstract) *1.0d +
				((double)n1_count_literal_other + (double)n2_count_literal_other) * 1.0 +
				((double)n1_wikiPageWikiLink + (double)n2_wikiPageWikiLink) * 1.0 +
				((double)n1_count_uri_dbo + (double)n2_count_uri_dbo) * 1.0 +
				((double)n1_seeAlso + (double)n2_seeAlso) * 1.0d +
				((double)n1_count_uri + (double)n2_count_uri) * 1.0 +
				((double)n1_count_uri_other + (double)n2_count_uri_other) * 1.0;
*/		
		return result;
	}

	public Map<String, Object> typeDependingFeatures_literal(Vector<Vector<Node>> p_o, Vector<Vector<Node>> p_o_valuable, Vector<Node> na_types, Vector<Node> nb_types, Node nb_wikidata_id) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(COUNT_ABSTRACT, 0);
		result.put(COUNT_COMMENT, 0);
		result.put(COUNT_LITERAL_OTHER, 0);
		if(nb_types.contains(NodeFactory.createURI(Constants.DBPEDIA_DBO_PERSON))) {
			String family_name = this.family_name(nb_wikidata_id);
//			System.out.println("family_name=-"+family_name+"-");
			for(Vector<Node> v:p_o_valuable) {
				if(v.get(0).getURI().toString().equalsIgnoreCase(Constants.DBPEDIA_DBO_ABSTRACT)) {
					int n_count_abstract_additional = 0;
					if(!family_name.equalsIgnoreCase("")) {
						n_count_abstract_additional = this.countSubStringOccurrences(v.get(1).getLiteral().toString(), family_name);
					}
					result.put(COUNT_ABSTRACT, n_count_abstract_additional);
				}
				if(v.get(0).getURI().toString().equalsIgnoreCase(Constants.RDFS_COMMENT)) {
					int n_count_comment_additional = 0;
					if(!family_name.equalsIgnoreCase("")) {
						n_count_comment_additional = this.countSubStringOccurrences(v.get(1).getLiteral().toString(), family_name);
					}
					result.put(COUNT_COMMENT, n_count_comment_additional);
				}
			}
			
			int n_count_other_additional = 0;
			Vector<Vector<Node>> p_o_additional = new Vector<Vector<Node>>();
			p_o.removeAll(p_o_valuable);
			for(Vector<Node> v:p_o) {
				if(!family_name.equalsIgnoreCase("")) {
					if(v.get(1).getLiteral().toString().contains(family_name)) {
						n_count_other_additional ++;
						p_o_additional.add(v);
					}
				}
			}
			result.put(COUNT_LITERAL_OTHER, n_count_other_additional);
			result.put(PRED_OBJ, p_o_additional);
		}
		return result;
	}
	
}
