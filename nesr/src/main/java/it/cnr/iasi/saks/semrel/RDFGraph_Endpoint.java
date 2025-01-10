package it.cnr.iasi.saks.semrel;

import it.cnr.iasi.saks.semrel.sparql.SPARQLEndpointConnector;

public class RDFGraph_Endpoint extends RDFGraph {
	
	private static RDFGraph_Endpoint instance = null;

	private RDFGraph_Endpoint(String graph, Filter p_filters, Filter so_filters) {
		super();
		this.setKnowledgeResourceRef(Constants.SPARQL_ENDPOINT);

		this.setSc(new SPARQLEndpointConnector());
		this.setGraph(graph);
		this.load_p_filters(p_filters);
		this.load_so_filters(so_filters);
		//this.countTriplesWithPredicateRDF_TYPE();
		//this.countAllTriples();
		//this.countAllTriples();
		//this.countAllNodes();
	}
		
	public synchronized static RDFGraph_Endpoint getInstance(String graph){
    	if (instance == null){
    		instance = new RDFGraph_Endpoint(graph, null, null);
    	}
    	return instance;
    }
	
	public synchronized static RDFGraph_Endpoint getInstance(String graph, Filter p_filters, Filter so_filters){
    	if (instance == null){
    		instance = new RDFGraph_Endpoint(graph, p_filters, so_filters);
    	}
    	return instance;
    }

	
	public void load_p_filters(Filter f1) {
		if(f1 != null)
			this.getP_filters().add(f1);
	}
	
	public void load_so_filters(Filter f1) {
		if(f1 != null)
			this.getSo_filters().add(f1);
	}
}
