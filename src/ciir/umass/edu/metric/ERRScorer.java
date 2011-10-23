package ciir.umass.edu.metric;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.RankList;

/**
 * 
 * @author Van Dang
 * Expected Reciprocal Rank
 */
public class ERRScorer extends MetricScorer {

	public ERRScorer()
	{
		this.k = 10;
	}
	public ERRScorer(int k)
	{
		this.k = k;
	}
	public ERRScorer clone()
	{
		return new ERRScorer();
	}
	/**
	 * Compute ERR at k. NDCG(k) = DCG(k) / DCG_{perfect}(k). Note that the "perfect ranking" must be computed based on the whole list,
	 * not just top-k portion of the list.
	 */
	public double score(RankList rl)
	{
		int size = k;
		if(k > rl.size() || k <= 0)
			size = rl.size();
		
		List<Integer> rel = new ArrayList<Integer>();
		for(int i=0;i<rl.size();i++)
			rel.add((int)rl.get(i).getLabel());
		
		double s = 0.0;
		/*for(int i=1;i<=size;i++)
		{
			double t = R(rel.get(i-1))/i;
			for(int j=1;j<=i-1;j++)
				t *= 1.0 - R(rel.get(j-1));
			s += t;
		}*/
		double p = 1.0;
		for(int i=1;i<=size;i++)
		{
			double R = R(rel.get(i-1)); 
			s += p*R/i;
			p *= (1.0 - R);
		}
		return s;
	}
	public String name()
	{
		return "ERR@" + k;
	}
	private double R(int rel)
	{
		return (double) ((Math.pow(2.0, rel) - 1)/16);
	}
}
