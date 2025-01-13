package it.cnr.iasi.saks.semsim;

import org.apache.jena.graph.Node;

public class OFVElem {
	Node node = null;
	String coeff = "";
	
	public Node getConc_id() {
		return node;
	}

	public void setConc_id(Node n) {
		this.node = n;
	}

	public String getCoeff() {
		return coeff;
	}

	public void setCoeff(String coeff) {
		this.coeff = coeff;
	}

	public OFVElem(Node n, String coeff) {
		super();
		this.node = n;
		this.coeff = coeff;
	}

	public String toString() {
		String result = "";
		result = this.getConc_id() + "(" + this.getCoeff() + ")";
		return result;
	}
}