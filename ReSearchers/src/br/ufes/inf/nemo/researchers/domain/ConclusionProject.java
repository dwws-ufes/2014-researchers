package br.ufes.inf.nemo.researchers.domain;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import br.ufes.inf.nemo.util.ejb3.persistence.PersistentObjectSupport;

@Entity
public class ConclusionProject extends PersistentObjectSupport implements Comparable<ConclusionProject> {
	private String title;
	
	@ManyToOne
	private Researcher author;
	
	private Integer issued;
	
	@ManyToOne
	private Researcher advisor;
	
	@ManyToOne
	private Researcher coadvisor;

	@Enumerated
	private ConclusionProjectType conclusionProjectType;
	
	private Boolean inProgress;
	
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

	public Researcher getAdvisor() {
		return advisor;
	}

	public void setAdvisor(Researcher advisor) {
		this.advisor = advisor;
	}

	public Researcher getCoadvisor() {
		return coadvisor;
	}

	public void setCoadvisor(Researcher coadvisor) {
		this.coadvisor = coadvisor;
	}

	public ConclusionProjectType getConclusionProjectType() {
		return conclusionProjectType;
	}

	public void setConclusionProjectType(ConclusionProjectType conclusionProjectType) {
		this.conclusionProjectType = conclusionProjectType;
	}

	public Boolean getInProgress() {
		return inProgress;
	}

	public void setInProgress(Boolean inProgress) {
		this.inProgress = inProgress;
		if(inProgress){
			this.setIssued(null);
		}
	}

	
	public Researcher getAuthor() {
		return author;
	}

	public void setAuthor(Researcher author) {
		this.author = author;
	}

	@Override
	public int compareTo(ConclusionProject o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
