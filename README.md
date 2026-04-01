# Calanques — Application Android

Application mobile Android dédiée à la découverte et à la réservation d'activités du Parc National des Calanques.

---

## Fonctionnalités

- Consultation des types d'activités disponibles
- Liste et filtrage des activités par catégorie
- Fiche détaillée par activité (description, durée, tarif, photo)
- Création de compte et authentification
- Consultation du profil et de l'historique de réservations

---

## Prérequis

- Android Studio (version récente recommandée)
- JDK 21
- Connexion internet (les données sont chargées depuis une API distante)
- Un appareil Android (physique ou émulateur) avec Android 7.0 minimum (API 24)

---

## Installation et lancement

1. Cloner le dépôt et ouvrir le dossier dans Android Studio
2. Attendre la fin de la synchronisation Gradle
3. Connecter un appareil ou démarrer un émulateur
4. Lancer l'application via le bouton **Run** (▶)

---

## Architecture

Le projet suit le pattern **MVVM** (Model – View – ViewModel) recommandé par Google pour Android.

```
app/src/main/java/com/example/calanques/
├── model/          # Classes de données (Activite, ActivityType, User...)
├── network/        # Configuration Retrofit, ApiService, RetrofitClient
├── viewmodel/      # ViewModels par écran, gestion des états UI
└── ui/
    ├── screens/    # Écrans Compose (Home, Activités, Détail, Login, Register, Profil)
    ├── components/ # Composants réutilisables (Scaffold, AppHeader, BottomNavBar)
    └── theme/      # Couleurs, typographie, thème Material 3
```

### Écrans principaux

| Écran | Description |
|-------|-------------|
| `HomeScreen` | Accueil — liste des types d'activités |
| `ActivitesScreen` | Activités filtrées par type |
| `ActiviteDetailScreen` | Détail complet d'une activité |
| `LoginScreen` | Authentification |
| `RegisterScreen` | Création de compte |
| `ProfileScreen` | Profil utilisateur et réservations |

---

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Réseau | Retrofit 2 + Gson |
| Chargement d'images | Coil |
| Architecture | ViewModel + StateFlow |
| Langage | Kotlin |
| Build | Gradle (Kotlin DSL) |

---

## Configuration réseau

L'application se connecte par défaut à `http://webngo.sio.bts:8004/`. En cas d'indisponibilité de ce serveur, un mécanisme de fallback automatique redirige les requêtes vers `http://webngo.inforostand14.net:8001/`.

Cette logique est gérée dans `ApiConfig.kt` via un intercepteur OkHttp.

---

## Structure des données principales

**Activite**
```
id, nom, description, tarif, duree, image_url, type_id
```

**ActivityType**
```
id, libelle, image_url
```

**Authentification** : token JWT transmis dans le header `Authorization: Bearer <token>`

---

## Charte graphique

| Élément | Valeur |
|---------|--------|
| Couleur principale | `#E51A2E` (rouge) |
| Fond | `#F5F5F5` (gris clair) |
| Surface | `#FFFFFF` |
| Texte principal | `#111111` |
| Texte secondaire | `#888888` |
