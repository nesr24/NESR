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
package it.cnr.iasi.saks.semrel.method.loddo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.KnowledgeBase;

/**
 *  
 * @author ftaglino
 *
 */
public abstract class Loddo_Abstract_withTerms extends Loddo_Abstract {
	private KnowledgeBase kb = null;
	Map<Node, Set<String>> descriptions = new HashMap<Node, Set<String>>();
	
	
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
 	
	public Map<Node, Set<String>> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<Node, Set<String>> descriptions) {
		this.descriptions = descriptions;
	}

	public Set<String> descriptionWithTerms(Node n) {
		Set<String> result = new HashSet<String>();
		
		result = this.getDescriptions().get(n);
		
		return result;
	}
			
	public void loadDescriptionsByTerms(KnowledgeBase kb, String descrFilename, String prefix, String namespace) {
		BufferedReader br = null;
    	try {     
        	br = new BufferedReader(new FileReader(kb.getClass().getResource(descrFilename).getFile()));
        	
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, "\t");
            	String term = st.nextToken();
            	String uri = st.nextToken().replace(prefix, namespace).trim();
            	Set<String> description = new HashSet<String>();
            	if(st.hasMoreTokens()) {
            		String descriptionAsString = st.nextToken();
            		descriptionAsString = descriptionAsString.substring(0, descriptionAsString.length()-5);
            		StringTokenizer st2 = new StringTokenizer(descriptionAsString, " ");
            		while(st2.hasMoreTokens()) {
            			String d_term = st2.nextToken();
            			description.add(d_term);
            		}
            	}
        		Node n = NodeFactory.createURI(uri);
        		this.getDescriptions().put(n, description);
            }
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}

	}
}
