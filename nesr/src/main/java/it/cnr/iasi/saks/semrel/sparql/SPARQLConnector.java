package it.cnr.iasi.saks.semrel.sparql;

import java.util.Vector;

import org.apache.jena.query.QuerySolution;

public interface SPARQLConnector {
	public Vector<QuerySolution> execQuery(String queryString, Object knowledgeSourceRef);
	public boolean execAsk(String queryString, Object knowledgeSourceRef);
}
