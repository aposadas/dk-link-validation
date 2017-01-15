package core;

import java.io.*;
import java.util.Iterator;

import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.api.StringWrapper;
import org.apache.xerces.dom.DeferredElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


/**
 * Skeleton for an evaluator of the type extraction
 */
public class Evaluator {

	private double getSimilarityScore (String uri1, String uri2){
		StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);

		return new JaroWinkler().score(stringWrapper1, stringWrapper2);
	}


	private float calculateRecallOrPrecision(String results, String goldStandard, boolean wantRecall) throws IOException
	{
		float truePositive = 0;
		float falseNegative = 0;
		float falsePositive = 0;
		float recall = 0;
		float precision = 0;

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document resultsDoc = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			resultsDoc = builder.parse(new FileInputStream(results));
			Document goldStandardDoc = builder.parse( new FileInputStream(goldStandard));
			XPath xPath =  XPathFactory.newInstance().newXPath();
			NodeList resultList = (NodeList) xPath.compile("//Cell").evaluate(resultsDoc, XPathConstants.NODESET);
			NodeList referenceList = (NodeList) xPath.compile("//Cell").evaluate(goldStandardDoc, XPathConstants.NODESET);
			int numberOfResult = referenceList.getLength();
			boolean resultWasFoundInGoldStandard = false;

			for(int i=0; i<resultList.getLength(); i++){
				resultWasFoundInGoldStandard = false;
				Node childNode = resultList.item(i);
				String firstRestaurant = ((DeferredElementImpl) childNode).getElementsByTagName("entity1")
						.item(0).getAttributes().item(0).getNodeValue();
				String secondRestaurant = ((DeferredElementImpl) childNode).getElementsByTagName("entity2")
						.item(0).getAttributes().item(0).getNodeValue();
				String relation = ((DeferredElementImpl) childNode).getElementsByTagName("relation")
						.item(0).getFirstChild().getNodeValue();

				for (int j=0; j<numberOfResult; j++) {
					Node refChildNode = referenceList.item(j);
					String refFirstRestaurant = ((DeferredElementImpl) refChildNode).getElementsByTagName("entity1")
							.item(0).getAttributes().item(0).getNodeValue();
					String refSecondRestaurant = ((DeferredElementImpl) refChildNode).getElementsByTagName("entity2")
							.item(0).getAttributes().item(0).getNodeValue();
					String refRelation = ((DeferredElementImpl) refChildNode).getElementsByTagName("relation")
							.item(0).getFirstChild().getNodeValue();


					if ((refFirstRestaurant.equals(firstRestaurant) && refSecondRestaurant.equals(secondRestaurant)) ||
							(refFirstRestaurant.equals(secondRestaurant) && refSecondRestaurant.equals(firstRestaurant))) {
						resultWasFoundInGoldStandard = true;
						if ( relation.equals("=") && refRelation.equals("=")) {
							truePositive ++;
						} else if ( relation.equals("!=") && refRelation.equals("=")) {
							falseNegative ++;
						} else if ( relation.equals("=") && refRelation.equals("!=")) {
							falsePositive ++;
						}
					}
				};

				if (resultWasFoundInGoldStandard == false) {
					if (relation.equals("=")) {
						falsePositive++;
					}
				}
			}


		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}


		recall = ((float) truePositive / (float) (truePositive + falseNegative));  //NOTE:Following the wikipedia formula, or should we use fabian's??
		precision = truePositive / (truePositive + falsePositive);  //NOTE:Following the wikipedia formula, or should we use fabian's??

		if (wantRecall) {
			return recall;
		} else {
			return precision;
		}
	}


	/*
	private float calculateRecall(String results, String goldStandard) throws IOException
	{
		float truePositive = 0;
		float falseNegative = 0;
		float recall = 0;

		Model resultModel = ModelFactory.createDefaultModel();
		InputStream resultStream = new FileManager().get().open(results);
		resultModel.read(resultStream, null);

		Model goldStandardModel = ModelFactory.createDefaultModel();
		InputStream goldStandardStream = new FileManager().get().open(goldStandard);
		goldStandardModel.read(goldStandardStream, null);


		Iterator<Statement> its = goldStandardModel.listStatements();

		while(its.hasNext()) {
			Statement statement = its.next();
			String s = statement.getObject().toString();
			System.out.println(s);
		}


		recall = ((float) truePositive / (float) (truePositive+falseNegative));  //NOTE:Following the wikipedia formula, or should we use fabian's??

		return recall;
	}
	*/

	private float calculateRecall(String results, String goldStandard) throws IOException {
		return calculateRecallOrPrecision(results, goldStandard, true);
	}

	private float calculatePrecision(String results, String goldStandard) throws IOException {
		return calculateRecallOrPrecision(results, goldStandard, false);
	}

	public float calculateFMeasure(double precision, double recall) throws IOException {
		float f1Score = 2*((float)(precision*recall)/(float)(precision+recall));
		
		return f1Score;
	}


	/**
	 * Takes as arguments (1) the gold standard and (2) the output of the owl
	 * same as link evaluation.
	 */
	public static void main(String[] args) throws Exception {
		//args = new String[] {
		 //"C:/.." 	 result file
		//"C:/...", goldStandard file
		//};

		Evaluator evaluator = new Evaluator();
		float recallFinal = evaluator.calculateRecall(args[0], args[1]);
		float precisionFinal = evaluator.calculatePrecision(args[0], args[1]);
		float f1Final = evaluator.calculateFMeasure(precisionFinal, recallFinal);

		System.out.println("recall = " + recallFinal );
		System.out.println("precision = " + precisionFinal);
		System.out.println("F1 Measure = " + f1Final);

	}
}