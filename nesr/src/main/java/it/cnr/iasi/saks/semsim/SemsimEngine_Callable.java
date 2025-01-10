package it.cnr.iasi.saks.semsim;

import java.util.Vector;
import java.util.concurrent.Callable;

public class SemsimEngine_Callable implements Callable<Double> {
	
	Vector<OFVElem> rv, av = null; 
	boolean coeff_option = false;
	SemsimEngine se = null;
	
	public SemsimEngine_Callable(SemsimEngine se, Vector<OFVElem> rv, Vector<OFVElem> av, boolean coeff_option) {
		this.se = se;
		this.rv = rv;
		this.av = av;
		this.coeff_option = coeff_option;
	}
	
	public Double call() {
		double result = 0.0d;
		result = this.se.semsim(this.rv, this.av, this.coeff_option);
		return result;
	}
}
