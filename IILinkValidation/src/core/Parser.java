package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;


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
		OWLOntology ont = null;
        try {
			ont = fuseAxioms(manager,new File[]{new File(owlPath1),new File(owlPath2),new File(owlPath3),new File(rdfPath1),new File(rdfPath2)});
		}catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		File resultFile = new File("./resources/results/result.txt");
		PrintWriter printWriter = initializeResultsFile(resultFile);

		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass class1 = factory.getOWLClass(IRI.create(prefix1 + owlClass));
		OWLClass class2 = factory.getOWLClass(IRI.create(prefix2 + owlClass));


		Set<OWLNamedIndividual> instances1 = reasoner.getInstances(class1, false).getFlattened();
		Set<OWLNamedIndividual> instances2 = reasoner.getInstances(class2, false).getFlattened();

		
		for (OWLNamedIndividual inst1 : instances1) {
			for (OWLNamedIndividual inst2 : instances2) {
				if(true) {
					
					Set<OWLAnnotationAssertionAxiom> list1 = ont.getAnnotationAssertionAxioms(inst1.getIRI());
					Set<OWLAnnotationAssertionAxiom> list2 = ont.getAnnotationAssertionAxioms(inst2.getIRI());

					double count = 0.0;
					double average = 0.0;

					StringBuilder explanation = new StringBuilder();

					for(OWLAnnotationAssertionAxiom oa1:list1) {
						for(OWLAnnotationAssertionAxiom oa2:list2) {

							String property1 = oa1.getProperty().toString().replace(prefix1, "");
							String property2 = oa2.getProperty().toString().replace(prefix2, "");
							
							if(property1.equals(property2)){
								count++;
								average += getSimilarityScore(oa1.getValue().toString(), oa2.getValue().toString());
								explanation.append(oa1.getValue().toString()).append(", ")
										.append(oa2.getValue().toString()).append("\n");
							}
						}
					}
					
					average = average / count;

					String result = (average >= 0.9) ? "=" : "!=";
					printResults(printWriter, inst1, inst2, result, average, explanation.toString());
				}
			}
		}
		finalizeResultsFile(printWriter);
	}

	private OWLOntology fuseAxioms(OWLOntologyManager manager, File[] files) throws OWLOntologyCreationException
	{
		OWLOntology ont = null;
		for(File f:files)
		{
			if(f.exists())
			{
				if(ont==null)
				{
					ont = manager.loadOntologyFromOntologyDocument(f);
				}else
				{
					OWLOntology ont2 = manager.loadOntologyFromOntologyDocument(f);
					manager.addAxioms(ont, ont2.getAxioms());
					}
			}
		}
		return ont;
	}
	
	private void finalizeResultsFile(PrintWriter printWriter) {
		printWriter.println("</Alignment>\n" +
				"</rdf:RDF>");
		printWriter.close();
	}

	private PrintWriter initializeResultsFile(File resultFile) {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(resultFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Add header to results file
		String header = "<?xml version='1.0' encoding='utf-8' standalone='no'?> \n" +
				"<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n" +
				"\txmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
				"\txmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n" +
				"\txmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n" +
				"<Alignment>\n" +
				"\t<xml>yes</xml>\n" +
				"\t<level>0</level>\n" +
				"\t<type>**</type>\n" +
				"\t<onto1>\n" +
				"\t\t<Ontology>\n" +
				"\t\t\t<location>null</location>\n" +
				"\t\t</Ontology>\n" +
				"\t</onto1>\n" +
				"\t<onto2>\n" +
				"\t\t<Ontology>\n" +
				"\t\t\t<location>null</location>\n" +
				"\t\t</Ontology>\n" +
				"\t</onto2>\n";
		printWriter.println(header);

		return printWriter;
	}

	private void printResults(PrintWriter printWriter, OWLNamedIndividual inst1, OWLNamedIndividual inst2,
	                          String result, double average, String explanation) {

		printWriter.println("\t<map>\n\t\t<Cell>");
		printWriter.println("\t\t\t<entity1 rdf:resource='" + inst1.toString().replace("<","").replace(">","") + "'/>");
		printWriter.println("\t\t\t<entity2 rdf:resource='" + inst2.toString().replace("<","").replace(">","") + "'/>");
		printWriter.println("\t\t\t<relation>" + result + "</relation>");
		printWriter.println("\t\t\t<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>" + average + "</measure>");
		printWriter.println("\t\t\t<explanation>" + explanation + "</explanation>");
		printWriter.println("\t\t</Cell>\n\t</map>");
	}

	private double getSimilarityScore (String uri1, String uri2){
		StringWrapper stringWrapper1 = new JaroWinkler().prepare(uri1);
		StringWrapper stringWrapper2 = new JaroWinkler().prepare(uri2);

		return new JaroWinkler().score(stringWrapper1, stringWrapper2);
	}

	
	
	public static void main(String[] args) throws Exception {
		/*String prefix1 = "http://www.okkam.org/ontology_person1.owl#";
		String prefix2 = "http://www.okkam.org/ontology_person2.owl#";
		String prefixRdf1 = "http://www.okkam.org/oaie/person1-";
		String prefixRdf2 = "http://www.okkam.org/oaie/person2-";
		String owlClass = "Person";
		args = new String[] {
		     "resources/PR-1/person2/ontology_people1.owl",
			 "resources/PR-1/person2/ontology_people2.owl",
			 "asdfasdf",
			 "resources/PR-1/person2/person21.rdf",
			 "resources/PR-1/person2/person22.rdf",
			 prefix1, prefix2, prefixRdf1, prefixRdf2, owlClass
			 };

		*/

		Parser parser = new Parser();
		parser.compareOwlOntologies(args[0], args[1], args[2], args[3], args[4],
				args[5], args[6], args[7], args[8], args[9]);
	}
}
