# SportStore

Boutique en ligne d'articles de sport : catalogue, panier, paiement par carte
simulé, facture PDF téléchargeable et courriels HTML de confirmation et de
livraison préparés côté serveur.

**Démonstration en ligne : <https://sportstore.romualdasonmene.cloud>**

| | |
| --- | --- |
| Frontend | Angular 19, à la racine du dépôt |
| Backend | Spring Boot 3.4 / Java 21, PostgreSQL — dossier [`backend/`](backend) |
| Backend alternatif | ASP.NET Core 9 — dossier [`backend-dotnet/`](backend-dotnet), **non déployé** |

Le frontend est branché définitivement sur le backend Spring Boot. Le backend
.NET est conservé comme exercice comparatif, avec sa propre suite de tests,
mais il ne participe pas au déploiement.

---

## Comptes de démonstration

Créés au premier démarrage par
[`DataInitializer`](backend/src/main/java/com/sportstore/backend/config/DataInitializer.java),
uniquement si la table des utilisateurs est vide.

| Identifiant | Mot de passe | Rôle | Ce qu'il permet |
| --- | --- | --- | --- |
| `admin` | `secret` | ADMIN | Créer, modifier et supprimer des produits, consulter les utilisateurs |
| `jane` | `password` | USER | Commander, consulter ses commandes, télécharger ses factures |

Le catalogue initial compte 9 produits répartis en trois catégories :
*Watersports*, *Soccer* et *Chess*.

> Ces identifiants sont publics et l'espace d'administration modifie réellement
> la base. Sur l'instance de démonstration, un visiteur peut donc altérer le
> catalogue. La sauvegarde quotidienne du serveur permet de revenir en arrière.

---

## Développement local

### Backend

```bash
cd backend && mvn spring-boot:run
```

L'API écoute sur `http://localhost:3500/api`. Le préfixe `/api` vient de
`server.servlet.context-path` : en production, nginx sert le frontend et relaie
tout ce qui commence par `/api` vers ce service, ce qui place les deux sur la
même origine et supprime toute question d'origine croisée.

Une base PostgreSQL est nécessaire. La plus simple :

```bash
cd backend && docker compose up -d
```

### Frontend

```bash
npm install && npm start
```

L'application est sur `http://localhost:4200`. `ng serve` relaie `/api` vers le
backend grâce à [`proxy.conf.json`](proxy.conf.json), si bien que l'URL de
l'API reste relative en développement comme en production : le même bundle
fonctionne dans les deux cas, sans recompilation.

### Tests

```bash
npm test                    # frontend Angular
cd backend && mvn test      # backend Spring Boot
dotnet test backend-dotnet.tests/SportStore.Api.Tests.csproj
```

---

## API

Toutes les routes sont préfixées par `/api`.

| Méthode | Route | Accès |
| --- | --- | --- |
| `POST` | `/login`, `/register` | public |
| `GET` | `/products` | public |
| `POST` `PUT` `DELETE` | `/products`, `/products/{id}` | ADMIN |
| `GET` `POST` | `/orders` | connecté |
| `PUT` `DELETE` | `/orders/{id}` | ADMIN |
| `GET` | `/orders/{id}/invoice` | propriétaire de la commande |
| `GET` | `/users` | ADMIN |

L'authentification se fait par jeton JWT, transmis dans l'en-tête
`Authorization: Bearer <jeton>`.

---

## Déploiement

Les images sont construites par GitHub Actions
([`.github/workflows/publish.yml`](.github/workflows/publish.yml)) et publiées
sur GHCR. Le serveur ne compile jamais : il récupère les images et bascule.

| Image | Contenu |
| --- | --- |
| `ghcr.io/ndele2024/sportstore-web` | bundle Angular servi par nginx, qui relaie `/api` |
| `ghcr.io/ndele2024/sportstore-api` | JAR Spring Boot sur une JVM sans privilège |

Construction locale des deux images, pour vérification :

```bash
docker build -t sportstore-web .
docker build -t sportstore-api ./backend
```

### Variables d'environnement du backend

| Variable | Rôle |
| --- | --- |
| `DB_URL` `DB_USERNAME` `DB_PASSWORD` | connexion PostgreSQL |
| `JWT_SECRET` | clé de signature des jetons, **à remplacer en production** |
| `JAVA_OPTS` | réglages JVM ; `-XX:MaxRAMPercentage` adapte le tas à la limite du conteneur |

La sonde de santé `/api/actuator/health` est interrogée par le healthcheck
Docker. Elle n'est pas relayée par nginx : elle reste interne.

---

## Structure

```
src/                 frontend Angular
  environments/      apiUrl, remplacé à la construction en production
backend/             API Spring Boot (déployée)
backend-dotnet/      API ASP.NET Core (non déployée)
backend-dotnet.tests/
nginx.conf           configuration du conteneur web
Dockerfile           image du frontend
backend/Dockerfile   image de l'API
```
