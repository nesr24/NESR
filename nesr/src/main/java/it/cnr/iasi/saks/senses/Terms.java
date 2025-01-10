package it.cnr.iasi.saks.senses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.OFVElem;

public class Terms {

	String[] domains = {"Art, architecture, and archaeology",
			"Biology",
			"Business, industry and finance",
			"Chemistry and mineralogy",
			"Communication and telecommunication",
			"Computing",
			"Craft, engineering and technology",
			"Culture, anthropology and society",
			"Education and science",
			"Emotions and feelings",
			"Environment and meteorology",
			"Farming, fishing and hunting",
			"Food, drink and taste",
			"Geography, geology and places",
			"Health and medicine",
			"Heraldry, honors, and vexillology",
			"History",
			"Language and linguistics",
			"Law and crime",
			"Literature and theatre",
			"Mathematics and statistics",
			"Media and press",
			"Music, sound and dancing",
			"Navigation and aviation",
			"Numismatics and currencies",
			"Philosophy, psychology and behavior",
			"Physics and astronomy",
			"Politics, government and nobility",
			"Possession",
			"Religion, mysticism and mythology",
			"Sex",
			"Smell and perfume",
			"Solid, liquid and gas matter",
			"Space and touch",
			"Sport, games and recreation",
			"Tasks, jobs, routine and evaluation",
			"Textile, fashion and clothing",
			"Time",
			"Transport and travel",
			"Vision and visual",
			"Warfare, violence and defense"};
	Map<String, Map<String, Double>> dict = new HashMap<String, Map<String, Double>>();
	
	public Terms() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String[] getDomains() {
		return domains;
	}

	public void setDomains(String[] domains) {
		this.domains = domains;
	}

	public Map<String, Map<String, Double>> getDict() {
		return dict;
	}

	public void setDict(Map<String, Map<String, Double>> dict) {
		this.dict = dict;
	}



	public void termsLoader(String in_file) {
//		String currentPath = Utils.currentFolder();
		
//		String in_folder = currentPath+"target/test-classes/semsim/similarityAndSenses/";
//		String out_folder = currentPath+"target/test-classes/semsim/similarityAndSenses/";
		final String DELIM = "\t";
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_file));
            String readLine = "";
            b.readLine();b.readLine();b.readLine();
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				StringTokenizer st = new StringTokenizer(s,DELIM);
				while (st.hasMoreTokens()) {
					String x = st.nextToken(); //salta il primo
					String term = st.nextToken(); //prende il termine
					for(int i=0; i<this.getDomains().length; i++) {
						String temp = st.nextToken();
						System.out.println(temp);
						if(!(temp.equalsIgnoreCase("0"))) {
							if(this.getDict().get(term)==null) {
								this.getDict().put(term, new HashMap<String, Double>());							
							}
							this.getDict().get(term).put(domains[i], Double.valueOf(temp));	
						}
					}
				}
            }
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }

	}
	
	
}
