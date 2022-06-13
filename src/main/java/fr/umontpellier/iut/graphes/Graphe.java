package fr.umontpellier.iut.graphes;


import java.util.*;

public class Graphe {
    /**
     * matrice d'adjacence du graphe, un entier supérieur à 0 représentant la distance entre deux sommets
     * mat[i][i] = 0 pour tout i parce que le graphe n'a pas de boucle
     */
    private final int[][] mat;

    /**
     * Construit un graphe à n sommets
     *
     * @param n le nombre de sommets du graphe
     */
    public Graphe(int n) {
        mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = 0;
            }
        }
    }


    public Graphe(Graphe copie) {
        int[][] laCopie = copie.getMat();
        mat = new int[copie.nbSommets()][copie.nbSommets()];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                mat[i][j] = laCopie[i][j];
                mat[j][i] = laCopie[j][i];
            }
        }
    }

    public int[][] getMat() {
        return mat;
    }

    /**
     * @return le nombre de sommets
     */
    public int nbSommets() {
        return mat.length;
    }

    /**
     * Supprime l'arête entre les sommets i et j
     *
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     */
    public void supprimerArete(int i, int j) {
        mat[i][j] = 0;
        mat[j][i] = 0;
    }

    /**
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     * @param k la distance entre i et j (k>0)
     */
    public void ajouterArete(int i, int j, int k) {
        mat[i][j] = k;
        mat[j][i] = k;
    }

    /*** 
     * @return le nombre d'arête du graphe
     */
    public int nbAretes() {
        int nbAretes = 0;
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] > 0) {
                    nbAretes++;
                }
            }
        }
        return nbAretes;
    }

    /**
     * @param i un entier représentant un sommet
     * @param j un autre entier représentant un sommet
     * @return vrai s'il existe une arête entre i et j, faux sinon
     */
    public boolean existeArete(int i, int j) {
        return mat[i][j] > 0;
    }

    /**
     * @param v un entier représentant un sommet du graphe
     * @return la liste des sommets voisins de v
     */
    public ArrayList<Integer> voisins(int v) {
        ArrayList<Integer> voisins = new ArrayList<>();
        for (int j = 0; j < mat.length; j++) {
            if (mat[v][j] != 0) {
                voisins.add(j);
            }
        }
        return voisins;
    }

    /**
     * @return une chaîne de caractères permettant d'afficher la matrice mat
     */
    public String toString() {
        StringBuilder res = new StringBuilder("\n");
        for (int[] ligne : mat) {
            for (int j = 0; j < mat.length; j++) {
                String x = String.valueOf(ligne[j]);
                res.append(x);
            }
            res.append("\n");
        }
        return res.toString();
    }

    /**
     * Calcule la classe de connexité du sommet v
     *
     * @param v un entier représentant un sommet
     * @return une liste d'entiers représentant les sommets de la classe de connexité de v
     */
    public ArrayList<Integer> calculerClasseDeConnexite(int v) {
        ArrayList<Integer> marqueBleu = new ArrayList<>();
        ArrayList<Integer> marqueRouge = new ArrayList<>();
        marqueBleu.add(v);
        while (marqueBleu.size() != 0) {
            int x = marqueBleu.remove(0);
            marqueRouge.add(x);
            for (int ex : voisins(x)) {
                if (!marqueRouge.contains(ex) && !marqueBleu.contains(ex)) {
                    marqueBleu.add(ex);
                }
            }
        }
        Collections.sort(marqueRouge);
        return marqueRouge;
    }

    /**
     * @return la liste des classes de connexité du graphe
     */
    public ArrayList<ArrayList<Integer>> calculerClassesDeConnexite() {
        ArrayList<ArrayList<Integer>> lcc = new ArrayList<>();
        ArrayList<Integer> sommetsAParcourir = new ArrayList<>();
        for (int i = 0; i < mat.length; i++) {
            sommetsAParcourir.add(i);
        }
        while (!sommetsAParcourir.isEmpty()) {
            Integer sommet = sommetsAParcourir.remove(0);
            ArrayList<Integer> clcSommet = calculerClasseDeConnexite(sommet);
            for (Integer i : clcSommet) {
                sommetsAParcourir.remove(i);
            }
            lcc.add(clcSommet);
        }
        return lcc;
    }

    /**
     * @return le nombre de classes de connexité
     */
    public int nbCC() {
        return calculerClassesDeConnexite().size();
    }

    /**
     * @param u un entier représentant un sommet
     * @param v un entie représentant un sommet
     * @return vrai si (u,v) est un isthme, faux sinon
     */
    public boolean estUnIsthme(int u, int v) {
        Graphe test = new Graphe(this);
        test.supprimerArete(u, v);
        return this.nbCC() != test.nbCC();
    }


    /**
     * Calcule le plus long chemin présent dans le graphe
     *
     * @return une liste de sommets formant le plus long chemin dans le graphe
     */
    public ArrayList<Integer> plusLongChemin() {
        ArrayList<Integer> path = new ArrayList();
        ArrayList<Integer> queue = new ArrayList();

        for (int i = 0; i < nbSommets(); i++)
            queue.add(i);
        while (!queue.isEmpty()) {
            ArrayList<Integer> startPath = new ArrayList();
            startPath.add(queue.get(0));
            ArrayList<Integer> temp = plusLongCheminAux(startPath, queue.get(0));
            if (getDistance(temp) > getDistance(path)) {
                path = temp;
            }
            queue.remove(0);
        }

        return path;

    }

    public Set<ArrayList<Integer>> getArretes() {
        Set<ArrayList<Integer>> listeArretes = new HashSet<>();
        Graphe test = new Graphe(this);
        for (int i = 0; i < nbSommets(); i++) {
            for (int j = 0; j < nbSommets(); j++) {
                if (test.existeArete(i, j)) {
                    ArrayList arrete = new ArrayList<>();
                    arrete.add(i);
                    arrete.add(j);
                    test.supprimerArete(i, j);
                    listeArretes.add(arrete);
                }
            }
        }
        return listeArretes;
    }

    public ArrayList<Integer> plusLongCheminAux(ArrayList<Integer> path, int coordinate) {
        ArrayList<Integer> adjacent = voisins(coordinate);
        if (adjacent.isEmpty()) {
            return path;
        } else {
            ArrayList<Integer> currPath = new ArrayList();
            for (Integer i : adjacent) {
                Graphe copy = new Graphe(this);
                copy.supprimerArete(coordinate, i);
                ArrayList<Integer> nextPath = new ArrayList<>(path);
                nextPath.add(i);
                ArrayList<Integer> maybePath = copy.plusLongCheminAux(nextPath, i);
                if (getDistance(currPath) < getDistance(maybePath)) {
                    currPath = maybePath;
                }
            }
            return currPath;
        }
    }

    public ArrayList<Integer> getEulerianPath() {
        //TODO : Parcourir le graph de cycle en cycle jusqu'à pouvoir les rattacher,
        // obtenant un parcours sur chaque arrêtes visitées qu'une seule fois cf :
        // https://math.unm.edu/~loring/links/discrete_f05/euler.pdf
        return null;
    }

    public int getDistance(ArrayList<Integer> path) {
        int distance = 0;
        for (int i = 1; i < path.size(); i++) {
            if (existeArete((path.get(i - 1)), path.get(i))) {
                distance += mat[path.get(i - 1)][path.get(i)];
            }
        }
        return distance;
    }

    public int nbSommetsImpairs() {
        int nb = 0;
        for (int i = 0; i < mat.length; i++) {
            int x = voisins(i).size();
            if (x % 2 != 0) {
                nb++;
            }
        }
        return nb;
    }

    /**
     * @return vrai s'il existe un parcours eulérien dans le graphe, faux sinon
     */
    public boolean existeParcoursEulerien() {
        return nbCC() == 1 && nbSommetsImpairs() <= 2;
    }

    /**
     * @return vrai si le graphe est un arbre, faux sinon
     */
    public boolean estUnArbre() {
        return nbCC() == 1 && nbAretes() < nbSommets();
    }

}