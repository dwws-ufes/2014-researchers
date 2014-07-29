package br.ufes.inf.nemo.researchers.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import br.ufes.inf.nemo.util.ejb3.persistence.PersistentObjectSupport;

@Entity
public class Researcher extends PersistentObjectSupport implements Comparable<Researcher>{
	private String completeName;
	private String localUri;
	private String dblpL3sHtmlUri;
	private String dblpL3sRdfUri;

	@Transient
	private ArrayList<Publication> publications = new ArrayList<Publication>();
	@Transient
	private ArrayList<PublicationsOfAYear> groupedPublicationsByYear = new ArrayList<PublicationsOfAYear>();
	
	@OneToMany(mappedBy="advisor", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<ConclusionProject> supervisions;
	@OneToMany(mappedBy="coadvisor", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<ConclusionProject> cosupervisions;
	
	public Set<ConclusionProject> getSupervisions() {
		return supervisions;
	}

	public void setSupervisions(Set<ConclusionProject> supervisions) {
		this.supervisions = supervisions;
	}

	public Set<ConclusionProject> getCosupervisions() {
		return cosupervisions;
	}

	public void setCosupervisions(Set<ConclusionProject> cosupervisions) {
		this.cosupervisions = cosupervisions;
	}

	public class PublicationsOfAYear implements Comparable<PublicationsOfAYear>{
		private Integer year;
		private ArrayList<Publication> publications = new ArrayList<Publication>();
		
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
		public ArrayList<Publication> getPublications() {
			return publications;
		}
		public void setPublications(ArrayList<Publication> publications) {
			this.publications = publications;
		}
		public void addPublication(Publication publication) {
			this.publications.add(publication);
		}
		@Override
		public int compareTo(PublicationsOfAYear o) {
			if (year == null) return 1;
			if (o.year == null)	return -1;
			int cmp = year.compareTo(o.year);
			if (cmp != 0)	return cmp;
			
			return 0;
		}
	}
	
	private static String dblpAuthorsHtmlUri = "http://dblp.L3S.de/Authors/";
	private static String dblpAuthorsRdfUri = "http://dblp.l3s.de/d2r/resource/authors/";
	
		
	public ArrayList<PublicationsOfAYear> getGroupedPublicationsByYear() {
		groupedPublicationsByYear = new ArrayList<PublicationsOfAYear>();
		Integer yearAux = 0;
		PublicationsOfAYear pubOfYearAux = null;
		
		Collections.sort(this.publications);
		
		for (Publication publication : publications) {
			if(!publication.getIssued().equals(yearAux)){
				yearAux = publication.getIssued();
				pubOfYearAux = new PublicationsOfAYear();
				
				pubOfYearAux.setYear(yearAux);
				
				groupedPublicationsByYear.add(pubOfYearAux);
			}
			
			pubOfYearAux.addPublication(publication);
		}
		
		Collections.sort(groupedPublicationsByYear, Collections.reverseOrder());
		
		return groupedPublicationsByYear;
	}

	public void setGroupedPublicationsByYear(ArrayList<PublicationsOfAYear> groupedPublications) {
		this.groupedPublicationsByYear = groupedPublications;
	}

	public String getCompleteName() {
		return completeName;
	}

	public String getCompleteNameUTFEncoded() throws UnsupportedEncodingException {
		byte ptext[] = completeName.getBytes();
		String value = new String(ptext, "UTF-8");
		return value;
	}
	
	public String getCitationName(){
		String citationName = "";
		
		String[] nameParts = this.completeName.split(" ");
		
		
		if(nameParts.length > 0){
			citationName += nameParts[nameParts.length-1].toUpperCase();
		}
		
		if(nameParts.length > 1){
			citationName += ", ";
		}
		
		for (int i = 0; i < nameParts.length-1; i++) {
			char firstLetter = nameParts[i].charAt(0);
			if(Character.isUpperCase(firstLetter)){
				citationName += nameParts[i].substring(0, 1);
				citationName += ".";
				citationName += " ";
			}			
		}
		
		citationName = citationName.substring(0, citationName.length()-1);
		
		return citationName;
	}
	
	public void setCompleteName(String completeName) throws UnsupportedEncodingException {
		this.completeName = completeName;
		this.generateDblpL3sUri();
	}
	
	public String getLocalUri() {
		return localUri;
	}
	
	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}
	
//	public void generateDblpL3sHtmlUri() throws UnsupportedEncodingException{
//		this.dblpL3sHtmlUri = this.completeName;
//		this.dblpL3sHtmlUri = this.dblpL3sHtmlUri.replace(" ", "_");
//		this.dblpL3sHtmlUri = dblpAuthorsHtmlUri + this.dblpL3sHtmlUri;
//	}
	
	public String getDblpL3sHtmlUri() {
		return dblpL3sHtmlUri;
	}
	
	public void setDblpL3sHtmlUri(String dblpL3sHtmlUri) {
		this.dblpL3sHtmlUri = dblpL3sHtmlUri;
	}
	
//	public void generateDblpL3sRdfUri() throws UnsupportedEncodingException{
//		this.dblpL3sRdfUri = this.completeName;
//		this.dblpL3sRdfUri = this.dblpL3sRdfUri.replace(" ", "_");
//		this.dblpL3sRdfUri = URLEncoder.encode(this.dblpL3sRdfUri, "UTF-8");
//		this.dblpL3sRdfUri = dblpAuthorsRdfUri + this.dblpL3sRdfUri;
//	}
	
	public void generateDblpL3sUri() throws UnsupportedEncodingException{
		this.dblpL3sRdfUri = this.completeName;
		this.dblpL3sRdfUri = this.dblpL3sRdfUri.replace(" ", "_");
		this.dblpL3sRdfUri = URLEncoder.encode(this.dblpL3sRdfUri, "UTF-8");
		
		this.dblpL3sHtmlUri = this.dblpL3sRdfUri;
		
		this.dblpL3sRdfUri = dblpAuthorsRdfUri + this.dblpL3sRdfUri;
		this.dblpL3sHtmlUri = dblpAuthorsHtmlUri + this.dblpL3sHtmlUri;
	}
	
	public String getDblpL3sRdfUri() {
		return dblpL3sRdfUri;
	}
	
	public void setDblpL3sRdfUri(String dblpL3sRdfUri) {
		this.dblpL3sRdfUri = dblpL3sRdfUri;
	}
	
	public ArrayList<Publication> getPublications() {
		return publications;
	}

	public void setPublications(ArrayList<Publication> publications) {
		this.publications = publications;
		Collections.sort(this.publications, Collections.reverseOrder());
	}

	@Override
	public int compareTo(Researcher o) {
		if (completeName == null) return 1;
		if (o.completeName == null)	return -1;
		int cmp = completeName.compareTo(o.completeName);
		if (cmp != 0)	return cmp;
		
		return uuid.compareTo(o.uuid);
	}
}
