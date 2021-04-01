package src.ants_colony;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentMobile extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentLanceur.class.getName());
    private ArrayList<AID> chemin = new ArrayList<>();
    private String information;
    @Override
	protected void setup() {
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
                    AgentMobile agentMobile = (AgentMobile) myAgent;
                    // Traitement des messages reçus.
					if(msg != null) { 
                        String[] args = msg.getContent().split(":");
						logger.log(Level.INFO, agentMobile.getLocalName() + " recoit un message de " + msg.getSender().getLocalName() + " contenant : " + msg.getContent());
                        switch (args[0]) {
                            //reçoit "lanceur:{localname}" depuis AgentLanceur
                            case "lanceur":
                                //envoie "mobile:forward" à AgentAiguilleur
                                agentMobile.information = "";
                                agentMobile.chemin.clear();
                                agentMobile.chemin.add(Program.findByLocalName(args[1]));
                                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                                message.addReceiver(Program.findByLocalName(args[1]));
                                message.setContent("mobile:forward");
                                myAgent.send(message);
                                break;

                            case "aiguilleur":
                                if(args[1].equals("finish")){
                                    logger.log(Level.SEVERE, agentMobile.getLocalName() + " est retourne au point de depart avec l'information : " + agentMobile.information);
                                }else if(args[1].equals("end")){
                                    ACLMessage retrieveInformation = new ACLMessage(ACLMessage.INFORM);
                                    retrieveInformation.addReceiver(Program.findByLocalName(args[2]));
                                    retrieveInformation.setContent("mobile:get");
                                    myAgent.send(retrieveInformation);
                                }else if(agentMobile.information.isEmpty()){
                                    agentMobile.chemin.add(Program.findByLocalName(args[1]));
                                    ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
                                    message2.addReceiver(Program.findByLocalName(args[1]));
                                    message2.setContent("mobile:forward");
                                    myAgent.send(message2);
                                }
                                else if(agentMobile.information.equals("notfound")){
                                    agentMobile.chemin.add(Program.findByLocalName(args[1]));
                                    AID receiver = agentMobile.chemin.get(agentMobile.chemin.size()-1);
                                    ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
                                    message2.addReceiver(receiver);
                                    message2.setContent("mobile:notfound:"+receiver.getLocalName());
                                    myAgent.send(message2);
                                }else{
                                    ACLMessage messageToGoBackward = new ACLMessage(ACLMessage.INFORM);
                                    AID receiver = agentMobile.chemin.get(agentMobile.chemin.size()-1);
                                    messageToGoBackward.addReceiver(receiver);
                                    agentMobile.chemin.remove(receiver);
                                    messageToGoBackward.setContent("mobile:backward:" + receiver.getLocalName());
                                    myAgent.send(messageToGoBackward);
                                }
                                break;
                            //Reçoit receveur:{information}
                            case "receveur":
                                agentMobile.chemin.removeAll(Collections.singleton(null));
                                AID receiver = agentMobile.chemin.get(agentMobile.chemin.size()-1);
                                agentMobile.chemin.remove(receiver);
                                if(args[1].equals("notfound")){
                                    agentMobile.information="notfound";
                                    //Envoie mobile:notfound:{localname} à AgentAiguilleur
                                    ACLMessage notfoundMessage = new ACLMessage(ACLMessage.INFORM);
                                    notfoundMessage.addReceiver(receiver);
                                    notfoundMessage.setContent("mobile:notfound:"+ receiver.getLocalName());
                                    myAgent.send(notfoundMessage);
                                }else{
                                    agentMobile.information=args[1];
                                    ACLMessage foundMessage= new ACLMessage(ACLMessage.INFORM);
                                    foundMessage.addReceiver(receiver);
                                    foundMessage.setContent("mobile:backward:" + receiver.getLocalName());
                                    myAgent.send(foundMessage);
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
    
    protected ACLMessage sendMessageToReceveur() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID agentReceveur = Program.receveurs.get(0);
		msg.addReceiver(agentReceveur);
		msg.setContent("lanceur:start-ant");
		return msg;
    }

}