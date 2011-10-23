package ciir.umass.edu.learning.neuralnet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vdang
 * 
 * This class implements individual neurons in the network.
 */
public class Neuron {
	public static double momentum = 0.9;
	public static double learningRate = 0.001;
	
	//protected TransferFunction tfunc = new HyperTangentFunction(); 
	protected TransferFunction tfunc = new LogiFunction();
	
	protected double output;//sigmoid(wsum) (range from 0.0 to 1.0): output for the current input
	protected double delta;//the adjustment for the current input
	
	protected double prev_output = -1;//output for the previous input
	protected double prev_delta = -1;//the adjustment for the previous input
	
	protected List<Synapse> inLinks = null;
	protected List<Synapse> outLinks = null;
	
	public Neuron()
	{
		output = 0.0;
		delta = 0.0;
		inLinks = new ArrayList<Synapse>();
		outLinks = new ArrayList<Synapse>();
	}
	public double getOutput()
	{
		return output;
	}
	public double getPrevOutput()
	{
		return prev_output;
	}
	public List<Synapse> getInLinks()
	{
		return inLinks;
	}
	public List<Synapse> getOutLinks()
	{
		return outLinks;
	}
	public double getDelta()
	{
		return delta;
	}
	public double getPrevDelta()
	{
		return prev_delta;
	}

	public void setOutput(double output)
	{
		this.output = output;
	}
	
	public void computeOutput()
	{
		//save current output
		prev_output = output;
		
		Synapse s = null;
		double wsum = 0.0;
		for(int i=0;i<inLinks.size();i++)
		{
			s = inLinks.get(i);
			wsum += s.getSource().getOutput() * s.getWeight();
		}
		output = (double) tfunc.compute(wsum);//using the specified transfer function to compute the output
	}
	
	/**
	 * Compute delta for neurons in the output layer. ONLY for neurons in the output layer. 
	 * MUST BE Overridden in sub-classes with respect to the specific lost function.
	 * @param targetValue
	 */
	public void computeDelta(double targetValue)
	{
		 
	}
	/**
	 * Update delta from neurons in the next layer (back-propagate). This is for quadratic lost function. 
	 * MUST BE Overridden in sub-classes with respect to the specific lost function.
	 */
	public void updateDelta()
	{
		
	}
	/**
	 * Update weights of incoming links.
	 */
	public void updateWeight()
	{
		Synapse s = null;
		for(int i=0;i<inLinks.size();i++)
		{
			s = inLinks.get(i);
			double dw = learningRate * (prev_delta*s.getSource().getPrevOutput() - delta*s.getSource().getOutput()) ;
							//+ momentum * s.getLastWeightAdjustment();
			s.setWeightAdjustment(dw);
			s.updateWeight();
		}
	}
	
}
