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

package it.cnr.iasi.saks.semrel.method.asrmp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class WSRM {

	public double wsrm(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0.0d;
		
		//compute the numerator of the wsrm function
		PathPattern pattern = new PathPattern(null);
		Node p = NodeFactory.createVariable("p1");		
		Triple t = new Triple(n1, p, n2);
		pattern.getTriples().add(t);
		pattern.setDistinct(Constants.SPARQL_DISTINCT);
		double numer = kb.countPathsByPattern(pattern);
//		System.out.println(pattern+": "+numer);
		
		//compute the denominator of the wsrm function
		PathPattern pattern2 = new PathPattern(null);
		Node p2 = NodeFactory.createVariable("p2");		
		Node o2 = NodeFactory.createVariable("o2");		
		Triple t2 = new Triple(n1, p2, o2);
		pattern2.getTriples().add(t2);
		pattern2.setDistinct(Constants.SPARQL_DISTINCT);
		pattern2.getVarsToSelect().add("p2");
		double denom = kb.countNodesByPattern(pattern2);
//		System.out.println(pattern2+": "+denom);
		
		result = numer/denom;
		
		return result;
	}
	
}
