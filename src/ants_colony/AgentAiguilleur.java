package src.ants_colony;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentAiguilleur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentLanceur.class.getName());

    private ArrayList<AID> associateCompteurs;

    @Override
	protected void setup() {
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
					
					if(msg != null){
						String[] args = msg.getContent().split(":");
						switch(args[0]){
							case "mobile" : 
								switch(args[1]){
									//reçoit mobile:forward de AgentMobile
									case "forward":
										// envoie aiguilleur:{localname} à AgentMobile
										ACLMessage response = new ACLMessage(ACLMessage.INFORM);
										response.addReceiver(msg.getSender());
										response.setContent("aiguilleur:"+((AgentAiguilleur) myAgent).selectHost().getLocalName());
										myAgent.send(response);
										break; 

									//reçoit mobile:backward:{localname} de AgentMobile
									case "backward":
										//envoie aiguilleur:inc à AgentCompteur
										ACLMessage increment = new ACLMessage(ACLMessage.INFORM);
										increment.addReceiver(((AgentAiguilleur) myAgent).findByLocalName(args[2]));
										increment.setContent("aiguilleur:inc");
										myAgent.send(increment);
										break;

									//reçoit mobile:notfound:{localname}
									case "notfound":
										//envoie aiguilleur:dec à AgentCompteur
										ACLMessage decrement = new ACLMessage(ACLMessage.INFORM);
										decrement.addReceiver(((AgentAiguilleur) myAgent).findByLocalName(args[2]));
										decrement.setContent("aiguilleur:dec");
										myAgent.send(decrement);
										
										// Ce cas est chiant
										break;
									default:
										break;
								}
								break;
							default:
								break;
						}						

					} 
				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}
	
	protected void addAssociateCompteurs(AID host){
	    this.associateCompteurs.add(host);
	}
	
	protected List<AID> getAssociateCompteurs() {
	    return this.associateCompteurs;
	}

	protected AID findByLocalName(String localname){
		for(AID ag : this.associateCompteurs){
			if(ag.getLocalName().equals(localname))
				return ag;
		}
		return null;
	}

	protected AID selectHost(){
		//Envoie aiguilleur:get à AgentCompteur
		for(AID ag : this.associateCompteurs){
			ACLMessage msgToCompteurs = new ACLMessage(ACLMessage.INFORM);
			msgToCompteurs.setContent("aiguilleur:get");
			msgToCompteurs.addReceiver(ag);
			this.send(msgToCompteurs);
		}
		//Reçoit deux messages compteur:{valeurpheromone}
		
		ACLMessage response1 = this.receive();
		ACLMessage response2 = this.receive();
		
		String[] args1 = response1.getContent().split(":");
		String[] args2 = response2.getContent().split(":");
		if(args1[0].equals(args2[0]) && args1[0].equals("compteur")){
			int val1 = Integer.parseInt(args1[1]);
			int val2 = Integer.parseInt(args2[1]);

			if(val1 > val2){
				for(AID ag : this.associateCompteurs){
					if( ag.equals( response1.getSender() ) ){
						return ag;
					}
				}
			}else{ 
				for(AID ag : this.associateCompteurs){
					if(	ag.equals( response2.getSender() ) ){
						return ag;
					}
				}
			}
		}
		return null;
	}
    
       
}
