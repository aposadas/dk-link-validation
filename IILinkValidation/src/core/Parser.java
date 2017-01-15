package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
	 * Compares OWL ontologies.
	 * @param owlPath1 OWL file path 1
	 * @param owlPath2 OWL file path 2
	 * @param owlPath3 OWL file path 3
	 * @param rdfPath1 RDF file path 1
	 * @param rdfPath2 RDF file path 2
	 * @param prefix1 Namespace 1
	 * @param prefix2 Namespace 2
	 * @param prefixRdf1 RDF prefix 1
	 * @param prefixRdf2 RDF prefix 2
	 * @param owlClass Class we want to compare
	 */
	public void compareOwlOntologies(String owlPath1, String owlPath2, String owlPath3, String rdfPath1, String rdfPath2,
	                                 String prefix1, String prefix2, String prefixRdf1, String prefixRdf2, String owlClass) {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null, ont2 = null, ont3 = null, ont4 = null, ont5 = null;

		try {
			ont = manager.loadOntologyFromOntologyDocument(new File(owlPath1));
			ont2 = manager.loadOntologyFromOntologyDocument(new File(owlPath2));
			ont3 = manager.loadOntologyFromOntologyDocument(new File(owlPath3));
			ont4 = manager.loadOntologyFromOntologyDocument(new File(rdfPath1));
			ont5 = manager.loadOntologyFromOntologyDocument(new File(rdfPath2));
		}
		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		manager.addAxioms(ont, ont2.getAxioms());
		manager.addAxioms(ont, ont3.getAxioms());
		manager.addAxioms(ont, ont4.getAxioms());
		manager.addAxioms(ont, ont5.getAxioms());

		PrintWriter printWriter = null;
		File resultFile = new File("./resources/results/result.txt");
		try {
			printWriter = new PrintWriter(resultFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);

		OWLDataFactory fac = manager.getOWLDataFactory();
		String[] classes = new String[] {owlClass};

		int length = classes.length;

		OWLClass[] classes1 = new OWLClass[length];
		OWLClass[] classes2 = new OWLClass[length];

		for(int i = 0; i < length; i++) {
			classes1[i] = fac.getOWLClass(IRI.create(prefix1 + classes[i]));
			classes2[i] = fac.getOWLClass(IRI.create(prefix2 + classes[i]));
		}

		Set<OWLNamedIndividual> instances1 = reasoner.getInstances(classes1[0], false).getFlattened();
		Set<OWLNamedIndividual> instances2 = reasoner.getInstances(classes2[0], false).getFlattened();

		for (OWLNamedIndividual inst1 : instances1) {
			for (OWLNamedIndividual inst2 : instances2) {
				if(inst1.toString().equals(inst2.toString().replace(prefixRdf2, prefixRdf1))) {
					Set<OWLAnnotationAssertionAxiom> list1 = ont.getAnnotationAssertionAxioms(inst1.getIRI());
					Set<OWLAnnotationAssertionAxiom> list2 = ont.getAnnotationAssertionAxioms(inst2.getIRI());

					double count = 0.0;
					double average = 0.0;

					for(OWLAnnotationAssertionAxiom oa1:list1) {
						for(OWLAnnotationAssertionAxiom oa2:list2) {

							String property1 = oa1.getProperty().toString().replace(prefix1, "");
							String property2 = oa2.getProperty().toString().replace(prefix2, "");
							
							if(property1.equals(property2)){
								count++;
								average += getSimilarityScore(oa1.getValue().toString(), oa2.getValue().toString());
								//printWriter.println(oa1.getValue().toString()+","+oa2.getValue().toString());
							}
						}
					}
					
					average = average / count;

					if(average >= 0.9) {
						printResults(printWriter, inst1, inst2, "=", average);
					}
					else {
						printResults(printWriter, inst1, inst2, "!=", average);
					}
				}
			}
		}
		printWriter.close();
	}

	private void printResults(PrintWriter printWriter, OWLNamedIndividual inst1, OWLNamedIndividual inst2,
	                          String result, double average) {
		printWriter.println(inst1);
		printWriter.println(inst2);
		printWriter.println(result);
		printWriter.println(average);
	}

	private double getSimilarityScore (String uri1, String uri2){
		StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);

		return new JaroWinkler().score(stringWrapper1, stringWrapper2);
	}

	/**
	 * This method reads the rdf file.
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
	
	public static void main(String[] args) throws Exception {
		args = new String[] {
		     "resources/PR-1/restaurants/ontology_restaurant.owl",
			 "resources/PR-1/restaurants/ontology_restaurant1.owl",
			 "resources/PR-1/restaurants/ontology_restaurant2.owl",
			 "resources/PR-1/restaurants/restaurant1.rdf",
			 "resources/PR-1/restaurants/restaurant2.rdf"
			 };

		String prefix1 = "http://www.okkam.org/ontology_restaurant1.owl#";
		String prefix2 = "http://www.okkam.org/ontology_restaurant2.owl#";
		String prefixRdf1 = "http://www.okkam.org/oaie/restaurant1-";
		String prefixRdf2 = "http://www.okkam.org/oaie/restaurant2-";
		String owlClass = "Restaurant";

		Parser parser = new Parser();
		parser.compareOwlOntologies(args[0], args[1], args[2], args[3], args[4],
				prefix1, prefix2, prefixRdf1, prefixRdf2, owlClass);
	}
}
