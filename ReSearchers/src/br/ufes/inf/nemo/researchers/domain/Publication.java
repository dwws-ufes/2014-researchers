package br.ufes.inf.nemo.researchers.domain;

import java.util.ArrayList;

public class Publication implements Comparable<Publication>{
	private String title;
	private String publicationUri;
	private ArrayList<Researcher> authors = new ArrayList<Researcher>();
	private Integer issued;
	private ArrayList<String> homepages = new ArrayList<String>();
	private String pages;
	private String venue;
	private String venueUri;
	private ArrayList<String> types = new ArrayList<String>();
	private String publisher;
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getVenue() {
		return venue;
	}

	public String getPublicationUri() {
		return publicationUri;
	}

	public void setPublicationUri(String publicationUri) {
		this.publicationUri = publicationUri;
	}

	public String getVenueUri() {
		return venueUri;
	}

	public void setVenueUri(String venueUri) {
		this.venueUri = venueUri;
	}

	@Override
	public String toString() {
		String out = "";
		
		for (int i = 0; i < this.authors.size(); i++) {
			//out += this.authors.get(i).getCitationName();
			out += this.authors.get(i).getCompleteName();
			
			if(i < this.authors.size() - 1){
				out += "; ";
			}
			
		}
		out += ". ";
		
		out += this.title;
		out += " In: ";
		
		out += this.venue;
		
		
		//local
		
		if(!this.venue.endsWith(this.issued.toString())){
			out += ", ";
			out += this.issued;
		}		
		
		return out;
	}
	
	public ArrayList<Researcher> getAuthors() {
		return authors;
	}
	public void setAuthors(ArrayList<Researcher> authors) {
		this.authors = authors;
	}
	public void addAuthor(Researcher author) {
		this.authors.add(author);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getIssued() {
		return issued;
	}
	public void setIssued(Integer issued) {
		this.issued = issued;
	}
	public String getHomepage(){
		if(this.homepages != null){
			if(this.homepages.size() > 0){
				return this.homepages.get(0);
			}
		}
		return "";
	}
	public ArrayList<String> getHomepages() {
		return homepages;
	}
	public void setHomepages(ArrayList<String> homepages) {
		this.homepages = homepages;
	}
	public void addHomepage(String homepage) {
		this.homepages.add(homepage);
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public String getConference() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public ArrayList<String> getTypes() {
		return types;
	}
	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}
	public void addTypes(String type) {
		this.types.add(type);
	}

	@Override
	public int compareTo(Publication o) {
		if (issued == null) return 1;
		if (o.issued == null)	return -1;
		int cmp = issued.compareTo(o.issued);
		if (cmp != 0)	return cmp;
		
		return 0;
	}
}
