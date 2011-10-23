package ciir.umass.edu.learning.neuralnet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vdang
 * 
 * This class implements layers of neurons in neural networks.
 */
public class Layer {
	protected List<NeuronCECost> neurons = null;
	
	public Layer(int size)
	{
		neurons = new ArrayList<NeuronCECost>();
		for(int i=0;i<size;i++)
			neurons.add(new NeuronCECost());
	}
	public Neuron get(int k)
	{
		return neurons.get(k);
	}
	public int size()
	{
		return neurons.size();
	}
	
	/**
	 * Have all neurons in this layer compute its output
	 */
	public void computeOutput()
	{
		for(int i=0;i<neurons.size();i++)
			neurons.get(i).computeOutput();
	}
	/**
	 * [Only for output layers] Compute delta for all neurons in the this (output) layer
	 * @param targetValues
	 */
	public void computeDelta(double[] targetValues)
	{
		for(int i=0;i<neurons.size();i++)
			neurons.get(i).computeDelta(targetValues[i]);
	}
	/**
	 * Update delta from neurons in the previous layers
	 */
	public void updateDelta()
	{
		for(int i=0;i<neurons.size();i++)
			neurons.get(i).updateDelta();
	}
	public void updateWeight()
	{
		for(int i=0;i<neurons.size();i++)
			neurons.get(i).updateWeight();
	}
}
