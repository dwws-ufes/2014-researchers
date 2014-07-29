package br.ufes.inf.nemo.researchers.application;

import java.util.List;

import javax.ejb.Local;

import br.ufes.inf.nemo.researchers.domain.ConclusionProjectType;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;

@Local
public interface ManageResearchersService extends CrudService<Researcher> {

	List<Researcher> getResearchers();
	List<ConclusionProjectType> getConclusionProjectTypes();
	
}
