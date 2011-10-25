package ciir.umass.edu.learning;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.boosting.WeakRanker;
import ciir.umass.edu.utilities.KeyValuePair;
import ciir.umass.edu.utilities.SimpleMath;
import ciir.umass.edu.utilities.Sorter;

public class BestFeatureRanker extends Ranker {
	protected double[] scores = null;
	protected int bestFeature = -1; 
	protected double bestScore = Double.MIN_VALUE;
	
	public void init()
	{
		int l = features.length;
		scores = new double[l];		
	}
	public void learn()
	{		
		int sampleSize = samples.size();
		
		System.out.println( "Number of query: " + sampleSize );
		
		for(int i=0; i<features.length; ++i)
		{
			System.out.print("Feature: " + features[i] + " " );
			double scoreByFeature = 0.0;
			for(int j=0; j < samples.size(); ++j)
			{
				RankList rl =  samples.get(j);
				double[] score = new double[rl.size()];
				for(int k=0;k<rl.size();k++)
					score[k] = rl.get(k).getFeatureValue(features[i]);
				
				int[] idx = Sorter.sort(score, false); 
				RankList srl = new RankList(rl, idx);
				scoreByFeature += scorer.score(srl);
			}
			scoreByFeature /= samples.size();
			scores[i] = scoreByFeature;
			if (bestScore<scoreByFeature)
			{
				bestScore = scoreByFeature;
				bestFeature = i+1;
			}
			
			System.out.println(scorer.name() + " for all query  " + SimpleMath.round(scoreByFeature, 4));
		}
		System.out.println( "Best feature: " + bestFeature );
	}
	public RankList rank(RankList rl)
	{
		double[] score = new double[rl.size()];
		for(int k=0;k<rl.size();k++)
			score[k] += rl.get(k).getFeatureValue(bestFeature);
		
		int[] idx = Sorter.sort(score, false); 
		RankList srl = new RankList(rl, idx);
	
		return srl;
	}
	public double eval(DataPoint p)
	{
		return p.getFeatureValue(bestFeature);
	}
	public Ranker clone()
	{
		return new BestFeatureRanker();
	}
	public String toString()
	{
		String ret = "";
		ret += bestFeature + "\n\n";
		
		for (int i = 0; i<scores.length; i++)
		{
			ret += features[i] + " ";
			ret += scores[i] + "\n";
		}
		
		return ret;
	}
	public String model()
	{
		String ret = "## BEST FEATURE\n";
		ret += "Metric: " + scorer.name() +"\n";
		ret += this.toString();
		return ret;
	}
	public void load(String fn)
	{
		try {
			String content = "";
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(fn), "ASCII"));
			
			int i=0;
			int maxFeatureIndex = -1;
			while((content = in.readLine()) != null)
			{
				//System.out.println( content );
				if ( i==2)
				{
					System.out.println( "Best Feature " + content );					
					bestFeature = Integer.parseInt(content);					
				}
				if (i>3)
				{
					String[] fs = content.split(" ");
					//System.out.println(fs[0]);
					int tmpIdx = Integer.parseInt(fs[0]);
					if ( maxFeatureIndex<tmpIdx) maxFeatureIndex = tmpIdx;  
				}
				
				i++;
			}
			
			features = new int[maxFeatureIndex];
			for(int idx=0;idx<maxFeatureIndex;idx++)
			{
				features[idx] = idx+1;
			}			
			in.close();
			
			//bestFeature = 0;
		}
		catch(Exception ex)
		{
			System.out.println("Error in BestFeature::load(): " + ex.toString());
		}
		
	}
	public void printParameters()
	{
		System.out.println("There is no prameter to be set.");
	}

}
