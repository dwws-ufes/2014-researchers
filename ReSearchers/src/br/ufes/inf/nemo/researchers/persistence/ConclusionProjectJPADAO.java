package br.ufes.inf.nemo.researchers.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.ConclusionProject_;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseJPADAO;
import br.ufes.inf.nemo.util.ejb3.persistence.exceptions.PersistentObjectNotFoundException;

@Stateless
public class ConclusionProjectJPADAO extends BaseJPADAO<ConclusionProject> implements ConclusionProjectDAO {
	/** The logger. */
	private static final Logger logger = Logger.getLogger(ConclusionProjectJPADAO.class.getCanonicalName());
	
	@PersistenceContext private EntityManager entityManager;
	
	private static Researcher selectedAdvisor;
	
	@Override
	public Class<ConclusionProject> getDomainClass() {
		return ConclusionProject.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	protected List<Order> getOrderList(CriteriaBuilder cb, Root<ConclusionProject> root) {
		System.out.println();
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(root.get(ConclusionProject_.inProgress)));
		orderList.add(cb.desc(root.get(ConclusionProject_.issued)));
		orderList.add(cb.desc(root.get(ConclusionProject_.conclusionProjectType)));
		orderList.add(cb.asc(root.get(ConclusionProject_.author.getName())));
		return orderList;
	}
	
	@Override
	public List<ConclusionProject> retrieveSome(int[] interval, Researcher advisor) {
		selectedAdvisor = advisor;
		// Constructs the query over the Spiritist class.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ConclusionProject> cq = cb.createQuery(ConclusionProject.class);
		Root<ConclusionProject> root = cq.from(ConclusionProject.class);

		// Filters the query with the email.
		//cq.where(cb.equal(root.get(ConclusionProject_.advisor), advisor));
		Predicate eqAd = cb.equal(root.get(ConclusionProject_.advisor), advisor);
		Predicate eqCoAd = cb.equal(root.get(ConclusionProject_.coadvisor), advisor);
		Predicate orAd = cb.or(eqAd, eqCoAd);
		cq.where(orAd);
		cq.where(cb.or(cb.equal(root.get(ConclusionProject_.advisor), advisor), cb.equal(root.get(ConclusionProject_.coadvisor), advisor)));
		List<ConclusionProject> result1 = null;
		try {
			result1 = executeMultipleResultQuery(cq, advisor);
		} catch (PersistentObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.log(Level.INFO, "Retrieve conclusion project by (co)advisor \"{0}\" returned \"{1}\"", new Object[] { advisor, result1 });
		return result1;
	}
	
	protected List<ConclusionProject> executeMultipleResultQuery(CriteriaQuery<ConclusionProject> cq, Researcher advisor) throws PersistentObjectNotFoundException{
		selectedAdvisor = advisor;
		// Looks for a single result. Throws a checked exception if the entity is not found or in case of multiple results.
		try {
			List<ConclusionProject> result = getEntityManager().createQuery(cq).getResultList();
			
			return result;
		}
		catch (NoResultException e) {
			logger.log(Level.WARNING, "NoResultException thrown for params: " + advisor, e);
			throw new PersistentObjectNotFoundException(e, getDomainClass(), advisor);
		}
	}
	
	@Override
	public long retrieveCount(Researcher advisor) {
		selectedAdvisor = advisor;
		// Using the entity manager, create a criteria query to retrieve the object count.
		EntityManager em = getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ConclusionProject> rt = cq.from(getDomainClass());
		cq.select(cb.count(rt));
		// Filters the query with the email.
		//cq.where(cb.equal(rt.get(ConclusionProject_.advisor), advisor));
		Predicate eqAd = cb.equal(rt.get(ConclusionProject_.advisor), advisor);
		Predicate eqCoAd = cb.equal(rt.get(ConclusionProject_.coadvisor), advisor);
		Predicate orAd = cb.or(eqAd, eqCoAd);
		cq.where(orAd);
		cq.where(cb.or(cb.equal(rt.get(ConclusionProject_.advisor), advisor), cb.equal(rt.get(ConclusionProject_.coadvisor), advisor)));
		Query q = em.createQuery(cq);

		// Retrieve the value and return.
		long count = ((Long) q.getSingleResult()).longValue();
		logger.log(Level.INFO, "Retrieved count for {0}: {1}", new Object[] { getDomainClass().getName(), count });
		return count;
	}
	
	@Override
	public List<ConclusionProject> retrieveSome(int[] interval) {
		if(selectedAdvisor == null){
			return super.retrieveSome(interval);
		}else{
			return retrieveSome(interval, selectedAdvisor);
		}
		
	}

}
