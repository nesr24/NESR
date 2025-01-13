package it.cnr.iasi.saks.semsim;


public class Result {
	String ofv_name = "";
	double value = 0f;

	public Result() {
		super();
	}
	
	public Result(String ofv_name, double value) {
		super();
		this.ofv_name = ofv_name;
		this.value = value;
	}
	public String getOfv_name() {
		return ofv_name;
	}
	public void setOfv_name(String ofv_name) {
		this.ofv_name = ofv_name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
