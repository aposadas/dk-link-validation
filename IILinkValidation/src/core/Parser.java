package core;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.wcohen.ss.api.*;
import com.wcohen.ss.*;

public class Parser {

	/**
	 * Read data from OWL file and rdf
	 * @param owlPath
	 * @param rdfPath1
	 * @param rdfPath2
	 */
	
	public static List<String> listLinksSameAs = new ArrayList<String>();
	
	public void sameAsFinder(String links) {
		String linkSameAs;
		String comp = "sameAs";

		if (links.contains(comp)) {
			linkSameAs = links;
			listLinksSameAs.add(linkSameAs);
		}
	}
	
	public static double getSimilarityScore (String uri1, String uri2){
		double scoreSimilarity  = 0;
		 StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		 StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);
		 scoreSimilarity = new JaroWinkler().score(uri1,uri2);
		 System.out.println("score: " + scoreSimilarity);
		return  scoreSimilarity;
	}

	public void readOWL(String owl1,String owl2, String owl3,String rdfPath1,String rdfPath2)
	{
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o;

		try {
			o = m.loadOntologyFromOntologyDocument(new File(owl1));
			System.out.println(o != null ? "True" : "False");
		}
		catch (OWLOntologyCreationException e) {
 			e.printStackTrace();
		}
		
//		Model schema1 = FileManager.get().loadModel(owl1);
//		Model schema2 = FileManager.get().loadModel(owl2);
//		Model schema3 = FileManager.get().loadModel(owl3);
//		
//		Model data1 = FileManager.get().loadModel(rdfPath1);
//		Model data2 = FileManager.get().loadModel(rdfPath2);
//		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
//		reasoner = reasoner.bindSchema(schema1);
//		reasoner = reasoner.bindSchema(schema2);
//		reasoner = reasoner.bindSchema(schema3);
//		
//		//InfModel infmodel = ModelFactory.createInfModel(reasoner, data1,data2);
//		InfModel infmodel = ModelFactory.createInfModel(reasoner, data1);
//		
//		//infmodel.samePrefixMappingAs(arg0)
//		infmodel.listStatements().forEachRemaining(s->sameAsFinder(s.toString()));
//		//infmodel.listStatements().forEachRemaining(sd->System.out.println(sd.toString()));
//		
//		for (String line: listLinksSameAs){
//			System.out.println(line);	
//		}
	}
	
	/**
	 * This method parses the rdf file and prints stuff.
	 * @param rdfFilePath File Path
	 *
	 */
	public Model readRDF(String rdfFilePath) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(rdfFilePath);

		// read the RDF/XML file
		return model.read(in, null);
	}
	
	/**
	 * This method parses the rdf file and prints stuff.
	 * @param m1 Model 1
	 * @param m2 Model 2
	 *
	 */
	public void compareRDF(Model m1, Model m2)
	{
		Iterator<Statement> it1 = m1.listStatements();

		while(it1.hasNext()) {
			Statement statement = it1.next();

			Iterator<Statement> itt = statement.getModel().listStatements();
			while(itt.hasNext()){
				System.out.println("Statement: \n"+ itt.next());
				//Property p = itt.next().getPredicate();
			}

			break;
		}
	}
	
	public static void main(String[] args) throws Exception {
		 args = new String[] {
		 		 "resources/PR-1/restaurants/ontology_restaurant.owl",
				 "resources/PR-1/restaurants/ontology_restaurant1.owl",
				 "resources/PR-1/restaurants/ontology_restaurant2.owl",
				 "resources/PR-1/restaurants/restaurant1.rdf",
				 "resources/PR-1/restaurants/restaurant2.rdf"
				 };

		 Parser parser = new Parser();

		 Model model_restaurant1 = parser.readRDF(args[3]);
		 Model model_restaurant2 = parser.readRDF(args[4]);
		 parser.compareRDF(model_restaurant1, model_restaurant2);

		//parser.readOWL(args[0],args[3],args[4]);
		//parser.readOWL(args[0], args[1], args[2], args[3], args[4]);

		String uri1 = "http://www.okkam.org/oaie/restaurant1-Restaurant0'";
		String uri2 = "http://www.okkam.org/oaie/restaurant2-Restaurant0";
		getSimilarityScore(uri1, uri2);
	}
}
