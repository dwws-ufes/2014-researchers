package br.ufes.inf.nemo.researchers.controller;

import com.hp.hpl.jena.query.ResultSet;

import br.ufes.inf.nemo.researchers.sparql.util.SPARQLUtil;

public class DBLP {
	public static String dblpSparqlEndPoint = "http://dblp.l3s.de/d2r/sparql";
	
	public static String prefixes = ""
			+ "PREFIX d2r: <" + SPARQLUtil.d2r + ">\n"
			+ "PREFIX swrc: <" + SPARQLUtil.swrc + ">\n"
			+ "PREFIX dcterms: <" + SPARQLUtil.dcterms + ">\n"
			+ "PREFIX xsd: <" + SPARQLUtil.xsd + ">\n"
			+ "PREFIX dc: <" + SPARQLUtil.dc + ">\n"
			+ "PREFIX map: <" + SPARQLUtil.map + ">\n"
			+ "PREFIX rdfs: <" + SPARQLUtil.rdfs + ">\n"
			+ "PREFIX foaf: <" + SPARQLUtil.foaf + ">\n"
			+ "PREFIX rdf: <" + SPARQLUtil.rdf + ">\n"
			+ "PREFIX owl: <" + SPARQLUtil.owl + ">\n";
	
	
	public static ResultSet getTypes(String publicationUri){
		String query = "";
		query += prefixes;
		query += ""
				+ "SELECT DISTINCT *"
				+ "WHERE {"
				+ "<"+ publicationUri + "> rdf:type ?type"
				+ "}";
		
		ResultSet results = SPARQLUtil.externalQuery(query, dblpSparqlEndPoint);
		
		return results;
	}
	
	public static ResultSet getHomepages(String publicationUri){
		String query = "";
		query += prefixes;
		query += ""
				+ "SELECT DISTINCT *"
				+ "WHERE {"
				+ "<"+ publicationUri + "> foaf:homepage ?homepage"
				+ "}";
		
		ResultSet results = SPARQLUtil.externalQuery(query, dblpSparqlEndPoint);
		
		return results;
	}
	
	public static ResultSet getAuthors(String publicationUri){
		String query = "";
		query += prefixes;
		query += ""
				+ "SELECT DISTINCT * WHERE {"
				+ "<" + publicationUri + "> dc:creator ?creator ."
				+ "?creator foaf:name ?name"
				+ "}";
		
		ResultSet results = SPARQLUtil.externalQuery(query, dblpSparqlEndPoint);
		
		return results;
	}
	
	public static ResultSet getPublicationsFromAuthor(String authorUri){
		String query = "";
		query += prefixes;
		query += ""
				+ "SELECT DISTINCT *\n"
				+ "WHERE {\n"
				+ "?publication dc:creator <" + authorUri + "> .\n"
				+ "?publication dc:title ?title .\n"
				+ "?publication dcterms:issued ?issued .\n"
				+ "?publication swrc:pages ?pages .\n"
				+ "?publication dcterms:partOf ?partOf .\n"
				+ "?partOf dc:title ?venueName .\n"
				+ "?partOf dc:publisher ?publisher\n"
				+ "}";

		ResultSet results = SPARQLUtil.externalQuery(query, dblpSparqlEndPoint);
		return results;
	}
}
