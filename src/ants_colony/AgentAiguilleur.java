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

	private ArrayList<AID> nextAiguilleurs;

	public String type;
    @Override
	protected void setup() {
		Object[] args = getArguments();
		switch((String) args[0]){
			case "final":
				this.type = "final";
				this.associateCompteur = (AID) args[1];
				break;
			case "notFinal":
				this.type = "notFinal";
				this.associateCompteur = (AID) args[1];
				for(int i=2; i<args.length;i++){
					nextAiguilleurs.add((AID) args[i]);
				}
				break;
			case "initial":
				this.type = "initial";
				this.associateCompteur = null;
				for(int i=1; i<args.length;i++){
					nextAiguilleurs.add((AID) args[i]);
				}
				break;
		}
		System.out.println((String) args[0]);
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
										increment.addReceiver(Program.findByLocalName(args[2]));
										increment.setContent("aiguilleur:inc");
										myAgent.send(increment);
										break;

									//reçoit mobile:notfound:{localname}
									case "notfound":
										//envoie aiguilleur:dec à AgentCompteur
										ACLMessage decrement = new ACLMessage(ACLMessage.INFORM);
										decrement.addReceiver(Program.findByLocalName(args[2]));
										decrement.setContent("aiguilleur:dec");
										myAgent.send(decrement);
										
										// Ce cas est chiant
										break;
									default:
										break;
								}
								break;
							case "aiguilleur":
								switch(args[1]){
									case "getvaluecompteur":
										ACLMessage msgToCompteur = new ACLMessage(ACLMessage.INFORM);
										msgToCompteur.setContent("aiguilleur:get");
										msgToCompteur.addReceiver(((AgentAiguilleur) myAgent).associateCompteur);
										myAgent.send(msgToCompteur);

										ACLMessage responseFromCompteur = blockingReceive(MessageTemplate.MatchSender(((AgentAiguilleur) myAgent).associateCompteur));
										
										ACLMessage msgToAiguilleur = new ACLMessage(ACLMessage.INFORM);
										msgToAiguilleur.setContent("aiguilleur:" + responseFromCompteur.getContent().split(":")[1]);
										msgToAiguilleur.addReceiver(msg.getSender());
										myAgent.send(msgToAiguilleur);
										
										break;
									case "":
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
		if(args1[0].equals(args2[0]) && args1[0].equals("compteur")){
			
			int val1 = Integer.parseInt(args1[1]);
			int val2 = Integer.parseInt(args2[1]);

			if(val1 > val2){
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
