package core;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




import java.util.Set;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
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
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont,ont2,ont3,ont4,ont5;
		try {
			ont = manager.loadOntologyFromOntologyDocument(new File(owl1));
			
			ont2 = manager.loadOntologyFromOntologyDocument(new File(owl2));
			ont3 = manager.loadOntologyFromOntologyDocument(new File(owl3));
			ont4 = manager.loadOntologyFromOntologyDocument(new File(rdfPath1));
			ont5 = manager.loadOntologyFromOntologyDocument(new File(rdfPath2));
			System.out.println("before"+ont.getAxiomCount());
			manager.addAxioms(ont, ont2.getAxioms());
			manager.addAxioms(ont, ont3.getAxioms());
			manager.addAxioms(ont, ont4.getAxioms());
			manager.addAxioms(ont, ont5.getAxioms());
			System.out.println("after"+ont.getAxiomCount());
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
			reasoner.precomputeInferences();
			boolean consistent = reasoner.isConsistent();
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			if (!unsatisfiable.isEmpty()) {
	            // System.out.println("The following classes are unsatisfiable: ");
	            for (OWLClass cls : unsatisfiable) {
	                 System.out.println(" " + cls);
	            }
	        } else {
	             System.out.println("There are no unsatisfiable classes");
	        }
			OWLDataFactory fac = manager.getOWLDataFactory();
			OWLClass adresses = fac.getOWLClass(IRI.create(
					"http://www.okkam.org/ontology_restaurant2.owl#Restaurant"));
			NodeSet<OWLClass> subClses = reasoner.getSubClasses(adresses, true);
			Set<OWLClass> clses = subClses.getFlattened();
	         for (OWLClass cls : clses) {
	         System.out.println(" " + cls);
			 }
	         NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(adresses, false);
	         Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
	         for (OWLNamedIndividual ind : individuals) {
	          System.out.println(" " + ind);
	         }
	         for (OWLNamedIndividual i : ont.getIndividualsInSignature()) {
	             for (OWLObjectProperty p : ont.getObjectPropertiesInSignature()) {
	                 NodeSet<OWLNamedIndividual> individualValues = reasoner.getObjectPropertyValues(i, p);
	                 Set<OWLNamedIndividual> values = individualValues.getFlattened();
	                  System.out.println("The property values for "+p+" for individual "+i+" are: ");
	                  for (OWLNamedIndividual ind : values) {
	                  System.out.println(" " + ind);
	                  }
	             }
	 }
	         Node<OWLClass> topNode = reasoner.getTopClassNode();
	         //print(topNode, reasoner, 0);*/
		} catch (OWLOntologyCreationException e) {
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
		 parser.compareRDF(model_restaurant1, model_restaurant2);

		//parser.readOWL(args[0],args[3],args[4]);
		parser.readOWL(args[0], args[1], args[2], args[3], args[4]);

		String uri1 = "http://www.okkam.org/oaie/restaurant1-Restaurant0'";
		String uri2 = "http://www.okkam.org/oaie/restaurant2-Restaurant0";

		double similarityScore = parser.getSimilarityScore(uri1, uri2);
		System.out.println("Similarity score = " + similarityScore);
	}
}
