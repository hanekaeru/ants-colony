package src.ants_colony;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentCompteur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentCompteur.class.getName());

	public int pheromone = 0;

    @Override
	protected void setup() {
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
					if(msg!=null){
						AgentCompteur agentCompteur = (AgentCompteur) myAgent;
						String[] args = msg.getContent().split(":");
						switch(args[1]){
							//reçoit aiguilleur:get depuis Aiguilleur
							case "get": 
								ACLMessage response = new ACLMessage(ACLMessage.INFORM);
								response.addReceiver(msg.getSender());
								response.setContent("compteur:"+ agentCompteur.pheromone);
								myAgent.send(response);
								break;
							case "inc":
								agentCompteur.pheromone++;
								break;
							case "dec":
								agentCompteur.pheromone--;
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
    
}
