package br.ufes.inf.nemo.researchers.application;

import java.util.List;

import javax.ejb.Local;

import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;

@Local
public interface ManageConclusionProjectsService extends CrudService<ConclusionProject> {

	public List<ConclusionProject> list(Researcher advisor, int... arg0);
	
	public long count(Researcher advisor);
}
