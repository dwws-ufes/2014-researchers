package br.ufes.inf.nemo.researchers.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.ufes.inf.nemo.researchers.domain.ConclusionProjectType;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.researchers.persistence.ResearcherDAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudServiceBean;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

@Stateless
public class ManageResearchersServiceBean extends CrudServiceBean<Researcher> implements ManageResearchersService {
	@EJB private ResearcherDAO researcherDAO;
	
	private List<Researcher> researchers;
	private List<ConclusionProjectType> conclusionProjectTypes;
	
	@Override
	public List<Researcher> getResearchers() {
		// TODO Auto-generated method stub
		if(researchers == null){
			researchers = new ArrayList<Researcher>();
			researchers.addAll(researcherDAO.retrieveAll());
		}
		Collections.sort(researchers);
		return researchers;
	}
	
	@Override
	public List<ConclusionProjectType> getConclusionProjectTypes() {
		// TODO Auto-generated method stub
		if(conclusionProjectTypes == null){
			conclusionProjectTypes = new ArrayList<ConclusionProjectType>();
			conclusionProjectTypes.addAll(Arrays.asList(ConclusionProjectType.values()));
		}
		return conclusionProjectTypes;
	}
	
	@Override
	public BaseDAO<Researcher> getDAO() {
		return researcherDAO;
	}

	@Override
	protected Researcher createNewEntity() {
		return new Researcher();
	}

}
