package br.ufes.inf.nemo.researchers.domain;

import java.util.Set;

import javax.persistence.metamodel.SingularAttribute;

import br.ufes.inf.nemo.util.ejb3.persistence.PersistentObjectSupport_;

public class Researcher_ extends PersistentObjectSupport_ {
	public static volatile SingularAttribute<Researcher, String> completeName;
	public static volatile SingularAttribute<Researcher, String> localUri;
	public static volatile SingularAttribute<Researcher, String> dblpL3sHtmlUri;
	public static volatile SingularAttribute<Researcher, String> dblpL3sRdfUri;
}
