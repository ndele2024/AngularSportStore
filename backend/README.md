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

Variables utiles:

- `DB_URL` default `jdbc:postgresql://localhost:5432/sportstore`
- `DB_USERNAME` default `postgres`
- `DB_PASSWORD` default `postgres`
- `JWT_SECRET` default valeur de dev embarquee

## Comptes de depart

- Admin: `admin` / `secret`
- Utilisateur: `jane` / `password`
