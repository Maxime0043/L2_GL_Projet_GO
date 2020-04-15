package traitement.moteurs;

import java.util.ArrayList;

import donnees.Cercle;
import donnees.Coordonnee;
import donnees.Couleur;
import donnees.Joueur;
import gui.Go;
import traitement.Didacticiel;
import traitement.FinDePartie;
import traitement.Goban;

/**
 * 
 * 
 * @author Maxime, Micael et Houssam
 *
 */
public class Moteur /*implements Runnable*/ {
	private Goban goban;
	private Didacticiel didacticiel;
	private MoteurJoueur moteur_joueur;
	private MoteurOrdi moteur_ordi;
	private MoteurPierre moteur_pierre;
	private FinDePartie fin;

	private int nb_joueurs;
	private int pass_compteur = 0;
	private int x = -1, y = -1;
	
	private String description;

	private ArrayList<Cercle> position_jouable;
	
	private boolean didacticiel_fini = false;
//	private boolean is_tour_ordi = false;
//	private Moteur instance = this;
	
	public Moteur(int cellule, int taille_goban, int nb_joueur, int nb_ordi, boolean isDidacticiel) {
		goban = new Goban(taille_goban);
		moteur_joueur = new MoteurJoueur(nb_joueur, nb_ordi, isDidacticiel);
		moteur_pierre = new MoteurPierre(moteur_joueur, goban, cellule, taille_goban, nb_joueur, nb_ordi, isDidacticiel);
		fin = new FinDePartie(taille_goban, goban, moteur_joueur, moteur_pierre);
		
		if(nb_ordi > 0) {
			moteur_ordi = new MoteurOrdi(moteur_joueur, moteur_pierre, goban, taille_goban, 2);
		}
		
		nb_joueurs = nb_joueur + nb_ordi; 
		
		didacticiel = null;
		position_jouable = null;
		
		if(isDidacticiel) {
			didacticiel = new Didacticiel(this, moteur_pierre);
			
			Go.logger.debug("D�but du Didacticiel");
			
			position_jouable = new ArrayList<Cercle>();
			didacticiel.chargeLevel();
		}
		
		initCoord();
		
//		Thread moteurThread = new Thread(instance);
//		moteurThread.start();
	}
	
//	@Override
	public void run() {
//		boolean isRunning = true;
//		
//		while (isRunning) {
//			try {
//				Thread.sleep(ParametrePartie.FPS);
//				
//			} catch (InterruptedException e) {
//				System.out.println(e.getMessage());
//			}
//
//			if (isRunning) {
				if(currentJoueur().isOrdi()) {
					long startTime = System.currentTimeMillis();
					
					System.out.println("-------Ordi D�but--------" + moteur_joueur.currentCouleur() + "\n");
					moteur_ordi.jouer();
					System.out.println("\n-------Ordi Fin--------");
					
//					moteur_pierre.addPierre(moteur_ordi.getCoup());
					
					System.out.println("Temps ordi: " + (System.currentTimeMillis() - startTime) + "\n\n");
				}
				
				else {
					if(x != -1 && y != -1) {
						clicEvent(x, y);
						initCoord();
					}
				}
//			}
//		}
	}
	
	public void reinitGoban() {
		goban.initPlateau();
		setPoseMegaPierre(false);
		getCercleList().clear();
		changeJoueur();
	}
	
	private void initPassCompteur() {
		pass_compteur = 0;
	}
	
	public void passer() {
		moteur_joueur.changeJoueur();
		pass_compteur++;
		
		if(pass_compteur == nb_joueurs) {
			initPassCompteur();
			fin.initFin(goban.getPlateau(), goban.getHmChaine());
			System.out.println("Fini");
		}
	}
	
//	public void tourOrdi() {
//		if(!is_tour_ordi) {
//			System.out.println("-------Ordi D�but--------");
//			is_tour_ordi = true;
//			moteur_pierre.setTourOrdi(true);
//		}
//		
//		else {
//			System.out.println("-------Ordi Fin--------");
//			is_tour_ordi = false;
//			moteur_pierre.setTourOrdi(false);
//		}
//	}
	
	public void survoleZone(int coordX, int coordY) {
		moteur_pierre.survoleZone(coordX, coordY);
	}
	
	private void initCoord() {
		x = -1;
		y = -1;
	}
	
	public void setCoord(int coordX, int coordY) {
		x = coordX;
		y = coordY;
	}
	
	public void clicEvent(int coordX, int coordY) {
		moteur_pierre.clicEvent(coordX, coordY);
		initPassCompteur();
	}
	
	public int[] getScores() {
		int[] scores = new int[nb_joueurs];
		Joueur[] joueurs = getJoueurs();
		
		for(int i = 0 ; i < nb_joueurs ; i++) {
			scores[i] = joueurs[i].getScore();
		}
		
		return scores;
	}
	
	public void changeLevel(boolean suivant) {
		if(didacticiel != null) {
			position_jouable.clear();
			
			if(!suivant) {
				didacticiel.changeLevel(suivant);
			}
			
			else if(didacticiel.getLevel() < didacticiel.getNbLevels()) {
				didacticiel.changeLevel(suivant);
			}
			
			else {
				didacticiel_fini = true;
				position_jouable = null;
			}
		}
	}
	
	public void resetLevel() {
		if(didacticiel != null) {
			position_jouable.clear();
			
			didacticiel.resetLevel();
		}
	}
	
	public ArrayList<Coordonnee> getHoshis() {
		return goban.getHoshis();
	}
	
	public void initPositionJouable(Coordonnee coord, Couleur couleur, boolean isMegaPierre) {
		position_jouable.add(new Cercle(coord, couleur, isMegaPierre));		
	}
	
	public ArrayList<Cercle> getPositionJouable() {
//		if(position_jouable != null && goban.existPierre(position_jouable.getX(), position_jouable.getY())) {
//			position_jouable = null;
//		}
		
		return position_jouable;
	}
	
	public void setPositionJouable() {
		moteur_pierre.setPositionJouable(position_jouable);
	}
	
	public boolean isDidacticielFini() {
		return didacticiel_fini;
	}
	
	public int getCurrentLevel() {
		return didacticiel.getLevel();
	}
	
	public int getNbLevel() {
		return didacticiel.getNbLevels();
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public ArrayList<Cercle> getCercleList(){
		return moteur_pierre.getCercleList();
	}
	
	public Cercle getSurvoleCercle() {
		return moteur_pierre.getSurvoleCercle();
	}
	
	public Couleur currentCouleur() {
		return moteur_joueur.currentCouleur();
	}
	
	public Joueur[] getJoueurs(){
		return moteur_joueur.getJoueurs();
	}
	
	public Joueur currentJoueur() {
		return moteur_joueur.currentJoueur();
	}
	
	public void changeJoueur() {
		moteur_joueur.changeJoueur();
	}
	
	public boolean isMegaPierre() {
		return moteur_pierre.isMegaPierre();
	}
	
	public void setPoseMegaPierre(boolean bool) {
		moteur_pierre.setPoseMegaPierre(bool);
	}
	
	public boolean canPlayMegaPierre() {
		return moteur_joueur.canPlayMegaPierre();
	}
}
