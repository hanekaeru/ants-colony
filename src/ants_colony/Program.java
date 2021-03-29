package src.ants_colony;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;

import java.util.logging.Level;
import java.util.logging.Logger;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

public class Program extends Agent {

    int nb_nodes = 9; // 1 & 2 will always be information sources.
	final static int NB_RECEVEURS = 2; // Il s'agit du nombre de noeuds qui n'auront pas de successeurs car ils contiennent l'informations.
	final static int NB_AIGUILLEURS = 14; // Il s'agit du nombre d'arcs du graphe. 
    
    private ArrayList<AID> nodes = new ArrayList<>();
	private transient Logger logger = Logger.getLogger(Program.class.getName());


    public static void main(String[] args) {


    }

	/**
	 * Création des agents receveurs. Ils sont au nombre de 2. Ils vont permettre le lien entre les agents mobiles
	 * et les informations présentes dans la base de données (image). Une fois qu'un agent mobile est arrivé ici,
	 * il va devoir créer des "phéromones".
	 */
    protected void createAgentsReceveurs() {
		logger.log(Level.INFO, "Création {} agents receveurs.", NB_RECEVEURS);
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_RECEVEURS + 1; i++) {
				String localName = "agent_receveur_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentReceveur", null);
				guest.start();
				nodes.add(new AID(localName, AID.ISLOCALNAME));
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents receveurs...", e);
		}
	}

	protected void createAgentsAiguilleurs() {
		logger.log(Level.INFO, "Création {} agents aiguilleurs.", NB_AIGUILLEURS);
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_AIGUILLEURS + 1; i++) {
				String localName = "agent_aiguilleurs_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentAiguilleur", null);
				guest.start();
				nodes.add(new AID(localName, AID.ISLOCALNAME));
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents aiguilleurs...", e);
		}
	}

}
