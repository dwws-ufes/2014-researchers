package br.ufes.inf.nemo.researchers.application;

import javax.ejb.Local;

import br.ufes.inf.nemo.researchers.domain.Semester;
import br.ufes.inf.nemo.util.ejb3.application.CrudService;

@Local
public interface ManageSemestersService extends CrudService<Semester> {

}
