package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;


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

	
	public void readOWL(String owl1,String owl2, String owl3,String rdfPath1,String rdfPath2)
	{
		File f2 = new File("./resources/results/result.txt");
		PrintWriter printWriter;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont,ont2,ont3,ont4,ont5;
		try {
			printWriter = new PrintWriter(f2);
			ont = manager.loadOntologyFromOntologyDocument(new File(owl1));
			ont2 = manager.loadOntologyFromOntologyDocument(new File(owl2));
			ont3 = manager.loadOntologyFromOntologyDocument(new File(owl3));
			ont4 = manager.loadOntologyFromOntologyDocument(new File(rdfPath1));
			ont5 = manager.loadOntologyFromOntologyDocument(new File(rdfPath2));

			manager.addAxioms(ont, ont2.getAxioms());
			manager.addAxioms(ont, ont3.getAxioms());
			manager.addAxioms(ont, ont4.getAxioms());
			manager.addAxioms(ont, ont5.getAxioms());

			OWLOntology ont6 = manager.loadOntologyFromOntologyDocument(new File(owl1));
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasoner6 = reasonerFactory.createReasoner(ont6);
			
			OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);

			OWLDataFactory fac = manager.getOWLDataFactory();
			String prefix1="http://www.okkam.org/ontology_restaurant1.owl#";
			String prefix2="http://www.okkam.org/ontology_restaurant2.owl#";
			String prefixrdf1="http://www.okkam.org/oaie/restaurant1-";
			String prefixrdf2="http://www.okkam.org/oaie/restaurant2-";
			String[] classes = new String[] {"Restaurant"};			
			

			OWLClass[] classes1 = new OWLClass[classes.length];
			OWLClass[] classes2 = new OWLClass[classes.length];
			
			
			for(int i=0;i<classes.length;i++)
			{
				classes1[i]=fac.getOWLClass(IRI.create(prefix1+classes[i]));
				classes2[i]=fac.getOWLClass(IRI.create(prefix2+classes[i]));
			}
			
			 
			 Set<OWLNamedIndividual> instances1 = reasoner.getInstances(classes1[0], false).getFlattened();
			 Set<OWLNamedIndividual> instances2 = reasoner.getInstances(classes2[0], false).getFlattened();
		     for (OWLNamedIndividual inst1 : instances1) {
		    	 for (OWLNamedIndividual inst2 : instances2) {
		    		 if(inst1.toString().equals(inst2.toString().replace(prefixrdf2, prefixrdf1)))
		    		 {
		    			 
				    	 Set<OWLAnnotationAssertionAxiom> list1 = ont.getAnnotationAssertionAxioms(inst1.getIRI());
				    	 Set<OWLAnnotationAssertionAxiom> list2 = ont.getAnnotationAssertionAxioms(inst2.getIRI());
						 double count = 0.0;
						 double average = 0.0;
				    	 for(OWLAnnotationAssertionAxiom oa1:list1)
						 {
							 for(OWLAnnotationAssertionAxiom oa2:list2)
							 {
								 if(oa1.getProperty().toString().replace(prefix1, "").equals(oa2.getProperty().toString().replace(prefix2, ""))){
									 count ++;
									 average += getSimilarityScore(oa1.getValue().toString(),oa2.getValue().toString());
									 //printWriter.println(oa1.getValue().toString()+","+oa2.getValue().toString());
								 }
							 }
							
						 }
				    	 average = average / count;
				    	 //printWriter.println(average);
				    	 if(average>=0.9)
				    	 {
				    		 printWriter.println(inst1);
				    		 printWriter.println(inst2);
				    		 printWriter.println("=");
				    	 }else
				    	 {
				    		 printWriter.println(inst1);
				    		 printWriter.println(inst2);
				    		 printWriter.println("!=");
				    	 }
		    		 }
		    	 }
		    	 
		    	 
			 }
	         printWriter.close();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double getSimilarityScore (String uri1, String uri2){
		StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);

		double similarityScore = new JaroWinkler().score(uri1,uri2);
//		System.out.println("score: " + similarityScore);
//		String scoreSimilarityExp = new JaroWinkler().explainScore(uri1,uri2);
//		System.out.println("Explain score: " + scoreSimilarityExp);
		return similarityScore;
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
	public void compareRDF(Model m1, Model m2) {
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
		 //parser.compareRDF(model_restaurant1, model_restaurant2);

		//parser.readOWL(args[0],args[3],args[4]);
		parser.readOWL(args[0], args[1], args[2], args[3], args[4]);

		String uri1 = "http://www.okkam.org/oaie/restaurant1-'";
		String uri2 = "http://www.okkam.org/oaie/restaurant2-Restaurant0";

		double similarityScore = parser.getSimilarityScore(uri1, uri2);
		//System.out.println("Similarity score = " + similarityScore);
	}
}
