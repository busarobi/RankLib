package ciir.	umass.edu.learning;

import java.util.Hashtable;
import java.util.List;

import ciir.umass.edu.learning.boosting.AdaRank;
import ciir.umass.edu.learning.boosting.RankBoost;
import ciir.umass.edu.learning.neuralnet.RankNet;
import ciir.umass.edu.utilities.FileUtils;

public class RankerFactory {

	protected Ranker[] rFactory = new Ranker[]{new RegRank(), new RankBoost(), new RankNet(), new AdaRank(), new CoorAscent(), new BestFeatureRanker()};
	protected static Hashtable<String, RANKER_TYPE> map = new Hashtable<String, RANKER_TYPE>();
	
	public RankerFactory()
	{
		map.put("RANKNET", RANKER_TYPE.RANK_NET);
		map.put("RANKBOOST", RANKER_TYPE.RANK_BOOST);
		map.put("ADARANK", RANKER_TYPE.ADA_RANK);
		map.put("COORDINATE ASCENT", RANKER_TYPE.COOR_ASCENT);
		map.put("BEST FEATURE", RANKER_TYPE.BEST_FEATURE);
	}
	
	public Ranker createRanker(RANKER_TYPE type)
	{
		Ranker r = rFactory[type.ordinal() - RANKER_TYPE.LREG.ordinal()].clone();
		return r;
	}
	public Ranker createRanker(RANKER_TYPE type, List<RankList> samples, int[] features)
	{
		Ranker r = createRanker(type);
		r.set(samples, features);
		return r;
	}
	public Ranker loadRanker(String modelFile)
	{
		Ranker r = null;
		try {
			String content = FileUtils.read(modelFile, "ASCII").split("\n")[0];
			content = content.replace("## ", "");
			System.out.println("Model:\t\t" + content);
			r = createRanker(map.get(content.toUpperCase()));
			r.load(modelFile);
		}
		catch(Exception ex)
		{
			System.out.println("Error in RankerFactory.load(): " + ex.toString());
		}
		return r;
	}
}
