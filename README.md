# MobiGait

MobiGait est une application Android qui analyse et modélise la démarche humaine à l'aide des capteurs du téléphone.

## Fonctionnalités

- Capture des données des capteurs (accéléromètre et gyroscope)
- Affichage en temps réel des données dans des graphiques
- Calcul des métriques de la démarche :
  - Fréquence des pas
  - Longueur moyenne d'un pas
  - Vitesse estimée
  - Symétrie de la marche
- Enregistrement des sessions d'analyse
- Historique des sessions avec détails

## Prérequis

- Android Studio Arctic Fox ou version ultérieure
- Android SDK 24 ou version ultérieure
- Un appareil Android avec accéléromètre et gyroscope

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/votre-username/MobiGait.git
```

2. Ouvrez le projet dans Android Studio

3. Synchronisez le projet avec les fichiers Gradle

4. Exécutez l'application sur un appareil ou un émulateur

## Utilisation

1. Lancez l'application
2. Sur l'écran d'accueil, appuyez sur "Démarrer l'analyse"
3. Placez le téléphone dans votre poche ou tenez-le à la main
4. Marchez normalement
5. Appuyez sur "Arrêter l'enregistrement" pour terminer la session
6. Consultez les résultats et l'historique des sessions

## Architecture

L'application utilise une architecture MVVM avec les composants suivants :

- **MainActivity** : Gère la navigation entre les fragments
- **Fragments** :
  - HomeFragment : Écran d'accueil
  - AnalysisFragment : Enregistrement et affichage en temps réel
  - ResultsFragment : Résultats de la dernière session
  - HistoryFragment : Historique des sessions
- **Base de données** : Room pour le stockage local
- **Capteurs** : Gestion des capteurs via SensorManager
- **Calculs** : Utilitaires mathématiques pour l'analyse de la démarche

## Bibliothèques utilisées

- MPAndroidChart : Graphiques en temps réel
- Room : Base de données locale
- Apache Commons Math : Calculs mathématiques avancés

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails. 