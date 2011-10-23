package ciir.umass.edu.learning.neuralnet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.utilities.SimpleMath;
import ciir.umass.edu.utilities.Sorter;

/**
 * @author vdang
 * 
 * This class implements neural networks. This is the basis for implementation of the RankNet algorithm.
 */
public class NeuralNetwork extends Ranker {

	//Parameters
	public static int nIteration = 300;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//logistic activation function
		//weight randomly in [-1, 1]
		//feature randomly in [0, 1]
		/*NeuralNetwork nn = new NeuralNetwork();
		nn.setInputOutput(40, 1);
		//nn.addHiddenLayer(1);
		nn.wire();
		
		//GENERATE TRAINING DATA
		int t = 0;
		int nDoc = 50;
		int nFeature = 40;
		String[] fids = new String[nFeature];
		nn.features = fids;
		for(int i=0;i<nFeature;i++)
			fids[i] = (i+1)+"";
		int countR = 0;
		int countNR = 0;
		Random random = new Random();
		
		List<RankList> samples = new ArrayList<RankList>();
		while(t<1000)
		{
			RankList rl = new RankList();
			//MetricScorer s = new PrecisionScorer(nDoc);
			int countRelSoFar = 0;
			for(int d=0;d<nDoc;d++)
			{
				String oid = (t+1)+"";
				double[] f = new double[40];
				for(int i=0;i<nFeature;i++)
					f[i] = random.nextDouble();
				
				DataPoint p = new DataPoint(oid, 0.0, fids, f);
				double label = nn.eval(p);
				//System.out.println("Label="+ label);
				if(label <= 0.8)
					label = 0.0;
				else if(label <= 0.9)
					label = 2.0;
				else if(label <= 0.95)
					label = 3.0;
				else
					label = 4.0;
				p.setLabel(label);
				
				if(label > 0.0)
					countR++;
				else
					countNR++;
				
				if(label > 0.0)
					countRelSoFar++;
				
				if(d==nDoc-1&&countRelSoFar<4)
				{
					d--;
					countNR--;
				}
				else
				{
					rl.add(p);
				}
				
			}
			samples.add(rl);
			//System.out.println(s.score(rl.getCorrectRanking()));
			t++;
		}
		System.out.println("# Rel=" + countR + "; NR="+ countNR);
		for(int i=0;i<samples.size();i++)
			for(int j=0;j<samples.get(i).size();j++)
				System.out.println(samples.get(i).get(j).toString());*/
	}

	protected List<Layer> layers = new ArrayList<Layer>();
	protected Layer inputLayer = null;
	protected Layer outputLayer = null;
	
	//to store the best model on validation data (if specified)
	protected double bestScoreOnValidation = 0.0;
	protected List<List<Double>> bestModelOnValidation = new ArrayList<List<Double>>();
	
	protected int totalPairs = 0;
	protected int misorderedPairs = 0; 
	protected double error = 0.0;
	protected double lastError = 0.0;
	
	protected NeuralNetwork()
	{
		
	}
	protected NeuralNetwork(List<RankList> samples, int [] features)
	{
		super(samples, features);
	}
	public NeuralNetwork(int nInput, int nOutput)
	{
		inputLayer = new Layer(nInput+1);//plus the "bias" (output threshold)
		outputLayer = new Layer(nOutput);
		layers.add(inputLayer);
		layers.add(outputLayer);
	}
	public void setInputOutput(int nInput, int nOutput)
	{
		inputLayer = new Layer(nInput+1);//plus the "bias" (output threshold)
		outputLayer = new Layer(nOutput);
		layers.clear();
		layers.add(inputLayer);
		layers.add(outputLayer);
	}
	public void addHiddenLayer(int size)
	{
		layers.add(layers.size()-1, new Layer(size));
	}
	public void wire()
	{
		//wire the input layer to the first hidden layer
		for(int i=0;i<inputLayer.size()-1;i++)//don't touch the "bias" input (the last item in the list)
			for(int j=0;j<layers.get(1).size();j++)
				connect(0, i, 1, j);
		
		//wire one layer to the next, starting at layer 1 (the first hidden layer)
		for(int i=1;i<layers.size()-1;i++)
			for(int j=0;j<layers.get(i).size();j++)
				for(int k=0;k<layers.get(i+1).size();k++)
					connect(i, j, i+1, k);
		
		//wire the "bias" neuron to all others (in all layers)
		for(int i=1;i<layers.size();i++)
			for(int j=0;j<layers.get(i).size();j++)
				connect(0, inputLayer.size()-1, i, j);
		
		//initialize weights
		/*Random random = new Random();
		for(int i=1;i<layers.size();i++)
		{
			for(int j=0;j<layers.get(i).size();j++)
			{
				Neuron n = layers.get(i).get(j);
				int s = n.getInLinks().size();
				double b = Math.sqrt(3.0/s);//if weight is drawn from Uniform(-b, b) ==> the standard deviation of weights will be 1.0/sqrt(m) 
				for(int k=0;k<s;k++)
					n.getInLinks().get(k).setWeight(b*random.nextDouble()*(random.nextInt(2)==0?1:-1));
			}
		}*/
	}
	private void connect(int sourceLayer, int sourceNeuron, int targetLayer, int targetNeuron)
	{
		new Synapse(layers.get(sourceLayer).get(sourceNeuron), layers.get(targetLayer).get(targetNeuron));
	}
	
	private void feedInput(DataPoint p)
	{
		for(int k=0;k<inputLayer.size()-1;k++)//not the "bias" node
			inputLayer.get(k).setOutput(p.getFeatureValue(features[k]));
		//  and now the bias node with a fix "1.0"
		inputLayer.get(inputLayer.size()-1).setOutput(1.0f);
	}
	private void propagate()
	{
		for(int k=1;k<layers.size();k++)//skip the input layer
			layers.get(k).computeOutput();
	}
	private void backpropagate(double targetValue)
	{
		outputLayer.computeDelta(new double[]{targetValue});//starting at the output layer
		for(int k=layers.size()-2;k>=1;k--)//back-propagate to the first hidden layer
			layers.get(k).updateDelta();
		//  update weights
		outputLayer.updateWeight();
		for(int k=layers.size()-2;k>=1;k--)
			layers.get(k).updateWeight();
	}
	
	protected void saveBestModelOnValidation()
	{
		for(int i=0;i<layers.size()-1;i++)//loop through all layers
		{
			List<Double> l = bestModelOnValidation.get(i);
			l.clear();
			for(int j=0;j<layers.get(i).size();j++)//loop through all neurons on in the current layer
			{
				Neuron n = layers.get(i).get(j);
				for(int k=0;k<n.getOutLinks().size();k++)//loop through all out links (synapses) of the current neuron
					l.add(n.getOutLinks().get(k).getWeight());
			}
		}
	}
	protected void restoreBestModelOnValidation()
	{
		try {
			for(int i=0;i<layers.size()-1;i++)//loop through all layers
			{
				List<Double> l = bestModelOnValidation.get(i);
				int c = 0;
				for(int j=0;j<layers.get(i).size();j++)//loop through all neurons on in the current layer
				{
					Neuron n = layers.get(i).get(j);
					for(int k=0;k<n.getOutLinks().size();k++)//loop through all out links (synapses) of the current neuron
						n.getOutLinks().get(k).setWeight(l.get(c++));
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error in NeuralNetwork.restoreBestModelOnValidation(): " + ex.toString());
		}
	}
	
	private double crossEntropy(double o1, double o2, double targetValue)
	{
		double oij = o1 - o2;
		double ce = -targetValue * oij + SimpleMath.logBase2(1+Math.exp(oij));
		return (double) ce;
	}
	private void estimateLoss() 
	{
		misorderedPairs = 0;
		error = 0.0;
		for(int j=0;j<samples.size();j++)
		{
			RankList rl = samples.get(j);
			for(int k=0;k<rl.size()-1;k++)
			{
				double o1 = eval(rl.get(k));
				for(int l=k+1;l<rl.size();l++)
				{
					if(rl.get(k).getLabel() > rl.get(l).getLabel())
					{
						double o2 = eval(rl.get(l));
						error += crossEntropy(o1, o2, 1.0f);
						//if(o1!=o2)
							//System.out.println("HAHAHAHA");
						if(o1 < o2)
							misorderedPairs++;
					}
				}
			}
		}
		error = SimpleMath.round(error/totalPairs, 4);
		
		if(error > lastError)
			Neuron.learningRate /= 2.0;
		lastError = error;
	}
	
	public void init()
	{
		PRINT("Initializing... ");
		if(validationSamples != null)
			for(int i=0;i<layers.size();i++)
				bestModelOnValidation.add(new ArrayList<Double>());
		PRINTLN("[Done]");
	}
	public void learn()
	{
		PRINTLN("-----------------------------------------");
		PRINTLN("Training starts...");
		PRINTLN("--------------------------------------------------");
		//PRINTLN(new int[]{7, 14, 12, 9, 9}, new String[]{"#epoch", "% mis-ordered", "Avg. CE Loss", scorer.name()+"-T", scorer.name()+"-V"});
		//PRINTLN(new int[]{7, 14, 12, 9, 9}, new String[]{" ", "  pairs", " ", " ", " "});
		PRINTLN(new int[]{7, 14, 9, 9}, new String[]{"#epoch", "% mis-ordered", scorer.name()+"-T", scorer.name()+"-V"});
		PRINTLN(new int[]{7, 14, 9, 9}, new String[]{" ", "  pairs", " ", " "});
		PRINTLN("--------------------------------------------------");
		
		for(int i=1;i<=nIteration;i++)
		{
			for(int j=0;j<samples.size();j++)
			{
				RankList rl = samples.get(j);
				for(int k=0;k<rl.size()-1;k++)
				{
					for(int l=k+1;l<rl.size();l++)
					{
						if(rl.get(k).getLabel() == rl.get(l).getLabel())//I mean "="
							continue;
						
						double target = 1.0;
						
						feedInput(rl.get(k));
						propagate();
						
						feedInput(rl.get(l));
						propagate();
						
						//Back-propagate (from the output layer backward to the first hidden layer) to update weights
						backpropagate(target);
					}
				}
			}
			//printWeightVector();
			estimateLoss();
			//PRINT(new int[]{7, 14, 12}, new String[]{i+"", SimpleMath.round(((double)misorderedPairs)/totalPairs, 4)+"", error+""});
			PRINT(new int[]{7, 14}, new String[]{i+"", SimpleMath.round(((double)misorderedPairs)/totalPairs, 4)+""});
			if(i % 1 == 0)
			{
				PRINT(new int[]{9}, new String[]{SimpleMath.round(scorer.score(rank(samples)), 4)+""});
				if(validationSamples != null)
				{
					double scoreOnValidation = scorer.score(rank(validationSamples));
					if(scoreOnValidation > bestScoreOnValidation)
					{
						bestScoreOnValidation = scoreOnValidation;
						saveBestModelOnValidation();
					}
					PRINT(new int[]{9}, new String[]{SimpleMath.round(scoreOnValidation, 4)+""});
				}
			}
			PRINTLN("");
		}
		
		//if validation data is specified ==> best model on this data has been saved
		//we now restore the current model to that best model
		if(validationSamples != null)
			restoreBestModelOnValidation();
		
		scoreOnTrainingData = SimpleMath.round(scorer.score(rank(samples)), 4);
		PRINTLN("--------------------------------------------------");
		PRINTLN("Finished sucessfully.");
		PRINTLN(scorer.name() + " on training data: " + scoreOnTrainingData);
		if(validationSamples != null)
			PRINTLN(scorer.name() + " on validation data: " + SimpleMath.round(scorer.score(rank(validationSamples)), 4));
		PRINTLN("---------------------------------");
	}
	public RankList rank(RankList rl)
	{
		double[] scores = new double[rl.size()];
		for(int i=0;i<rl.size();i++)
			scores[i] = eval(rl.get(i));
		int[] idx = Sorter.sort(scores, false);
		return new RankList(rl, idx);
	}
	public double eval(DataPoint p)
	{
		feedInput(p);
		propagate();
		return outputLayer.get(0).getOutput();
	}
	public Ranker clone()
	{
		return new NeuralNetwork();
	}
	public String toString()
	{
		String output = "";
		for(int i=0;i<layers.size()-1;i++)//loop through all layers
		{
			for(int j=0;j<layers.get(i).size();j++)//loop through all neurons on in the current layer
			{
				output += i + " " + j + " ";
				Neuron n = layers.get(i).get(j);
				for(int k=0;k<n.getOutLinks().size();k++)//loop through all out links (synapses) of the current neuron
					output += n.getOutLinks().get(k).getWeight() + ((k==n.getOutLinks().size()-1)?"":" ");
				output += "\n";
			}
		}
		return output;
	}
	public String model()
	{
		String output = "## RankNet\n";
		output += "## Epochs = " + nIteration + "\n";
		output += "## No. of features = " + features.length + "\n";
		output += "## No. of hidden layers = " + (layers.size()-2) + "\n";
		for(int i=1;i<layers.size()-1;i++)
			output += "## Layer " + i + ": " + layers.get(i).size() + " neurons\n";
		
		//print used features
		for(int i=0;i<features.length;i++)
			output += features[i] + ((i==features.length-1)?"":" ");
		output += "\n";
		//print network information
		output += layers.size()-2 + "\n";//[# hidden layers]
		for(int i=1;i<layers.size()-1;i++)
			output += layers.get(i).size() + "\n";//[#neurons]
		//print learned weights
		output += toString();
		return output;
	}
	public void load(String fn)
	{
		try {
			String content = "";
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(fn), "ASCII"));
			
			List<String> l = new ArrayList<String>();
			while((content = in.readLine()) != null)
			{
				content = content.trim();
				if(content.length() == 0)
					continue;
				if(content.indexOf("##")==0)
					continue;
				l.add(content);
			}
			in.close();
			//load the network
			//the first line contains features information
			String[] tmp = l.get(0).split(" ");
			features = new int[tmp.length];
			for(int i=0;i<tmp.length;i++)
				features[i] = Integer.parseInt(tmp[i]);
			//the 2nd line is a scalar indicating the number of hidden layers
			int nHiddenLayer = Integer.parseInt(l.get(1));
			int[] nn = new int[nHiddenLayer];
			//the next @nHiddenLayer lines contain the number of neurons in each layer
			int i=2;
			for(;i<2+nHiddenLayer;i++)
				nn[i-2] = Integer.parseInt(l.get(i));
			//create the network
			setInputOutput(features.length, 1);
			for(int j=0;j<nHiddenLayer;j++)
				addHiddenLayer(nn[j]);
			wire();
			//fill in weights
			for(;i<l.size();i++)//loop through all layers
			{
				String[] s = l.get(i).split(" ");
				int iLayer = Integer.parseInt(s[0]);//which layer?
				int iNeuron = Integer.parseInt(s[1]);//which neuron?
				Neuron n = layers.get(iLayer).get(iNeuron);
				for(int k=0;k<n.getOutLinks().size();k++)//loop through all out links (synapses) of the current neuron
					n.getOutLinks().get(k).setWeight(Double.parseDouble(s[k+2]));
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error in NeuralNetwork::load(): " + ex.toString());
		}
	}
	
	/**
	 * FOR DEBUGGING PURPOSE ONLY
	 */
	protected void printNetworkConfig()
	{
		for(int i=1;i<layers.size();i++)
		{
			System.out.println("Layer-" + (i+1));
			for(int j=0;j<layers.get(i).size();j++)
			{
				Neuron n = layers.get(i).get(j);
				System.out.print("Neuron-" + (j+1) + ": " + n.getInLinks().size() + " inputs\t");
				for(int k=0;k<n.getInLinks().size();k++)
					System.out.print(n.getInLinks().get(k).getWeight() + "\t");
				System.out.println("");
			}
		}
	}
	protected void printWeightVector()
	{
		double[] w = new double[features.length];
		/*for(int j=0;j<inputLayer.size()-1;j++)
		{
			w[j] = inputLayer.get(j).getOutLinks().get(0).getWeight();
			System.out.print(w[j] + " ");
		}*/
		for(int j=0;j<outputLayer.get(0).getInLinks().size();j++)
			System.out.print(outputLayer.get(0).getInLinks().get(j).getWeight() + " ");
		System.out.println("");
	}
}
