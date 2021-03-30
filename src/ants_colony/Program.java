package src.ants_colony;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.util.logging.Level;
import java.util.logging.Logger;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

public class Program extends Agent {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	int nb_nodes = 9; // 1 & 2 will always be information sources.
	final static int NB_RECEVEURS = 2; // Il s'agit du nombre de noeuds qui n'auront pas de successeurs car ils contiennent l'informations.
	final static int NB_AIGUILLEURS = 14; // Il s'agit du nombre d'arcs du graphe. 
    final static int NB_COMPTEURS = 7; // 

    private ArrayList<AID> receveurs = new ArrayList<>();
    private ArrayList<AID> aiguilleurs = new ArrayList<>();
    private ArrayList<AID> compteurs = new ArrayList<>();

	private ArrayList<AID> nodes = new ArrayList<>();

	private transient Logger logger = Logger.getLogger(Program.class.getName());

    @Override
	protected void setup() {
		try {
			logger.log(Level.INFO, " {} setting up", getLocalName());
			createAgentsReceveurs();

			addBehaviour(new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1L;
				@Override
				public void action() {
					// no action
				}
			});

		} catch (Exception e) {
			logger.log(Level.INFO, "Got an exception.", e);
		}
	}

	/**
	 * Création des agents receveurs. Ils sont au nombre de 2 dans une configuration à 9 noeuds. Ils vont permettre
	 * le lien entre les agents mobiles et les informations présentes dans la base de données (image). Une fois qu'un 
	 * agent mobile est arrivé ici, il va devoir créer des "phéromones".
	 */
    protected void createAgentsReceveurs() {
		logger.log(Level.INFO, "Creation {} agents receveurs.", NB_RECEVEURS);
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_RECEVEURS + 1; i++) {
				String localName = "agent_receveur_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentReceveur", null);
				guest.start();
				receveurs.add(new AID(localName, AID.ISLOCALNAME));
				nodes.add(new AID(localName, AID.ISLOCALNAME));
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents receveurs...", e);
		}
	}

	/**
	 * Création des agents aiguilleurs. Ils sont au nombre de 14 dans une configuration à 9 noeuds. Ce sont eux
	 * qui vont permettre aux agents mobiles de pouvoir se diriger vers un autre noeud (si la configuration le
	 * permet : n/2 et n/2+1) ou alors rentrer vers la base via le chemin qu'il a emprunté.
	 */
	protected void createAgentsAiguilleurs() {
		logger.log(Level.INFO, "Creation {} agents aiguilleurs.", NB_AIGUILLEURS);
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_AIGUILLEURS + 1; i++) {
				String localName = "agent_aiguilleurs_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentAiguilleur", null);
				guest.start();
				aiguilleurs.add(new AID(localName, AID.ISLOCALNAME));
				nodes.add(new AID(localName, AID.ISLOCALNAME));
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents aiguilleurs...", e);
		}
	}

	protected void createAgentsCompteurs() {
		logger.log(Level.INFO, "Creation {} agents compteurs.", NB_COMPTEURS);
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_COMPTEURS + 1; i++) {
				String localName = "agent_compteurs_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentCompteur", null);
				guest.start();
				compteurs.add(new AID(localName, AID.ISLOCALNAME));
				nodes.add(new AID(localName, AID.ISLOCALNAME));
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents compteurs...", e);
		}
	}
	
}
