package br.ufes.inf.nemo.researchers.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import br.ufes.inf.nemo.researchers.domain.ConclusionProject_;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.researchers.domain.Researcher_;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseJPADAO;

@Stateless
public class ResearcherJPADAO extends BaseJPADAO<Researcher> implements ResearcherDAO {

	@PersistenceContext private EntityManager entityManager;
	
	@Override
	public Class<Researcher> getDomainClass() {
		return Researcher.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	protected List<Order> getOrderList(CriteriaBuilder cb, Root<Researcher> root) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(root.get(Researcher_.completeName)));
		return orderList;
	}

}
