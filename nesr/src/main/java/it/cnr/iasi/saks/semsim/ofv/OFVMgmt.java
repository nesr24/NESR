package it.cnr.iasi.saks.semsim.ofv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class OFVMgmt {
	
	private Map<String, Vector<OFVElem>> avs = new HashMap<String, Vector<OFVElem>>();
	private Map<String, Vector<OFVElem>> rvs = new HashMap<String, Vector<OFVElem>>();
	private String avsFile;
	private String rvsFile;
	private WeightedTaxonomy wt;
	
	public WeightedTaxonomy getWt() {
		return wt;
	}


	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}


	public Map<String, Vector<OFVElem>> getAvs() {
		return avs;
	}


	public void setAvs(Map<String, Vector<OFVElem>> avs) {
		this.avs = avs;
	}


	public Map<String, Vector<OFVElem>> getRvs() {
		return rvs;
	}


	public void setRvs(Map<String, Vector<OFVElem>> rvs) {
		this.rvs = rvs;
	}

	public String getAvsFile() {
		return avsFile;
	}


	public void setAvsFile(String avsFile) {
		this.avsFile = avsFile;
	}


	public String getRvsFile() {
		return rvsFile;
	}


	public void setRvsFile(String rvsFile) {
		this.rvsFile = rvsFile;
	}


	/** This method considers the annotation in the following form 
	 *  "id_res = [id_c1; ...; id_cn]"
	 */
	private void loadOFV(String input_file, String annotationMode, String ofvType, boolean coeff_option) {
        try {
            BufferedReader b = new BufferedReader(new FileReader(input_file));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
            	System.out.println(readLine);
				String s = readLine;
				String res_num = s.substring(0, s.indexOf(Constants.SEMSIM_EQUAL)).trim();
				s = s.substring(s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_START)+1, s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_END));
				StringTokenizer st = new StringTokenizer(s,Constants.SEMSIM_ANNOTATION_SEPARATOR);
				Vector<OFVElem> ofv = new Vector<OFVElem>();
				while (st.hasMoreTokens()) {
					Node id = null; 
					String temp = st.nextToken().trim();
					String token = "";
					String coeff = "";
					if(coeff_option) {
						String ofv_elem_parts[] = temp.split(":");
						token = ofv_elem_parts[0].trim();
						coeff = ofv_elem_parts[1].trim();
					}
					else {
						token = temp;
						coeff = Constants.COEFF_NULL;
					}
					if(annotationMode.equals(Constants.SEMSIM_BY_LABEL))
						id = this.getWt().getIdByLabel(token);
					else if(annotationMode.equals(Constants.SEMSIM_BY_ID))
						id = NodeFactory.createURI(token);
					else if(annotationMode.equals(Constants.SEMSIM_BY_NAME))
						id = this.getWt().getIdByName(token);
										
					OFVElem ofv_elem = new OFVElem(id, coeff);
					ofv.add(ofv_elem);
				}
				
				if(ofvType.equals(Constants.SEMSIM_AV)) {
					this.getAvs().put(res_num, ofv);
				}
				else
					this.getRvs().put(res_num, ofv);

            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	
	/** This method considers the annotation in the following form 
	 *  "id_res id_c1 id_c2 ... id_cn"
	 */
	private void loadOFV_simpleForm(String input_file, String annotationMode, String ofvType, boolean coeff_option) {
		ofvType = Constants.SEMSIM_AV;
		try {
            BufferedReader b = new BufferedReader(new FileReader(input_file));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				StringTokenizer st = new StringTokenizer(s, " ");
				String res_num = st.nextToken().trim();
				Vector<OFVElem> ofv = new Vector<OFVElem>();
				while (st.hasMoreTokens()) {
					Node id = null; 
					String temp = st.nextToken().trim();
					String token = "";
					String coeff = "";
					if(coeff_option) {
						String ofv_elem_parts[] = temp.split(":");
						token = ofv_elem_parts[0].trim();
						coeff = ofv_elem_parts[1].trim();
					}
					else {
						token = temp;
						coeff = Constants.COEFF_NULL;
					}
					if(annotationMode.equals(Constants.SEMSIM_BY_LABEL))
						id = this.getWt().getIdByLabel(token);
					else if(annotationMode.equals(Constants.SEMSIM_BY_ID)) {
//						id = NodeFactory.createURI("http://terin-sen-apic.org/SemSim-AI-Bert#"+token);
//						id = NodeFactory.createURI("http://terin-sen-apic.org/SemSim-AI-w2v#"+token);
//						id = NodeFactory.createURI("http://terin-sen-apic.org/SemSim-AI-TF-IDF#"+token);
						id = NodeFactory.createURI(token);
					}
					else if(annotationMode.equals(Constants.SEMSIM_BY_NAME))
						id = this.getWt().getIdByName(token);
					

					System.out.println(id);


					
					OFVElem ofv_elem = new OFVElem(id, coeff);
					ofv.add(ofv_elem);
				}
				if(ofvType.equals(Constants.SEMSIM_AV)) {
					this.getAvs().put(res_num, ofv);
				}
				else
					this.getRvs().put(res_num, ofv);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	
	
	public void loadAnnotatedResources(String input_file, String annotationMode, boolean coeff_option) {
//		System.out.println("loadAnnotatedResources");
//		System.out.println("input_file: "+input_file);
//		System.out.println("annotationMode: "+annotationMode);

		this.loadOFV(input_file, annotationMode, Constants.SEMSIM_AV, coeff_option);
//		this.loadOFV_simpleForm(input_file, annotationMode, annotationMode, coeff_option);
	}
	
	public void loadRequests(String input_file, String annotationMode, boolean coeff_option) {
		this.loadOFV(input_file, annotationMode, Constants.SEMSIM_RV, coeff_option);
	}

	
}
