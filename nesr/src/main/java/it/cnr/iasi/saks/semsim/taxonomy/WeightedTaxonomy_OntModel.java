package it.cnr.iasi.saks.semsim.taxonomy;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import it.cnr.iasi.saks.semrel.sparql.SPARQLOntModelConnector;

public class WeightedTaxonomy_OntModel extends WeightedTaxonomy {
	private static WeightedTaxonomy_OntModel instance = null;
	
		
	public synchronized static WeightedTaxonomy_OntModel getInstance(String ontoFile){
    	if (instance == null){
    		instance = new WeightedTaxonomy_OntModel(ontoFile);
    	}
    	return instance;
    }

	
	
	public synchronized static WeightedTaxonomy_OntModel getInstance(){
    	if (instance == null){
    		instance = new WeightedTaxonomy_OntModel();
    	}
    	return instance;
    }

	
	public WeightedTaxonomy_OntModel(String ontoFile) {
		super(); 
		this.setKnowledgeResourceRef(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null));
		this.setSc(new SPARQLOntModelConnector());
		this.WeightedTaxonomy_init(ontoFile);
	}

	public WeightedTaxonomy_OntModel() {
		super(); 
		this.setKnowledgeResourceRef(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null));
		this.setSc(new SPARQLOntModelConnector());
	}

	
}
