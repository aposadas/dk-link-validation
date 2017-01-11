package core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;




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
	
	public void readOWL(String owlPath,String rdfPath1,String rdfPath2)
	{
		Model schema = FileManager.get().loadModel(owlPath);
		Model data1 = FileManager.get().loadModel(rdfPath1);
		Model data2 = FileManager.get().loadModel(rdfPath2);
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		//InfModel infmodel = ModelFactory.createInfModel(reasoner, data1,data2);
		InfModel infmodel = ModelFactory.createInfModel(reasoner, data1);
		//infmodel.samePrefixMappingAs(arg0)
		infmodel.listStatements().forEachRemaining(s->sameAsFinder(s.toString()));
		//infmodel.listStatements().forEachRemaining(sd->System.out.println(sd.toString()));
		
		for (String line: listLinksSameAs){
			System.out.println(line);	
		}
		
		
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
		model.write(System.out);
	}
	
	public static void main(String[] args) throws Exception {
		 args = new String[] {"resources/PR-1/restaurants/ontology_restaurant.owl",
				 "resources/PR-1/restaurants/restaurant1.rdf",
				 "resources/PR-1/restaurants/restaurant2.rdf"
				 };
		 Parser parser = new Parser();
		 parser.readRDF(args[1]);
		 parser.readOWL(args[0],args[1],args[2]);
	}
}
