package br.ufes.inf.nemo.researchers.sparql.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

public class SPARQLUtil {
	public static String d2r = "http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#";
	public static String swrc = "http://swrc.ontoware.org/ontology#";
	public static String dcterms = "http://purl.org/dc/terms/";
	public static String xsd = "http://www.w3.org/2001/XMLSchema#";
	public static String dc = "http://purl.org/dc/elements/1.1/";
	public static String map = "file:///home/diederich/d2r-server-0.3.2/dblp-mapping.n3#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String owl = "http://www.w3.org/2002/07/owl#";
	
	public static void printQuery(String queryString, String inputFileName)
	{
		 
		Model model = loadRDF(inputFileName);
		
		Query query = QueryFactory.create(queryString); 
        
		QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        
        ResultSet results = queryExecution.execSelect();
        
        ResultSetFormatter.out(System.out, results, query) ;
        
        queryExecution.close();
	}
	
	public static void printQueryMAP(String queryString, String inputFileName, String parameter, String value)
	{
		 
		Model model = loadRDF(inputFileName);
		
		RDFNode node = model.createResource(value);
		
		QuerySolutionMap map = new QuerySolutionMap();
		
		map.add(parameter, node);
		
		Query query = QueryFactory.create(queryString); 
        
		QueryExecution queryExecution = QueryExecutionFactory.create(query,model, map);
        
        ResultSet results = queryExecution.execSelect();
        
        ResultSetFormatter.out(System.out, results) ;
     
        queryExecution.close();
     
	}
	
	
	private static Model loadRDF(String inputFileName) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputFileName);

		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		// read the RDF/XML file
		model.read(new InputStreamReader(in), "");
		
		return model;
	}
	
	public static  ResultSet dbpediaQuery (String query)
	{
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		ResultSet results = queryExecution.execSelect();
		
		return results;
	}
	
	public static  ResultSet externalQuery (String query, String serviceURL)
	{
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(serviceURL, query);
		
		ResultSet results = queryExecution.execSelect();
		
		return results;
	}
	


	public static ResultSet getPropertiesAndValuesFromResource(String resourceUri, String sparqlEndPoint) {
		
		String query1 = ""
				+ "SELECT DISTINCT * \n"
				+ "WHERE {\n"
				+ "{ <" + resourceUri + "> ?property ?value }\n"
				+ "UNION\n"
				+ "{ ?value ?property <" + resourceUri + "> }\n"
				+ "}";
		
		
		
		ResultSet results = SPARQLUtil.externalQuery(query1, sparqlEndPoint);
//		ResultSetFormatter.out(System.out, results);
//
//		while (results.hasNext()) {
//
//			QuerySolution soln = results.next();
//
//			Literal albumName = soln.getLiteral("o");
//			
//			System.out.println(albumName);
//
//		}
//		System.out.println();

		return results;
	}
	
}
