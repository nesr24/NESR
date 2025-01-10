package it.cnr.iasi.saks.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class MiniContext implements Clusterable {
	
	public MiniContext(String props, double d[]) {
		this.setProps(props);
		point = d;
	}
	
	String props = "";
	double[] point = new double[663];

	public double[] getPoint() {
		return point;
	}

	public void setPoint(double[] point) {
		this.point = point;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}
}
