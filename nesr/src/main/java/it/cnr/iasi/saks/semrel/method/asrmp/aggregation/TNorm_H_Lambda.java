package it.cnr.iasi.saks.semrel.method.asrmp.aggregation;

public class TNorm_H_Lambda extends AggregationStrategy_Abstract {
	
	double lambda = 0.0d;
	
	public TNorm_H_Lambda(double lambda) {
		super();
		this.lambda = lambda;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double t_norm(double x, double y) {
		double result = 0.0d;
		result =  x*y/(this.getLambda() + (1-this.getLambda())*(x+y-x*y));
		return result;
	}
}
