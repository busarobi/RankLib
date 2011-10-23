package ciir.umass.edu.learning.neuralnet;

/**
 * @author vdang
 */
public class NeuronCECost extends Neuron {

	/**
	 * Compute delta for neurons in the output layer. ONLY for neurons in the output layer.
	 * @param targetValue
	 */
	public void computeDelta(double targetValue)
	{
		//System.out.println(prev_output + "\t" + output);
		double pij = (double) (1.0 / (1.0 + Math.exp(-(prev_output-output))));
		prev_delta = (targetValue-pij) * tfunc.computeDerivative(prev_output);
		delta =      (targetValue-pij) * tfunc.computeDerivative(output);
	}
	
	/**
	 * Update delta from neurons in the next layer (back-propagate)
	 */
	public void updateDelta()
	{
		double errorSum = 0.0;
	    Synapse s = null;
	    for(int i=0;i<outLinks.size();i++)
	    {
	    	s = outLinks.get(i);
	    	errorSum += (s.getTarget().getPrevDelta()-s.getTarget().getDelta()) * s.getWeight();
	    }
	    prev_delta = errorSum * tfunc.computeDerivative(prev_output);
		delta =      errorSum * tfunc.computeDerivative(output);
	}
}
