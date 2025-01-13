package it.cnr.iasi.saks.semrel.method.asrmp.aggregation;

public class TNorm_E extends AggregationStrategy_Abstract {
		
	public double t_norm(double x, double y) {
		double result = 0.0d;

		result = x*y/(1+(1-x)*(1-y));

		return result;
	}
}
