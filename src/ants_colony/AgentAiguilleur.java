package src.ants_colony;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentAiguilleur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentLanceur.class.getName());
	 
    private AID associateCompteur;

	private ArrayList<AID> nextAiguilleurs = new ArrayList<>();

	private AID associateReceveur;
	
	private String type;

    @Override
	protected void setup() {
		Object[] args = getArguments();

		switch((String) args[0]){
			case "final":
				this.type = "final";
				this.associateCompteur = Program.findByLocalName((String) args[1]);
				this.associateReceveur = Program.findByLocalName((String) args[2]);
				break;
			case "notFinal":
				this.type = "notFinal";
				this.associateCompteur = Program.findByLocalName((String) args[1]);
				for(int i=2; i<args.length;i++){ 
					if(args.length > i){
						nextAiguilleurs.add(Program.findByLocalName((String) args[i])); 
					}
				} 
				break;
			case "initial": 
				this.type = "initial";
				this.associateCompteur = null;
				for(int i=1; i<args.length;i++){
					if(args.length>i){
						nextAiguilleurs.add(Program.findByLocalName((String) args[i]));
					}				
				}
				break;
		}
		try {
			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					ACLMessage msg = myAgent.receive();
					
					if(msg != null){
						AgentAiguilleur agentAiguilleur = (AgentAiguilleur) myAgent;
						String[] args = msg.getContent().split(":");
						switch(args[0]){
							case "mobile" : 
								switch(args[1]){ 
									//reçoit mobile:forward de AgentMobile
									case "forward":
										
										if(agentAiguilleur.type.equals("final")){
											// envoie aiguilleur:end:{localname} à AgentMobile
											ACLMessage response = new ACLMessage(ACLMessage.INFORM);
											response.addReceiver(msg.getSender());
											response.setContent("aiguilleur:end:"+agentAiguilleur.associateReceveur.getLocalName());
											myAgent.send(response);
										}else{
											// envoie aiguilleur:{localname} à AgentMobile
											ACLMessage response = new ACLMessage(ACLMessage.INFORM);
											response.addReceiver(msg.getSender());
											response.setContent("aiguilleur:"+agentAiguilleur.selectHost().getLocalName());
											myAgent.send(response);
										}

										break; 

									//reçoit mobile:backward:{localname} de AgentMobile
									case "backward":
										if(agentAiguilleur.type.equals("initial")){
											//Envoie "aiguilleur:finish" à AgentMobile
											ACLMessage msgToMobile = msg.createReply();
											msgToMobile.setContent("aiguilleur:finish");
											myAgent.send(msgToMobile);
										}else {
											//Envoie "aiguilleur:inc" à AgentCompteur
											ACLMessage msgToIncCompteur = new ACLMessage(ACLMessage.INFORM);
											msgToIncCompteur.setContent("aiguilleur:inc");
											msgToIncCompteur.addReceiver(agentAiguilleur.associateCompteur);
											myAgent.send(msgToIncCompteur);

											//envoie aiguilleur:go à AgentMobile
											ACLMessage responseToMobile = msg.createReply();
											responseToMobile.setContent("aiguilleur:go");
											myAgent.send(responseToMobile);
										}
										break;



									//reçoit mobile:notfound:{localname}
									case "notfound":

										//envoie aiguilleur:dec à AgentCompteur
										ACLMessage msgToDecCompteur = new ACLMessage(ACLMessage.INFORM);
										msgToDecCompteur.setContent("aiguilleur:dec");
										msgToDecCompteur.addReceiver(agentAiguilleur.associateCompteur);
										myAgent.send(msgToDecCompteur);

										//envoie aiguilleur:go à AgentMobile
										ACLMessage responseToMobileNotFound = msg.createReply();
										responseToMobileNotFound.setContent("aiguilleur:go");
										myAgent.send(responseToMobileNotFound);
										break;

									default:
										break;
								}
								break;
							case "aiguilleur":
								switch(args[1]){
									//Reçoit "aiguilleur:getvaluecompteur" depuis AgentAiguilleur
									case "getvaluecompteur":
										//Envoie "aiguilleur:get" à AgentCompteur
										ACLMessage msgToCompteur = new ACLMessage(ACLMessage.INFORM);
										msgToCompteur.setContent("aiguilleur:get");
										msgToCompteur.addReceiver(agentAiguilleur.associateCompteur);
										myAgent.send(msgToCompteur);

										//Reçoit "compteur:{valeurpheromone}" depuis AgentCompteur
										ACLMessage responseFromCompteur = blockingReceive(MessageTemplate.MatchSender(agentAiguilleur.associateCompteur));
										
										//Envoit "aiguilleur:{valeurpheromone}" à AgentAiguilleur
										ACLMessage msgToAiguilleur = new ACLMessage(ACLMessage.INFORM);
										msgToAiguilleur.setContent("aiguilleur:" + responseFromCompteur.getContent().split(":")[1]);
										msgToAiguilleur.addReceiver(msg.getSender());
										myAgent.send(msgToAiguilleur);
										break;
								}
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

	protected AID findNextAiguilleurByLocalName(String localname){
		for(AID ag : this.nextAiguilleurs){
			if(ag.getLocalName().equals(localname)){
				return ag;
			}
		}
		return null;
	}

	protected AID getAssociateCompteur() {
	    return this.associateCompteur;
	}

	protected AID selectHost(){
		//Envoie aiguilleur:getvaluecompteur à AgentAiguilleur
		for(AID ag : this.nextAiguilleurs){
			ACLMessage msgToCompteurs = new ACLMessage(ACLMessage.INFORM);
			msgToCompteurs.setContent("aiguilleur:getvaluecompteur");
			msgToCompteurs.addReceiver(ag);
			this.send(msgToCompteurs);
		}
		//Reçoit deux messages aiguilleur:{valeurpheromone}

		ACLMessage response1 = this.blockingReceive(MessageTemplate.MatchSender(this.nextAiguilleurs.get(0)));
		ACLMessage response2 = this.blockingReceive(MessageTemplate.MatchSender(this.nextAiguilleurs.get(1)));

		String[] args1 = response1.getContent().split(":");
		String[] args2 = response2.getContent().split(":");
		if(args1[0].equals(args2[0]) && args1[0].equals("aiguilleur")){
			
			int val1 = Integer.parseInt(args1[1]);
			int val2 = Integer.parseInt(args2[1]);
			val1++;
			val2++;

			double somme = val1 + val2;

			double randomValue = Math.random();
			if(randomValue <= ((double)val1/somme)){
				for(AID ag : this.nextAiguilleurs){
					if( ag.equals( response1.getSender() ) ){
						return ag;
					}
				}
			}else{
				for(AID ag : this.nextAiguilleurs){
					if(	ag.equals( response2.getSender() ) ){
						return ag;
					}
				}
			}
		}
		return null;
	}
    
       
}
