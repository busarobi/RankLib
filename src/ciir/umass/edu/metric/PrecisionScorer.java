package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;

public class PrecisionScorer extends MetricScorer {

	public PrecisionScorer()
	{
		this.k = 10;
	}
	public PrecisionScorer(int k)
	{
		this.k = k;
	}
	public double score(RankList rl)
	{
		int count = 0;
		
		int size = k;
		if(k > rl.size() || k <= 0)
			size = rl.size();
		
		for(int i=0;i<size;i++)
		{
			if(rl.get(i).getLabel() > 0.0)//relevant
				count++;
		}
		return ((double)count)/size;
	}
	public MetricScorer clone()
	{
		return new PrecisionScorer();
	}
	public String name()
	{
		return "P@"+k;
	}
}
