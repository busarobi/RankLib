package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;

public class ReciprocalRankScorer extends MetricScorer {
	
	public ReciprocalRankScorer()
	{
		this.k = 0;//consider the whole list
	}
	public double score(RankList rl)
	{
		int firstRank = -1;
		for(int i=0;i<rl.size()&&(firstRank==-1);i++)
		{
			if(rl.get(i).getLabel() > 0.0)//relevant
				firstRank = i+1;
		}
		return (firstRank==-1)?0:(1.0f/firstRank);
	}
	public MetricScorer clone()
	{
		return new ReciprocalRankScorer();
	}
	public String name()
	{
		return "RR@"+k;
	}
}
