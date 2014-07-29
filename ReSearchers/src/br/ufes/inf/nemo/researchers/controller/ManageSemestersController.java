package br.ufes.inf.nemo.researchers.controller;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.ufes.inf.nemo.researchers.application.ManageSemestersService;
import br.ufes.inf.nemo.researchers.domain.Semester;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;
import br.ufes.inf.nemo.util.ejb3.application.filters.SimpleFilter;
import br.ufes.inf.nemo.util.ejb3.controller.CrudController;

@Named
@SessionScoped
public class ManageSemestersController extends CrudController<Semester> {
	@EJB
	private ManageSemestersService manageSemestersService;
	
	@Override
	protected Semester createNewEntity() {
		return new Semester();
	}

	@Override
	protected CrudService<Semester> getCrudService() {
		return manageSemestersService;
	}

	@Override
	protected void initFilters() {
		addFilter(new SimpleFilter("manageSemesters.filter.byYear", "year", getI18nMessage("msgs", "manageSemesters.text.filter.byYear")));

	}
	
	public ManageSemestersController() {
		viewPath = "/manageSemesters/";
		bundleName = "msgs";
	}

}
