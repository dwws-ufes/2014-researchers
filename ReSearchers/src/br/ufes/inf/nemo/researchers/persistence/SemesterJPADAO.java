package br.ufes.inf.nemo.researchers.persistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.ufes.inf.nemo.researchers.domain.Semester;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseJPADAO;

@Stateless
public class SemesterJPADAO extends BaseJPADAO<Semester> implements SemesterDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Class<Semester> getDomainClass() {
		return Semester.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

}
