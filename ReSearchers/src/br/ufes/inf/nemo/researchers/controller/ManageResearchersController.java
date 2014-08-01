package br.ufes.inf.nemo.researchers.controller;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.convert.Converter;
import javax.inject.Named;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import br.ufes.inf.nemo.researchers.application.ManageResearchersService;
import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.ConclusionProjectType;
import br.ufes.inf.nemo.researchers.domain.Publication;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.researchers.persistence.ResearcherDAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;
import br.ufes.inf.nemo.util.ejb3.application.filters.SimpleFilter;
import br.ufes.inf.nemo.util.ejb3.controller.CrudController;
import br.ufes.inf.nemo.util.ejb3.controller.PersistentObjectConverterFromId;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
//import org.semanticweb.HermiT.Reasoner;
//import org.semanticweb.owlapi.reasoner.OWLReasoner;


@Named 
@SessionScoped
public class ManageResearchersController extends CrudController<Researcher> {
	@EJB private ManageResearchersService manageResearchersService;
	
	@EJB
	private ResearcherDAO researcherDAO;
	
	private PersistentObjectConverterFromId<Researcher> researcherConverter;
	
	private List<Researcher> researchers;
	private List<Researcher> selectAdvisors;
	private List<Researcher> selectCoAdvisors;
	private List<Researcher> selectAuthors;
	
	private List<ConclusionProjectType> conclusionProjectTypes;
	
	private Boolean comingFromConclusionProject = false;
	
	@Override
	public String create() {
		comingFromConclusionProject = false;
		return super.create();
	}
	
	public String createFromConclusionProject() {
		String ret = super.create();
		this.comingFromConclusionProject = true;
		return ret;
	}
	
	@Override
	public String save() {
		String ret = super.save();
		
		if(comingFromConclusionProject){
			// Goes to the form.
			ManageConclusionProjectsController manageConclusionProjectsController = new ManageConclusionProjectsController();
			
			return manageConclusionProjectsController.getViewPath() + "form.xhtml?faces-redirect=" + getFacesRedirect();
		}
		
		return ret;
	}
	
	public List<ConclusionProjectType> getConclusionProjectTypes(){
		conclusionProjectTypes = new ArrayList<ConclusionProjectType>(manageResearchersService.getConclusionProjectTypes());
		return conclusionProjectTypes;
	}
	
	public Converter getResearcherConverter(){
		if(researcherConverter == null) {
			researcherConverter = new PersistentObjectConverterFromId<Researcher>(researcherDAO);
		}
		return researcherConverter;
	}
	
	public List<Researcher> getResearchers() {
		researchers = new ArrayList<Researcher>(manageResearchersService.getResearchers());
		return researchers;
	}
	
	public List<Researcher> getSelectAuthors() {
		selectAuthors = new ArrayList<Researcher>();
		
		selectAuthors = getResearchers();
		
		selectAuthors.remove(selectedEntity);
		
		return selectAuthors;
	}
	
	public List<Researcher> getSelectAdvisors(Boolean isAdvisor, Researcher author, Researcher advisor){
		selectAdvisors = new ArrayList<Researcher>();
		if(isAdvisor != null && isAdvisor){			
			selectAdvisors.add(advisor);
		}else{
			selectAdvisors = getResearchers();
			selectAdvisors.remove(advisor);
			selectAdvisors.remove(author);
		}
		
		return selectAdvisors;
	}
	
	public List<Researcher> getSelectCoAdvisors(Boolean isAdvisor, Researcher author, Researcher advisor) {
		selectCoAdvisors = new ArrayList<Researcher>();
		if(isAdvisor!= null && isAdvisor){
			selectCoAdvisors = getResearchers();
			selectCoAdvisors.remove(advisor);
			selectCoAdvisors.remove(author);
		}else{
			selectCoAdvisors.add(advisor);
		}
		
		return selectCoAdvisors;
	}

	public ManageResearchersController() {
		viewPath = "/manageResearchers/";
		bundleName = "msgs";
	}
	
	@Override
	protected Researcher createNewEntity() {
		return new Researcher();
	}

	@Override
	protected CrudService<Researcher> getCrudService() {
		return manageResearchersService;
	}

	@Override
	protected void initFilters() {
		addFilter(new SimpleFilter("manageResearchers.filter.byName", "completeName", getI18nMessage("msgs", "manageResearchers.text.filter.byName")));

	}
	
	public void populatePublicationsFromDblp() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
		Researcher researcher = this.selectedEntity;
		
		ResultSet publicationsFromAuthor = DBLP.getPublicationsFromAuthor(researcher.getDblpL3sRdfUri());
		
		ArrayList<Publication> publications = new ArrayList<Publication>();
		
		while (publicationsFromAuthor.hasNext()) {
			Publication publication = new Publication();
			
			QuerySolution publicationFromAuthor = publicationsFromAuthor.next();
			
			Literal publicationUri = publicationFromAuthor.getLiteral("publisher");
			publication.setPublisher(publicationUri.toString());
			
			Literal title = publicationFromAuthor.getLiteral("title");
			publication.setTitle(title.getString());
			
			Resource publicationRsrc = publicationFromAuthor.getResource("publication");
			String publicationRsrcUri = publicationRsrc.getURI();
			
			publication.setPublicationUri(publicationRsrcUri);
			
			ResultSet authors = DBLP.getAuthors(publicationRsrcUri);
			
			while (authors.hasNext()) {
				QuerySolution author = authors.next();
				
				Literal name = author.getLiteral("name");
				Resource authorRsrc = author.getResource("creator");
				String authorRsrcUri = authorRsrc.getURI();
				
				Researcher researcherAuthor = new Researcher();
				researcherAuthor.setCompleteName(name.getString());
				researcherAuthor.setDblpL3sRdfUri(authorRsrcUri);
				
				publication.addAuthor(researcherAuthor);
			}
			
			
			Literal issued = publicationFromAuthor.getLiteral("issued");
			publication.setIssued(Integer.valueOf(issued.getString()));
			
			ResultSet homepages = DBLP.getHomepages(publicationRsrcUri);
			
			while (homepages.hasNext()) {
				QuerySolution homepage = homepages.next();
				
				Resource homepageRsrc = homepage.getResource("homepage");
				String homepageRsrcUri = homepageRsrc.getURI();
				
				publication.addHomepage(homepageRsrcUri);
			}
			
			Literal pages = publicationFromAuthor.getLiteral("pages");
			publication.setPages(pages.getString());
			
			Resource partPfRsrc = publicationFromAuthor.getResource("partOf");
			String partPfRsrcUri = partPfRsrc.getURI();
			publication.setVenueUri(partPfRsrcUri);
			
			Literal venueName = publicationFromAuthor.getLiteral("venueName");
			publication.setVenue(venueName.getString());
			
			ResultSet types = DBLP.getTypes(publicationRsrcUri);
			
			while (types.hasNext()) {
				QuerySolution type = types.next();
				
				Resource typeRsrc = type.getResource("type");
				String typeRsrcUri = typeRsrc.getURI();
				
				publication.addTypes(typeRsrcUri);
			}
			
			publications.add(publication);
			
			System.out.println(publication);
			
			//System.out.println();
		}
		
		researcher.setPublications(publications);
		
		// Sets the data as read-only.
		readOnly = true;
		
		// Asks the CRUD service to fetch any lazy collection that possibly exists.
		selectedEntity = getCrudService().fetchLazy(selectedEntity);
		checkSelectedEntity();
		
		researcherToOwl(researcher);
		
	}
	
	public String publications()  {
		try {
			populatePublicationsFromDblp();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryExceptionHTTP e) {
			System.out.println("Error connecting...");
			System.out.println(e.getCause());
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Goes to the form.
		return getViewPath() + "publications.xhtml?faces-redirect=" + getFacesRedirect();
	}
	
	public void researcherToOwl(Researcher researcher) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
		//String researcherFileName = "researchers.owl";
		//String researchersOwlUri = "https://github.com/nemo-ufes/nemo-utils/"+researcherFileName;
		String researchersOwlUri = "http://dev.nemo.inf.ufes.br:28080/ReSearchers/resources/owl/researchers.owl";
		String researchersPfxStr = "researchers";
		String researcherUri = "http://"+researcher.getLocalUri();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology(IRI.create(researcherUri));
		
		//OWLImportsDeclaration importResDec = factory.getOWLImportsDeclaration(IRI.create(researchersOwlUri));
		//OWLImportsDeclaration importResDec = factory.getOWLImportsDeclaration(IRI.create("file:"+researcherFileName));
		OWLImportsDeclaration importResDec = factory.getOWLImportsDeclaration(IRI.create(researchersOwlUri));
		
		manager.applyChange(new AddImport(ontology, importResDec));
		
		researchersOwlUri += "#";
		researcherUri += "#";
		
		PrefixOWLOntologyFormat researchersPfx = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		researchersPfx.setPrefix(researchersPfxStr , researchersOwlUri);
		
		
		//create the individual of the researcher and set the Researcher class
		OWLClass researcherClass = factory.getOWLClass(researchersPfxStr+":Researcher", researchersPfx);
		OWLIndividual tIndividual = factory.getOWLNamedIndividual(IRI.create(researcherUri+"me"));
		OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(researcherClass, tIndividual);
		manager.addAxiom(ontology, classAssertion);
		
		//create the dblp individuals and set them as same as of the researcher
		OWLIndividual dblpHtmlIndividual = factory.getOWLNamedIndividual(IRI.create(researcher.getDblpL3sHtmlUri()));
		OWLIndividual dblpRdfIndividual = factory.getOWLNamedIndividual(IRI.create(researcher.getDblpL3sRdfUri()));
		OWLSameIndividualAxiom sameAsAsser = factory.getOWLSameIndividualAxiom(tIndividual, dblpHtmlIndividual, dblpRdfIndividual);
		manager.addAxiom(ontology, sameAsAsser);
		
		//set the name of the researcher
		OWLDataProperty personNameOWL = factory.getOWLDataProperty(researchersPfxStr+":Person.complete_name", researchersPfx);
		OWLLiteral personNameLiteral = factory.getOWLLiteral(researcher.getCompleteName());
		OWLDataPropertyAssertionAxiom personNameAsser = factory.getOWLDataPropertyAssertionAxiom(personNameOWL, tIndividual, personNameLiteral);
		manager.addAxiom(ontology, personNameAsser);
		
		publicationsToOwl(researcher, manager, factory, ontology, researchersPfxStr, researchersPfx);
		supervisionsToOwl(researcher, researcherUri, researcher.getSupervisions(), manager, factory, ontology, researchersPfxStr, researchersPfx);
		supervisionsToOwl(researcher, researcherUri, researcher.getCosupervisions(), manager, factory, ontology, researchersPfxStr, researchersPfx);
		String realPath = getExternalContext().getRealPath("");
		//String dir = "C:/Users/fredd_000/Dropbox/Freddy/UFES - Mestrado/2014-1 - Desenvolvimento Web e Web Semântica - Vítor/Trabalho/Modelo OWL/";
		String dir = realPath+"/resources/owl/";
		String fileName = dir+researcher.getCompleteName()+".owl";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		manager.saveOntology(ontology, os);
		
		String owl = new String(os.toByteArray(),"ISO-8859-1");
		Writer output1;
		File f = new File(fileName);
		output1 = new BufferedWriter(new FileWriter(f));
		output1.write(owl);
		output1.close();
		
		//toFileZip(researcher.getCompleteName());
	}
	
	public static void publicationsToOwl(Researcher researcher, OWLOntologyManager manager, OWLDataFactory factory, OWLOntology ontology, String researchersPfxStr, PrefixOWLOntologyFormat researchersPfx) throws OWLOntologyStorageException, IOException{
		OWLClassAssertionAxiom classAssertion;
		
		String swrcPfxStr = "swrc";
		String swrcUri = "http://swrc.ontoware.org/ontology#";
		PrefixOWLOntologyFormat swrcPfx = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		swrcPfx.setPrefix(swrcPfxStr , swrcUri);
		
		String dctermsPfxStr = "dcterms";
		String dctermsUri = "http://purl.org/dc/terms/";
		PrefixOWLOntologyFormat dctermsPfx = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		dctermsPfx.setPrefix(dctermsPfxStr , dctermsUri);
		
		String dcPfxStr = "dc";
		String dcUri = "http://purl.org/dc/elements/1.1/";
		PrefixOWLOntologyFormat dcPfx = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		dcPfx.setPrefix(dcPfxStr , dcUri);
		
		String foafPfxStr = "foaf";
		String foafUri = "http://xmlns.com/foaf/0.1/";
		PrefixOWLOntologyFormat foafPfx = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		foafPfx.setPrefix(foafPfxStr , foafUri);
		
		//create OWL properties of publications
		OWLClass paperClass = factory.getOWLClass(researchersPfxStr+":Paper", researchersPfx);
		OWLDataProperty titleOWL = factory.getOWLDataProperty(dcPfxStr+":title", dcPfx);
		OWLDataProperty issuedOWL = factory.getOWLDataProperty(dctermsPfxStr+":issued", dctermsPfx);
		OWLDataProperty pagesOWL = factory.getOWLDataProperty(swrcPfxStr+":pages", swrcPfx);
		OWLObjectProperty partOfOWL = factory.getOWLObjectProperty(dctermsPfxStr+":partOf", dctermsPfx);
		OWLDataProperty partOfTitleOWL = factory.getOWLDataProperty(dcPfxStr+":title", dcPfx);
		OWLDataProperty partOfPublisherOWL = factory.getOWLDataProperty(dcPfxStr+":publisher", dcPfx);
		OWLObjectProperty creatorOWL = factory.getOWLObjectProperty(dcPfxStr+":creator", dcPfx);
		OWLObjectProperty homepageOWL = factory.getOWLObjectProperty(foafPfxStr+":homepage", foafPfx);
		OWLObjectProperty paperAuthorOWL = factory.getOWLObjectProperty(researchersPfxStr+":author_of.Paper_Author.Paper", researchersPfx);		
		
		for (Publication publication : researcher.getPublications()) {
			//create the publication individual and set the Paper class
			OWLIndividual publicationInd = factory.getOWLNamedIndividual(IRI.create(publication.getPublicationUri()));
			OWLClassAssertionAxiom publicationClassAssertion = factory.getOWLClassAssertionAxiom(paperClass, publicationInd);
			manager.addAxiom(ontology, publicationClassAssertion);
			
			//set the title of the publication
			OWLLiteral titleLiteral = factory.getOWLLiteral(publication.getTitle());
			OWLDataPropertyAssertionAxiom titleAsser = factory.getOWLDataPropertyAssertionAxiom(titleOWL, publicationInd, titleLiteral);
			manager.addAxiom(ontology, titleAsser);
			
			//set the issue date
			OWLLiteral issuedLiteral = factory.getOWLLiteral(publication.getIssued());
			OWLDataPropertyAssertionAxiom issuedAsser = factory.getOWLDataPropertyAssertionAxiom(issuedOWL, publicationInd, issuedLiteral);
			manager.addAxiom(ontology, issuedAsser);
			
			//set the pages
			OWLLiteral pagesLiteral = factory.getOWLLiteral(publication.getPages());
			OWLDataPropertyAssertionAxiom pagesAsser = factory.getOWLDataPropertyAssertionAxiom(pagesOWL, publicationInd, pagesLiteral);
			manager.addAxiom(ontology, pagesAsser);
			
			//set partOf
			//partOf means the conference that the paper was published
			OWLIndividual partOfInd = factory.getOWLNamedIndividual(IRI.create(publication.getVenueUri()));
			OWLObjectPropertyAssertionAxiom partOfAsser = factory.getOWLObjectPropertyAssertionAxiom(partOfOWL, publicationInd, partOfInd);
			manager.addAxiom(ontology, partOfAsser);
			
			//set the name of the conference
			OWLLiteral partOfTitleLiteral = factory.getOWLLiteral(publication.getVenue());
			OWLDataPropertyAssertionAxiom partOfTitleAsser = factory.getOWLDataPropertyAssertionAxiom(partOfTitleOWL, partOfInd, partOfTitleLiteral);
			manager.addAxiom(ontology, partOfTitleAsser);
			
			//set the name of the publisher
			OWLLiteral partOfPublisherLiteral = factory.getOWLLiteral(publication.getPublisher());
			OWLDataPropertyAssertionAxiom partOfPublisherAsser = factory.getOWLDataPropertyAssertionAxiom(partOfPublisherOWL, partOfInd, partOfPublisherLiteral);
			manager.addAxiom(ontology, partOfPublisherAsser);
			
			//get authors
			for (Researcher author : publication.getAuthors()) {
				//create dblp of the authors and set as same as
				OWLIndividual authorHtmlIndividual = factory.getOWLNamedIndividual(IRI.create(author.getDblpL3sHtmlUri()));
				OWLIndividual authorRdfIndividual = factory.getOWLNamedIndividual(IRI.create(author.getDblpL3sRdfUri()));
				OWLSameIndividualAxiom sameAsAuthorAsser = factory.getOWLSameIndividualAxiom(authorHtmlIndividual, authorRdfIndividual);
				manager.addAxiom(ontology, sameAsAuthorAsser);
				
				//make the author as creator of the publication
				OWLObjectPropertyAssertionAxiom creatorAsser = factory.getOWLObjectPropertyAssertionAxiom(creatorOWL, publicationInd, authorHtmlIndividual);
				manager.addAxiom(ontology, creatorAsser);
				
				OWLObjectPropertyAssertionAxiom paperAuthorAsser = factory.getOWLObjectPropertyAssertionAxiom(paperAuthorOWL, authorHtmlIndividual, publicationInd);
				manager.addAxiom(ontology, paperAuthorAsser);
			}
			
			//get the homepage links
			for (String homepage : publication.getHomepages()) {
				//set the homepage of the publication
				OWLIndividual homepageIndividual = factory.getOWLNamedIndividual(IRI.create(homepage));
				OWLObjectPropertyAssertionAxiom homepageAsser = factory.getOWLObjectPropertyAssertionAxiom(homepageOWL, publicationInd, homepageIndividual);
				manager.addAxiom(ontology, homepageAsser);
			}
			
			//get the publication types
			for (String type : publication.getTypes()) {
				//set the publication 
				OWLClass typeClass = factory.getOWLClass(IRI.create(type));
				classAssertion = factory.getOWLClassAssertionAxiom(typeClass, publicationInd);
				manager.addAxiom(ontology, classAssertion);
			}
		}
		
	}
	
	public void supervisionsToOwl(Researcher researcher, String researcherUri, Set<ConclusionProject> supervisions, OWLOntologyManager manager, OWLDataFactory factory, OWLOntology ontology, String researchersPfxStr, PrefixOWLOntologyFormat researchersPfx){
		//create the owl classes and properties
		OWLDataProperty titleOWL = factory.getOWLDataProperty(researchersPfxStr+":Publication.title", researchersPfx);
		OWLObjectProperty authorOWL = factory.getOWLObjectProperty(researchersPfxStr+":author_of.Conclusion_Project_Author.Conclusion_Project", researchersPfx);
		OWLDataProperty conclusionYearOWL = factory.getOWLDataProperty(researchersPfxStr+":Completed.conclusion_year", researchersPfx);
		OWLObjectProperty advisorOfOWL = factory.getOWLObjectProperty(researchersPfxStr+":advisor_of", researchersPfx);
		OWLObjectProperty coadvisorOfOWL = factory.getOWLObjectProperty(researchersPfxStr+":co-advisor_of", researchersPfx);
		OWLClass bscClass = factory.getOWLClass(researchersPfxStr+":BSc_Project", researchersPfx);
		OWLClass mscClass = factory.getOWLClass(researchersPfxStr+":MSc_Project", researchersPfx);
		OWLClass phdClass = factory.getOWLClass(researchersPfxStr+":PhD_Project", researchersPfx);
		OWLClass postPhdClass = factory.getOWLClass(researchersPfxStr+":Post_PhD_Project", researchersPfx);
		OWLClass completedClass = factory.getOWLClass(researchersPfxStr+":Completed", researchersPfx);
		OWLClass inProgressClass = factory.getOWLClass(researchersPfxStr+":In_Progress", researchersPfx);
		OWLDataProperty personNameOWL = factory.getOWLDataProperty(researchersPfxStr+":Person.complete_name", researchersPfx);
		
		//iterate supervisions
		for (ConclusionProject project : supervisions) {
			//create the project individual
			String projName = researcherUri+project.getConclusionProjectType()+"_"+project.getId();
			projName = projName.replace(" ","_");
			OWLIndividual projectIndividual = factory.getOWLNamedIndividual(IRI.create(projName));
			
			//create and set the project title
			OWLLiteral titleLiteral = factory.getOWLLiteral(project.getTitle());
			OWLDataPropertyAssertionAxiom partOfPublisherAsser = factory.getOWLDataPropertyAssertionAxiom(titleOWL, projectIndividual, titleLiteral);
			manager.addAxiom(ontology, partOfPublisherAsser);
			
			//create the author individuals and set them as same as
			OWLIndividual authorIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAuthor().getLocalUri()));
			OWLIndividual authorDblpHtmlIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAuthor().getDblpL3sHtmlUri()));
			OWLIndividual authorDblpRdfIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAuthor().getDblpL3sRdfUri()));
			OWLSameIndividualAxiom sameAsAsser = factory.getOWLSameIndividualAxiom(authorIndividual, authorDblpHtmlIndividual, authorDblpRdfIndividual);
			manager.addAxiom(ontology, sameAsAsser);
			
			//set the author of the project
			OWLObjectPropertyAssertionAxiom authorAsser = factory.getOWLObjectPropertyAssertionAxiom(authorOWL, authorIndividual, projectIndividual);
			manager.addAxiom(ontology, authorAsser);
			
			//set the name of the author
			OWLLiteral authorNameLiteral = factory.getOWLLiteral(project.getAuthor().getCompleteName());
			OWLDataPropertyAssertionAxiom authorNameAsser = factory.getOWLDataPropertyAssertionAxiom(personNameOWL, authorIndividual, authorNameLiteral);
			manager.addAxiom(ontology, authorNameAsser);
			
			if(project.getInProgress()){
				//if the project is in progress, set as In Progress class
				OWLClassAssertionAxiom inProgressAssertion = factory.getOWLClassAssertionAxiom(inProgressClass, projectIndividual);
				manager.addAxiom(ontology, inProgressAssertion);
			}else{
				//else, set the conclusion year
				OWLLiteral conclusionYearLiteral = factory.getOWLLiteral(project.getIssued());
				OWLDataPropertyAssertionAxiom conclusionYearAsser = factory.getOWLDataPropertyAssertionAxiom(conclusionYearOWL, projectIndividual, conclusionYearLiteral);
				manager.addAxiom(ontology, conclusionYearAsser);
				
				OWLClassAssertionAxiom completedAssertion = factory.getOWLClassAssertionAxiom(completedClass, projectIndividual);
				manager.addAxiom(ontology, completedAssertion);
			}
			
			OWLIndividual advisorIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAdvisor().getLocalUri()));
			OWLIndividual advisorDblpHtmlIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAdvisor().getDblpL3sHtmlUri()));
			OWLIndividual advisorDblpRdfIndividual = factory.getOWLNamedIndividual(IRI.create(project.getAdvisor().getDblpL3sRdfUri()));
			sameAsAsser = factory.getOWLSameIndividualAxiom(advisorIndividual, advisorDblpHtmlIndividual, advisorDblpRdfIndividual);
			manager.addAxiom(ontology, sameAsAsser);
			
			OWLObjectPropertyAssertionAxiom advisorAsser = factory.getOWLObjectPropertyAssertionAxiom(advisorOfOWL, advisorIndividual, projectIndividual);
			manager.addAxiom(ontology, advisorAsser);
			
			//set the name of the researcher
			OWLLiteral advisorNameLiteral = factory.getOWLLiteral(project.getAdvisor().getCompleteName());
			OWLDataPropertyAssertionAxiom advisorNameAsser = factory.getOWLDataPropertyAssertionAxiom(personNameOWL, advisorIndividual, advisorNameLiteral);
			manager.addAxiom(ontology, advisorNameAsser);
			
			if(project.getCoadvisor() != null){
				OWLIndividual coadvisorIndividual = factory.getOWLNamedIndividual(IRI.create(project.getCoadvisor().getLocalUri()));
				OWLIndividual coadvisorDblpHtmlIndividual = factory.getOWLNamedIndividual(IRI.create(project.getCoadvisor().getDblpL3sHtmlUri()));
				OWLIndividual coadvisorDblpRdfIndividual = factory.getOWLNamedIndividual(IRI.create(project.getCoadvisor().getDblpL3sRdfUri()));
				sameAsAsser = factory.getOWLSameIndividualAxiom(coadvisorIndividual, coadvisorDblpHtmlIndividual, coadvisorDblpRdfIndividual);
				manager.addAxiom(ontology, sameAsAsser);
				
				OWLObjectPropertyAssertionAxiom coadvisorAsser = factory.getOWLObjectPropertyAssertionAxiom(coadvisorOfOWL, coadvisorIndividual, projectIndividual);
				manager.addAxiom(ontology, coadvisorAsser);
				
				//set the name of the researcher
				OWLLiteral coadvisorNameLiteral = factory.getOWLLiteral(project.getCoadvisor().getCompleteName());
				OWLDataPropertyAssertionAxiom coadvisorNameAsser = factory.getOWLDataPropertyAssertionAxiom(personNameOWL, coadvisorIndividual, coadvisorNameLiteral);
				manager.addAxiom(ontology, coadvisorNameAsser);
			}
			
			OWLClassAssertionAxiom projectTypeAssertion = null;// = factory.getOWLClassAssertionAxiom(paperClass, publicationInd);
			ConclusionProjectType projectType = project.getConclusionProjectType();
			if(projectType.equals(ConclusionProjectType.BScProject)){
				projectTypeAssertion = factory.getOWLClassAssertionAxiom(bscClass, projectIndividual);
			}else if(projectType.equals(ConclusionProjectType.MScProject)){
				projectTypeAssertion = factory.getOWLClassAssertionAxiom(mscClass, projectIndividual);
			}else if(projectType.equals(ConclusionProjectType.PhDProject)){
				projectTypeAssertion = factory.getOWLClassAssertionAxiom(phdClass, projectIndividual);
			}else if(projectType.equals(ConclusionProjectType.PostPhdProject)){
				projectTypeAssertion = factory.getOWLClassAssertionAxiom(postPhdClass, projectIndividual);
			} 
			manager.addAxiom(ontology, projectTypeAssertion);
		}	
	}
	
	public void inferModel(){
		//String infFileName = dir+researcher.getCompleteName()+"-inf.owl";
		////////////////////////////////////////////////////
		///pellet
		////////////////////////////////////////////////////
		
		//		OntModel model = null;
		//model = ModelFactory.createOntologyModel();
		//InputStream in = FileManager.get().open(fileName);
		//model.read(in,null);	
		//
		//model.loadImports();
		//
		//Reasoner r = PelletReasonerFactory.theInstance().create();
		//InfModel infModel = ModelFactory.createInfModel(r, model);
		//
		//System.out.println("RODOU O REASONER");
		//
		//FileOutputStream output = new FileOutputStream(infFileName);
		//infModel.write(output,"RDF/XML");
		
		
		////////////////////////////////////////////////////
		///hermit
		////////////////////////////////////////////////////
		
		//InputStream in = FileManager.get().open(fileName);
		//OWLOntologyManager m=OWLManager.createOWLOntologyManager();
		//OWLOntology o = m.loadOntologyFromOntologyDocument(in);
		//
		//Configuration config=new Configuration();
		//ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		//config.reasonerProgressMonitor = progressMonitor;
		//OWLReasoner hermit = new Reasoner.ReasonerFactory().createReasoner(o, config);
		//
		//System.out.println("is consistent: "+hermit.isConsistent());
		//
		//in = FileManager.get().open(fileName);
		//OntModel model = ModelFactory.createOntologyModel();
		//model.read(in,null);
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//model.write(baos, "RDF/XML");
		//InputStream bais = new ByteArrayInputStream(baos.toByteArray());
		//BufferedReader in2 = new BufferedReader(new InputStreamReader(bais));
		//while ((in2.readLine()) != null) {
		////  System.out.println(line);
		//}
		//m.loadOntologyFromOntologyDocument(bais);
		//
		//List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		//gens.add(new InferredEquivalentClassAxiomGenerator());//class hierarchy
		//gens.add(new InferredSubClassAxiomGenerator());//class hierarchy
		//gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());//object properties
		//gens.add(new InferredInverseObjectPropertiesAxiomGenerator());//object properties
		//gens.add(new InferredSubObjectPropertyAxiomGenerator());//object properties
		//gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());//data properties
		//gens.add(new InferredSubDataPropertyAxiomGenerator());//data properties
		//gens.add(new InferredClassAssertionAxiomGenerator()); //class instance data structures
		//gens.add(new InferredPropertyAssertionGenerator());//property instance data structures, data properties, class instance data structures
		//
		////gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
		////gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
		////gens.add(new InferredDisjointClassesAxiomGenerator());
		//
		//InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);
		//Date date = new Date();
		//iog.fillOntology(m, o);
		//Date date2 = new Date();
		//ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		//m.saveOntology(o, new RDFXMLOntologyFormat(), baos2);
		//
		//owl = new String(baos2.toByteArray(),"UTF-8");
		//owl = Normalizer.normalize(owl, Normalizer.Form.NFD);
		//owl = owl.replaceAll("[^\\p{ASCII}]", "");
		//File arquivo;   
		//
		//String outputFileName = fileName.replace(".owl", "_inferred.owl"); 
		//arquivo = new File(outputFileName);  // Chamou e nomeou o arquivo txt.  
		//try{
		//FileOutputStream fos = new FileOutputStream(arquivo);  
		//fos.write(owl.getBytes());    
		//fos.close();  
		//}catch(Exception e){
		//e.printStackTrace();
		//}
	}
	
	public void toFileZip(String researcherOwlFileName) throws IOException{
		byte[] buffer = new byte[1024];
		String realPath = getExternalContext().getRealPath("");
		String dir = realPath+"/resources/owl/";
		FileOutputStream fos = new FileOutputStream(dir+researcherOwlFileName+".zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		researcherOwlFileName = researcherOwlFileName+".owl";
		ZipEntry researcherEntry = new ZipEntry(researcherOwlFileName);
		zos.putNextEntry(researcherEntry);
		FileInputStream researcherIn = new FileInputStream(dir+researcherOwlFileName);
		int len;
		while ((len = researcherIn.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		researcherIn.close();
		
		String foafFileName = "foaf.rdf";
		ZipEntry foafEntry = new ZipEntry(foafFileName);
		zos.putNextEntry(foafEntry);
		FileInputStream foafIn = new FileInputStream(dir+foafFileName);
		while ((len = foafIn.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		foafIn.close();
		
		String swrcFileName = "swrc_v0.3.owl";
		ZipEntry swrcEntry = new ZipEntry(swrcFileName);
		zos.putNextEntry(swrcEntry);
		FileInputStream swrcIn = new FileInputStream(dir+swrcFileName);
		while ((len = swrcIn.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		swrcIn.close();
		
		String researchersFileName = "researchers.owl";
		ZipEntry researchersEntry = new ZipEntry(researchersFileName);
		zos.putNextEntry(researchersEntry);
		FileInputStream researchersIn = new FileInputStream(dir+researchersFileName);
		while ((len = researchersIn.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		researchersIn.close();
		
		zos.closeEntry();
		 
		//remember close it
		zos.close();
	}
}
