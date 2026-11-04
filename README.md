# Description d'image

Cette application web analyse une image et en génère une description en
français à l'aide d'un modèle de vision exécuté par [Ollama](https://ollama.com/).
Sans question, le modèle décrit la scène et ses détails visuels importants.
Vous pouvez aussi poser une question pour obtenir une réponse ciblée sur
l'image. Les descriptions peuvent être lues à voix haute par le navigateur.

L'application accepte les images JPEG, PNG et WEBP de 10 Mo maximum. Un mode
de démonstration sans modèle est également disponible ; il renvoie des
réponses prédéfinies et ne décrit pas réellement l'image.

## Prérequis

- Java 17 ou supérieur.
- Ollama installé et en cours d'exécution pour l'analyse réelle.
- Le modèle de vision configuré dans l'application, `gemma3:latest` par défaut.

## Installation et démarrage

1. Récupérez le projet et placez-vous dans son dossier :

   ```bash
   git clone https://github.com/SkeyMMF/projet-description-image.git
   cd projet-description-image
   ```

2. Installez et démarrez Ollama, puis téléchargez le modèle configuré :

   ```bash
   ollama pull gemma3:latest
   ```

   Ollama doit être accessible à l'adresse `http://localhost:11434` (son
   adresse par défaut).

3. Démarrez l'application avec le wrapper Gradle fourni :

   Sous Windows :

   ```powershell
   .\gradlew.bat bootRun
   ```

   Sous Linux ou macOS :

   ```bash
   ./gradlew bootRun
   ```

4. Ouvrez [http://localhost:8080](http://localhost:8080) dans votre navigateur.

Le wrapper télécharge automatiquement la version de Gradle nécessaire au
premier lancement.

## Utilisation

1. Choisissez une image ou déposez-la dans la zone prévue.
2. Facultativement, saisissez une question sur son contenu.
3. Cliquez sur **Analyser l'image** et patientez pendant le traitement.
4. Consultez la description. Les boutons permettent de la relire à voix haute,
   d'arrêter la lecture ou de choisir une nouvelle image.

La synthèse vocale dépend de la prise en charge par le navigateur et des voix
installées sur l'appareil.

## Mode de démonstration

Pour lancer l'application sans installer Ollama ni télécharger de modèle,
choisissez le descripteur factice au démarrage :

Sous Windows :

```powershell
.\gradlew.bat bootRun --args="--description.agent=faux"
```

Sous Linux ou macOS :

```bash
./gradlew bootRun --args='--description.agent=faux'
```

Ce mode est destiné à tester l'interface : ses réponses sont fixes et ne sont
pas produites à partir de l'image envoyée.

## Configuration

Les paramètres se trouvent dans `src/main/resources/application.properties` :

| Paramètre | Valeur par défaut | Description |
|---|---|---|
| `description.agent` | `ollama` | Descripteur à utiliser : `ollama` ou `faux`. |
| `description.ollama.url` | `http://localhost:11434` | Adresse du serveur Ollama. |
| `description.ollama.modele` | `gemma3:latest` | Nom du modèle Ollama à utiliser. |
| `description.ollama.delai-ms` | `120000` | Délai maximal d'attente, en millisecondes. |

Si vous changez le modèle, vérifiez qu'il prend en charge les images et
téléchargez-le avec `ollama pull nom-du-modele`.

## Tests

Sous Windows :

```powershell
.\gradlew.bat test
```

Sous Linux ou macOS :

```bash
./gradlew test
```

## API

L'interface envoie une requête `multipart/form-data` à `POST /api/description`
avec le champ obligatoire `image` et le champ facultatif `question`. En cas de
succès, l'API renvoie la description dans le champ JSON `description`. En cas
d'erreur, le message est renvoyé dans le champ `erreur`. Un contrôle de
disponibilité est accessible via `GET /api/health`.
