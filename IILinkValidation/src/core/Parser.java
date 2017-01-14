package core;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.Node;
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
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.wcohen.ss.expt.MatchData;
import com.wcohen.ss.api.*;
import com.wcohen.ss.*;




public class Parser {

	/**
	 * Read stuff from OWL file and rdf
	 * @param owlPath
	 * @param rdfPath1
	 * @param rdfPath2
	 */
	
	public static List<String> listLinksSameAs = new ArrayList<String>();
	
	public void sameAsFinder(String links){
		String linkSameAs = "";
		String comp = "sameAs";
		if (links.contains(comp)){
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
			System.out.println(o!=null?"True":"False");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
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
//		
		
	}
	
	/**
	 * This method parses the rdf file and prints stuff.
	 * @param rdf File Path
	 * FIXME
	 */
	public void readRDF(String rdfFilePath)
	{

		 // create an empty model
		 Model model = ModelFactory.createDefaultModel();

		 // use the FileManager to find the input file
		 InputStream in = FileManager.get().open( rdfFilePath );
		
		// read the RDF/XML file
		model.read(in, null);

		// write it to standard out
		Iterator<Statement>its = model.listStatements();
		//Iterator<RDFNode> it = model.listObjects();
		/*while(it.hasNext())
		{
			RDFNode node = it.next();
			System.out.println(node.toString());
		}*/
		while(its.hasNext())
		{
			Statement statement = its.next();
			Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode rdfNode = statement.getObject();
			if(rdfNode.isResource())
			{
				Resource object = rdfNode.asResource();
			}else if(rdfNode.isLiteral())
			{
				Literal object = rdfNode.asLiteral();
			}
		}
		//model.write(System.out);
	}
	
	/**
	 * This method parses the rdf file and prints stuff.
	 * @param rdf File Path
	 * FIXME
	 */
	public void readRDF(String rdf1, String rdf2)
	{

		 // create an empty model
		 Model model1 = ModelFactory.createDefaultModel();
		 Model model2 = ModelFactory.createDefaultModel();

		 // use the FileManager to find the input file
		 InputStream in1 = FileManager.get().open( rdf1 );
		 InputStream in2 = FileManager.get().open( rdf2 );
		
		// read the RDF/XML file
		model1.read(in1, null);
		model2.read(in2, null);

		// write it to standard out
		Iterator<Statement>it1 = model1.listStatements();
		//Iterator<RDFNode> it = model.listObjects();
		while(it1.hasNext())
		{
			Statement statement = it1.next();
			Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			/*RDFNode rdfNode = statement.getObject();
			if(rdfNode.isResource())
			{
				Resource object = rdfNode.asResource();
			}else if(rdfNode.isLiteral())
			{
				Literal object = rdfNode.asLiteral();
			}*/
			Iterator<Statement>it2 = model2.listStatements();
			while(it2.hasNext())
			{
				Statement statement2 = it2.next();
				Resource subject2 = statement2.getSubject();
				Property predicate2 = statement2.getPredicate();
			/*	RDFNode rdfNode2 = statement2.getObject();
				if(rdfNode2.isResource())
				{
					Resource object2 = rdfNode2.asResource();
				}else if(rdfNode.isLiteral())
				{
					Literal object2 = rdfNode2.asLiteral();
				} */
				System.out.println("namespace:"+subject.getNameSpace());
				System.out.println("id:"+subject.getId());
				System.out.println("subject: ");
				System.out.println(subject.getLocalName()+":"+subject2.getLocalName());
				
				if(subject.getURI().equals(subject2.getURI()))
				{
					System.out.println("subject: ");
					System.out.println(subject.getLocalName()+":"+subject2.getLocalName());
				}
				
				//if(predicate.getURI().equals(predicate2.getURI()))
				//{
					//System.out.println("predicate");
					//System.out.println(predicate.getLocalName()+":"+predicate2.getLocalName());
				//}
				
			}
		}
		//model.write(System.out);
	}
	
	public static void main(String[] args) throws Exception {
		 args = new String[] {"resources/PR-1/restaurants/ontology_restaurant.owl",
				 "resources/PR-1/restaurants/ontology_restaurant1.owl",
				 "resources/PR-1/restaurants/ontology_restaurant2.owl",
				 "resources/PR-1/restaurants/restaurant1.rdf",
				 "resources/PR-1/restaurants/restaurant2.rdf"
				 };
		 Parser parser = new Parser();
		 //parser.readRDF(args[1]);
		 //parser.readOWL(args[0],args[3],args[4]);
		 parser.readOWL(args[0],args[1],args[2],args[3],args[4]);
		 //parser.readRDF(args[3],args[4]);
		 

		 String uri1 = "http://www.okkam.org/oaie/restaurant1-Restaurant0'";
		 String uri2 ="http://www.okkam.org/oaie/restaurant2-Restaurant0";
		 double simiratityScore = 0;
		 String relation ="=";
		 float measure = 1;
		 simiratityScore = getSimilarityScore(uri1, uri2);
		
		 
		 
		 
		 
		 
		 
		
		 
	}
}
