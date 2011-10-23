package ciir.umass.edu.stats;

import java.util.Random;

import flanagan.analysis.Regression;

public class RegressionEngine {

	public static void main(String[] args)
	{
		int n = 50;
		double[][] x = new double[2][];
		double[] y = new double[n];
		for(int i=0;i<x.length;i++)
			x[i] = new double[n];
		for(int i=0;i<n;i++)
		{
			Random r = new Random();
			x[0][i] = r.nextDouble();
			x[1][i] = r.nextDouble();
			y[i] = get(x[0][i], x[1][i]);
		}
		
		/*for(int i=0;i<20;i++)
			System.out.print(x[0][i] + " ");
		System.out.println("");
		for(int i=0;i<20;i++)
			System.out.print(x[1][i] + " ");
		System.out.println("");
		for(int i=0;i<20;i++)
			System.out.print(y[i] + " ");*/

		run(x, y);
	}
	
	public static double get(double x1, double x2)
	{
		return 2*x1 + x2;
	}
	public static void run(double[][] x, double[] y)
	{
		Regression r = new Regression(x, y);
		r.linear();
		//r.print();
		
		//test
		double[] test = new double[]{1, 2};
		double[] c = r.getCoeff();
		double v = c[0];
		System.out.print(c[0] + " ");
		for(int i=1;i<c.length;i++)
		{
			System.out.print(c[i] + " ");
			v += c[i]*test[i-1];
		}
		System.out.println("Predict=" + v);
		
	}
}

