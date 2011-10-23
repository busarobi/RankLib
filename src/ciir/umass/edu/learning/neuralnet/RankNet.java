package ciir.umass.edu.learning.neuralnet;

import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.neuralnet.NeuralNetwork;

/**
 * @author vdang
 * 
 * This class implements RankNet.
 *  C.J.C. Burges, T. Shaked, E. Renshaw, A. Lazier, M. Deeds, N. Hamilton and G. Hullender. Learning to rank using gradient descent.
 *  In Proc. of ICML, pages 89-96, 2005.
 */
public class RankNet extends NeuralNetwork {
	
	public static int nHiddenLayer = 1;
	public static int nHiddenNodePerLayer = 10;
	
	public RankNet()
	{
		
	}
	public RankNet(List<RankList> samples, int [] features)
	{
		super(samples, features);
	}
	
	public void init()
	{
		setInputOutput(features.length, 1);
		for(int i=0;i<nHiddenLayer;i++)
			addHiddenLayer(nHiddenNodePerLayer);
		wire();
		
		//make sure the training samples are in correct ranking
		totalPairs = 0;
		for(int i=0;i<samples.size();i++)
		{
			samples.set(i, samples.get(i).getCorrectRanking());
			RankList rl = samples.get(i);
			for(int j=0;j<rl.size()-1;j++)
				for(int k=j+1;k<rl.size();k++)
					if(rl.get(j).getLabel() > rl.get(k).getLabel())//strictly ">"
						totalPairs++;
		}
		
		super.init();
	}
	public Ranker clone()
	{
		return new RankNet();
	}
	public void printParameters()
	{
		PRINTLN("No. of epochs: " + nIteration);
		PRINTLN("No. of hidden layers: " + nHiddenLayer);
		PRINTLN("No. of hidden nodes per layer: " + nHiddenNodePerLayer);
	}
}
