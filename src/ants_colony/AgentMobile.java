package src.ants_colony;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentMobile extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentLanceur.class.getName());
    private boolean goToInformation = false;

    private ArrayList<AID> chemin = new ArrayList<>();
    @Override
	protected void setup() {
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
                    // Traitement des messages reçus.
					if(msg != null) {
                        String prefix = msg.getContent().split(":")[0];
                        System.out.println("Name : " + myAgent.getAID().getLocalName() + " | Content : " + msg.getContent());
                        switch (prefix) {
                            case "lanceur":
                                goToInformation = true;
                                break;
                            case "aiguilleur":
                                break;
                            case "receveur":
                            
                                break;
                            default:
                                break;
                        }
                        
					}
                    if(goToInformation) {
                        // Liaison avec agent aiguilleur.

                    } else {
                        // Attendre le message.
                    }
				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}
    
    protected ACLMessage sendMessageToReceveur() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID agentReceveur = Program.receveurs.get(0);
		msg.addReceiver(agentReceveur);
		msg.setContent("lanceur:start-ant");
		return msg;
    }

}