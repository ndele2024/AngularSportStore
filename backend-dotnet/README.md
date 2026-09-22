# Sport Store Backend .NET

Second backend pour l'application, base sur:

- ASP.NET Core 9
- Entity Framework Core
- SQL Server
- JWT Bearer

## Demarrage

1. Lancer SQL Server (local ou Docker).
2. Ajuster la chaine de connexion dans `appsettings.json` si besoin.
3. Demarrer l'API:

```bash
dotnet run
```

L'API ecoute sur le port configure par ASP.NET Core. Pour coller au frontend actuel, lance-la idealement sur `http://localhost:3500`.

## Comptes seedes

- `admin` / `secret`
- `jane` / `password`

## Contrat

Routes compatibles avec le frontend actuel:

- `POST /login`
- `POST /register`
- `GET /products`
- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`
- `GET /orders`
- `POST /orders`
- `PUT /orders/{id}`
- `DELETE /orders/{id}`
- `GET /orders/{id}/invoice`
- `GET /users`
- `PATCH /users/{id}`

## Paiement et livraison

- paiement par carte simule au moment du checkout
- statut de commande `EN_TRAITEMENT` puis `LIVRE`
- facture PDF telechargeable pour chaque commande
- emails HTML de confirmation et de livraison prepares automatiquement dans `backend-dotnet/build/emails`
