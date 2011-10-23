package ciir.umass.edu.learning.boosting;

import ciir.umass.edu.learning.DataPoint;

/**
 * @author vdang
 * 
 * Weak rankers for RankBoost.
 */
public class RBWeakRanker {
	private int  fid = -1;
	private double threshold = 0.0;
	
	public RBWeakRanker(int  fid, double threshold)
	{
		this.fid = fid;
		this.threshold = threshold;
	}
	public int score(DataPoint p)
	{
		if(p.getFeatureValue(fid) > threshold)
			return 1;
		return 0;
	}
	public int getFid()
	{
		return fid;
	}
	public double getThreshold()
	{
		return threshold;
	}
	public String toString()
	{
		return fid + ":" + threshold;
	}
}
