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

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;
import it.cnr.iasi.saks.semrel.method.asrmp.aggregation.AggregationStrategy_Interface;

public abstract class ASRMP_Abstract implements SemanticRelatednessStrategy {	
	public double pathAggregation(Path path, KnowledgeBase kb, AggregationStrategy_Interface aggregationStrategy) {
		double result = 0.0d;
		WSRM wsrm = new WSRM();
		
		Vector<Triple> triples = path.getTriples();
		double temp = wsrm.wsrm(triples.get(0).getSubject(), triples.get(0).getObject(), kb);
//		System.out.println("wsrm("+triples.get(0).getSubject()+", "+triples.get(0).getObject()+")="+temp);
		if(path.size()>1) {
			for(int i=1; i<triples.size(); i++) {
				double wsrm_value = wsrm.wsrm(triples.get(i).getSubject(), triples.get(i).getObject(), kb);
				//				System.out.println("wsrm("+triples.get(i).getSubject()+", "+triples.get(i).getObject()+")="+wsrm_value);
				temp = aggregatingValues(temp, wsrm_value, aggregationStrategy);
			}
		}		
		result = temp;
		return result;
	}
	
	
	public double aggregatingValues(double x, double y, AggregationStrategy_Interface ag) {
		double result = 0.0d;
		result = ag.t_norm(x, y);
		return result;
	}		
}
