package ciir.umass.edu.learning;

import java.util.List;

import ciir.umass.edu.learning.boosting.AdaRank;
import ciir.umass.edu.learning.boosting.RankBoost;
import ciir.umass.edu.learning.neuralnet.RankNet;
import ciir.umass.edu.metric.MetricScorer;

/**
 * @author vdang
 * 
 * This class is for users who want to use this library programmatically. It provides trained rankers of different types with respect to user-specified parameters.
 */
public class RankerTrainer {

	public enum RANKER_TYPE {
		LREG, RANK_BOOST, RANK_NET, ADA_RANK, COOR_ASCENT
	};
	
	protected Ranker[] rFactory = new Ranker[]{new RegRank(), new RankBoost(), new RankNet(), new AdaRank(), new CoorAscent()};
	
	
	public Ranker train(RANKER_TYPE type, List<RankList> samples, int[] features, MetricScorer scorer)
	{
		Ranker ranker = createRanker(type);
		ranker.set(samples, features);
		ranker.set(scorer);
		ranker.init();
		ranker.learn();
		return ranker;
	}
	
	private Ranker createRanker(RANKER_TYPE type)
	{
		Ranker r = rFactory[type.ordinal() - RANKER_TYPE.LREG.ordinal()].clone();
		return r;
	}
}
