package it.cnr.iasi.saks.semrel;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import it.cnr.iasi.saks.semrel.sparql.SPARQLOntModelConnector;

public abstract class RDFGraph_OntModel extends RDFGraph {
	public RDFGraph_OntModel() {
		super();
		this.setKnowledgeResourceRef(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null));
		this.setSc(new SPARQLOntModelConnector());
	}
}
