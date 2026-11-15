# Projet Java : application de streaming

Application console en Java qui simule une plateforme de streaming audio : des utilisateurs avec un abonnement (Free ou Premium) créent des playlists à partir d'une bibliothèque de chansons, podcasts et livres audio chargée depuis un fichier CSV.

Fonctionnalités :

- chargement de la bibliothèque depuis `data/media.csv` au démarrage (les lignes invalides sont ignorées et signalées)
- ajout / suppression de médias dans une playlist, avec les règles de l'abonnement (taille max, contenu premium, pas de doublon)
- durée totale d'une playlist
- lecture aléatoire qui évite deux titres du même artiste à la suite
- limite de "skips" par heure pour les utilisateurs Free
- reprise de lecture des podcasts et livres audio là où on s'était arrêté
- recommandations : les N meilleurs médias pour un utilisateur

## Comment lancer le projet

Prérequis : Java 17 ou plus, Maven.

Pour lancer les tests :

```bash
mvn test
```

Pour lancer l'application :

```bash
mvn package
java -cp target/classes streaming.App
```

On peut aussi donner un autre fichier : `java -cp target/classes streaming.App chemin/vers/fichier.csv`.

Le programme doit être lancé depuis la racine du projet (là où se trouve le dossier `data`).

## Structure du projet

```
src/main/java/streaming
├── App.java                  point d'entrée, scénario de démonstration
├── model/                    les objets du domaine
│   ├── Media.java            classe abstraite
│   ├── Song.java, Podcast.java, Audiobook.java
│   ├── Resumable.java        interface
│   ├── User.java, Playlist.java
│   └── Genre.java            enum
├── subscription/             les règles des abonnements
│   ├── SubscriptionPlan.java interface
│   └── FreePlan.java, PremiumPlan.java
├── service/                  la logique métier
│   ├── MediaLibrary.java     recherche des médias
│   ├── PlaylistService.java
│   ├── PlaybackService.java
│   └── RecommendationService.java
├── loader/                   lecture du fichier
│   ├── MediaCsvLoader.java
│   └── MediaFactory.java
└── exception/                exceptions métier
```

## L'héritage

`Media` est la classe mère de `Song`, `Podcast` et `Audiobook`. Elle contient ce qui est commun à tous les médias : id, titre, créateur, durée, genre, date de sortie, et la validation de ces champs dans le constructeur. Chaque sous-classe ajoute son propre attribut : l'album pour une chanson, le numéro d'épisode pour un podcast, le nombre de chapitres pour un livre audio.

## Le polymorphisme

`Media` déclare la méthode abstraite `recommendationScore(User user)`, et chaque sous-classe la calcule différemment :

- `Song` : score fixe + bonus si le genre fait partie des genres préférés de l'utilisateur
- `Podcast` : le score baisse d'un point par semaine depuis la sortie, car les épisodes récents sont plus intéressants
- `Audiobook` : bonus si le livre est commencé mais pas terminé

`RecommendationService` trie une `List<Media>` en appelant `recommendationScore` sans savoir de quel type est chaque média. C'est la bonne méthode qui est appelée à l'exécution.

Il y a aussi du polymorphisme avec l'interface `SubscriptionPlan` : `PlaylistService` et `PlaybackService` appellent `maxPlaylistSize()`, `maxSkipsPerHour()` et `canAccess()` sans savoir si l'utilisateur est Free ou Premium.

## Classe abstraite et interfaces

- **`Media` est une classe abstraite** parce qu'un "média" tout seul n'existe pas, et parce que les sous-classes partagent de vrais attributs et du code (getters, validation, `genreBonus`).
- **`Resumable` est une interface** (`savePosition`, `getResumePosition`) implémentée seulement par `Podcast` et `Audiobook`. Une chanson ne se reprend pas en cours de route, donc `Song` ne l'implémente pas. C'est une capacité, pas un "type de", donc une interface est plus adaptée qu'une classe.
- **`SubscriptionPlan` est une interface** car un abonnement n'est qu'un ensemble de règles, sans état commun.

## Les exceptions

Exceptions personnalisées (dans `exception/`, toutes héritent de `Exception`, donc on est obligé de les gérer) :

| Exception | Quand |
| --- | --- |
| `PlaylistFullException` | la playlist a atteint la taille max de l'abonnement |
| `PremiumContentException` | un utilisateur Free ajoute un livre audio |
| `DuplicateMediaException` | le média est déjà dans la playlist |
| `SkipLimitReachedException` | plus de 6 skips en une heure pour un utilisateur Free |
| `MediaNotFoundException` | id inconnu dans la bibliothèque, ou suppression d'un média absent de la playlist |
| `InvalidMediaDataException` | ligne du CSV invalide |

Exceptions standard utilisées :

- `IllegalArgumentException` pour les valeurs impossibles (titre vide, durée négative, position de lecture hors du média)
- `IOException` si le fichier ne peut pas être lu
- `NumberFormatException` et `DateTimeParseException` sont attrapées dans `MediaFactory` et transformées en `InvalidMediaDataException`

## Les principes SOLID

- **S (responsabilité unique)** : chaque classe a un seul rôle. `Playlist` stocke les médias, `PlaylistService` applique les règles, `MediaCsvLoader` lit le fichier, `MediaFactory` crée les objets, `App` affiche.
- **O (ouvert/fermé)** : pour ajouter un abonnement (par exemple étudiant), il suffit de créer une classe qui implémente `SubscriptionPlan`. Aucun service n'est modifié.
- **L (substitution de Liskov)** : partout où on attend un `Media`, on peut passer une `Song`, un `Podcast` ou un `Audiobook`, et partout où on attend un `SubscriptionPlan`, un `FreePlan` ou un `PremiumPlan`.
- **I (ségrégation des interfaces)** : `Resumable` est séparée de `Media`, pour que `Song` n'ait pas à implémenter des méthodes qui n'ont pas de sens pour elle.
- **D (inversion des dépendances)** : les services dépendent de l'interface `SubscriptionPlan`, pas de `FreePlan` ou `PremiumPlan`.

## Mes choix techniques

1. **Les lignes invalides du CSV sont ignorées, pas bloquantes.** Une seule ligne cassée ne doit pas empêcher de charger toute la bibliothèque. Les erreurs sont gardées dans une liste et affichées au démarrage. Le fichier fourni contient volontairement deux lignes fausses (une durée `abc` et un type `VIDEO`) pour le montrer.
2. **La position de lecture est stockée dans l'objet média.** C'est suffisant pour une démo console avec un seul utilisateur qui écoute un livre. Avec plusieurs utilisateurs, il faudrait plutôt une `Map` par utilisateur dans `PlaybackService`.
3. **`PlaylistService` reçoit un `Random` dans son constructeur.** Ça permet d'avoir toujours le même mélange dans les tests, et donc des tests qui ne changent pas d'un lancement à l'autre.
4. **Le temps est passé en paramètre de `skip(User, LocalDateTime)`** au lieu d'appeler `LocalDateTime.now()` dans la méthode. Ça permet de tester la règle "6 skips par heure" sans attendre une heure.
5. **`Playlist.getItems()` renvoie une copie** (`List.copyOf`) pour qu'on ne puisse pas contourner les règles du service en ajoutant directement dans la liste.

## Les tests

Tests JUnit 5 dans `src/test/java`, lancés avec `mvn test` :

- `PlaylistServiceTest` : ajout et durée totale, playlist vide, limite de 20 pour Free, pas de limite pour Premium, livre audio refusé en Free, doublon refusé, suppression d'un média absent, shuffle sans artiste répété, shuffle avec un seul artiste
- `PlaybackServiceTest` : 7e skip refusé, les skips de plus d'une heure ne comptent plus, pas de limite en Premium, reprise d'un livre audio, une chanson repart du début, position invalide refusée
- `RecommendationServiceTest` : genre préféré en premier, pas de livre audio pour Free, livre commencé mis en avant, limite N
- `MediaFactoryTest` : bonne sous-classe créée, type inconnu, durée non numérique, mauvaise date, colonne manquante, durée négative
- `MediaLibraryTest` : recherche par id, id inconnu
