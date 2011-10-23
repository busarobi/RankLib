package ciir.umass.edu.learning;

import java.util.List;

import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.utilities.SimpleMath;
import ciir.umass.edu.utilities.Sorter;
import flanagan.analysis.Regression;

public class RegRank extends Ranker {
	protected double[][] fData = null;
	protected double[] vals = null;
	protected Regression r = null;
	
	public RegRank()
	{
		
	}
	public RegRank(List<RankList> samples, int[] features)
	{
		super(samples, features);
	}
	
	private void convert()//assuming that @samples and @features have been loaded properly
	{
		/*int count = 0;
		for(int i=0;i<samples.size();i++)
		{
			RankList l = samples.get(i);
			count += l.size();
		}
		
		fData = new double[features.length][];
		vals = new double[count];
		for(int i=0;i<fData.length;i++)
			fData[i] = new double[count];

		int col = 0;
		for(int i=0;i<samples.size();i++)
		{
			RankList l = samples.get(i);
			for(int j=0;j<l.size();j++)
			{
				DataPoint p = l.get(j);
				double[] fVals = p.getFVector(features);
				for(int k=0;k<fVals.length;k++)
					fData[k][col] = fVals[k];
				vals[col] = p.getLabel();
				col++;
			}
		}*/
	}
	private double predict(double[] test)
	{
		double[] c = r.getCoeff();
		double v = c[0];
		//System.out.print(c[0] + " ");
		for(int i=1;i<c.length;i++)
		{
			//System.out.print(c[i] + " ");
			v += c[i] * test[i-1];
		}
		return v;
	}
	
	public void init()
	{
		super.init();
	}
	public void learn()
	{
		convert();
		
		PRINTLN("---------------------------");
		PRINTLN("Training starts...");
		PRINTLN("---------------------------");
		PRINT("Running regression... ");
		r = new Regression(fData, vals);
		r.linear();
		PRINTLN("[Done.]");
		
		scoreOnTrainingData = scorer.score(rank(samples));
		
		PRINTLN(scorer.name() + " on training data: " + scoreOnTrainingData);
		if(validationSamples != null)
			PRINTLN(scorer.name() + " on validation data: " + SimpleMath.round(scorer.score(rank(validationSamples)), 4));
		PRINTLN("[Done.]");
	}
	public RankList rank(RankList rl)
	{
		/*double[] score = new double[rl.size()];
		for(int i=0;i<rl.size();i++)
			score[i] = predict(rl.get(i).getFVector(features));
		int[] idx = Sorter.sort(score, false); 
		return new RankList(rl, idx);*/
		return null;
	}
	public double eval(DataPoint p)
	{
		return 0.0f;//predict(p.getFVector(features));
	}
	public Ranker clone()
	{
		return new RegRank();
	}
}
