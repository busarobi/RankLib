package ciir.umass.edu.learning.neuralnet;

/**
 * @author vdang
 * 
 * This is the abstract class for implementing transfer functions for neuralnet.
 */
public interface TransferFunction {
	public double compute(double x);
	public double computeDerivative(double x);
}
