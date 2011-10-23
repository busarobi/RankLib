package ciir.umass.edu.learning;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.metric.MetricScorer;
import ciir.umass.edu.utilities.FileUtils;

public class Ranker {
	public static boolean verbose = true;

	protected List<RankList> samples = new ArrayList<RankList>();
	protected int[] features = null;
	protected MetricScorer scorer = null;
	protected double scoreOnTrainingData = 0.0;
	
	protected List<RankList> validationSamples = null;
	protected List<RankList> testSamples = null;
	
	public Ranker()
	{
		
	}
	
	public Ranker(List<RankList> samples, int[] features)
	{
		this.samples = samples;
		this.features = features;
	}
	
	//Utility functions
	public void set(List<RankList> samples, int[] features)
	{
		this.samples = samples;
		this.features = features;
	}
	public void setValidationSet(List<RankList> samples)
	{
		this.validationSamples = samples;
	}
	public void setTestSet(List<RankList> samples)
	{
		this.testSamples = samples;
	}
	public void set(MetricScorer scorer)
	{
		this.scorer = scorer;
	}
	public double getScoreOnTrainingData()
	{
		return scoreOnTrainingData;
	}
	public int[] getFeatures()
	{
		return features;
	}
	
	public List<RankList> rank(List<RankList> l)
	{
		List<RankList> ll = new ArrayList<RankList>();
		for(int i=0;i<l.size();i++)
			ll.add(rank(l.get(i)));
		return ll;
	}
	public void save(String modelFile) 
	{
		FileUtils.write(modelFile, "ASCII", model());
	}
	
	public void PRINT(String msg)
	{
		if(verbose)
			System.out.print(msg);
	}
	public void PRINTLN(String msg)
	{
		if(verbose)
			System.out.println(msg);
	}
	public void PRINT(int[] len, String[] msgs)
	{
		if(verbose)
		{
			for(int i=0;i<msgs.length;i++)
			{
				String msg = msgs[i];
				if(msg.length() > len[i])
					msg = msg.substring(0, len[i]);
				else
					while(msg.length() < len[i])
						msg += " ";
				System.out.print(msg + " | ");
			}
		}
	}
	public void PRINTLN(int[] len, String[] msgs)
	{
		PRINT(len, msgs);
		PRINTLN("");
	}

	protected void copy(double[] source, double[] target)
	{
		for(int j=0;j<source.length;j++)
			target[j] = source[j];
	}
	/**
	 * HAVE TO BE OVER-RIDDEN IN SUB-CLASSES
	 */
	public void init()
	{
	}
	public void learn()
	{
	}
	public RankList rank(RankList rl)
	{
		return null;
	}
	public double eval(DataPoint p)
	{
		return -1.0;
	}
	public Ranker clone()
	{
		return null;
	}
	public String toString()
	{
		return "[Not yet implemented]";
	}
	public String model()
	{
		return "[Not yet implemented]";
	}
	public void load(String fn)
	{
	}
	public void printParameters()
	{
	}
}
