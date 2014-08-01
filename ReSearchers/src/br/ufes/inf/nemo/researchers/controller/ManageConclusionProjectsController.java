package br.ufes.inf.nemo.researchers.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import br.ufes.inf.nemo.researchers.application.ManageConclusionProjectsService;
import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.researchers.persistence.ConclusionProjectJPADAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;
import br.ufes.inf.nemo.util.ejb3.application.filters.LikeFilter;
import br.ufes.inf.nemo.util.ejb3.controller.CrudController;
import br.ufes.inf.nemo.util.ejb3.controller.PrimefacesLazyEntityDataModel;

@Named 
@SessionScoped
public class ManageConclusionProjectsController extends CrudController<ConclusionProject> {
	/** The logger. */
	private static final Logger logger = Logger.getLogger(ConclusionProjectJPADAO.class.getCanonicalName());
	
	@EJB private ManageConclusionProjectsService manageConclusionProjectsService;
	
	private Researcher selectedAdvisor;

	private Boolean isAdvisor = true;
	
	public Boolean getIsAdvisor() {
		if(selectedEntity != null && selectedEntity.getAdvisor() != null){
			if(selectedEntity.getAdvisor().equals(selectedAdvisor)){
				isAdvisor = true;
			}else{
				isAdvisor = false;
			}
		}		
		return isAdvisor;
	}
	
	@Override
	public String create() {
		isAdvisor = true;
		return super.create();
	}
	
	public void setIsAdvisor(Boolean isAdvisor) {
		this.isAdvisor = isAdvisor;
	}
	
	@Override
	public String list() {
		// TODO Auto-generated method stub
		return super.list();
	}
	
	public String list(Researcher selectedAdvisor) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		this.selectedAdvisor = selectedAdvisor;
		
		logger.log(Level.INFO, "Listing entities...");

		// Clears the selection.
		selectedEntity = null;

		// Gets the entity count.
		count(selectedAdvisor);

		// Checks if the index of the listing should be changed and reload the page.
		if (firstEntityIndex < 0)
			goFirst(selectedAdvisor);
		else if (lastEntityIndex > entityCount)
			goLast(selectedAdvisor);
		else retrieveEntities(selectedAdvisor);

		(new ManageResearchersController()).researcherToOwl(selectedAdvisor);
		
		// Goes to the listing.
		return getViewPath() + "list.xhtml?faces-redirect=" + getFacesRedirect();
	}
	
	protected void retrieveEntities(Researcher selectedAdvisor) {
		// Checks if the last entity index is over the number of entities and correct it.
		if (lastEntityIndex > entityCount)
			lastEntityIndex = (int) entityCount;

		// Checks if there's an active filter.
		if (filtering) {
			// There is. Retrieve not only within range, but also with filtering.
			logger.log(Level.INFO, "Retrieving from the application layer {0} of a total of {1} entities: interval [{2}, {3}) using filter \"{4}\" and search param \"{5}\"", new Object[] { (lastEntityIndex - firstEntityIndex), entityCount, firstEntityIndex, lastEntityIndex, filter.getKey(), filterParam });
			entities = getListingService().filter(filter, filterParam, firstEntityIndex, lastEntityIndex);
		}
		else {
			// There's not. Retrieve all entities within range.
			logger.log(Level.INFO, "Retrieving from the application layer {0} of a total of {1} entities: interval [{2}, {3})", new Object[] { (lastEntityIndex - firstEntityIndex), entityCount, firstEntityIndex, lastEntityIndex });
			entities = ((ManageConclusionProjectsService)getListingService()).list(selectedAdvisor, firstEntityIndex, lastEntityIndex);
		}

		// Adjusts the last entity index.
		lastEntityIndex = firstEntityIndex + entities.size();
	}
	
	public ManageConclusionProjectsController() {
		viewPath = "/manageConclusionProjects/";
		bundleName = "msgs";
	}
	
	@Override
	protected ConclusionProject createNewEntity() {
		return new ConclusionProject();
	}

	@Override
	protected CrudService<ConclusionProject> getCrudService() {
		return manageConclusionProjectsService;
	}

	@Override
	protected void initFilters() {
		addFilter(new LikeFilter("manageConclusionProjects.filter.byTitle", "title", getI18nMessage("msgs", "manageConclusionProjects.filter.byTitle")));
		addFilter(new LikeFilter("manageConclusionProjects.filter.byAuthor", "author.completeName", getI18nMessage("msgs", "manageConclusionProjects.filter.byAuthor")));
	}

	public Researcher getSelectedAdvisor() {
		return selectedAdvisor;
	}

	public void setSelectedAdvisor(Researcher selectedAdvisor) {
		this.selectedAdvisor = selectedAdvisor;
	}
	
	protected void count(Researcher advisor) {
		logger.log(Level.INFO, "Counting entities. Filtering is {0}", (filtering ? "ON" : "OFF"));

		// Checks if there's an active filter.
		if (filtering)
			// There is. Count only filtered entities.
			entityCount = getListingService().countFiltered(filter, filterParam);
		else
		// There's not. Count all entities.
		entityCount = ((ManageConclusionProjectsService)getListingService()).count(advisor);

		// Since the entity count might have changed, force reloading of the lazy entity model.
		lazyEntities = null;

		// Updates the index of the last entity and checks if it has gone over the limit.
		lastEntityIndex = firstEntityIndex + MAX_DATA_TABLE_ROWS_PER_PAGE;
		if (lastEntityIndex > entityCount)
			lastEntityIndex = (int) entityCount;
	}
	
	public void goFirst(Researcher advisor) {
		// Move the first entity index to zero to show the first page.
		firstEntityIndex = 0;

		// Always counts the entities in this method, as it can be called via AJAX from the pages. This also sets the last
		// entity index.
		count(advisor);

		// Retrieve the entities from the application layer.
		retrieveEntities(advisor);
	}
	
	public void goPrevious(Researcher advisor) {
		// Only moves to the previous page if there is one.
		if (firstEntityIndex > 0) {
			// Shift the first entity index backward by the max number of entities in a page.
			firstEntityIndex -= MAX_DATA_TABLE_ROWS_PER_PAGE;

			// Checks if, by any chance, the above shifting took the first entity index too far and correct it.
			if (firstEntityIndex < 0)
				firstEntityIndex = 0;

			// Always counts the entities in this method, as it can be called via AJAX from the pages. This also sets the last
			// entity index.
			count(advisor);

			// Retrieve the entities from the application layer.
			retrieveEntities(advisor);
		}
	}
	
	public void goNext(Researcher advisor) {
		// Always counts the entities in this method, as it can be called via AJAX from the pages.
		count(advisor);

		// Only moves to the next page if there is one.
		if (lastEntityIndex < entityCount) {
			// Shift the first entity index forward by the max number of entities in a page.
			firstEntityIndex += MAX_DATA_TABLE_ROWS_PER_PAGE;

			// Set the last entity index to a full page of entities starting from the first index.
			lastEntityIndex = firstEntityIndex + MAX_DATA_TABLE_ROWS_PER_PAGE;

			// Retrieve the entities from the application layer.
			retrieveEntities(advisor);
		}
	}
	
	public void goLast(Researcher advisor) {
		// Always counts the entities in this method, as it can be called via AJAX from the pages.
		count(advisor);

		// Checks for the trivial case of no entities.
		if (entityCount == 0)
			firstEntityIndex = lastEntityIndex = 0;
		else {
			// Calculates how many entities there are in the last page (the remainder of dividing the count by the max
			// entities in a page).
			int remainder = ((int) entityCount % MAX_DATA_TABLE_ROWS_PER_PAGE);

			// Check if the remainder is zero, in which case the last page is full. Otherwise, the remainder is the number of
			// entities in
			// the last page. Sets the first and last index accordingly.
			firstEntityIndex = (remainder == 0) ? (int) entityCount - MAX_DATA_TABLE_ROWS_PER_PAGE : (int) entityCount - remainder;
			lastEntityIndex = (int) entityCount;
		}

		// Retrieve the entities from the application layer.
		retrieveEntities(advisor);
	}
	
	@Override
	protected void count() {
		if(selectedAdvisor != null){
			count(selectedAdvisor);
		}else{
			super.count();
		}
		
	}
	
	public <T> LazyDataModel<ConclusionProject> getLazyEntities(Researcher advisor) {
		if (lazyEntities == null) {
			count(advisor);
			lazyEntities = new PrimefacesLazyEntityDataModel<ConclusionProject>(getListingService().getDAO()) {
			};
			lazyEntities.setRowCount((int) entityCount);
		}

		return lazyEntities;
	}

}
