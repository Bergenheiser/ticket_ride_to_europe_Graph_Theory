package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Représentation des couleurs du jeu utilisées pour les cartes wagon et les routes
 */
public enum CouleurWagon {
    NOIR, BLANC, JAUNE, ROUGE, ORANGE, BLEU, VERT, ROSE, GRIS, LOCOMOTIVE;

    @Override
    public String toString() {
        return switch (this) {
            case NOIR -> "Noir";
            case BLANC -> "Blanc";
            case JAUNE -> "Jaune";
            case ROUGE -> "Rouge";
            case ORANGE -> "Orange";
            case BLEU -> "Bleu";
            case VERT -> "Vert";
            case ROSE -> "Rose";
            case GRIS -> "Gris"; // représente un couleur indéterminée
            case LOCOMOTIVE -> "Locomotive"; // peut remplacer n'importe quelle couleur
        };
    }

    public String toLog() {
        return String.format("<img class=\"couleur\" src=\"images/symbole-%s.png\"><span class=\"couleur %s\">%s</span>", name(), name().toLowerCase(), this);
    }

    /**
     * Renvoie la liste des couleurs "simples" c'est-à-dire sans LOCOMOTIVE ni GRIS
     */
    public static ArrayList<CouleurWagon> getCouleursSimples() {
        return new ArrayList<>(List.of(NOIR, BLANC, JAUNE, ROUGE, ORANGE, BLEU, VERT, ROSE));
    }

    /**
     * Renvoie la représentation sous forme d'une chaîne de caractères d'une liste
     * non ordonnée de couleurs.
     * 
     * La chaîne est constituée du nom de chaque couleur qui apparaît dans la liste,
     * suivie éventuellement d'une chaîne de la forme " x n" où n est le nombre de
     * fois que la couleur apparaît dans la liste, si n > 1. Les couleurs sont
     * séparées par des virgules.
     * 
     * @param liste une liste de couleurs (considérée comme non ordonnée)
     * @return une chaîne de caractères décrivant les éléments qui apparaissent dans
     *         la liste
     */
    public static String listToString(List<CouleurWagon> liste) {
        StringJoiner joiner = new StringJoiner(", ");
        for (CouleurWagon c : CouleurWagon.values()) {
            int count = Collections.frequency(liste, c);
            if (count == 1) {
                joiner.add(c.toString());
            } else if (count > 1) {
                joiner.add(c.toString() + " x" + count);
            }
        }
        return joiner.toString();
    }

    public static String listToLog(List<CouleurWagon> liste) {
        StringJoiner joiner = new StringJoiner(", ");
        for (CouleurWagon c : CouleurWagon.values()) {
            int count = Collections.frequency(liste, c);
            if (count == 1) {
                joiner.add(c.toLog());
            } else if (count > 1) {
                joiner.add(c.toLog() + " x" + count);
            }
        }
        return joiner.toString();
    }

    /**
     * Renvoie un tableau associatif indiquant pour chaque couleur le nombre
     * d'éléments de cette couleur dans la liste passée en paramètre.
     * 
     * La valeur associée à la couleur GRIS est la plus grande valeur des autres
     * couleurs normales (hors LOCOMOTIVE).
     * 
     * @param couleurs une liste de couleurs (ne devrait pas contenir GRIS)
     * @return un tableau associatif de la forme {@code {couleur: nombre
     *         d'éléments}}
     */
    public static Map<CouleurWagon, Integer> compteur(List<CouleurWagon> couleurs) {
        HashMap<CouleurWagon, Integer> c = new HashMap<>();
        // initialisation
        for (CouleurWagon couleur : CouleurWagon.values()) {
            c.put(couleur, 0);
        }
        // décompte des valeurs dans la liste `couleurs`
        for (CouleurWagon couleur : couleurs) {
            c.put(couleur, c.get(couleur) + 1);
        }

        // la valeur correspondant à GRIS est le max des autres valeurs hors LOCOMOTIVE
        for (CouleurWagon couleur : CouleurWagon.values()) {
            if (couleur != CouleurWagon.LOCOMOTIVE && couleur != CouleurWagon.GRIS) {
                c.put(CouleurWagon.GRIS, Math.max(c.get(CouleurWagon.GRIS), c.get(couleur)));
            }
        }
        return c;
    }
}
