package traitement;

import java.util.ArrayList;
import java.util.HashMap;

import donnees.AbstractPierre;
import donnees.Coordonnee;
import donnees.Couleur;
import donnees.Pierre;
import traitement.Goban;


public class FinDePartie {

	private int taille_goban;
	
	private boolean BordHaut = false;
	private boolean BordDroit = false;
	private boolean BordGauche = false;
	private boolean BordBas = false;
	private boolean vivante = false;
	
	private Goban goban;
	
	public FinDePartie(int taille_goban, Goban goban) {
		this.taille_goban = taille_goban;
		this.goban = goban;
	}
	
	public void setChaineTwoEye(HashMap<Integer, Chaine> hmChaine, AbstractPierre[][] plateau) {
		
		ArrayList<Coordonnee> interVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> listeInterVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> finalList = new ArrayList<Coordonnee>();
		
		boolean ajouter = true;
		
		int compteur;
		int compteurYeux;
		int bordHaut = 0;
		int bordBas = taille_goban - 1;
		int bordGauche = 0;
		int bordDroit = taille_goban - 1;
		
		for(Chaine chaine : hmChaine.values()) {
			if(chaine.getChaine().size() >= 6) {
				for(AbstractPierre pierre : chaine.getChaine()) {
					interVide = GoPierre.intersectionVide(pierre, plateau, taille_goban);
					if (!interVide.isEmpty()) {
						for(Coordonnee coord : interVide) {
							listeInterVide.add(coord);
						}
					}
				}
			
				interVide.clear();
				interVide.addAll(listeInterVide);
				for(Coordonnee coord : listeInterVide) {
					compteur = 0;
					for(Coordonnee c : interVide) {
						if(coord.getX() == c.getX() && coord.getY() == c.getY()) {
							compteur ++;
						}
					}
					if(compteur >= 2) {
						for (Coordonnee fl : finalList) {
							if(fl.getX() == coord.getX() && fl.getY() == coord.getY()) {
								ajouter = false;
							}
						}
						if(ajouter) {
							finalList.add(coord);
						}
						ajouter = true;
					}
				}
				
				if(finalList.size() > 1) {
					compteurYeux = finalList.size();
					for(Coordonnee c : finalList) {
						if(c.getX() > bordHaut) {
							if(goban.existPierre(c.getX()-1, c.getY())) {
								if(goban.getPierre(c.getX()-1, c.getY()).getCouleur() != chaine.getCouleur()) {
									compteurYeux--;
								}
							}
							else {
								compteurYeux--;
							}
						}
						else if(c.getX() < bordBas) {
							if(goban.existPierre(c.getX()+1, c.getY())) {
								if(goban.getPierre(c.getX()+1, c.getY()).getCouleur() != chaine.getCouleur()) {
									compteurYeux--;
								}
							}
							else {
								compteurYeux--;
							}
						}
						else if(c.getY() > bordGauche) {
							if(goban.existPierre(c.getX(), c.getY()-1)) {
								if(goban.getPierre(c.getX(), c.getY()-1).getCouleur() != chaine.getCouleur()) {
									compteurYeux--;
								}
							}
							else {
								compteurYeux--;
							}
						}
						else if(c.getY() < bordDroit) {
							if(goban.existPierre(c.getX(), c.getY()+1)) {
								if(goban.getPierre(c.getX(), c.getY()+1).getCouleur() != chaine.getCouleur()) {
									compteurYeux--;
								}
							}
							else {
								compteurYeux--;
							}
						}
					}
					
					if(compteurYeux >= 2) {
						chaine.setTwoEyes(true);
					}
				}
			}
			
			interVide.clear();
			listeInterVide.clear();
			finalList.clear();
		}
	}
	
	public void pierreMorte (AbstractPierre[][] plateau, HashMap<Integer, Chaine> hmChaine) {
		
		int i, j;
		
		ArrayList<AbstractPierre> ListPierre = new ArrayList<AbstractPierre>();
		ArrayList<Integer> ListChaine = new ArrayList<Integer>();
		ArrayList<Coordonnee> listeInterVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> finalInterVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> InterVideDejaParcourue = new ArrayList<Coordonnee>();
		ArrayList<AbstractPierre> listeInterPleine = new ArrayList<AbstractPierre>();
		
		for(i=0;i<taille_goban;i++) {
			for(j=0;j<taille_goban;j++) {
				if(goban.existPierre(i, j)) {
					ListPierre.add(goban.getPierre(i, j));
				}
			}
		}
		
		for(AbstractPierre pierre : ListPierre) {
			
			if(pierre.hasChaine()) {
				if(!ListChaine.contains(pierre.getNomChaine())) {
					if(!hmChaine.get(pierre.getNomChaine()).getTwoEyes()) {
						
						for(AbstractPierre p : hmChaine.get(pierre.getNomChaine()).getChaine()) {
							setBord(p);
							listeInterVide = GoPierre.intersectionVide(p, plateau, taille_goban);
							
							for(Coordonnee c : listeInterVide) {
								if(!dejaParcourue(c, finalInterVide)) {
									setBord(c);
									finalInterVide.add(c);
								}
							}
							
							listeInterVide.clear();
						}
						
						InterVideDejaParcourue.addAll(finalInterVide);
						
						while (!vivante && finalInterVide.size() > 0) {
							listeInterVide.clear();
							finalInterVide.clear();
							
							for(Coordonnee dc : InterVideDejaParcourue) {
								setBord(dc);
								listeInterVide = GoPierre.intersectionVide(new Pierre(Couleur.NOIR, dc), plateau, taille_goban);
								
								for(Coordonnee c : listeInterVide) {   
									if(!dejaParcourue(c, finalInterVide) && !dejaParcourue(c, InterVideDejaParcourue)) {
										setBord(c);
										finalInterVide.add(c);
									}
								}
							}
							
							for(Coordonnee fc : finalInterVide) {
								InterVideDejaParcourue.add(fc);
							}

							setVivante();
						}
						
						if(!vivante) {
							for(Coordonnee c : InterVideDejaParcourue) {
								listeInterPleine = GoPierre.voisins(new Pierre(Couleur.NOIR, c), plateau, taille_goban);
								for(AbstractPierre p : listeInterPleine) {
									if(p.hasChaine()) {
										if(p.getCouleur() == pierre.getCouleur() && p.getNomChaine() != pierre.getNomChaine() && hmChaine.get(p.getNomChaine()).getTwoEyes()) {
											vivante = true;
										}
									}
								}
							}
						}

						
						if(vivante){
							System.out.println("chaine, " + pierre.getCouleur()  + " / " + pierre.getX() + "," + pierre.getY()+ " : vivante");
						}
						else {
							System.out.println("chaine, " + pierre.getCouleur()  + " / " + pierre.getX() + "," + pierre.getY()+ " : morte");
						}
						
						for(AbstractPierre p : hmChaine.get(pierre.getNomChaine()).getChaine()) {
							p.setVivante(vivante);
						}
						
						vivante = false;
						BordHaut = false;
						BordBas = false;
						BordGauche = false;
						BordDroit = false;
						
						listeInterVide.clear();
						finalInterVide.clear();
						InterVideDejaParcourue.clear();
					}
					
					else {
						vivante = true;
						for(AbstractPierre p : hmChaine.get(pierre.getNomChaine()).getChaine()) {
							p.setVivante(vivante);
						}
					}
					
					ListChaine.add(pierre.getNomChaine());
				}
			}
			
			else {
				setBord(pierre);
				finalInterVide = GoPierre.intersectionVide(pierre, plateau, taille_goban);
				

						
				InterVideDejaParcourue.addAll(finalInterVide);
						
				while (!vivante && finalInterVide.size() > 0) {
					listeInterVide.clear();
					finalInterVide.clear();
							
					for(Coordonnee dc : InterVideDejaParcourue) {
						setBord(dc);
						listeInterVide = GoPierre.intersectionVide(new Pierre(Couleur.NOIR, dc), plateau, taille_goban);
								
						for(Coordonnee c : listeInterVide) {   
							if(!dejaParcourue(c, finalInterVide) && !dejaParcourue(c, InterVideDejaParcourue)) {
								setBord(c);
								finalInterVide.add(c);
							}
						}
					}
							
					for(Coordonnee fc : finalInterVide) {
						InterVideDejaParcourue.add(fc);
					}

					setVivante();
				}
						
				if(!vivante) {
					for(Coordonnee c : InterVideDejaParcourue) {
						listeInterPleine = GoPierre.voisins(new Pierre(Couleur.NOIR, c), plateau, taille_goban);
						for(AbstractPierre p : listeInterPleine) {
							if(p.hasChaine()) {
								if(p.getCouleur() == pierre.getCouleur() && p.getNomChaine() != pierre.getNomChaine() && hmChaine.get(p.getNomChaine()).getTwoEyes()) {
									vivante = true;
								}
							}
						}
					}
				}
						
				if(vivante){
					System.out.println("pierre, " + pierre.getCouleur()  + " / " + pierre.getX() + "," + pierre.getY()+ " : vivante");
				}
				else {
					System.out.println("pierre, " + pierre.getCouleur()  + " / " + pierre.getX() + "," + pierre.getY()+ " : morte");
				}
				
				pierre.setVivante(vivante);
				
				vivante = false;
				BordHaut = false;
				BordBas = false;
				BordGauche = false;
				BordDroit = false;
				
				listeInterVide.clear();
				finalInterVide.clear();
				InterVideDejaParcourue.clear();
			}
		}
	}
	
	public void calculTerritoire(AbstractPierre[][] plateau, HashMap<Integer, Chaine> hmChaine) {
		
		int i, j;
		
		ArrayList<AbstractPierre> ListPierre = new ArrayList<AbstractPierre>();
		ArrayList<Integer> ListChaine = new ArrayList<Integer>();
		ArrayList<Coordonnee> listeInterVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> finalInterVide = new ArrayList<Coordonnee>();
		ArrayList<Coordonnee> InterVideDejaParcourue = new ArrayList<Coordonnee>();
		ArrayList<AbstractPierre> listeInterPleine = new ArrayList<AbstractPierre>();
		
		for(i=0;i<taille_goban;i++) {
			for(j=0;j<taille_goban;j++) {
				if(goban.existPierre(i, j)) {
					ListPierre.add(goban.getPierre(i, j));
				}
			}
		}
		for(AbstractPierre pierre : ListPierre) {
			if(pierre.hasChaine()) {				
				if(!ListChaine.contains(pierre.getNomChaine())) {				
					if(!hmChaine.get(pierre.getNomChaine()).getTwoEyes()) {
						
						for(AbstractPierre p : hmChaine.get(pierre.getNomChaine()).getChaine()) {
							setBord(p);
							listeInterVide = GoPierre.intersectionVide(p, plateau, taille_goban);
							
							for(Coordonnee c : listeInterVide) {
								if(!dejaParcourue(c, finalInterVide)) {
									setBord(c);
									finalInterVide.add(c);
								}
							}
							
							listeInterVide.clear();
						}
						
						InterVideDejaParcourue.addAll(finalInterVide);
					}
				}
			}
		}
	}
	
	public boolean dejaParcourue(Coordonnee nc, ArrayList<Coordonnee> InterVideDejaParcourue) {
		boolean ajouter = true; 
		for(Coordonnee c : InterVideDejaParcourue) {
			if(nc.getX() == c.getX() && nc.getY() == c.getY()) {
				ajouter = false;
			}
		}
		if(!ajouter) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setBord(AbstractPierre p) {
		if(p.getX() == 0) {
			BordHaut = true;
		}
		if (p.getX() == taille_goban-1) {
			BordBas = true;
		}
		if (p.getY() == 0) {
			BordGauche = true;
		}
		if (p.getY() == taille_goban-1) {
			BordDroit = true;
		}
	}
	
	public void setBord(Coordonnee c) {
		if(c.getX() == 0) {
			BordHaut = true;
		}
		if (c.getX() == taille_goban-1) {
			BordBas = true;
		}
		if (c.getY() == 0) {
			BordGauche = true;
		}
		if (c.getY() == taille_goban-1) {
			BordDroit = true;
		}
	}
	
	public void setVivante() {
		if(BordHaut && BordBas && BordGauche && BordDroit) {
			vivante = true;
		}
	}
}


