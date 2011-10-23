package ciir.umass.edu.features;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.RankList;

/**
 * @author vdang
 */
public class StandardNormalizor implements Normalizer {

	@Override
	public void normalize(RankList rl, int[] fids) {
		
		float[] mean = new float[fids.length];
		float[] std = new float[fids.length];
		
		for(int j=0;j<fids.length;j++)
		{
			mean[j] = 0.0f;
			std[j] = 0.0f;
		}
		for(int i=0;i<rl.size();i++)
		{
			DataPoint dp = rl.get(i);
			for(int j=0;j<fids.length;j++)
				mean[j] += dp.getFeatureValue(fids[j]);
		}
		
		for(int j=0;j<fids.length;j++)
		{
			mean[j] = mean[j] / rl.size();
			for(int i=0;i<rl.size();i++)
			{
				DataPoint p = rl.get(i);
				float x = p.getFeatureValue(fids[j]);
				std[j] += (x-mean[j])*(x-mean[j]);
			}
			std[j] = (float) Math.sqrt(std[j] / (rl.size()-1));
			//normalize
			for(int i=0;i<rl.size();i++)
			{
				DataPoint p = rl.get(i);
				float x = (p.getFeatureValue(fids[j]) - mean[j])/std[j];//x ~ standard normal (0, 1)
				if((x+"").compareTo("NaN")==0)
					x = p.getFeatureValue(fids[j]);
				p.setFeatureValue(fids[j], x);
			}
		}
	}
}
