package br.ufes.inf.nemo.researchers.application;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.ufes.inf.nemo.researchers.domain.Semester;
import br.ufes.inf.nemo.researchers.persistence.SemesterDAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudServiceBean;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

@Stateless
public class ManageSemestersServiceBean extends CrudServiceBean<Semester>
		implements ManageSemestersService {

	@EJB
	private SemesterDAO semesterDAO;
	
	@Override
	public BaseDAO<Semester> getDAO() {
		return semesterDAO;
	}

	@Override
	protected Semester createNewEntity() {
		return new Semester();
	}

}
