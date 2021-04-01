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

	private static final long serialVersionUID = 1L;
	
	int nb_nodes = 9; // 1 & 2 will always be information sources.
	final static int NB_RECEVEURS = 2; // Il s'agit du nombre de noeuds qui n'auront pas de successeurs car ils contiennent l'informations.
	final static int NB_AIGUILLEURS = 9; // Il s'agit du nombre d'arcs du graphe. 
    final static int NB_COMPTEURS = 5; //
	final static int NB_MOBILES = 1; // Nombre d'agents de recherche. On peut en ajouter autant qu'on veut mais pour les tests.
	final static int NB_LANCEURS = 1;
	
    public static ArrayList<AID> receveurs = new ArrayList<>();
    public static ArrayList<AID> aiguilleurs = new ArrayList<>();
    public static ArrayList<AID> compteurs = new ArrayList<>();
	public static ArrayList<AID> mobiles = new ArrayList<>();
	public static ArrayList<AID> lanceurs = new ArrayList<>();
	public static ArrayList<AID> agents = new ArrayList<>(); 

	private transient Logger logger = Logger.getLogger(Program.class.getName());
	
    @Override
	protected void setup() {
		try {
			logger.log(Level.INFO, getLocalName() + " setting up");
			createAgentsMobiles();
			createAgentsReceveurs();
			createAgentsCompteurs(); 
			createAgentsAiguilleurs();
			createAgentsLanceurs();
			
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
		logger.log(Level.INFO, "Creation "+ NB_RECEVEURS + " agents receveurs.");
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_RECEVEURS + 1; i++) {
				String localName = "agent_receveur_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentReceveur", null);
				guest.start();
				AID newAgent = new AID(localName, AID.ISLOCALNAME);
				receveurs.add(newAgent);
				agents.add(newAgent);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents receveurs...", e);
		}
	}

	/**
	 * Création des agents compteurs. Ils sont au nombre de 7 dans une configuration de 9 noeuds. Ce sont ceux qui,
	 * si un agent mobile atteint une source d'information et que celui-ci a été obligé de passer par l'agent compteur,
	 * celui-ci va activer des "phéromones" afin d'indiquer que le chemin menant à la source d'informations est ici.
	 */
	protected void createAgentsCompteurs() {
		logger.log(Level.INFO, "Creation " + NB_COMPTEURS + " agents compteurs.");
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_COMPTEURS + 1; i++) {
				String localName = "agent_compteurs_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentCompteur", null);
				guest.start();
				AID newAgent = new AID(localName, AID.ISLOCALNAME);
				compteurs.add(newAgent);
				agents.add(newAgent);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents compteurs...", e);
		}
	}
	
	/**
	 * Création des agents mobiles. Il n'y en a qu'un seul pour le moment, mais le code a été conçu de façon à pouvoir
	 * en ajouter facilement. 
	 */
	protected void createAgentsMobiles() {
		logger.log(Level.INFO, "Creation "+ NB_MOBILES + " agents mobiles.");
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_MOBILES + 1; i++) {
				String localName = "agent_mobile_" + i + "_node";
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentMobile", null);
				guest.start();
				AID newAgent = new AID(localName, AID.ISLOCALNAME);
				mobiles.add(newAgent);
				agents.add(newAgent);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents mobiles...", e);
		}
	}

	protected void createAgentsLanceurs() {
		logger.log(Level.INFO, "Creation "+ NB_LANCEURS + " agents lanceurs.");
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_LANCEURS + 1; i++) {
				String localName = "agent_lanceur_" + i + "_node";
				Object[] args = {Program.aiguilleurs.get(5).getLocalName()};
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentLanceur", args); //On lance l'agent mobile depuis le 6e neud
				guest.start();
				AID newAgent = new AID(localName, AID.ISLOCALNAME);
				lanceurs.add(newAgent);
				agents.add(newAgent);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents lanceurs...", e);
		}
		
	}

		/**
	 * Création des agents aiguilleurs. Ils sont au nombre de 14 dans une configuration à 9 noeuds. Ce sont eux
	 * qui vont permettre aux agents mobiles de pouvoir se diriger vers un autre noeud (si la configuration le
	 * permet : n/2 et n/2+1) ou alors rentrer vers la base via le chemin qu'il a emprunté.
	 */
	protected void createAgentsAiguilleurs() {
		logger.log(Level.INFO, "Creation " + NB_AIGUILLEURS + " agents aiguilleurs.");
		PlatformController container = getContainerController();
		try {
			for (int i = 1; i < NB_AIGUILLEURS + 1; i++) {
				String localName = "agent_aiguilleurs_" + i + "_node";
				ArrayList<Object> args = new ArrayList<>();
				switch(i){
					case 1:
					case 2:
						args.add("final");
						args.add(compteurs.get(i-1).getLocalName());
						break;
					case 3:
					case 4:
					case 5:
						args.add("notFinal");
						args.add(compteurs.get(i-1).getLocalName());
						args.add(aiguilleurs.get(0).getLocalName());
						args.add(aiguilleurs.get(1).getLocalName());
						break;
					case 6:
					case 7:
						args.add("initial");
						args.add(aiguilleurs.get(2).getLocalName());
						args.add(aiguilleurs.get(3).getLocalName());
						break;
					case 8:
					case 9:
						args.add("initial");
						args.add(aiguilleurs.get(3).getLocalName());
						args.add(aiguilleurs.get(4).getLocalName());
				}
				AgentController guest = container.createNewAgent(localName, "src.ants_colony.AgentAiguilleur", args.toArray());
				guest.start();
				AID newAgent = new AID(localName, AID.ISLOCALNAME);
				aiguilleurs.add(newAgent);
				agents.add(newAgent);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Un problème est survenu lors de la mise en place des agents aiguilleurs...", e);
		}
	}

	protected static AID findByLocalName(String localname){
		for(AID ag : agents){
			if(ag.getLocalName().equals(localname)) // 
				return ag;
		}
		return null;
	}
}
