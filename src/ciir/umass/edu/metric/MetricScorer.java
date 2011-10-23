package ciir.umass.edu.metric;

import java.util.List;

import ciir.umass.edu.learning.RankList;

public class MetricScorer {

	protected int k = 10;
	
	public void setK(int k)
	{
		this.k = k;
	}
	public double score(List<RankList> rl)
	{
		double score = 0.0;
		for(int i=0;i<rl.size();i++)
			score += score(rl.get(i));
		return score/rl.size();
	}
	
	/**
	 * MUST BE OVER-RIDDEN
	 * @param rl
	 * @return
	 */
	public double score(RankList rl)
	{
		return 0.0;
	}
	public MetricScorer clone()
	{
		return null;
	}
	public String name()
	{
		return "";
	}
}
