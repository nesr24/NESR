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
package it.cnr.iasi.saks.semrel.method.reword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.Utils;

public class Reword_Complete extends Reword_Mip {
	
	public Reword_Complete(KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic) {
		super(kb, minLength, maxLength, mode, acyclic);
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0;		
	
		Reword_Simple reword_simple = new Reword_Simple(this.getKb());  
		
		Map<Node, Double> n1_relSpace_in = reword_simple.relatednessSpace_in(n1);
		
		Map<Node, Double> n2_relSpace_in = reword_simple.relatednessSpace_in(n2);
		
		// Add the contribution of the mip		
		mip(n1, n2);
		if(this.getMip() != null) {
			Map<Triple, Double> triple_informativeness = path_triple_informativeness(this.getMip().getPath());
			for(Triple t:triple_informativeness.keySet()) {
				Node p = t.getPredicate();
				if(n1_relSpace_in.containsKey(p))
					n1_relSpace_in.put(p, n1_relSpace_in.get(p) + triple_informativeness.get(t));
				else n1_relSpace_in.put(p, triple_informativeness.get(t));
				if(n2_relSpace_in.containsKey(p))
					n2_relSpace_in.put(p, n2_relSpace_in.get(p) + triple_informativeness.get(t));
				else n2_relSpace_in.put(p, triple_informativeness.get(t));
			}
		}
		result = cosine(n1_relSpace_in, n2_relSpace_in);

		return result;
	}
}
