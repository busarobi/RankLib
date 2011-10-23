package ciir.umass.edu.learning;

import java.util.ArrayList;
import java.util.List;

import ciir.umass.edu.features.FeatureManager;
import ciir.umass.edu.features.Normalizer;
import ciir.umass.edu.features.SumNormalizor;
import ciir.umass.edu.learning.boosting.*;
import ciir.umass.edu.learning.neuralnet.*;
import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.metric.METRIC;
import ciir.umass.edu.metric.MetricScorer;
import ciir.umass.edu.metric.MetricScorerFactory;
import ciir.umass.edu.utilities.FileUtils;
import ciir.umass.edu.utilities.LinearComputer;
import ciir.umass.edu.utilities.SimpleMath;
import ciir.umass.edu.utilities.Sorter;

/**
 * @author  vdang
 * 
 * This class is meant to provide the interface to run and compare different ranking algorithms. It lets users specify general parameters (e.g. what algorithm to run, 
 * training/testing/validating data, etc.) as well as algorithm-specific parameters. Type "java -jar bin/RankLib.jar" at the command-line to see all the options. 
 */
public class Evaluator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String[] rType = new String[]{"Linear regression", "RankNet", "RankBoost", "AdaRank", "Coordinate Ascent", "Best Feature"};
		RANKER_TYPE[] rType2 = new RANKER_TYPE[]{RANKER_TYPE.LREG, RANKER_TYPE.RANK_NET, RANKER_TYPE.RANK_BOOST, RANKER_TYPE.ADA_RANK, RANKER_TYPE.COOR_ASCENT, RANKER_TYPE.BEST_FEATURE};
		
		String trainFile = "";
		String featureDescriptionFile = "";
		double split = 0.0;
		int foldCV = -1;
		String validationFile = "";
		String testFile = "";
		int rankerType = 4;
		String trainMetric = "ERR@10";
		String testMetric = "";
		Evaluator.normalize = false;
		String savedModelFile = "";
		String rankFile = "";
		String scoreFile = "";
		boolean printIndividual = false;
		
		if(args.length < 2)
		{
			System.out.println("Usage: java -jar RankLib.jar <Params>");
			System.out.println("Params:");
			System.out.println("  [+] Training (+ tuning and evaluation)");
			System.out.println("\t-train <file>\t\tTraining data");
			System.out.println("\t-ranker <type>\t\tSpecify which ranking algorithm to use");
			//System.out.println("\t\t\t\t0: Linear regression");
			System.out.println("\t\t\t\t1: RankNet");
			System.out.println("\t\t\t\t2: RankBoost");
			System.out.println("\t\t\t\t3: AdaRank");
			System.out.println("\t\t\t\t4: Coordinate Ascent");
			System.out.println("\t[ -feature <file> ]\tFeature description file: list features to be considered by the learner, each on a separate line");
			System.out.println("\t\t\t\tIf not specified, all features will be used.");
			System.out.println("\t[ -metric2t <metric> ]\tMetric to optimize on the training data. Supported: MAP, NDCG@k, DCG@k, P@k, RR@k, BEST@k, ERR@k (default=" + trainMetric + ")");
			System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default to the same as specified for -metric2t)");


			System.out.println("\t[ -tp <x \\in [0..1]> ]\tSet train-test split to be (x)(1.0-x)");
			System.out.println("\t[ -kcv <k> ]\t\tSpecify if you want to perform k-fold cross validation using ONLY the specified training data (default=NoCV)");
			System.out.println("\t[ -validate <file> ]\tSpecify if you want to tune your system on the validation data (default=unspecified)");
			System.out.println("\t\t\t\tIf specified, the final model will be the one that performs best on the validation data");
			System.out.println("\t[ -test <file> ]\tSpecify if you want to evaluate the trained model on this data (default=unspecified)");
			
			System.out.println("\t[ -norm ]\t\tNormalize feature vectors (default=" + Evaluator.normalize + ")");
			
			System.out.println("\t[ -save <model> ]\tSave the learned model to the specified file (default=not-save)");
			
			System.out.println("\t[ -silent ]\t\tDo not print progress messages (which are printed by default)");
			
			System.out.println("");
			System.out.println("    [-] RankNet-specific parameters");
			System.out.println("\t[ -epoch <T> ]\t\tThe number of epochs to train (default=" + NeuralNetwork.nIteration + ")");
			System.out.println("\t[ -layer <layer> ]\tThe number of hidden layers (default=" + RankNet.nHiddenLayer + ")");
			System.out.println("\t[ -node <node> ]\tThe number of hidden nodes per layer (default=" + RankNet.nHiddenNodePerLayer + ")");
			
			System.out.println("");
			System.out.println("    [-] RankBoost-specific parameters");
			System.out.println("\t[ -round <T> ]\t\tThe number of rounds to train (default=" + RankBoost.nIteration + ")");
			System.out.println("\t[ -tc <k> ]\t\tThe number of threshold candidates to search (default=" + RankBoost.nThreshold + ")");
			
			System.out.println("");
			System.out.println("    [-] AdaRank-specific parameters");
			System.out.println("\t[ -round <T> ]\t\tThe number of rounds to train (default=" + AdaRank.nIteration + ")");
			System.out.println("\t[ -noeq ]\t\tTrain without enqueuing too-strong features (default=unspecified)");
			System.out.println("\t[ -tolerance <t> ]\tTolerance between two consecutive rounds of learning (default=" + AdaRank.tolerance + ")");
			System.out.println("\t[ -max <times> ]\tThe maximum number of times can a feature be consecutively selected without changing performance (default=" + AdaRank.maxSelCount + ")");

			System.out.println("");
			System.out.println("    [-] Coordinate Ascent-specific parameters");
			System.out.println("\t[ -r <k> ]\t\tThe number of random restarts (default=" + CoorAscent.nRestart + ")");
			System.out.println("\t[ -i <iteration> ]\tThe number of iterations to search in each dimension (default=" + CoorAscent.nMaxIteration + ")");
			System.out.println("\t[ -tolerance <t> ]\tPerformance tolerance between two solutions (default=" + CoorAscent.tolerance + ")");
			System.out.println("\t[ -reg <slack> ]\tRegularization parameter (default=no-regularization)");

			System.out.println("");
			System.out.println("  [+] Testing previously saved models");
			System.out.println("\t-load <model>\t\tThe model to load");
			System.out.println("\t-test <file>\t\tTest data to evaluate the model (specify either this or -test or -rank but not all of them)");
			System.out.println("\t-rank <file>\t\tRank the samples in the specified file (specify either this or -test or -rank but not all of them)");
			System.out.println("\t-score <file>\t\tOutput the scores for the samples in the specified file (specify either this or -test or -rank but not all of them)");
			System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default=" + trainMetric + ")");
			System.out.println("\t[ -idv ]\t\tPrint score on individual ranked lists in the specified test set");
			System.out.println("\t[ -norm ]\t\tNormalize feature vectors (default=" + Evaluator.normalize + ")");

			System.out.println("");
			System.out.println("  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("  + NOTE: ALWAYS include -letor if you're doing experiments on LETOR 4.0 dataset.       +");
			System.out.println("  +       The reason is a relevance degree of 2 in the dataset is actually counted as 3 +");
			System.out.println("  +       (this is based on the evaluation script they provided). To be consistent      +");
			System.out.println("  +       with their numbers, this program will change 2 to 3 when it loads the data    +");
			System.out.println("  +       into memory if the -letor flag is specified.                                  +");
			System.out.println("  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

			System.out.println("");
			return;
		}
		
		for(int i=0;i<args.length;i++)
		{
			if(args[i].compareTo("-train")==0)
				trainFile = args[++i];
			else if(args[i].compareTo("-ranker")==0)
				rankerType = Integer.parseInt(args[++i]);
			else if(args[i].compareTo("-feature")==0)
				featureDescriptionFile = args[++i];
			else if(args[i].compareTo("-metric2t")==0)
				trainMetric = args[++i];
			else if(args[i].compareTo("-metric2T")==0)
				testMetric = args[++i];
			else if(args[i].compareTo("-tp")==0)
				split = Double.parseDouble(args[++i]);
			else if(args[i].compareTo("-kcv")==0)
				foldCV = Integer.parseInt(args[++i]);
			else if(args[i].compareTo("-validate")==0)
				validationFile = args[++i];
			else if(args[i].compareTo("-test")==0)
				testFile = args[++i];
			else if(args[i].compareTo("-norm")==0)
				Evaluator.normalize = true;
			else if(args[i].compareTo("-save")==0)
				Evaluator.modelFile = args[++i];
			else if(args[i].compareTo("-silent")==0)
				Ranker.verbose = false;

			else if(args[i].compareTo("-load")==0)
			{
				savedModelFile = args[++i];
				modelToLoad = args[i];
			}
			else if(args[i].compareTo("-inv")==0)
				printIndividual = true;
			else if(args[i].compareTo("-rank")==0)
				rankFile = args[++i];
			else if(args[i].compareTo("-score")==0)
				scoreFile = args[++i];

			//Ranker-specific parameters
			//RankNet
			else if(args[i].compareTo("-epoch")==0)
				RankNet.nIteration = Integer.parseInt(args[++i]);
			else if(args[i].compareTo("-layer")==0)
				RankNet.nHiddenLayer = Integer.parseInt(args[++i]);
			else if(args[i].compareTo("-node")==0)
				RankNet.nHiddenNodePerLayer = Integer.parseInt(args[++i]);
			
			//RankBoost
			else if(args[i].compareTo("-tc")==0)
				RankBoost.nThreshold = Integer.parseInt(args[++i]);
			
			//AdaRank
			else if(args[i].compareTo("-noeq")==0)
				AdaRank.trainWithEnqueue = false;
			else if(args[i].compareTo("-max")==0)
				AdaRank.maxSelCount = Integer.parseInt(args[++i]);
			
			//COORDINATE ASCENT
			else if(args[i].compareTo("-r")==0)
				CoorAscent.nRestart = Integer.parseInt(args[++i]);
			else if(args[i].compareTo("-i")==0)
				CoorAscent.nMaxIteration = Integer.parseInt(args[++i]);
			
			//ranker-shared parameters
			else if(args[i].compareTo("-round")==0)
			{
				RankBoost.nIteration = Integer.parseInt(args[++i]);
				AdaRank.nIteration = Integer.parseInt(args[i]);
			}
			else if(args[i].compareTo("-reg")==0)
			{
				CoorAscent.slack = Double.parseDouble(args[++i]);
				CoorAscent.regularized = true;
			}
			else if(args[i].compareTo("-tolerance")==0)
			{
				AdaRank.tolerance = Double.parseDouble(args[++i]);
				CoorAscent.tolerance = Double.parseDouble(args[i]);
			}
			
			else if(args[i].compareTo("-letor")==0)
				letor = true;
			//temporary
			else if(args[i].compareTo("-nf")==0)
				newFeatureFile = args[++i];
			else if(args[i].compareTo("-keep")==0)
				keepOrigFeatures = true;
			else if(args[i].compareTo("-t")==0)
				topNew = Integer.parseInt(args[++i]);
		}
		
		if(testMetric.compareTo("")==0)
			testMetric = trainMetric;
		
		System.out.println("");
		//System.out.println((keepOrigFeatures)?"Keep orig. features":"Discard orig. features");
		System.out.println("[+] General Parameters:");
		System.out.println("LETOR 4.0 dataset: " + (letor?"Yes":"No"));
		Evaluator e = new Evaluator(rType2[rankerType], trainMetric, testMetric);
		if(trainFile.compareTo("")!=0)
		{
			System.out.println("Training data:\t" + trainFile);
			
			if(foldCV != -1)
			{
				System.out.println("Cross validation: " + foldCV + " folds.");
			}
			else
			{
				if(testFile.compareTo("")!=0)
				{
					System.out.println("Test data:\t" + testFile);
					if(validationFile.compareTo("")!=0)//the user did specify the validation set 
						System.out.println("Validation data:\t" + validationFile);
				}
				else if(split > 0.0)//choose to split train data into train and test
				{
					System.out.println("Train-Test split: " + split);
				}
			}
			System.out.println("Ranking method:\t" + rType[rankerType]);
			if(featureDescriptionFile.compareTo("")!=0)
				System.out.println("Feature description file:\t" + featureDescriptionFile);
			else
				System.out.println("Feature description file:\tUnspecified. All features will be used.");
			System.out.println("Train metric:\t" + trainMetric);
			System.out.println("Test metric:\t" + testMetric);
			System.out.println("Feature normalization: " + ((Evaluator.normalize)?"Yes":"No"));
			if(modelFile.compareTo("")!=0)
				System.out.println("Model file: " + modelFile);
			
			System.out.println("");
			System.out.println("[+] " + rType[rankerType] + "'s Parameters:");
			RankerFactory rf = new RankerFactory();
			
			rf.createRanker(rType2[rankerType]).printParameters();
			System.out.println("");
			
			//starting to do some work
			//Evaluator e = new Evaluator(rType2[rankerType], trainMetric, testMetric);
			//if(1==1)return;
			if(foldCV != -1)
				e.evaluate(trainFile, featureDescriptionFile, foldCV);
			else
			{
				if(testFile.compareTo("")!=0)
				{
					if(validationFile.compareTo("")!=0)
						e.evaluate(trainFile, validationFile, testFile, featureDescriptionFile);
					else
						e.evaluate(trainFile, testFile, featureDescriptionFile);
				}
				else if(split > 0.0)
					e.evaluate(trainFile, featureDescriptionFile, split);
				else
					e.evaluate(trainFile, featureDescriptionFile);
			}
		}
		else //scenario: test a saved model
		{
			System.out.println("Model file:\t" + savedModelFile);
			System.out.println("Feature normalization: " + ((Evaluator.normalize)?"Yes":"No"));
			if(rankFile.compareTo("")!=0)
			{
				e.rank(savedModelFile, rankFile);
			}
			else if(scoreFile.compareTo("")!=0)
			{
				e.score(savedModelFile, scoreFile);
			}			
			else
			{
				System.out.println("Test metric:\t" + testMetric);
				e.test(savedModelFile, testFile, printIndividual);
			}
		}
	}

	//main settings
	public static boolean letor = false;
	public static boolean normalize = true;
	public static String modelFile = "";
 	public static String modelToLoad = "";
	
 	//tmp settings
 	public static String newFeatureFile = "";
 	public static boolean keepOrigFeatures = false;
 	public static int topNew = 2000;

 	protected RankerFactory rFact = new RankerFactory();
	protected MetricScorerFactory mFact = new MetricScorerFactory();
	
	protected MetricScorer trainScorer = null;
	protected MetricScorer testScorer = null;
	protected RANKER_TYPE type = RANKER_TYPE.LREG;
	protected Normalizer nml = new SumNormalizor();
	
	//variables for feature selection
	protected List<LinearComputer> lcList = new ArrayList<LinearComputer>();
	
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, METRIC testMetric)
	{
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric);
		testScorer = mFact.createScorer(testMetric);
	}
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, int trainK, METRIC testMetric, int testK)
	{
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric, trainK);
		testScorer = mFact.createScorer(testMetric, testK);
	}
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, METRIC testMetric, int k)
	{
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric, k);
		testScorer = mFact.createScorer(testMetric, k);
	}
	public Evaluator(RANKER_TYPE rType, METRIC metric, int k)
	{
		this.type = rType;
		trainScorer = mFact.createScorer(metric, k);
		testScorer = trainScorer;
	}
	public Evaluator(RANKER_TYPE rType, String trainMetric, String testMetric)
	{
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric);
		testScorer = mFact.createScorer(testMetric);
	}
	
	public List<RankList> readInput(String inputFile)	
	{
		FeatureManager fm = new FeatureManager();
		List<RankList> samples = fm.read(inputFile, letor);
		return samples;
	}
	public int[] readFeature(String featureDefFile)
	{
		FeatureManager fm = new FeatureManager();
		int[] features = fm.getFeatureIDFromFile(featureDefFile);
		return features;
	}
	public void normalize(List<RankList> samples, int[] fids)
	{
		for(int i=0;i<samples.size();i++)
			nml.normalize(samples.get(i), fids);
	}
	
	public double evaluate(Ranker ranker, List<RankList> rl)
	{
		List<RankList> l = rl;
		if(ranker != null)
			l = ranker.rank(rl);
		return testScorer.score(l);
	}
	
	/**
	 * Evaluate the currently selected ranking algorithm using <training data, testing data and defined features>.
	 * @param trainFile The training data.
	 * @param testFile The test data.
	 * @param featureDefFile The feature description file.
	 */
	public void evaluate(String trainFile, String testFile, String featureDefFile)
	{
		List<RankList> train = readInput(trainFile);//read input
		int[] features = readFeature(featureDefFile);//read features
		if(features == null)//no features specified ==> use all features in the training file
			features = getFeatureFromSampleVector(train);
		
		List<RankList> test = readInput(testFile);
		if(normalize)
		{
			normalize(train, features);
			normalize(test, features);
		}
		if(newFeatureFile.compareTo("")!=0)
		{
			System.out.print("Loading new feature description file... ");
			List<String> descriptions = FileUtils.readLine(newFeatureFile, "ASCII");
			for(int i=0;i<descriptions.size();i++)
			{
				if(descriptions.get(i).indexOf("##")==0)
					continue;
				LinearComputer lc = new LinearComputer("", descriptions.get(i));
				//if we keep the orig. features ==> discard size-1 linear computer
				if(!keepOrigFeatures || lc.size()>1)
					lcList.add(lc);
			}
			applyNewFeatures(train, features);
			features = applyNewFeatures(test, features);
			System.out.println("[Done]");
		}
		
		Ranker ranker = rFact.createRanker(type, train, features);
		ranker.set(trainScorer);
		ranker.init();
		ranker.learn();
		
		double rankScore = evaluate(ranker, test);
		
		System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
		if(modelFile.compareTo("")!=0)
		{
			System.out.println("");
			ranker.save(modelFile);
			System.out.println("Model saved to: " + modelFile);
		}
	}
	public void evaluate(String trainFile, String validationFile, String testFile, String featureDefFile)
	{
		List<RankList> train = readInput(trainFile);//read input
		List<RankList> validation = readInput(validationFile);
		List<RankList> test = readInput(testFile);
		int[] features = readFeature(featureDefFile);//read features
		if(features == null)//no features specified ==> use all features in the training file
			features = getFeatureFromSampleVector(train);
		
		if(normalize)
		{
			normalize(train, features);
			normalize(validation, features);
			normalize(test, features);
		}
		if(newFeatureFile.compareTo("")!=0)
		{
			System.out.print("Loading new feature description file... ");
			List<String> descriptions = FileUtils.readLine(newFeatureFile, "ASCII");
			int taken = 0;
			for(int i=0;i<descriptions.size();i++)
			{
				if(descriptions.get(i).indexOf("##")==0)
					continue;
				LinearComputer lc = new LinearComputer("", descriptions.get(i));
				//if we keep the orig. features ==> discard size-1 linear computer
				if(!keepOrigFeatures || lc.size()>1)
				{
					lcList.add(lc);
					taken++;
					if(taken == topNew)
						break;
				}
				//System.out.println(lc.toString());
			}
			applyNewFeatures(train, features);
			applyNewFeatures(validation, features);
			features = applyNewFeatures(test, features);
			System.out.println("[Done]");
		}
		
		Ranker ranker = rFact.createRanker(type, train, features);
		ranker.set(trainScorer);
		ranker.setValidationSet(validation);
		//ranker.setTestSet(test);
		ranker.init();
		ranker.learn();
		
		double rankScore = evaluate(ranker, test);
		
		System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
		if(modelFile.compareTo("")!=0)
		{
			System.out.println("");
			ranker.save(modelFile);
			System.out.println("Model saved to: " + modelFile);
		}
	}
	public void evaluate(String sampleFile, String featureDefFile, double percentTrain)
	{
		List<RankList> trainingData = new ArrayList<RankList>();
		List<RankList> testData = new ArrayList<RankList>();
		int[] features = prepareSplit(sampleFile, featureDefFile, percentTrain, normalize, trainingData, testData);
		
		Ranker ranker = rFact.createRanker(type, trainingData, features);
		ranker.set(trainScorer);
		ranker.init();
		ranker.learn();
		
		double rankScore = evaluate(ranker, testData);
		
		System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
		if(modelFile.compareTo("")!=0)
		{
			System.out.println("");
			ranker.save(modelFile);
			System.out.println("Model saved to: " + modelFile);
		}
	}
	public void evaluate(String trainFile, String featureDefFile)
	{
		List<RankList> train = readInput(trainFile);//read input
		int[] features = readFeature(featureDefFile);//read features
		if(features == null)//no features specified ==> use all features in the training file
			features = getFeatureFromSampleVector(train);
		
		if(normalize)
			normalize(train, features);
		
		if(newFeatureFile.compareTo("")!=0)
		{
			System.out.print("Loading new feature description file... ");
			List<String> descriptions = FileUtils.readLine(newFeatureFile, "ASCII");
			for(int i=0;i<descriptions.size();i++)
			{
				if(descriptions.get(i).indexOf("##")==0)
					continue;
				LinearComputer lc = new LinearComputer("", descriptions.get(i)); 
				//if we keep the orig. features ==> discard size-1 linear computer
				if(!keepOrigFeatures || lc.size()>1)
					lcList.add(lc);
			}
			features = applyNewFeatures(train, features);
			System.out.println("[Done]");
		}
		
		Ranker ranker = rFact.createRanker(type, train, features);
		ranker.set(trainScorer);
		ranker.init();
		
		if(type==RANKER_TYPE.COOR_ASCENT && modelToLoad.compareTo("")!=0)
		{
			Ranker r = rFact.loadRanker(modelToLoad);
			((CoorAscent)ranker).copyModel(((CoorAscent)r));
		}
		
		ranker.learn();
		
		if(modelFile.compareTo("")!=0)
		{
			System.out.println("");
			ranker.save(modelFile);
			System.out.println("Model saved to: " + modelFile);
		}
	}
	/**
	 * Evaluate the currently selected ranking algorithm using <data, defined features> with k-fold cross validation.
	 * @param sampleFile
	 * @param featureDefFile
	 * @param nFold
	 */
	public void evaluate(String sampleFile, String featureDefFile, int nFold)
	{
		List<List<RankList>> trainingData = new ArrayList<List<RankList>>();
		List<List<RankList>> testData = new ArrayList<List<RankList>>();
		int[] features = prepareCV(sampleFile, featureDefFile, nFold, normalize, trainingData, testData);
		
		Ranker ranker = null;
		double origScore = 0.0;
		double rankScore = 0.0;
		double oracleScore = 0.0;
		
		for(int i=0;i<nFold;i++)
		{
			List<RankList> train = trainingData.get(i);
			List<RankList> test = testData.get(i);
			
			ranker = rFact.createRanker(type, train, features);
			ranker.set(trainScorer);
			ranker.init();
			ranker.learn();
			
			double s1 = evaluate(null, test);
			origScore += s1;
			
			double s2 = evaluate(ranker, test);
			rankScore += s2;
			
			double s3 = evaluate(null, createOracles(test));
			oracleScore += s3;
		}
		
		System.out.println("Total: " + SimpleMath.round(origScore/nFold, 4) + "\t" + 
										SimpleMath.round(rankScore/nFold, 4) + "\t" +
										SimpleMath.round(oracleScore/nFold, 4) + "\t");
	}
	
	public void test(String modelFile, String testFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		double rankScore = evaluate(ranker, test);
		System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
	}
	public void test(String modelFile, String testFile, boolean printIndividual)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		double rankScore = 0.0;
		double score = 0.0;
		for(int i=0;i<test.size();i++)
		{
			RankList l = ranker.rank(test.get(i));
			score = testScorer.score(l);
			if(printIndividual)
				System.out.println(testScorer.name() + "   " + l.getID() + "   " + SimpleMath.round(score, 4));
			rankScore += score;
		}
		rankScore /= test.size();
		if(printIndividual)
			System.out.println(testScorer.name() + "   all   " + SimpleMath.round(rankScore, 4));
		else
			System.out.println(testScorer.name() + " on test data: " + SimpleMath.round(rankScore, 4));
	}
	public void rank(String modelFile, String testFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		for(int i=0;i<test.size();i++)
		{
			RankList l = test.get(i);
			double[] scores = new double[l.size()]; 
			for(int j=0;j<l.size();j++)
				scores[j] = ranker.eval(l.get(j));
			int[] idx = Sorter.sort(scores, false);
			List<Integer> ll = new ArrayList<Integer>();
			for(int j=0;j<idx.length;j++)
				ll.add(idx[j]);
			for(int j=0;j<l.size();j++)
			{
				int index = ll.indexOf(j) + 1;
				System.out.print(index + ((j==l.size()-1)?"":" "));
			}
			System.out.println("");
		}
	}
	public void score(String modelFile, String testFile)
	{
		Ranker ranker = rFact.loadRanker(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if(normalize)
			normalize(test, features);
		
		for(int i=0;i<test.size();i++)
		{
			RankList l = test.get(i);
			double[] scores = new double[l.size()]; 
			for(int j=0;j<l.size();j++)
				scores[j] = ranker.eval(l.get(j));
			for(int j=0;j<l.size();j++)
			{				
				System.out.print(scores[j] + ((j==l.size()-1)?"":" "));
			}
			System.out.println("");
		}
	}	
	private int[] prepareCV(String sampleFile, String featureDefFile, int nFold, boolean normalize, List<List<RankList>> trainingData, List<List<RankList>> testData)
	{
		List<RankList> data = readInput(sampleFile);//read input
		int[] features = readFeature(featureDefFile);//read features
		if(features == null)//no features specified ==> use all features in the training file
			features = getFeatureFromSampleVector(data);
		
		if(normalize)
			normalize(data, features);
		if(newFeatureFile.compareTo("")!=0)
		{
			System.out.print("Loading new feature description file... ");
			List<String> descriptions = FileUtils.readLine(newFeatureFile, "ASCII");
			for(int i=0;i<descriptions.size();i++)
			{
				if(descriptions.get(i).indexOf("##")==0)
					continue;
				LinearComputer lc = new LinearComputer("", descriptions.get(i));
				//if we keep the orig. features ==> discard size-1 linear computer
				if(!keepOrigFeatures || lc.size()>1)
					lcList.add(lc);
			}
			features = applyNewFeatures(data, features);
			System.out.println("[Done]");
		}
		
		List<List<Integer>> trainSamplesIdx = new ArrayList<List<Integer>>();
		int size = data.size()/nFold;
		int start = 0;
		int total = 0;
		for(int f=0;f<nFold;f++)
		{
			List<Integer> t = new ArrayList<Integer>();
			for(int i=0;i<size && start+i<data.size();i++)
				t.add(start+i);
			trainSamplesIdx.add(t);
			total += t.size();
			start += size;
		}
		for(;total<data.size();total++)
			trainSamplesIdx.get(trainSamplesIdx.size()-1).add(total);
		
		for(int i=0;i<trainSamplesIdx.size();i++)
		{
			List<RankList> train = new ArrayList<RankList>();
			List<RankList> test = new ArrayList<RankList>();
			
			List<Integer> t = trainSamplesIdx.get(i);
			for(int j=0;j<data.size();j++)
			{
				if(t.contains(j))
					test.add(new RankList(data.get(j)));
				else
					train.add(new RankList(data.get(j)));
			}
			
			trainingData.add(train);
			testData.add(test);
		}
		
		return features;
	}
	private int[] prepareSplit(String sampleFile, String featureDefFile, double percentTrain, boolean normalize, List<RankList> trainingData, List<RankList> testData)
	{
		List<RankList> data = readInput(sampleFile);//read input
		int[] features = readFeature(featureDefFile);//read features
		if(features == null)//no features specified ==> use all features in the training file
			features = getFeatureFromSampleVector(data);
		
		if(normalize)
			normalize(data, features);
		if(newFeatureFile.compareTo("")!=0)
		{
			System.out.print("Loading new feature description file... ");
			List<String> descriptions = FileUtils.readLine(newFeatureFile, "ASCII");
			for(int i=0;i<descriptions.size();i++)
			{
				if(descriptions.get(i).indexOf("##")==0)
					continue;
				LinearComputer lc = new LinearComputer("", descriptions.get(i));
				//if we keep the orig. features ==> discard size-1 linear computer
				if(!keepOrigFeatures || lc.size()>1)
					lcList.add(lc);
			}
			features = applyNewFeatures(data, features);
			System.out.println("[Done]");
		}
			
		int size = (int) (data.size() * percentTrain);
		
		for(int i=0; i<size; i++)
			trainingData.add(new RankList(data.get(i)));
		for(int i=size; i<data.size(); i++)
			testData.add(new RankList(data.get(i)));
		
		return features;
	}
	private List<RankList> createOracles(List<RankList> rl)
	{
		List<RankList> oracles = new ArrayList<RankList>();
		for(int i=0;i<rl.size();i++)
		{
			oracles.add(rl.get(i).getCorrectRanking());
		}
		return oracles;
	}
	
	public int[] getFeatureFromSampleVector(List<RankList> samples)
	{
		DataPoint dp = samples.get(0).get(0);
		int fc = dp.getFeatureCount();
		int[] features = new int[fc];
		for(int i=0;i<fc;i++)
			features[i] = i+1;
		return features;
	}
	
	private int[] applyNewFeatures(List<RankList> samples, int[] features)
	{
		int totalFeatureCount = samples.get(0).get(0).getFeatureCount();
		int[] newFeatures = new int[features.length+lcList.size()];
		System.arraycopy(features, 0, newFeatures, 0, features.length);
		//for(int i=0;i<features.length;i++)
			//newFeatures[i] = features[i];
		for(int k=0;k<lcList.size();k++)
			newFeatures[features.length+k] = totalFeatureCount+k+1;
		
		float[] addedFeatures = new float[lcList.size()];
		for(int i=0;i<samples.size();i++)
		{
			RankList rl = samples.get(i);
			for(int j=0;j<rl.size();j++)
			{
				DataPoint p = rl.get(j);
				for(int k=0;k<lcList.size();k++)
					addedFeatures[k] = lcList.get(k).compute(p.getExternalFeatureVector());

				p.addFeatures(addedFeatures);
			}
		}
		
		int[] newFeatures2 = new int[lcList.size()];
		for(int i=0;i<lcList.size();i++)
			newFeatures2[i] = newFeatures[i+features.length];
		
		if(keepOrigFeatures)
			return newFeatures;
		return newFeatures2;
	}
}
