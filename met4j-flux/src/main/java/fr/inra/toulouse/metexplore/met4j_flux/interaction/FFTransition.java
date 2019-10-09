package fr.inra.toulouse.metexplore.met4j_flux.interaction;

import java.util.ArrayList;
import java.util.List;


public class FFTransition {
	
	
	
	private List<Interaction> conditionalInteractions= new ArrayList<Interaction>();
	private Interaction defaultInteraction;
	
	public FFTransition(){
		
		
		
	}
	
	public void setdefaultInteraction(Interaction defaultInt){
		defaultInteraction=defaultInt;
	}
	
	public Interaction getdefaultInteraction(){
		return defaultInteraction;
	}
	
	public void addConditionalInteraction(Interaction inter){
		conditionalInteractions.add(inter);
	}
	
	public List<Interaction> getConditionalInteractions(){
		return conditionalInteractions;
	}
	
	

}
