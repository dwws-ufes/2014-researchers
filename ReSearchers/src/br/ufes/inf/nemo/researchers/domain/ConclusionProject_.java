package br.ufes.inf.nemo.researchers.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import br.ufes.inf.nemo.util.ejb3.persistence.PersistentObjectSupport_;

public class ConclusionProject_ extends PersistentObjectSupport_ {
	public static volatile SingularAttribute<ConclusionProject, String> title;
	public static volatile SingularAttribute<ConclusionProject, Researcher> author;
	public static volatile SingularAttribute<ConclusionProject, Date> issued;
	public static volatile SingularAttribute<ConclusionProject, Researcher> advisor;
	public static volatile SingularAttribute<ConclusionProject, Researcher> coadvisor;
	public static volatile SingularAttribute<ConclusionProject, ConclusionProjectType> conclusionProjectType;
	public static volatile SingularAttribute<ConclusionProject, Boolean> inProgress;
}
