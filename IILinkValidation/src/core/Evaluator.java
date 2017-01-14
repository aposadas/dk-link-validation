package core;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/**
 * Skeleton for an evaluator of the type extraction
 */
public class Evaluator {


	
	public float calculateRecall(File results, File goldStandard) throws IOException
	{
		float truePositive = 0;
		float falseNegative = 0;
		float recall = 0;
		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));
		recall = ((float) truePositive / (float) (truePositive+falseNegative));  //NOTE:Following the wikipedia formula, or should we use fabian's??

		return recall;
	}

	
	public float calculatePrecission(File results, File goldStandard) throws IOException
	{
		float truePositive = 0;
		float falsePositive = 0;
		float prescission = 0;
		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));
		
		prescission  = ((float) truePositive / (float) (truePositive+falsePositive));  //NOTE:Following the wikipedia formula, or should we use fabian's??
 	
		return prescission;
	}
	public float calculateFMeasuare(double precission, double recall) throws IOException
	{
		float f1Score  = 0;
		f1Score = 2*((float)(precission*recall)/(float)(precission+recall));
		
		return f1Score;
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
		 
		 float recallFinal =0;
		 float precissionFinal = 0;
		 float  f1Final = 0;
		 Evaluator evaluator = new Evaluator();
		 recallFinal = evaluator.calculateRecall(new File(args[1]), new File(args[0]));
		 precissionFinal = evaluator.calculatePrecission(new File(args[1]), new File(args[0]));
		 f1Final = evaluator.calculateFMeasuare(precissionFinal, recallFinal);
		 
		 System.out.println("recall = "+ recallFinal );
		 System.out.println("precision = "+ precissionFinal);
		 System.out.println("F1 Measure = "+ f1Final);
	}
	
	
}