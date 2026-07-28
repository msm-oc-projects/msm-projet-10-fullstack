# Your Car Your Way — PoC de tchat

Ce repository contient une preuve de concept volontairement limitée au tchat
temps réel. Son objectif est de valider la structure Angular/Spring Boot, les
échanges REST et WebSocket/STOMP, la persistance PostgreSQL et la séparation
entre domaine, application et infrastructure. Il ne s'agit pas de l'application
de location complète.

## Ce que démontre le PoC

- rejoindre le salon `demo` avec un pseudonyme ;
- charger les 50 derniers messages par REST ;
- envoyer et recevoir des messages par WebSocket/STOMP ;
- valider le pseudonyme et le contenu côté serveur ;
- enregistrer chaque message avant sa diffusion ;
- éviter les doublons grâce à `clientMessageId` ;
- signaler une perte de connexion et tenter une reconnexion ;
- utiliser l'interface au clavier et annoncer les nouveaux messages.

Les conversations privées, pièces jointes, notifications, modération avancée,
présence et authentification réelle sont hors périmètre.

Cognito, Stripe et SES ne sont pas intégrés : ils ne participent pas au cas de
tchat démontré. Leur branchement par ports et adaptateurs est planifié dans la
proposition d'architecture. Le PoC valide uniquement PostgreSQL comme composant
externe nécessaire à la persistance des messages.

## Stack technique

- Java 21, Spring Boot et Maven ;
- Angular, TypeScript et npm ;
- REST pour l'historique ;
- WebSocket avec STOMP pour le temps réel ;
- PostgreSQL 18 et Flyway ;
- H2 pour un démarrage backend simplifié ;
- Docker Compose pour PostgreSQL.

## Organisation

```text
.
├── .github/workflows/poc-ci.yml
├── backend/
│   └── src/main/java/.../chat/
│       ├── domain/
│       ├── application/
│       └── infrastructure/
├── frontend/
├── docker-compose.yml
└── README.md
```

Le domaine ne dépend pas de Spring Data ni du transport WebSocket. Le port
`ChatMessageRepository` est implémenté par l'adaptateur JPA. Cette organisation
illustre l'architecture hexagonale retenue sans surdimensionner le PoC.

## Prérequis

Installez et vérifiez :

```bash
java --version
mvn --version
node --version
npm --version
docker --version
docker compose version
```

Versions conseillées : Java 21, Node.js 24, npm 11, Maven 3.9 ou version
compatible, Docker Engine récent avec Compose V2.

Les commandes sans variables d'environnement fonctionnent dans Bash,
PowerShell et `cmd.exe`. Lorsque la syntaxe diffère selon le terminal, les
exemples ci-dessous fournissent un bloc pour chaque environnement :

- Bash : Linux, macOS, WSL et Git Bash sous Windows ;
- PowerShell : terminal PowerShell sous Windows ;
- Invite de commandes : `cmd.exe` sous Windows.

Vous pouvez utiliser IntelliJ IDEA, VS Code ou un autre IDE prenant en charge
Java 21 et Angular. Clonez d'abord le dépôt, placez-vous à sa racine, importez
`backend` comme projet Maven et ouvrez `frontend` pour TypeScript :

```bash
git clone https://github.com/msm-oc-projects/msm-projet-10-fullstack.git
cd msm-projet-10-fullstack
git status
```

La commande `git status` doit confirmer que vous êtes sur une branche et que
l'arbre de travail est propre avant vos modifications.

## Démarrage rapide avec PostgreSQL

Depuis la racine du repository :

```bash
docker compose up -d
```

Dans un premier terminal, configurez la connexion à PostgreSQL et démarrez le
backend avec la syntaxe correspondant à votre environnement.

Linux, macOS, WSL ou Git Bash :

```bash
cd backend
export DB_URL=jdbc:postgresql://localhost:5432/ycwy_chat
export DB_USERNAME=ycwy
export DB_PASSWORD=ycwy
mvn spring-boot:run
```

Windows PowerShell :

```powershell
cd backend
$env:DB_URL = "jdbc:postgresql://localhost:5432/ycwy_chat"
$env:DB_USERNAME = "ycwy"
$env:DB_PASSWORD = "ycwy"
mvn spring-boot:run
```

Invite de commandes Windows (`cmd.exe`) :

```bat
cd backend
set DB_URL=jdbc:postgresql://localhost:5432/ycwy_chat
set DB_USERNAME=ycwy
set DB_PASSWORD=ycwy
mvn spring-boot:run
```

Dans un second terminal :

```bash
cd frontend
npm ci
npm start
```

Ouvrez ensuite <http://localhost:4200>. Le proxy Angular transmet `/api` et
`/ws` au backend disponible sur <http://localhost:8080>.

Pour arrêter PostgreSQL sans effacer ses données :

```bash
docker compose down
```

Pour supprimer aussi le volume local de démonstration :

```bash
docker compose down -v
```

Cette dernière commande efface uniquement les messages de la base locale du PoC.

## Démarrage simplifié avec H2

Le profil `dev` utilise une base H2 en mémoire. Il permet de travailler sur le
backend sans Docker :

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Les données H2 disparaissent à l'arrêt de l'application. PostgreSQL reste le
mode représentatif de l'architecture cible.

## Vérifications

Tests backend :

```bash
cd backend
mvn verify
```

Tests et build frontend :

```bash
cd frontend
npm ci
npm test -- --watch=false
npm run build
```

Lorsque PostgreSQL, le backend et le frontend sont lancés, le test de fumée
vérifie qu'un message est persisté puis diffusé :

```bash
cd frontend
npm run smoke
```

Le backend utilise un autre port ? Indiquez son URL sans modifier le script.

Linux, macOS, WSL ou Git Bash :

```bash
CHAT_BACKEND_URL=http://localhost:18080 npm run smoke
```

Windows PowerShell :

```powershell
$env:CHAT_BACKEND_URL = "http://localhost:18080"
npm run smoke
```

Invite de commandes Windows (`cmd.exe`) :

```bat
set CHAT_BACKEND_URL=http://localhost:18080
npm run smoke
```

Pour une vérification manuelle, ouvrez deux onglets sur
<http://localhost:4200>, rejoignez le même salon et envoyez un message.

## Contrats utiles

- historique : `GET /api/v1/chat/rooms/{roomId}/messages?limit=50` ;
- connexion STOMP : `/ws` ;
- envoi : `/app/chat/{roomId}/send` ;
- réception : `/topic/chat/{roomId}` ;
- erreurs : `/topic/chat/{roomId}/errors` ;
- santé du backend : `GET /actuator/health`.

Un message sortant contient `clientMessageId`, `author` et `content`. Le serveur
ajoute notamment `id`, `roomId` et `sentAt`.

## Configuration

`application.yml` constitue la configuration de référence, utilisée en
production et lors du démarrage local avec PostgreSQL. Elle exige les variables
suivantes sans fournir de valeur par défaut :

| Variable | Exemple local uniquement |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/ycwy_chat` |
| `DB_USERNAME` | `ycwy` |
| `DB_PASSWORD` | `ycwy` |

Cette configuration utilise `ddl-auto: validate`, active Flyway et masque les
messages d'erreur internes. Le profil `dev` surcharge uniquement la base avec
H2, utilise `ddl-auto: create-drop`, désactive Flyway et affiche les messages
d'erreur pour faciliter le diagnostic local. Les identifiants ci-dessus sont
réservés au développement local ; n'enregistrez jamais de secret réel dans Git.

## Problèmes fréquents

- **Le port 5432 est occupé** : arrêtez l'autre instance PostgreSQL ou adaptez
  le port et `DB_URL`.
- **Le port 8080 est occupé** : lancez le backend avec `--server.port=18080`,
  adaptez le proxy Angular pour l'interface et utilisez `CHAT_BACKEND_URL` pour
  le test de fumée.
- **Le frontend ne joint pas le backend** : vérifiez que le backend répond sur
  <http://localhost:8080/actuator/health>.
- **`npm start` échoue après un changement de dépendances** : relancez `npm ci`.
- **La migration Flyway échoue** : vérifiez les variables de base et l'état du
  conteneur avec `docker compose ps`.
- **Les messages H2 ont disparu** : c'est normal après l'arrêt du backend.

## Contribution

Créez une branche courte, limitez les changements au périmètre du PoC et
exécutez les tests avant une pull request. Ne transformez pas ce repository en
application de location complète : une extension doit d'abord servir la
validation d'un choix architectural documenté.

Avant de demander une revue :

1. vérifiez les tests backend et frontend ;
2. vérifiez le build Angular ;
3. testez le parcours avec deux onglets ;
4. contrôlez le clavier, le focus, les erreurs et l'annonce des messages ;
5. mettez à jour ce README si une commande ou un prérequis change.

## Limites architecturales

Le pseudonyme local ne constitue pas une authentification. Le broker STOMP
intégré ne permet pas de partager les abonnements entre plusieurs instances.
Pour une production distribuée, il faudrait connecter l'identité OIDC prévue et
utiliser un broker relay ou un service de messagerie compatible. Ces évolutions
sont documentées mais volontairement exclues du PoC.
