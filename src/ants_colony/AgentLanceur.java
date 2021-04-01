package src.ants_colony;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentLanceur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentLanceur.class.getName());

	private AID associatedAiguilleur;
    @Override
	protected void setup() {
		Object[] args = getArguments(); 
		associatedAiguilleur = Program.findByLocalName((String) args[0]);
		try {
			addBehaviour(new OneShotBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ArrayList<ACLMessage> msgs = sendStartMessage();
					for(int i = 0; i < Program.mobiles.size(); i++) {
						myAgent.send(msgs.get(i));
					}
				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}

	protected ArrayList<ACLMessage> sendStartMessage() {
		ArrayList<ACLMessage> messages = new ArrayList<>();
		for (AID agentMobile : Program.mobiles) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(agentMobile);
			msg.setContent("lanceur:start-ant");
			messages.add(msg);
		}
		return messages;
	}
    
}