# Password CLI — Générateur et auditeur de mots de passe

Outil en ligne de commande développé en **Java 21**, capable de générer des
mots de passe robustes et de valider leur solidité réelle via un
micro-service **zxcvbn** exécuté dans un conteneur **Docker**.

## 1. Architecture

```
MyPasswordCLI/                 # Application Java 21 (CLI)
├── docker/strength-checker/   # Micro-service Python/Flask + zxcvbn (conteneur)
│   ├── app.py
│   ├── requirements.txt
│   └── Dockerfile
├── docker-compose.yml         # Démarrage simplifié du conteneur
├── pom.xml
├── src/main/java/com/devultraapp/
│   ├── Main.java
│   ├── cli/                # Parsing des arguments / mode interactif
│   ├── generator/          # Génération SecureRandom des mots de passe
│   └── strength/           # Client HTTP vers le conteneur Docker
└── DOCUMENTATION.md
```

L'application Java communique avec le conteneur via une **API REST HTTP**
(`POST http://localhost:5000/check`), ce qui découple totalement les deux
composants : ils pourraient tourner sur deux machines différentes sans
aucune modification du code.

## 2. Prérequis

- Java 21 (JDK)
- Maven 3.9+
- Docker et Docker Compose

## 3. Construire et lancer le conteneur de validation

Depuis la racine du projet :

```bash
docker compose up -d --build
```

Vérifier que le service répond :

```bash
curl http://localhost:5000/health
# {"status": "ok"}
```

Pour l'arrêter :

```bash
docker compose down
```

## 4. Compiler l'application Java

```bash
cd MyPasswordCLI
mvn clean package
```

Le jar exécutable est généré dans `MyPasswordCLI/target/password-cli.jar`.

## 5. Exécuter l'application

Mode interactif (sans argument) :

```bash
java -jar target/password-cli.jar
```

Mode CLI paramétré, génération unique :

```bash
java -jar target/password-cli.jar --length=20 --upper --lower --digits --symbols
```

Mode rafale (10 mots de passe) :

```bash
java -jar target/password-cli.jar --length=16 --count=10
```

Sans audit Docker (estimation locale uniquement) :

```bash
java -jar target/password-cli.jar --length=12 --no-server
```

Aide complète :

```bash
java -jar target/password-cli.jar --help
```

## 6. Notes

- Si le conteneur Docker n'est pas démarré, l'application bascule
  automatiquement sur une estimation locale dégradée plutôt que de planter
  (voir `LocalStrengthEstimator`), mais l'audit de référence reste celui
  du conteneur zxcvbn.
- La génération utilise `java.security.SecureRandom`, adapté à un usage de
  sécurité (contrairement à `java.util.Random`).
