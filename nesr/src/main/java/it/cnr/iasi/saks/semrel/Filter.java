/*
 * 	 This file is part of SemRel, originally promoted and
 *	 developed at CNR-IASI. For more information visit:
 *	 http://saks.iasi.cnr.it/tools/semrel
 *	     
 *	 This is free software: you can redistribute it and/or modify
 *	 it under the terms of the GNU General Public License as 
 *	 published by the Free Software Foundation, either version 3 of the 
 *	 License, or (at your option) any later version.
 *	 
 *	 This software is distributed in the hope that it will be useful,
 *	 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	 GNU General Public License for more details.
 * 
 *	 You should have received a copy of the GNU General Public License
 *	 along with this source.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.cnr.iasi.saks.semrel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.jena.graph.Node;

/**
 * 
 * @author francesco
 *
 */
public class Filter {
	private String value = "";
	private final static String property_in_filename = "/filters/prop_filters_in.txt";
	
	public Filter() {
		super();
	}
	
	public Filter(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
	
	public static String generateFilterIn_nodesInDBO(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"\")) ";	//DBO
		return result;
	}
	
	public static String generateFilterIn_nodesInDBR(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBR_NS+"\")) ";	//DBR
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBR_or_DBO(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.DBPEDIA_DBR_NS+"\")) ";				//DBR
		return result;
	}
	
	public static String generateFilterIn_nodesInRDF(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.RDF_NS+"\")) ";	//RDF
		return result;
	}
	
	public static String generateFilterIn_nodesInRDFS(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.RDFS_NS+"\")) ";	//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesInOWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.OWL_NS+"\")) ";	//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_RDF_or_RDFS_or_OWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"|"	//RDFS
				+"^"+Constants.OWL_NS+"\")) ";				//OWL
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_RDF_or_RDFS(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"\")) ";				//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_DBR_or_RDF_or_RDFS_or_OWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.DBPEDIA_DBR_NS+"|"	//DBR
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"|"	//RDFS
				+"^"+Constants.OWL_NS+"\")) ";				//OWL
		return result;
	}
	
	public static String generateFilterOut_nodesEquality(String v1, String v2) {
		String result = ""; 
		result = " FILTER(STR(?"+v1+") != STR(?"+v2+")) "; 
		return result;
	}
	
	public static String generateFilterOut_nodesInDBO(String var) {
		String result = ""; 
		result = " FILTER(!(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_WIKIPAGE+"|"	//wikiPageRedirects and wikiPageDisambiguates
				+"^"+Constants.DBPEDIA_DBO_THUMBNAIL+"\"))) ";				//thumbnail
		return result;
	}
	
	public static String generateFilterOut_isLiteral(String var) {
		String result = ""; 
		result = " FILTER(!(isLiteral(?"+var+")))";
		return result;
	}
	
	public String generateFilterIn_PropertiesFromFile(String var) {
		String result = "";

		BufferedReader br = null;
        try {     
        	br = new BufferedReader(new FileReader(this.getClass().getResource(Filter.property_in_filename).getFile()));
            String line;
            
            
            line = br.readLine();

            result = " FILTER(REGEX(?"+var+", \""+line+"\") ";
            while ((line = br.readLine()) != null) {
        		result = result + " || REGEX(?"+var+", \""+line+"\") ";				
            }
            result = result + ")";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
		return result;
	}
	
	public void mustBeDifferent(Node n1, Node n2) {
		String result = "FILTER ( ";
		if(n1.isVariable())
			result = result + n1 + " != ";
		else
			result = result + "<"+ n1 + "> != ";
		if(n2.isVariable())
			result = result + n2 + ") ";
		else
			result = result + "<"+ n2 + ">) ";
		this.setValue(result);
	}
	
	@Override
	public boolean equals(Object f) {
		boolean result = false;
		if(f instanceof Filter) {
			if(this.getValue().equals(((Filter)f).getValue()))
				result = true;
		}
		return result;
	}
	
	@Override
	public int hashCode(){
		return this.getValue().hashCode();
	}
	
	@Override
	public String toString() {
		String result = "";
		result = this.getValue();
		return result;
	}
	
}


