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

import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.KnowledgeBase;

/**
 * 
 * @author francesco
 *
 */
public class Lod_Overlap extends Loddo_Abstract {
	
	public Lod_Overlap(KnowledgeBase kb) {
		this.setKb(kb);
	}

	
	public double semrel(Node n1, Node n2) {
		
		Set<String> desc1 = this.description(n1);
		Set<String> desc2 = this.description(n2);

		double min = this.min(desc1, desc2);
		
//		System.out.println("overlap");
		double result = 0;
		double temp =	((double)this.commonDescription(desc1, desc2).size()) / ((double)(min));
		if(!(Double.isNaN(temp)))
			result = temp; 
		return result;
	}

}
