package core;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/**
 * Skeleton for an evaluator of the type extraction
 */
public class Evaluator {


	
	public double calculateRecall(File results, File goldStandard) throws IOException
	{
		double captured = 0;
		double count = 0;
		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));

		//TODO
		return 0.0;
	}

	
	public double calculatePrecission(File results, File goldStandard) throws IOException
	{
		double captured = 0;
		double count = 0;
		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));

		//TODO
		return 0.0;
	}
	/**
	 * Takes as arguments (1) the gold standard and (2) the output of the owl
	 * same as link evaluation.
	 */
	public static void main(String[] args) throws Exception {
		 args = new String[] {
		 //"C:/...",
		 //"C:/.." 
				 };
		 Evaluator evaluator = new Evaluator();
		 System.out.println("recall = "+evaluator.calculateRecall(new File(args[1]), new File(args[0])));
		 System.out.println("precision = "+evaluator.calculatePrecission(new File(args[1]), new File(args[0])));
	}
	
	
}
