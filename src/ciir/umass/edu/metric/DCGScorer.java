package ciir.umass.edu.metric;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.SimpleMath;

public class DCGScorer extends MetricScorer {
	public DCGScorer()
	{
		this.k = 10;
	}
	public DCGScorer(int k)
	{
		this.k = k;
	}
	public MetricScorer clone()
	{
		return new DCGScorer();
	}
	/**
	 * Compute DCG at k. 
	 */
	public double score(RankList rl)
	{
		List<Integer> rel = new ArrayList<Integer>();
		for(int i=0;i<rl.size();i++)
			rel.add((int)rl.get(i).getLabel());
		if(rl.size() < 1)
			return -1.0;
		
		return getDCG(rel, k);
	}
	public String name()
	{
		return "DCG@"+k;
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
}
