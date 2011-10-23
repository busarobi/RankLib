package ciir.umass.edu.utilities;

/**
 * @author vdang
 */
public class SimpleMath {
	public static double logBase2(double value)
	{
		return Math.log(value)/Math.log(2);
	}
	public static double logBase10(double value)
	{
		return Math.log(value)/Math.log(10);
	}
	public static double ln(double value)
	{
		return Math.log(value)/Math.log(Math.E);
	}
	public static int min(int a, int b)
	{
		return (a>b)?b:a;
	}
	public static double p(long count, long total)
	{
		return ((double)count+0.5)/(total+1);
	}
	public static double round(double val)
	{
		int precision = 10000; //keep 4 digits
		return Math.floor(val * precision +.5)/precision;
	}
	public static double round(float val)
	{
		int precision = 10000; //keep 4 digits
		return Math.floor(val * precision +.5)/precision;
	}
	public static double round(double val, int n)
	{
		int precision = 1; 
		for(int i=0;i<n;i++)
			precision *= 10;
		return Math.floor(val * precision +.5)/precision;
	}
	public static float round(float val, int n)
	{
		int precision = 1; 
		for(int i=0;i<n;i++)
			precision *= 10;
		return (float) (Math.floor(val * precision +.5)/precision);
	}
}
