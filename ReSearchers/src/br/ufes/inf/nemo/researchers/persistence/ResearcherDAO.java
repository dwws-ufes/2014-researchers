package br.ufes.inf.nemo.researchers.persistence;

import javax.ejb.Local;

import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

@Local
public interface ResearcherDAO extends BaseDAO<Researcher> {

}
