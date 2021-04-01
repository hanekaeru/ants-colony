package src.ants_colony;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentReceveur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentReceveur.class.getName());

	public String information;

    @Override
	protected void setup() {
		this.information = "2+2=4";
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
					if(msg!=null){
						AgentReceveur agentReceveur = (AgentReceveur) myAgent;
						logger.log(Level.INFO, agentReceveur.getLocalName() + " recoit un message de " + msg.getSender().getLocalName() + " contenant : " + msg.getContent());
						String[] args = msg.getContent().split(":");
						//Reçoit mobile:get depuis AgentMobile
						if(args[0].equals("mobile") && args[1].equals("get")){
							ACLMessage response = msg.createReply();
							if(agentReceveur.information!=null){
								response.setContent("receveur:" + agentReceveur.information);
							}else{
								response.setContent("receveur:notfound");
							}
							myAgent.send(response);
						}
					}

				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}
    
}
