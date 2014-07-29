package br.ufes.inf.nemo.researchers.persistence;

import java.util.List;

import javax.ejb.Local;

import br.ufes.inf.nemo.researchers.domain.ConclusionProject;
import br.ufes.inf.nemo.researchers.domain.Researcher;
import br.ufes.inf.nemo.util.ejb3.persistence.BaseDAO;

@Local
public interface ConclusionProjectDAO extends BaseDAO<ConclusionProject> {

	public List<ConclusionProject> retrieveSome(int[] arg0, Researcher advisor);
	
	public long retrieveCount(Researcher advisor);
}
