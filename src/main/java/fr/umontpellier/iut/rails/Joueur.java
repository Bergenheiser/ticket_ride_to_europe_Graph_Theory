package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.graphes.Graphe;

import java.util.*;
import java.util.stream.Collectors;

public class Joueur {
    public static enum Couleur {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private Jeu jeu;
    /**
     * Nom du joueur
     */
    private String nom;
    /**
     * Couleur du joueur (pour représentation sur le plateau)
     */
    private Couleur couleur;
    /**
     * Nombre de gares que le joueur peut encore poser sur le plateau
     */
    private int nbGares;
    /**
     * Nombre de wagons que le joueur peut encore poser sur le plateau
     */
    private int nbWagons;
    /**
     * Liste des missions à réaliser pendant la partie
     */
    private List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private List<CouleurWagon> cartesWagon;
    /**
     * Liste temporaire de cartes wagon que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'une gare
     */
    private List<CouleurWagon> cartesWagonPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées)
     */
    private int score;

    private Graphe graphe ;

    public Joueur(String nom, Jeu jeu, Couleur couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        this.nbGares = 3;
        this.nbWagons = 45;
        this.cartesWagon = new ArrayList<>();
        this.cartesWagonPosees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.score = 12; // chaque gare non utilisée vaut 4 points
        this.graphe=new Graphe(jeu.getVilles().size());
    }
    public Joueur(Joueur j ){
        this.nom=j.getNom();
        this.jeu=j.getJeu();
        this.couleur=j.getCouleur();
        this.nbGares=j.getNbGares();
        this.nbWagons=j.getNbWagons();
        this.cartesWagon=j.getCartesWagon();
        this.cartesWagonPosees=j.getCartesWagonPosees();
        this.destinations=j.getDestinations();
        this.score=j.getScore();
    }

    public void setGraphe(Graphe g){
        this.graphe=g;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public void setNom(String nom){
        this.nom=nom;
    }

    public String getNom() {
        return nom;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getNbGares() {
        return nbGares;
    }

    public int getNbWagons() {
        return nbWagons;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public List<CouleurWagon> getCartesWagon() {
        return cartesWagon;
    }

    public List<CouleurWagon> getCartesWagonPosees() {
        return cartesWagonPosees;
    }

    public int getScore() {
        return score;
    }

    /**
     * Met à jour le score du joueur
     *
     * @param n
     */
    public void setScore(int n){
        this.score=n;
    }

    /**
     * Teste si le joueur a une carte wagon de la couleur passée en argument
     *
     * @param c une couleur de carte wagon
     * @return true si le joueur a une carte de la couleur indiquée
     */
    public boolean hasCarteWagon(CouleurWagon c) {
        return cartesWagon.contains(c);
    }

    /**
     * Déplace une carte wagon de la main du joueur vers sa pile de cartes posées
     *
     * @param c
     */
    public void poserCarteWagon(CouleurWagon c) {
        if (cartesWagon.remove(c)) {
            cartesWagonPosees.add(c);
        }
    }

    /**
     * Défausse toutes les cartes wagon posées devant le joueur (elles sont mises
     * dans la pile de défausse du jeu)
     */
    public void defausserCartesWagonPosees() {
        while (!cartesWagonPosees.isEmpty()) {
            jeu.defausserCarteWagon(cartesWagonPosees.remove(0));
        }
    }

    /**
     * Reprend les cartes wagon posées devant le joueur (elles sont remises dans la
     * pile cartesWagon)
     */
    public void prendreCartesWagonPosees() {
        cartesWagon.addAll(cartesWagonPosees);
        cartesWagonPosees.clear();
    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     * <p>
     * Cette méthode lit les entrées du jeu ({@code Jeu.lireligne()}) jusqu'à ce
     * qu'un choix valide (un élément de {@code choix} ou de {@code boutons} ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     * <p>
     * Si l'ensemble des choix valides ({@code choix} + {@code boutons}) ne comporte
     * qu'un seul élément et que {@code canPass} est faux, l'unique choix valide est
     * automatiquement renvoyé sans lire l'entrée de l'utilisateur.
     * <p>
     * Si l'ensemble des choix est vide, la chaîne vide ("") est automatiquement
     * renvoyée par la méthode (indépendamment de la valeur de {@code canPass}).
     * <p>
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     * <p>
     * {@code
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez vous faire ceci ?", choix, new ArrayList<>(), false);
     * }
     * <p>
     * <p>
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     * <p>
     * {@code
     * List<String> boutons = Arrays.asList("1", "2", "3");
     * String input = choisir("Choisissez un nombre.", new ArrayList<>(), boutons, false);
     * }
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur qui doivent être
     *                    représentés par des boutons sur l'interface graphique.
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élement de {@code choix}, ou de
     * {@code boutons} ou la chaîne vide)
     */
    public String choisir(String instruction, Collection<String> choix, Collection<String> boutons,
                          boolean peutPasser) {
        // on retire les doublons de la liste des choix
        HashSet<String> choixDistincts = new HashSet<>();
        choixDistincts.addAll(choix);
        choixDistincts.addAll(boutons);

        // Aucun choix disponible
        if (choixDistincts.isEmpty()) {
            return "";
        } else {
            // Un seul choix possible (renvoyer cet unique élément)
            if (choixDistincts.size() == 1 && !peutPasser)
                return choixDistincts.iterator().next();
            else {
                String entree;
                // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
                while (true) {
                    jeu.prompt(instruction, boutons, peutPasser);
                    entree = jeu.lireLigne();
                    // si une réponse valide est obtenue, elle est renvoyée
                    if (choixDistincts.contains(entree) || (peutPasser && entree.equals("")))
                        return entree;
                }
            }
        }
    }

    /**
     * Ajoute une carte wagon à la main du joueur
     *
     * @param c la carte à ajouter
     */
    public void ajouterCarteWagon(CouleurWagon c) {
        cartesWagon.add(c);
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Gares: %d, Wagons: %d", nbGares, nbWagons));
        joiner.add("  Destinations: "
                + destinations.stream().map(Destination::toString).collect(Collectors.joining(", ")));
        joiner.add("  Cartes wagon: " + CouleurWagon.listToString(cartesWagon));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    /**
     * Renvoie une représentation du joueur sous la forme d'un objet Java simple
     * (POJO)
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        data.put("couleur", couleur);
        data.put("score", score);
        data.put("nbGares", nbGares);
        data.put("nbWagons", nbWagons);
        data.put("estJoueurCourant", this == jeu.getJoueurCourant());
        data.put("destinations", destinations.stream().map(Destination::asPOJO).collect(Collectors.toList()));
        data.put("cartesWagon", cartesWagon.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        data.put("cartesWagonPosees",
                cartesWagonPosees.stream().sorted().map(CouleurWagon::name).collect(Collectors.toList()));
        return data;
    }

    /**
     * Propose une liste de cartes destinations, parmi lesquelles le joueur doit en
     * garder un nombre minimum n.
     * <p>
     * Tant que le nombre de destinations proposées est strictement supérieur à n,
     * le joueur peut choisir une des destinations qu'il retire de la liste des
     * choix, ou passer (en renvoyant la chaîne de caractères vide).
     * <p>
     * Les destinations qui ne sont pas écartées sont ajoutées à la liste des
     * destinations du joueur. Les destinations écartées sont renvoyées par la
     * fonction.
     *
     * @param destinationsPossibles liste de destinations proposées parmi lesquelles
     *                              le joueur peut choisir d'en écarter certaines
     * @param n                     nombre minimum de destinations que le joueur
     *                              doit garder
     * @return liste des destinations qui n'ont pas été gardées par le joueur
     */
    public List<Destination> choisirDestinations(List<Destination> destinationsPossibles, int n) {
        List<Destination> defaussees = new ArrayList<>();
        while (destinationsPossibles.size() > n) {
            // Préparer les boutons à afficher
            Collection<String> boutons = new ArrayList<>();
            for (Destination d : destinationsPossibles) {
                boutons.add(d.getNom());
            }

            // Attendre un choix de l'utilisateur
            String choix = choisir(
                    "Choisissez les destinations à défausser.",
                    new ArrayList<>(),
                    boutons,
                    true);
            if (choix.equals("")) {
                break;
            }

            // Défausser la destination choisie
            for (Destination m : destinationsPossibles) {
                if (m.getNom().equals(choix)) {
                    destinationsPossibles.remove(m);
                    defaussees.add(m);
                    break;
                }
            }
        }
        destinations.addAll(destinationsPossibles);
        return defaussees;
    }

    /**
     * Exécute un tour de jeu du joueur.
     * <p>
     * Cette méthode attend que le joueur choisisse une des options suivantes :
     * - le nom d'une carte wagon face visible à prendre ;
     * - le nom "GRIS" pour piocher une carte wagon face cachée s'il reste des
     * cartes à piocher dans la pile de pioche ou dans la pile de défausse ;
     * - la chaîne "destinations" pour piocher des cartes destination ;
     * - le nom d'une ville sur laquelle il peut construire une gare (ville non
     * prise par un autre joueur, le joueur a encore des gares en réserve et assez
     * de cartes wagon pour construire la gare) ;
     * - le nom d'une route que le joueur peut capturer (pas déjà capturée, assez de
     * wagons et assez de cartes wagon) ;
     * - la chaîne de caractères vide pour passer son tour
     * <p>
     * Lorsqu'un choix valide est reçu, l'action est exécutée (il est possible que
     * l'action nécessite d'autres choix de la part de l'utilisateur, comme par
     * exemple choisit les cartes wagon à défausser pour capturer une route ou
     * construire une gare, choisir les destinations à défausser, etc.)
     */
    public void jouerTour() {
        log("<span class=\"tour\">Tour de " + toLog() + "</span>");
        Set<String> optionsPossibles = new HashSet<>();
        // piocher une mission
        if (!jeu.pileDestinationsEstVide()) {
            optionsPossibles.add("destinations");
        }

        // prendre des cartes wagon
        Set<String> couleurs = new HashSet<>();
        for (CouleurWagon c : jeu.getCartesWagonVisibles()) {
            optionsPossibles.add(c.name());
            couleurs.add(c.name());
        }
        if (!jeu.pileCartesWagonEstVide()) {
            optionsPossibles.add(CouleurWagon.GRIS.name());
            couleurs.add(CouleurWagon.GRIS.name());
        }

        // capturer une route
        HashMap<String, Route> routes = new HashMap<>();
        for (Route route : jeu.getRoutes()) {
            if (route.getProprietaire() == null && route.getLongueur() <= nbWagons
                    && route.peutCapturerParJoueur(this)) {
                optionsPossibles.add(route.getNom());
                routes.put(route.getNom(), route);
            }
        }

        // construire une ville
        HashMap<String, Ville> villes = new HashMap<>();
        if (nbGares > 0 && peutPayerCartesWagon(4 - nbGares, CouleurWagon.GRIS, 0)) {
            for (Ville ville : jeu.getVilles()) {
                if (ville.getProprietaire() == null) {
                    optionsPossibles.add(ville.getNom());
                    villes.put(ville.getNom(), ville);
                }
            }
        }

        String choix = choisir("Début du tour.", optionsPossibles, new ArrayList<>(), true);

        if (choix.equals("destinations")) {
            prendreDestinations();
        } else if (couleurs.contains(choix)) {
            prendreCartesWagons(CouleurWagon.valueOf(choix));
        } else if (routes.containsKey(choix)) {
            capturerRoute(routes.get(choix));
        } else if (villes.containsKey(choix)) {
            construireGare(villes.get(choix));
        }
    }

    /**
     * Exécute l'action "prendre des cartes wagon" pendant le tour d'un joueur
     *
     * @param couleur couleur de la première carte à prendre (GRIS si la première
     *                carte est prise dans la pioche)
     */
    private void prendreCartesWagons(CouleurWagon couleur) {
        if (couleur == CouleurWagon.GRIS) {
            log(toLog() + " pioche une carte wagon");
            cartesWagon.add(jeu.piocherCarteWagon());
        } else {
            log(toLog() + " prend une carte wagon " + couleur.toLog());
            cartesWagon.add(couleur);
            jeu.retirerCarteWagonVisible(couleur);
        }

        if (couleur != CouleurWagon.LOCOMOTIVE) {
            // le joueur peut prendre une autre carte (pas Locomotive)
            List<String> choixPossibles = new ArrayList<>();
            for (CouleurWagon c : jeu.getCartesWagonVisibles()) {
                if (c != CouleurWagon.LOCOMOTIVE) {
                    choixPossibles.add(c.name());
                }
            }
            if (!jeu.pileCartesWagonEstVide()) {
                choixPossibles.add(CouleurWagon.GRIS.name());
            }

            String choix = choisir(
                    "Vous pouvez prendre une autre carte wagon.",
                    choixPossibles,
                    new ArrayList<>(), true);
            if (choix.equals(CouleurWagon.GRIS.name())) {
                log(toLog() + " pioche une carte wagon");
                cartesWagon.add(jeu.piocherCarteWagon());
            } else if (choixPossibles.contains(choix)) {
                couleur = CouleurWagon.valueOf(choix);
                log(toLog() + " prend une carte wagon " + couleur.toLog());
                cartesWagon.add(couleur);
                jeu.retirerCarteWagonVisible(couleur);
            }
        }
    }

    /**
     * Détermine si le joueur a suffisamment de cartes wagon en main pour payer le
     * prix indiqué en paramètres.
     * <p>
     * Exemple 1: pour déterminer si un joueur peut construire un ferry d'une
     * longueur totale de 5 qui nécessite 1 locomotive, il faut appeler
     * {@code peutPayerCartesWagon(4, Couleur.GRIS, 1)}
     * <p>
     * Exemple 2: pour déterminer si un joueur peut construire une route verte de
     * longueur 3, il faut appeler
     * {@code peutPayerCartesWagon(3, Couleur.VERT, 0)}
     *
     * @param nbCouleur     nombre de cartes demandées de la couleur indiquée
     * @param couleur       couleur demandée
     * @param nbLocomotives nombre de locomotives supplémentaires demandées
     * @return
     */
    public boolean peutPayerCartesWagon(int nbCouleur, CouleurWagon couleur, int nbLocomotives) {
        Map<CouleurWagon, Integer> compteur = CouleurWagon.compteur(cartesWagon);
        return compteur.get(CouleurWagon.LOCOMOTIVE) >= nbLocomotives
                && compteur.get(couleur) + compteur.get(CouleurWagon.LOCOMOTIVE) >= nbCouleur + nbLocomotives;
    }

    /**
     * Demande à l'utilisateur de choisir des cartes wagon pour couvrir exactement
     * le prix indiqué en paramètre.
     * Les cartes choisies par le joueur sont placées dans la liste
     * {@code cartesWagonPosees} au fur et à mesure.
     * <p>
     * Prérequis: le joueur peut payer le cout total demandé
     *
     * @param nbCouleur
     * @param couleur
     * @param nbLocomotives
     */
    public List<CouleurWagon> payerCartesWagon(int nbCouleur, CouleurWagon couleur, int nbLocomotives,
                                               String instruction) {
        // Payer les locomotives en premier
        for (int i = 0; i < nbLocomotives; i++) {
            cartesWagon.remove(CouleurWagon.LOCOMOTIVE);
            cartesWagonPosees.add(CouleurWagon.LOCOMOTIVE);
        }

        Map<CouleurWagon, Integer> compteur = CouleurWagon.compteur(cartesWagon);
        while (nbCouleur > 0) {
            // il reste des cartes à payer
            List<CouleurWagon> choixPossibles = new ArrayList<>();
            if (compteur.get(CouleurWagon.LOCOMOTIVE) > 0) {
                choixPossibles.add(CouleurWagon.LOCOMOTIVE);
            }
            if (couleur == CouleurWagon.GRIS) {
                for (CouleurWagon c : CouleurWagon.getCouleursSimples()) {
                    if (compteur.get(c) > 0
                            && compteur.get(c) + compteur.get(CouleurWagon.LOCOMOTIVE) >= nbCouleur) {
                        choixPossibles.add(c);
                    }
                }
            } else if (compteur.get(couleur) > 0) {
                choixPossibles.add(couleur);
            }

            String choix = choisir(
                    instruction,
                    new ArrayList<>(choixPossibles.stream()
                            .map(CouleurWagon::name)
                            .collect(Collectors.toList())),
                    new ArrayList<>(),
                    false);
            CouleurWagon couleurChoisie = CouleurWagon.valueOf(choix);

            if (couleur == CouleurWagon.GRIS && couleurChoisie != CouleurWagon.LOCOMOTIVE) {
                couleur = couleurChoisie;
            }

            cartesWagon.remove(couleurChoisie);
            cartesWagonPosees.add(couleurChoisie);
            compteur.put(couleurChoisie, compteur.get(couleurChoisie) - 1);
            nbCouleur -= 1;
        }

        return cartesWagonPosees;
    }

    /**
     * Cette méthode est appelée lorsque le joueur demande la capture d'une route.
     * <p>
     * Le joueur doit choisir les cartes wagon à défausser pour la capture de la
     * route puis :
     * - le joueur est marqué comme propriétaire de la route ;
     * - le score du joueur est augmenté de la valeur de la route ;
     * - le nombre de wagons du joueur est décrémenté de la longueur de la route.
     * <p>
     * Pré-requis : le joueur peut capturer la route (la route est disponible et le
     * joueur a les cartes wagon nécessaires)
     *
     * @param route la route à capturer
     */
    private void capturerRoute(Route route) {
        if (route.capturerParJoueur(this)) {
            route.setProprietaire(this);
            int n = route.getLongueur();
            nbWagons -= n;
            switch (n) {
                case 1 -> score += 1;
                case 2 -> score += 2;
                case 3 -> score += 4;
                case 4 -> score += 7;
                case 6 -> score += 15;
                case 8 -> score += 21;
            }
            graphe.ajouterArete(route.getVille1().getNumVille(),route.getVille2().getNumVille(), route.getLongueur());
        }
    }

    /**
     * Cette méthode est appelée quand le joueur choisit de piocher une ou plusieurs
     * cartes destination.
     * <p>
     * Le joueur doit choisir les cartes destinations qu'il souhaite défausser parmi
     * celles qui sont retournées. Les cartes qui ne sont pas gardées sont replacées
     * en bas de la pioche de destinations.
     */
    private void prendreDestinations() {
        ArrayList<Destination> destinationsPossibles = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Destination d = jeu.piocherDestination();
            if (d == null) {
                break;
            }
            destinationsPossibles.add(d);
        }
        log(toLog() + " pioche " + destinationsPossibles.size() + " cartes destination");
        List<Destination> defausse = choisirDestinations(destinationsPossibles, 1);
        log("   ... en défausse " + defausse.size());
        jeu.defausserDestinations(defausse);
    }

    /**
     * Cette méthode est appelée lorsque le joueur choisit de construire une gare
     * sur une ville.
     * <p>
     * Le joueur doit choisir les cartes wagon à défausser pour construire la gare
     * puis :
     * - le joueur est marqué comme propriétaire de la gare ;
     * - le score du joueur est décrémenté de 4 ;
     * - le nombre de gares disponibles du joueur est décrémenté de 1.
     * <p>
     * Pré-requis : le joueur peut construire la gare (pas de gare sur la ville,
     * assez de gares en réserve et de cartes wagon)
     *
     * @param ville la ville sur laquelle construire la gare
     */
    private void construireGare(Ville ville) {
        log(toLog() + " construit une gare à " + ville.toLog());
        int nbCartes = 4 - nbGares;
        if (nbCartes == 1) {
            payerCartesWagon(4 - nbGares, CouleurWagon.GRIS, 0,
                    String.format("Défaussez 1 carte wagon pour construire la gare à %s",
                            ville.getNom()));
        } else {
            payerCartesWagon(4 - nbGares, CouleurWagon.GRIS, 0,
                    String.format("Défaussez %d cartes wagon pour construire la gare à %s",
                            nbCartes, ville.getNom()));
        }
        log("   ... défausse " + CouleurWagon.listToLog(cartesWagonPosees));
        defausserCartesWagonPosees();
        nbGares -= 1;
        score -= 4;
        ville.setProprietaire(this);
    }

    public Graphe getGraphe() {
        return graphe;
    }
}
