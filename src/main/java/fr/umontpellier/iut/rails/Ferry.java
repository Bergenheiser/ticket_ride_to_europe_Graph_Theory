package fr.umontpellier.iut.rails;

public class Ferry extends Route {
    /**
     * Nombre de locomotives qu'un joueur doit payer pour capturer le ferry
     */
    private int nbLocomotives;

    public Ferry(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur, int nbLocomotives) {
        super(ville1, ville2, longueur, couleur);
        this.nbLocomotives = nbLocomotives;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s (%d, %s, %d)]", getVille1(), getVille2(), getLongueur(), getCouleur(),
                nbLocomotives);
    }

    @Override
    public boolean peutCapturerParJoueur(Joueur joueur) {
        return joueur.peutPayerCartesWagon(getLongueur() - nbLocomotives, getCouleur(), nbLocomotives);
    }

    public boolean capturerParJoueur(Joueur joueur) {
        joueur.log(joueur.toLog() + " capture le ferry " + toLog());
        joueur.payerCartesWagon(
                getLongueur() - nbLocomotives,
                getCouleur(),
                nbLocomotives,
                String.format("Défaussez des cartes wagon pour capturer le ferry %s - %s.", getVille1().getNom(),
                        getVille2().getNom()));
        joueur.log("   ... défausse " + CouleurWagon.listToLog(joueur.getCartesWagonPosees()));
        joueur.defausserCartesWagonPosees();
        return true;
    }
}
