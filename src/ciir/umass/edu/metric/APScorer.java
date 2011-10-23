package ciir.umass.edu.metric;

import ciir.umass.edu.learning.RankList;

public class APScorer extends MetricScorer {

	int totalRel = 0;

	public APScorer()
	{
		this.k = 0;//consider the whole list
	}
	public APScorer(int totalRel)
	{
		this.totalRel = totalRel;
		this.k = 0;
	}
	public MetricScorer clone()
	{
		return new APScorer();
	}
	/**
	 * Compute Average Precision (AP) of the list. AP of a list is the average of precision evaluated at ranks where a relevant document 
	 * is observed. 
	 * @return AP of the list.
	 */
	public double score(RankList rl)
	{
		double ap = 0.0;
		int count = 0;
		for(int i=0;i<rl.size();i++)
		{
			if(rl.get(i).getLabel() > 0.0)//relevant
			{
				count++;
				ap += ((double)count)/(i+1);
			}
		}
		if(count==0)
			return 0.0;
		return ap / ((totalRel>0)?totalRel:count);
	}
	public String name()
	{
		return "MAP";
	}
}
