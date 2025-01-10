package it.cnr.iasi.saks.semrel.method.loddo;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.KnowledgeBase;

public class Lod_Overlap_withTerms extends Loddo_Abstract_withTerms {

	public Lod_Overlap_withTerms(KnowledgeBase kb, String descrFilename, String prefix, String namespace) {
		this.setKb(kb);
		loadDescriptionsByTerms(kb, descrFilename, prefix, namespace);
	}
	
	@Override
	public double semrel(Node n1, Node n2) {
		//Set<String> d1 = this.description(n1);
		//Set<String> d2 = this.description(n2);

		Set<String> d1 = new HashSet<String>();
		Set<String> d2 = new HashSet<String>();
		
		System.out.println(n1.getURI().toString()+" --- "+n2.getURI().toString());

		Set<String> temp1 = this.descriptionWithTerms(n1);
		if(temp1.size()>0)
			d1.addAll(temp1);
		
		Set<String> temp2 = this.descriptionWithTerms(n2);
		if(temp2.size()>0)
			d2.addAll(temp2);

		
		double min = this.min(d1, d2);
		
//		System.out.println("overlap");
		double result = 0;
		double temp =	((double)this.commonDescription(d1, d2).size()) / ((double)(min));
		if(!(Double.isNaN(temp)))
			result = temp; 
		return result;
	}
	
}
