package br.ufes.inf.nemo.researchers.domain;

public enum ConclusionProjectType {
	BScProject, 
	MScProject, 
	PhDProject, 
	PostPhdProject;
	
	@Override
	public String toString() {
		String out = "";
		switch (this) {
		case BScProject:
			out = "B.Sc Project"; 
			break;
		case MScProject:
			out = "M.Sc Project"; 
			break;
		case PhDProject:
			out = "PhD Project"; 
			break;
		case PostPhdProject:
			out = "Postdoc Project"; 
			break;			
		}
		
		if(!out.equals("")){
			return out;
		}
		return super.toString();
	}
}
