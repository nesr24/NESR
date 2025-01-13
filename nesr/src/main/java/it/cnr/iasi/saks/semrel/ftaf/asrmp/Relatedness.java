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
package it.cnr.iasi.saks.semrel.ftaf.asrmp;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.ftaf.FTAF;

/**
 * The implementation of this semantic relatedness method refers to the following publication
 * Cheikh Brahim El Vaigh, Goasdoué F., Gravier G., Sébillot P.
 * A Novel Path-Based Entity Relatedness Measure for Efficient Collective Entity Linking
 * ISWC (1) 2020: 164-182
 *  
 * @author francesco
 *
 */
public class Relatedness {
	

	public static double semrel(Node n1, Node n2, ASRMP_Interface method) {
		double result = 0.0d;
		
		if(method instanceof ASRMP_a) {
			result = (method.semrel(n1, n2) + method.semrel(n2, n1))/2.0d;
			System.out.println("ASRMP_a_"+((ASRMP_a)method).getLength()+" ORIGINAL="+result);
			FTAF ftaf = new FTAF();
			result = result + (1-result)*ftaf.semsim_types(((ASRMP_a) method).getKb(), n1, n2, ((ASRMP_a) method).getWt());
			//			result = method.semrel(n1, n2);
		}
		if(method instanceof ASRMP_b) {
			result = (method.semrel(n1, n2) + method.semrel(n2, n1))/2.0d;
//			result = method.semrel(n1, n2);
		}
		return result;
	}
}
