package ciir.umass.edu.features;

import ciir.umass.edu.learning.RankList;

/**
 * @author vdang
 *
 * Abstract class for feature normalization
 */
public interface Normalizer {
	public void normalize(RankList rl, int[] fids);
}
