package src.ants_colony;

import jade.core.Agent;
import jade.core.behaviours.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentReceveur extends Agent {

    private static final long serialVersionUID = 1L;
    private transient Logger logger = Logger.getLogger(AgentReceveur.class.getName());

    @Override
	protected void setup() {
		try {
			addBehaviour(new OneShotBehaviour(this) {
				private static final long serialVersionUID = 1L;
				public void action() {
					System.out.println("Agent " + getLocalName() + " ready.");
				}
			});
		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}
    
}
