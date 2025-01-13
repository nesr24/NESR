package it.cnr.iasi.saks.semrel.method.asrmp.aggregation;

public abstract class AggregationStrategy_Abstract implements AggregationStrategy_Interface {
	public double t_conorm(double x, double y) {
		double result = 0.0d;

		result = 1-t_norm(1-x, 1-y);

		return result;
	}
}
