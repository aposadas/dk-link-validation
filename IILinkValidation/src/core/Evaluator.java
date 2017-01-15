package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.api.StringWrapper;

/**
 * Skeleton for an evaluator of the type extraction
 */
public class Evaluator {

	private double getSimilarityScore (String uri1, String uri2){
		StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);

		return new JaroWinkler().score(stringWrapper1, stringWrapper2);
	}
	
	private float calculateRecall(File results, File goldStandard) throws IOException {
		float truePositive = 0;
		float falseNegative = 0;

		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));

		float recall = truePositive / (truePositive+falseNegative);  //NOTE:Following the wikipedia formula, or should we use fabian's??

		return recall;
	}

	private float calculatePrecision(File results, File goldStandard) throws IOException {
		float truePositive = 0;
		float falsePositive = 0;
		BufferedReader br = new BufferedReader(new FileReader(results));
		BufferedReader br2 = new BufferedReader(new FileReader(goldStandard));

		float precision = truePositive / (truePositive + falsePositive);  //NOTE:Following the wikipedia formula, or should we use fabian's??
 	
		return precision;
	}

	public float calculateFMeasure(double precission, double recall) throws IOException {
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

		Evaluator evaluator = new Evaluator();
		float recallFinal = evaluator.calculateRecall(new File(args[1]), new File(args[0]));
		float precisionFinal = evaluator.calculatePrecision(new File(args[1]), new File(args[0]));
		float f1Final = evaluator.calculateFMeasure(precisionFinal, recallFinal);

		System.out.println("recall = " + recallFinal );
		System.out.println("precision = " + precisionFinal);
		System.out.println("F1 Measure = " + f1Final);
	}
}