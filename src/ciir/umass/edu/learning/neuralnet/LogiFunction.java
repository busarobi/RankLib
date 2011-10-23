package ciir.umass.edu.learning.neuralnet;

/**
 * @author vdang
 */
public class LogiFunction implements TransferFunction {
	
	@Override
	public double compute(double x)
	{
		return (double) (1.0 / (1.0 + Math.exp(-x)));
	}
	
	@Override
	public double computeDerivative(double x)
	{
		double output = compute(x);
		return (double) (output * (1.0 - output));
	}
}
