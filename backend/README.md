# Sport Store Backend

Backend Spring Boot compatible avec le frontend Angular du projet.

## Stack

- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL

## Demarrage

1. Lancer PostgreSQL:

```bash
docker compose up -d
```

2. Demarrer l'application Spring Boot depuis `backend/`:

```bash
./mvnw spring-boot:run
```

ou avec Maven installe:

```bash
mvn spring-boot:run
```

## Configuration

Le backend ecoute par defaut sur le port `3500` pour rester compatible avec le frontend actuel.

Fonctionnalites commande:

- creation de commande avec paiement carte simule
- generation d'une facture PDF via `GET /orders/{id}/invoice`
- preparation automatique des emails HTML de confirmation et de livraison dans `backend/build/emails`
- changement de statut de commande de `EN_TRAITEMENT` a `LIVRE` depuis l'admin

Variables utiles:

- `DB_URL` default `jdbc:postgresql://localhost:5432/sportstore`
- `DB_USERNAME` default `postgres`
- `DB_PASSWORD` default `postgres`
- `JWT_SECRET` default valeur de dev embarquee

## Comptes de depart

- Admin: `admin` / `secret`
- Utilisateur: `jane` / `password`
