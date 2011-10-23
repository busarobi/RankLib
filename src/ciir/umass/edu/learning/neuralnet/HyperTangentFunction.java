package ciir.umass.edu.learning.neuralnet;

/**
 * @author vdang
 */
public class HyperTangentFunction implements TransferFunction {

	@Override
	public double compute(double x) {
		return (double) (1.7159 * Math.tanh(x*2/3));
	}

	@Override
	public double computeDerivative(double x) {
		double output = Math.tanh(x*2/3);
		return (double) (1.7159 * (1.0 - output*output) * 2 /3);
	}

}
