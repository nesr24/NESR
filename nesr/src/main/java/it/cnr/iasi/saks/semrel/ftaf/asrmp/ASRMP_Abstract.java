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

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.ftaf.asrmp.aggregation.AggregationStrategy_Interface;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy_OntModel;
import it.cnr.iasi.saks.semrel.ftaf.FTAF;

public abstract class ASRMP_Abstract implements ASRMP_Interface {	
	public double pathAggregation(Path path, KnowledgeBase kb, AggregationStrategy_Interface aggregationStrategy, WeightedTaxonomy_OntModel wt) {
		double result = 0.0d;		
		WSRM wsrm = new WSRM();
		
		Vector<Triple> triples = path.getTriples();
		double temp = wsrm.wsrm(triples.get(0).getSubject(), triples.get(0).getObject(), kb);
//		System.out.println("wsrm("+triples.get(0).getSubject()+", "+triples.get(0).getObject()+")="+temp);
		if(path.size()>1) {
			for(int i=1; i<triples.size(); i++) {
				Node s = triples.get(i).getSubject();
				Node p = triples.get(i).getPredicate();
				Node o = triples.get(i).getObject();
//				if((s.getURI().toString().startsWith(Constants.DBPEDIA_DBR_NS)) && 
//						(p.getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS)) &&
//						(o.getURI().toString().startsWith(Constants.DBPEDIA_DBR_NS))) {
	
				if(false) {
					FTAF ftaf = new FTAF();
					double wsrm_value = wsrm.wsrm(s, o, kb);
					//double ftaf_value = ftaf.dbr_dbo_dbr__ft(kb, s, p, o);
						
					double ftaf_value = ftaf.dbr_dbo_dbr__af(kb, s, p, o, wt);
					
					//wsrm_value = (wsrm_value + ftaf_value)/2;
					
//					System.out.println("wsrm_value="+wsrm_value);
					//System.out.println("ftaf_value="+ftaf_value);
					
					wsrm_value = wsrm_value + (1-wsrm_value)*ftaf_value;
										
					
					//double wsrm_value = ftaf_value;
					
					temp = aggregatingValues(temp, wsrm_value, aggregationStrategy);
				}
				else {				
					double wsrm_value = wsrm.wsrm(s, o, kb);
					//				System.out.println("wsrm("+triples.get(i).getSubject()+", "+triples.get(i).getObject()+")="+wsrm_value);
					temp = aggregatingValues(temp, wsrm_value, aggregationStrategy);
				}
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
