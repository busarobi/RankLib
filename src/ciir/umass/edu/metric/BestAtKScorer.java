package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;

public class BestAtKScorer extends MetricScorer {
	
	public BestAtKScorer()
	{
		this.k = 10;
	}
	public BestAtKScorer(int k)
	{
		this.k = k;
	}
	public double score(RankList rl)
	{
		return rl.get(maxToK(rl, k-1)).getLabel();
	}
	public MetricScorer clone()
	{
		return new BestAtKScorer();
	}
	
	/**
	 * Return the position of the best object (e.g. docs with highest degree of relevance) among objects in the range [0..k]
	 * NOTE: If you want best-at-k (i.e. best among top-k), you need maxToK(rl, k-1)
	 * @param l The rank list.
	 * @param k The last position of the range.
	 * @return The index of the best object in the specified range.
	 */
	public int maxToK(RankList rl, int k)
	{
		int size = k;
		if(size < 0 || size > rl.size()-1)
			size = rl.size()-1;
		
		double max = -1.0;
		int max_i = 0;
		for(int i=0;i<=size;i++)
		{
			if(max < rl.get(i).getLabel())
			{
				max = rl.get(i).getLabel();
				max_i = i;
			}
		}
		return max_i;
	}
	public String name()
	{
		return "Best@"+k;
	}
}
