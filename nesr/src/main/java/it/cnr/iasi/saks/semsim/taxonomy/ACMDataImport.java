package it.cnr.iasi.saks.semsim.taxonomy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;

public class ACMDataImport {
	final String DELIM_SUPER_SUB_CLASS = " ";
	final String DELIM_WEIGHTS = " ";
	final String DELIM_ANNOTATION_CONCEPTS = " ";
	String prefix = "";
	String out_file = "";
	OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
	
	public ACMDataImport(String out_file, String prefix) {
		this.prefix = prefix;
		this.out_file = out_file;
	}

	public void initTaxonomy(int numberOfConcepts) {
				
		//this.m.createClass(Constants.OWL_THING);
		for(int i=0; i<numberOfConcepts; i++) {
			String uri = prefix + i;
			this.m.createClass(uri);
		}
		writeOntModelOnFile(this.m, out_file);
	}
	
	public void addLabel(String in_file) {
        try {
            BufferedReader b = new BufferedReader(new FileReader(in_file));
            String line = "";
            String start = " %%%%%% ";
            String end = "_";
            int i = 0;
            /*String uri = "Constants.OWL_THING";
            OntClass c = m.getOntClass(uri);
            String label = "Thing";
            */
            String uri = prefix+"0";
            OntClass c = m.getOntClass(uri);
            String label = "thing";
			c.addLabel(label, null);
            //skip the first
            b.readLine();	
            while ((line = b.readLine()) != null) {
            	i++;
				int s_index = line.indexOf(start) + start.length();
				int e_index = line.indexOf(end);
				label = line.substring(s_index, e_index);
				uri = prefix + i;
				c = m.getOntClass(uri);
				c.addLabel(label, null);
            }
            writeOntModelOnFile(this.m, out_file);
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	public void addSubClasses(String in_file) {
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_file));
            String id_super = "";
            String id_sub = "";
            String line = "";
            while ((line = b.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, DELIM_SUPER_SUB_CLASS);
            	id_super = prefix+st.nextToken();
            	id_sub = prefix+st.nextToken();
            	//System.out.println(id_super + " - " + id_sub);
				OntClass c = m.getOntClass(id_super);
				OntClass r = m.getOntClass(id_sub);
				c.addSubClass(r);
            }
            writeOntModelOnFile(this.m, out_file);
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void generateWeightFiles(String in_file, String out_cf, String out_af, String out_td, String out_iic, String out_rnd) {
		System.out.println(in_file);
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_file));
            String id_c = "";
            String w_cf = "", w_af = "", w_td = "", w_iic = "", w_rnd = "";
            String cf = "", af = "", td = "", iic = "", rnd = "";
            String line = "";
            while ((line = b.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, DELIM_WEIGHTS);
            	id_c = this.prefix + st.nextToken();
            	w_cf = st.nextToken();
            	w_af = st.nextToken();
            	w_td = st.nextToken();
            	w_iic = st.nextToken();
            	w_rnd = st.nextToken();
            	cf = cf + id_c + " = [" + w_cf + "]\n";
            	af = af + id_c + " = [" + w_af + "]\n";
            	td = td + id_c + " = [" + w_td + "]\n";
            	iic = iic + id_c + " = [" + w_iic + "]\n";
            	rnd = rnd + id_c + " = [" + w_rnd + "]\n";

            }
        	Utils.writeOnFile(out_cf, cf, false);
        	Utils.writeOnFile(out_af, af, false);
        	Utils.writeOnFile(out_td, td, false);
        	Utils.writeOnFile(out_iic, iic, false);
        	Utils.writeOnFile(out_rnd, rnd, false);
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void generateAVsFile(String in_annot_file, String out_annot_file) {
		System.out.println("generateAVsFile");
		System.out.println(in_annot_file);
		System.out.println(out_annot_file);
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_annot_file));
            String out = "";
            String line = "";
            while ((line = b.readLine()) != null) {
            	String id_res = line.substring(0, line.indexOf(" "));
            	String annot_concepts = line.substring(line.indexOf(" ")+1).replace(" ", ";");
            	if(annot_concepts.length() > 0) {
            		annot_concepts = this.prefix+annot_concepts;
            		annot_concepts = annot_concepts.replace(";", ";"+this.prefix);
            	}
            	out = out + id_res + " = [" + annot_concepts + "]\n";
            	
            }
            b.close();
            Utils.writeOnFile(out_annot_file, out, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void writeOntModelOnFile(OntModel m, String outFile) {
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			this.m.writeAll(fos, "RDF/XML");
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
