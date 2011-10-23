package ciir.umass.edu.learning.boosting;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.Sorter;

/**
 * @author vdang
 * 
 * Weak rankers for AdaRank.
 */
public class WeakRanker {
	private int fid = -1;
	
	public WeakRanker(int fid)
	{
		this.fid = fid;
	}
	public int getFID()
	{
		return fid;
	}
	
	public RankList rank(RankList l)
	{
		double[] score = new double[l.size()];
		for(int i=0;i<l.size();i++)
			score[i] = l.get(i).getFeatureValue(fid);
		int[] idx = Sorter.sort(score, false); 
		return new RankList(l, idx);
	}
	public List<RankList> rank(List<RankList> l)
	{
		List<RankList> ll = new ArrayList<RankList>();
		for(int i=0;i<l.size();i++)
			ll.add(rank(l.get(i)));
		return ll;
	}
}
