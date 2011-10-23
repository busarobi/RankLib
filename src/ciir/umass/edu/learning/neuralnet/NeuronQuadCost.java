package ciir.umass.edu.learning.neuralnet;

/**
 * @author vdang
 */
public class NeuronQuadCost extends Neuron {

	/**
	 * Compute delta for neurons in the output layer. ONLY for neurons in the output layer. This is for quadratic lost function. 
	 * @param targetValue
	 */
	public void computeDelta(double targetValue)
	{
		prev_delta = (targetValue - prev_output+output) * prev_output * (1.0f - prev_output); // output * (1.0 - output) ~ g'(x)
		delta = (targetValue - prev_output+output) * output * (1.0f - output); // output * (1.0 - output) ~ g'(x) 
	}
	/**
	 * Update delta from neurons in the next layer (back-propagate). This is for quadratic lost function. 
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
	    prev_delta = errorSum * prev_output * ( 1.0f - prev_output);
	    delta = errorSum * output * ( 1.0f - output);
	}
}
