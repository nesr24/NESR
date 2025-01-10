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
package it.cnr.iasi.saks.semrel;

/**
 * 
 * @author francesco
 *
 */
public class Constants {
	public static final String SAKS_NS = "http://saks.iasi.cnr.it/";
	
	public static final String IN = "IN";
	public static final String OUT = "OUT";
	public static final String IN_OUT = "IN_OUT";
	

	public static final String SPARQL_ENDPOINT_PUBLIC = "https://dbpedia.org/sparql";
//	public static final String SPARQL_ENDPOINT_PUBLIC = "https://yago-knowledge.org/sparql/query";

//	public static final String SPARQL_ENDPOINT = "https://databus.dbpedia.org/repo/sparql";
//	public static final String SPARQL_ENDPOINT = "https://query.wikidata.org/bigdata/namespace/categories/sparql";
	public static final String SPARQL_DBPEDIA_GRAPH = "http://dbpedia.org";
	public static final String SPARQL_YAGO_GRAPH = "";	
	public static final String SPARQL_ENDPOINT = "http://localhost:8890/sparql";
	public static final String SPARQL_WORDNET_GRAPH = "http://wordnet30";
		
	public static final String SPARQL_DISTINCT = "DISTINCT";
	public static final String SPARQL_NOT_DISTINCT = "";

	public static final String SUBJECT = "SUBJECT";
	public static final String OBJECT = "OBJECT";
	public static final String PREDICATE = "PREDICATE";

	public static final String CONDITIONED_TO_SUBJECT = "CONDITIONED_TO_SUBJECT";
	public static final String CONDITIONED_TO_OBJECT = "CONDITIONED_TO_OBJECT";
	public static final String CONDITIONED_TO_PREDICATE = "CONDITIONED_TO_PREDICATE";
	
	public static final String DIRECTED_PATH = "DIRECTED_PATH";
	public static final String UNDIRECTED_PATH = "UNDIRECTED_PATH";
	
	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDF_TYPE = RDF_NS+"type";
	
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_SUBCLASSOF = RDFS_NS+"subClassOf";
	public static final String RDFS_SUBPROPERTYOF = RDFS_NS+"subPropertyOf";
	public static final String RDFS_DOMAIN = RDFS_NS+"domain";
	public static final String RDFS_RANGE = RDFS_NS+"range";
	public static final String RDFS_SEEALSO = RDFS_NS+"seeAlso";
	public static final String RDFS_LABEL = RDFS_NS+"label";
	public static final String RDFS_COMMENT = RDFS_NS+"comment";
	
	public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	public static final String OWL_CLASS = OWL_NS+"Class";
	public static final String OWL_OBJECT_PROPERTY = OWL_NS+"ObjectProperty";
	public static final String OWL_THING = OWL_NS+"Thing";
	public static final String OWL_SAMEAS = OWL_NS+"sameAs";
	public static final String OWL_EQUIVALENT_CLASS = OWL_NS+"equivalentClass";
	
	
	public static final String DBPEDIA_NS = "http://dbpedia.org/";
	public static final String DBPEDIA_DBO_NS = DBPEDIA_NS+"ontology/";
	public static final String DBPEDIA_DBO_WIKIPAGEREDIRECTS = DBPEDIA_DBO_NS+"wikiPageRedirects";
	public static final String DBPEDIA_DBO_WIKIPAGEDISAMBIGUATES = DBPEDIA_DBO_NS+"wikiPageDisambiguates";
	public static final String DBPEDIA_DBO_WIKIPAGE = DBPEDIA_DBO_NS+"wikiPageWikiLink";
	public static final String DBPEDIA_DBO_THUMBNAIL = DBPEDIA_DBO_NS+"thumbnail";
	public static final String DBPEDIA_DBO_TYPE = DBPEDIA_DBO_NS+"type";
	public static final String DBPEDIA_DBO_ABSTRACT = DBPEDIA_DBO_NS+"abstract";
	public static final String DBPEDIA_DBO_PERSON = DBPEDIA_DBO_NS+"Person";
	
	public static final String DBPEDIA_DBR_NS = DBPEDIA_NS+"resource/";
	
	public static final String YAGO_NS = "http://yago-knowledge.org/";
	public static final String YAGO_RES_NS = YAGO_NS+"resource/";
	
	
	public static final boolean ACYCLIC = true;
	public static final boolean NOT_ACYCLIC = false;
	
	public static final boolean FILTERING = true;
	public static final boolean NOT_FILTERING = false;
	
	public static final int MEAN = -1;
	
	public static final String METHOD_HJ = "METHOD_HJ";
	
	public static final String METHOD_LOD_JACCARD = "METHOD_LOD_JACCARD";
	public static final String METHOD_LOD_OVERLAP = "METHOD_LOD_OVERLAP";
	
	public static final String METHOD_REWORD_SIMPLE_IN = "METHOD_REWORD_SIMPLE_IN";
	public static final String METHOD_REWORD_SIMPLE_OUT = "METHOD_REWORD_SIMPLE_OUT";
	public static final String METHOD_REWORD_SIMPLE_IN_OUT = "METHOD_REWORD_SIMPLE_IN_OUT";
	public static final String METHOD_REWORD_MIP = "METHOD_REWORD_MIP";
	public static final String METHOD_REWORD = "METHOD_REWORD";
	
	public static final String METHOD_PROXIMITY_CONSTANT = "METHOD_PROXIMITY_CONSTANT";
	public static final String METHOD_PROXIMITY_IC = "METHOD_PROXIMITY_IC";
	public static final int METHOD_PROXIMITY_GRAPH_DEGREE = 10000;
	
	public static final String METHOD_IC_SIMPLE = "METHOD_IC_SIMPLE";
	public static final String METHOD_IC_JOINT = "METHOD_IC_JOINT";
	public static final String METHOD_IC_COMB = "METHOD_IC_COMB";
	public static final String METHOD_IC_PLUS_PMI = "METHOD_IC_PLUS_PMI";
	
	public static final double METHOD_IC_MAX_COST = Double.MAX_VALUE;

	public static final String METHOD_EXCLUSIVITY_025 = "METHOD_EXCLUSIVITY_025";
	public static final String METHOD_EXCLUSIVITY_050 = "METHOD_EXCLUSIVITY_050";
	public static final String METHOD_EXCLUSIVITY_075 = "METHOD_EXCLUSIVITY_075";
	public static final String METHOD_EXCLUSIVITY_100 = "METHOD_EXCLUSIVITY_100";
	
	public static final String METHOD_EXCLUSIVITY_1_025 = "METHOD_EXCLUSIVITY_1_025";
	public static final String METHOD_EXCLUSIVITY_1_050 = "METHOD_EXCLUSIVITY_1_050";
	public static final String METHOD_EXCLUSIVITY_1_075 = "METHOD_EXCLUSIVITY_1_075";
	public static final String METHOD_EXCLUSIVITY_1_100 = "METHOD_EXCLUSIVITY_1_100";

	public static final String METHOD_EXCLUSIVITY_5_025 = "METHOD_EXCLUSIVITY_5_025";
	public static final String METHOD_EXCLUSIVITY_5_050 = "METHOD_EXCLUSIVITY_5_050";
	public static final String METHOD_EXCLUSIVITY_5_075 = "METHOD_EXCLUSIVITY_5_075";
	public static final String METHOD_EXCLUSIVITY_5_100 = "METHOD_EXCLUSIVITY_5_100";

	public static final String METHOD_EXCLUSIVITY_10_025 = "METHOD_EXCLUSIVITY_10_025";
	public static final String METHOD_EXCLUSIVITY_10_050 = "METHOD_EXCLUSIVITY_10_050";
	public static final String METHOD_EXCLUSIVITY_10_075 = "METHOD_EXCLUSIVITY_10_075";
	public static final String METHOD_EXCLUSIVITY_10_100 = "METHOD_EXCLUSIVITY_10_100";

	
	public static final String METHOD_LDSD_D = "METHOD_LDSD_D";
	public static final String METHOD_LDSD_DW = "METHOD_LDSD_DW";
	public static final String METHOD_LDSD_I = "METHOD_LDSD_I";
	public static final String METHOD_LDSD_IW = "METHOD_LDSD_IW";
	public static final String METHOD_LDSD_CW = "METHOD_LDSD_CW";
	public static final String METHOD_LDSD_INCOMING = "METHOD_LDSD_INCOMING";
	public static final String METHOD_LDSD_OUTGOING = "METHOD_LDSD_OUTGOING";
	
	public static final String METHOD_GNLDSD_ALPHA = "METHOD_LDSD_ALPHA";
	public static final String METHOD_GNLDSD_BETA = "METHOD_LDSD_BETA";
	public static final String METHOD_GNLDSD_GAMMA = "METHOD_LDSD_GAMMA";
	
	public static final String METHOD_WLM = "METHOD_WLM";
	
	public static final String METHOD_ASRMP_A_1 = "METHOD_ASRMP_A_1";
	public static final String METHOD_ASRMP_A_2 = "METHOD_ASRMP_A_2";
	public static final String METHOD_ASRMP_B_2 = "METHOD_ASRMP_B_2";
	public static final String METHOD_ASRMP_C = "METHOD_ASRMP_C";	
	
	public static final String METHOD_PLDSD = "METHOD_PLDSD";
	
	public static final String METHOD_FT = "METHOD_FT";
	
	public static final int DBR_RDFTYPE_DBO = 1;
	public static final int DBR_DBO_DBR = 2;
	public static final int DBR_RDFSSEEALSO_DBR = 3;
	public static final int DBR_RDFTYPE_OWLTHING = 4;

	public static final String SEMSIM_METHOD_P1 = "P1";
	public static final String SEMSIM_METHOD_P2 = "P2";
	public static final String SEMSIM_METHOD_P3 = "P3";
	public static final String SEMSIM_METHOD_P4 = "P4";
	public static final String SEMSIM_METHOD_P5 = "P5";
	
	public static final String SEMSIM_ANNOTATION_DELIM_START = "[";
	public static final String SEMSIM_ANNOTATION_DELIM_END = "]";
	public static final String SEMSIM_ANNOTATION_SEPARATOR = ";";
	public static final String SEMSIM_BY_ID = "BY_ID";
	public static final String SEMSIM_BY_NAME = "BY_NAME";
	public static final String SEMSIM_BY_LABEL = "BY_LABEL";
	public static final String SEMSIM_EQUAL = "=";
	public static final String SEMSIM_AV = "AV";
	public static final String SEMSIM_RV = "RV";
	public static final String SEMSIM_WEIGHT_DELIM_START = "[";
	public static final String SEMSIM_WEIGHT_DELIM_END = "]";
	public static final String SEMSIM_WEIGHT_SEPARATOR = ";";
	public static final String SEMSIM_ORDERING = "ordering";
	
	public static final String XMLNS_PREFIX = "";
	public static final String URINAME_SEPARATOR = "#";
	
	public static final double CONSIM_MATRIX_INIT = -1.0d;
	
	public static final String COEFF_HIGH = "H";
	public static final String COEFF_MEDIUM = "M";
	public static final String COEFF_LOW = "L";
	public static final String COEFF_NULL = "N";
	
	public static final String ENDS_IN_HIGH = "_HIGH";
	public static final String ENDS_IN_MEDIUM = "_MEDIUM";
	public static final String ENDS_IN_LOW = "_LOW";
	
	
	public static final String WORDNET__NS = "https://w3id.org/own-pt/wn30/";
	public static final String WORDNET__INSTANCES = WORDNET__NS+"instances/";
	public static final String WORDNET__SCHEMA = WORDNET__NS+"schema/";
	public static final String WORDNET__CONTAINS_WORD_SENSE = WORDNET__SCHEMA+"containsWordSense";
	public static final String WORDNET__HYPERNYM_OF = WORDNET__SCHEMA+"hypernymOf";
	public static final String WORDNET__HYPONYM_OF = WORDNET__SCHEMA+"hyponymOf";
	
	public static final String WIKIDATA_PARAM = "query";
	public static final String WIKIDATA_SPARQL_SERVER = "https://query.wikidata.org/sparql";
	public static final String WIKIDATA_NS = "http://www.wikidata.org/";
	public static final String WIKIDATA_ENTITY = WIKIDATA_NS+"entity/";
	public static final String WIKIDATA_PROPERTY = WIKIDATA_NS+"prop/direct/";
	public static final String WIKIDATA_PROP_FAMILYNAME = WIKIDATA_PROPERTY+"P734";
	
}
	