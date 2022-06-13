package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Tunnel extends Route {
    public Tunnel(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur) {
        super(ville1, ville2, longueur, couleur);
    }

    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }

    @Override
    public boolean capturerParJoueur(Joueur joueur) {
        joueur.log(joueur.toLog() + " capture le tunnel " + toLog());
        // le joueur défausse le coût initial du tunnel
        joueur.payerCartesWagon(
                getLongueur(), getCouleur(), 0,
                String.format("Défaussez des cartes wagon pour capturer le tunnel %s - %s.",
                        getVille1().getNom(),
                        getVille2().getNom()));
        joueur.log("   ... pose " + CouleurWagon.listToLog(joueur.getCartesWagonPosees()));

        // la couleur du tunnel est la couleur des cartes "simples" que le joueur a
        // posées pour construire le tunnel (ou LOCOMOTIVE) si aucune couleur simple n'a
        // été jouée
        CouleurWagon couleurTunnel = CouleurWagon.LOCOMOTIVE;
        for (CouleurWagon c : joueur.getCartesWagonPosees()) {
            if (c != CouleurWagon.LOCOMOTIVE) {
                couleurTunnel = c;
                break;
            }
        }

        // déterminer le coût supplémentaire du tunnel en retournant les 3 premières
        // cartes de la pioche
        int coutTunnel = 0;
        ArrayList<CouleurWagon> cartesRetournees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CouleurWagon c = joueur.getJeu().piocherCarteWagon();
            if (c == null) {
                break;
            }
            cartesRetournees.add(c);
            if (c == CouleurWagon.LOCOMOTIVE || c == couleurTunnel) {
                coutTunnel++;
            }
        }
        joueur.log("   ... retourne " + CouleurWagon.listToLog(cartesRetournees));
        // défausser toutes les cartes retournées
        while (!cartesRetournees.isEmpty()) {
            joueur.getJeu().defausserCarteWagon(cartesRetournees.remove(0));
        }

        // le joueur doit payer le coût supplémentaire pour capturer le tunnel (mais
        // peut abandonner et récupérer les cartes)
        while (coutTunnel > 0) {
            Set<String> optionsPossibles = new HashSet<>();
            if (joueur.hasCarteWagon(CouleurWagon.LOCOMOTIVE)) {
                optionsPossibles.add(CouleurWagon.LOCOMOTIVE.name());
            }
            if (joueur.hasCarteWagon(couleurTunnel)) {
                optionsPossibles.add(couleurTunnel.name());
            }
            String choix = joueur.choisir(
                    String.format("Vous devez défausser %d cartes wagon supplémentaires pour construire le tunnel.",
                            coutTunnel),
                    optionsPossibles,
                    new ArrayList<>(), true);
            if (choix.equals("")) {
                // annulation de la capture
                break;
            } else {
                joueur.poserCarteWagon(CouleurWagon.valueOf(choix));
                coutTunnel -= 1;
            }
        }
        if (coutTunnel == 0) {
            // le tunnel est capturé
            joueur.log("   ... défausse " + CouleurWagon.listToLog(joueur.getCartesWagonPosees()));
            joueur.defausserCartesWagonPosees();
            return true;
        } else {
            // le tunnel n'est pas capturé, les cartes sont rendues au joueur
            joueur.log("   ... abandonne la capture du tunnel");
            joueur.prendreCartesWagonPosees();
            return false;
        }
    }
}
