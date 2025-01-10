package it.cnr.iasi.saks.similarity.setSimilarity.setTheoreticMethods;

import java.util.Map;


public class JaccardFuzzy {
	public double sim(Map<String, Double> s1, Map<String, Double> s2) {
		double result = 0.0d;

		double num = 0.0d;
		double denom = 0.0d;
		for(String d:s1.keySet()) {
			if(s2.keySet().contains(d)) {
				num = num + min(s1.get(d), s2.get(d));
				denom = denom + max(s1.get(d), s2.get(d));
			}
			else {
					num = num + 0;
					denom = denom + s1.get(d);
			}
		}

		for(String d:s2.keySet()) {
			if(s1.keySet().contains(d)) ;
			else {
					num = num + 0;
					denom = denom + s2.get(d);
			}
		}
		
		result = num/denom;
		
		return result;
	}
	
	
	public double sim_reduced(Map<String, Double> s1, Map<String, Double> s2) {
		double result = 0.0d;

		double num = 0.0d;
		double denom = 0.0d;
		for(String d:s1.keySet()) {
			if(s2.keySet().contains(d)) {
				num = num + min(s1.get(d), s2.get(d));
				denom = denom + max(s1.get(d), s2.get(d));
			}
		}
		
		result = num/denom;
		
		return result;
	}
	
	
	private double min(double d1, double d2) {
		if(d1<d2)
			return d1;
		else
			return d2;
	}
	
	private double max(double d1, double d2) {
		if(d1>d2)
			return d1;
		else
			return d2;
	}
}
