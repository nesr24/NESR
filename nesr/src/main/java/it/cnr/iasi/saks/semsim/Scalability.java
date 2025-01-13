package it.cnr.iasi.saks.semsim;

import java.util.Random;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Scalability {

	public Vector<Vector<OFVElem>> createRandomAnnotations(WeightedTaxonomy wt, String annotFolder, int length, int howMany) {
		String avs_file = "avs_"+length+".txt";
		String ofvFile = annotFolder+avs_file;
		Vector<Vector<OFVElem>> result = new Vector<Vector<OFVElem>>();
		Vector<Node> classes = new Vector<Node>();
		classes.addAll(wt.allClasses());
		System.out.println(classes);
		Utils.createFile(ofvFile);
		for(int i=0; i<howMany; i++) {
			String temp = i+" = [";
			Vector<OFVElem> ofv = new Vector<OFVElem>();
			for(int j=0; j<length; j++) {
				Random rnd = new Random();
				int r = rnd.nextInt(classes.size()-1)+1;
				OFVElem elem = new OFVElem(classes.get(r), Constants.COEFF_NULL);
				ofv.add(elem);
				temp = temp + elem.getConc_id().getURI().toString()+"; ";
			}			
			temp = temp.substring(0, temp.length()-2);
			Utils.writeOnFile(ofvFile, temp+"]\n", true);
			result.add(ofv);
		}
		return result;
	}
}
