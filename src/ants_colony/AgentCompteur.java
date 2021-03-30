package src.ants_colony;

import jade.core.Agent;
import jade.core.behaviours.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentCompteur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentCompteur.class.getName());

	public int pheromone = 0;

    @Override
	protected void setup() {
		try {
			addBehaviour(new OneShotBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
					String[] args = msg.getContent().split(":");
					switch(args[1]){
						//reçoit aiguilleur:get depuis Aiguilleur
						case "get": // Bah écoute ça va
							ACLMessage response = new ACLMessage(ACLMessage.INFO);
							response.addReceiver(msg.getSender());
							response.addContent("compteur:"+(AgentCompteur myAgent).pheromone);
							myAgent.send(response);
							break;
						case "inc":
							(AgentCompteur myAgent).pheromone++;
							break;
						case "dec":
							(AgentCompteur myAgent).pheromone--;
							break;
						default:
							break;
					}
					
					
				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}
    
}
