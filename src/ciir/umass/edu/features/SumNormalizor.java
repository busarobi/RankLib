package ciir.umass.edu.features;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.RankList;

/**
 * @author vdang
 */
public class SumNormalizor implements Normalizer {

	@Override
	public void normalize(RankList rl, int[] fids) {
		float[] norm = new float[fids.length];
		for(int i=0;i<fids.length;i++)
			norm[i] = 0.0f;
		for(int i=0;i<rl.size();i++)
		{
			DataPoint dp = rl.get(i);
			for(int j=0;j<fids.length;j++)
				norm[j] += Math.abs(dp.getFeatureValue(fids[j]));
		}
		for(int i=0;i<rl.size();i++)
		{
			DataPoint dp = rl.get(i);
			dp.normalize(fids, norm);
		}
	}
}
