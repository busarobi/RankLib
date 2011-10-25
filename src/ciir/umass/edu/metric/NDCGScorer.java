package ciir.umass.edu.metric;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.SimpleMath;
import ciir.umass.edu.utilities.Sorter;

public class NDCGScorer extends MetricScorer {

	public NDCGScorer()
	{
		this.k = 10;
	}
	public NDCGScorer(int k)
	{
		this.k = k;
	}
	public MetricScorer clone()
	{
		return new NDCGScorer();
	}
	
	/**
	 * Compute NDCG at k. NDCG(k) = DCG(k) / DCG_{perfect}(k). Note that the "perfect ranking" must be computed based on the whole list,
	 * not just top-k portion of the list.
	 */
	public double score(RankList rl)
	{
		List<Integer> rel = new ArrayList<Integer>();
		for(int i=0;i<rl.size();i++)
			rel.add((int)rl.get(i).getLabel());
		if(rl.size() < 1)
			return -1.0;

		double d2 = getIdealDCG(rel, k);
		if(d2 <= 0.0)//I mean precisely "="
			return 0.5; //as the yahoo script does
		return getDCG(rel, k)/d2;
	}
	public String name()
	{
		return "NDCG@"+k;
	}
	
	private double getDCG(List<Integer> rel, int k)
	{
		int size = k;
		if(k > rel.size() || k <= 0)
			size = rel.size();
		/*
		double dcg = rel.get(0);
		for(int i=1;i<size;i++)
		{
			dcg += ((double)rel.get(i))/SimpleMath.logBase2(i+1);
		}
		*/
		//used by yahoo! L2R challenge
		double dcg = 0.0;
		for(int i=1;i<=size;i++)
		{
			dcg += (Math.pow(2.0, rel.get(i-1))-1.0)/SimpleMath.logBase2(i+1);
		}
		return dcg;
	}
	private double getIdealDCG(List<Integer> rel, int k)
	{
		int size = k;
		if(k > rel.size() || k <= 0)
			size = rel.size();
		
		int [] idx = Sorter.sort(rel, false);
		/*
		double dcg = rel.get(idx[0]);
		for(int i=1;i<size;i++)
		{
			dcg += ((double)rel.get(idx[i]))/SimpleMath.logBase2(i+1);
		}
		*/
		//used by yahoo! L2R challenge		
		double dcg = 0.0;
		for(int i=1;i<=size;i++)
		{
			dcg += (Math.pow(2.0, rel.get(idx[i-1]))-1.0)/SimpleMath.logBase2(i+1);
		}
		
		return dcg;
	}
}
