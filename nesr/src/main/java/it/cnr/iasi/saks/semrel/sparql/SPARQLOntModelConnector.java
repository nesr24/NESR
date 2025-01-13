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
package it.cnr.iasi.saks.semrel.sparql;

import java.util.Vector;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author ftaglino
 *
 */
public class SPARQLOntModelConnector implements SPARQLConnector{	
	protected final Logger logger = LoggerFactory.getLogger(SPARQLOntModelConnector.class);
		
	/**
	 * Execute a query on the configured Sparql endpoint
	 * @param queryString The SPARQL query
	 * @return Vector<QuerySolution> 
	 */	
	public Vector<QuerySolution> execQuery(String queryString, Object knowledgeSourceRef) {
//		System.out.println("qs="+queryString);
		Vector<QuerySolution> qss = new Vector<QuerySolution>();
		OntModel ontoModel = (OntModel)knowledgeSourceRef;
		QueryExecution qexec = null;
		
		try {	
			Query query = QueryFactory.create(queryString);
			qexec = QueryExecutionFactory.create(query, ontoModel);			
			ResultSet r = qexec.execSelect();
			while(r.hasNext()){
				qss.add(r.next());
			}
			// TO BE REMOVED
			qexec.close();
		}
		catch(Exception ex) {
			this.logger.error("#Query: {}; #Message: {}; #Cause: {}", queryString, ex.getMessage(), ex.getCause());
			for(int i=0; i<ex.getStackTrace().length; i++)
				this.logger.error(ex.getStackTrace()[i].toString());
			ex.printStackTrace();
			System.exit(0);
		}
/*		finally {
			if(qexec!=null)
				qexec.close();
		}
*/		return qss;
	}
	

	/**
	 * Execute a query on the configured Sparql endpoint
	 * @param queryString The SPARQL query
	 * @return true or false  
	 */	
	public boolean execAsk(String queryString, Object knowledgeSourceRef) {	
		boolean result = false;
		OntModel ontoModel = (OntModel)knowledgeSourceRef;
		QueryExecution qexec = null;
		
		try {	
			Query query = QueryFactory.create(queryString);
			qexec = QueryExecutionFactory.create(query, ontoModel);
			
			result = qexec.execAsk();			
		}
		catch(Exception ex) {
			this.logger.error("#Query: {}; #Message: {}; #Cause: {}", queryString, ex.getMessage(), ex.getCause());
		}
		finally {
			if(qexec!=null)
				qexec.close();
		}
		return result;
	}

}
