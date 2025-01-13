package it.cnr.iasi.saks.semsim.ontology;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy_OntModel;

public class Ontology_OntModel extends Ontology {
	private static Ontology_OntModel instance = null;

	public Ontology_OntModel() {
		this.wt = WeightedTaxonomy_OntModel.getInstance();
	}
	
	public synchronized static Ontology_OntModel getInstance(){
    	if (instance == null){
    		instance = new Ontology_OntModel();
    	}
    	return instance;
    }
}
