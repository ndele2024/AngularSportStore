# SportStore

Application e-commerce composee de :

- un frontend Angular dans la racine du projet
- un backend officiel Spring Boot dans [backend](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/backend)
- un backend alternatif ASP.NET Core dans [backend-dotnet](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/backend-dotnet)

L'application n'utilise plus `json-server`, `data.js` ni `authMiddleware.js`.

Le checkout inclut maintenant un paiement carte simule, une facture PDF telechargeable et la preparation automatique d'emails HTML de confirmation/livraison cote backend.

## Architecture officielle

Le frontend Angular est maintenant branche definitivement sur le backend **Spring Boot + PostgreSQL** expose sur :

```text
http://localhost:3500
```

## Lancer le frontend

```bash
npm install
npm start
```

Frontend : `http://localhost:4200`

## Lancer le backend Spring Boot

Depuis la racine :

```bash
npm run backend:start
```

ou directement :

```bash
cd backend
mvn spring-boot:run
```

API Spring : `http://localhost:3500`

## Lancer les tests

Frontend Angular :

```bash
npm test
```

Backend Spring :

```bash
npm run backend:test
```

Backend .NET :

```bash
dotnet test backend-dotnet.tests/SportStore.Api.Tests.csproj
```

## Build Angular

```bash
npm run build
```

## Dossiers utiles

- [src](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/src) : frontend Angular
- [backend](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/backend) : backend Spring Boot officiel
- [backend-dotnet](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/backend-dotnet) : backend alternatif .NET
- [backend-dotnet.tests](/C:/Users/18192/OneDrive%20-%20Universit%C3%A9%20du%20Qu%C3%A9bec%20%C3%A0%20Trois-Rivi%C3%A8res/Bureau/travail_css/angular/sportStore/backend-dotnet.tests) : tests du backend .NET

## Note

Les scripts `backend:start` et `backend:test` supposent que `mvn` est disponible dans le `PATH`.
