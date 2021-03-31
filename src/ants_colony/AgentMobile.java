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
                        String[] args = msg.getContent().split(":");
                        System.out.println("Name : " + myAgent.getAID().getLocalName() + " | Content : " + msg.getContent());
                        switch (args[0]) {
                            //reçoit "lanceur:{localname}" depuis AgentLanceur
                            case "lanceur":
                                //envoie "mobile:forward" à AgentAiguilleur
                                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                                message.addReceiver(Program.findByLocalName(args[1]));
                                message.setContent("mobile:forward");
                                myAgent.send(message);
                                break;
                            case "aiguilleur":
                                
                                ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
                                message2.addReceiver(Program.findByLocalName(args[1]));
                                message2.setContent("mobile:forward");
                                myAgent.send(message2);
                                break;
                            case "receveur":

                            
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
    
    protected ACLMessage sendMessageToReceveur() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID agentReceveur = Program.receveurs.get(0);
		msg.addReceiver(agentReceveur);
		msg.setContent("lanceur:start-ant");
		return msg;
    }

}