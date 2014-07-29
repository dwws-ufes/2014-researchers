package br.ufes.inf.nemo.researchers.application;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.researchers.persistence.ConclusionProjectDAO;
import br.ufes.inf.nemo.researchers.persistence.ConclusionProjectJPADAO;
import br.ufes.inf.nemo.util.ejb3.application.CrudOperation;
import br.ufes.inf.nemo.util.ejb3.application.CrudServiceBean;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

@Stateless
public class ManageConclusionProjectsServiceBean extends CrudServiceBean<ConclusionProject> implements ManageConclusionProjectsService {
	/** The logger. */
	private static final Logger logger = Logger.getLogger(ConclusionProjectJPADAO.class.getCanonicalName());
	
	@EJB private ConclusionProjectDAO conclusionProjectDAO;
	
	@Override
	public BaseDAO<ConclusionProject> getDAO() {
		return conclusionProjectDAO;
	}

	@Override
	protected ConclusionProject createNewEntity() {
		return new ConclusionProject();
	}

	@Override
	public List<ConclusionProject> list(Researcher advisor, int... interval) {
		ConclusionProjectDAO dao = (ConclusionProjectDAO)getDAO();
		
		List<ConclusionProject> entities = dao.retrieveSome(interval, advisor);
		log(CrudOperation.LIST, entities, interval);
		return entities;
	}

	@Override
	public long count(Researcher advisor) {
		logger.log(Level.FINER, "Retrieving the object count...");
		return ((ConclusionProjectDAO)getDAO()).retrieveCount(advisor);
	}

}
