# Carrefour Java Kata — Location de voitures (MVP)

Ce dépôt contient une implémentation fonctionnelle (MVP) d’un service de location de voitures, destinée à un exercice technique à réaliser en temps limité. L’objectif est de démontrer des choix d’architecture, de conception, de tests et de respect des contraintes.

## Rappel du cahier des charges

- **Objectif**
  - **MVP** permettant à un client de louer une voiture.
  - **MVP** permettant à un client de retourner une voiture louée.
- **Contraintes**
  - Java 21.
  - Fournir un `README.md` clair et détaillé.
  - La solution doit embarquer de la logique métier (pas une simple « passerelle »).
  - Pas de critères d’acceptation fournis : à définir et démontrer via l’analyse et les tests.
- **Livraison**
  - Code accessible dans un dépôt Git.
  - Informer de la fin via le canal requis.
- **Évaluation**
  - Qualité des choix d’implémentation, architecture, techniques, respect des contraintes.
  - Le code est personnel et doit pouvoir être défendu en entretien.

## Pile technique et prérequis

- **Langage et runtime**
  - Java 21
  - Maven Wrapper (inclus: `mvnw`, `mvnw.cmd`)
- **Frameworks et dépendances clés** (voir `pom.xml`)
  - Spring Boot 3.3.5 (Web, Validation, Data JPA)
  - Base de données en mémoire H2 (runtime)
  - Tests: Spring Boot Test, JUnit 5, Mockito (via starter test)
- **Configuration applicative** (`src/main/resources/application.properties`)
  - H2 en mémoire: `jdbc:h2:mem:carrental;DB_CLOSE_DELAY=-1`
  - DDL: `spring.jpa.hibernate.ddl-auto=update`
  - Console H2: `http://localhost:8080/h2-console` (login `sa`, mot de passe vide)

## Démarrage rapide

- **Exécuter l’application (Windows)**
  - `mvnw.cmd spring-boot:run`
- **Exécuter l’application (Linux/macOS)**
  - `./mvnw spring-boot:run`
- **Port par défaut**
  - `http://localhost:8080`
- **Construire le JAR**
  - `mvnw.cmd -DskipTests package`
  - Lancer: `java -jar target/car-0.0.1-SNAPSHOT.jar`
- **Lancer les tests**
  - `mvnw.cmd test`

## Conteneurisation (Docker)

Cette application inclut un `Dockerfile` multi-stage à la racine:

- Stage build: `maven:3.9.9-eclipse-temurin-21` (compile + package avec `-DskipTests`).
- Stage runtime: `eclipse-temurin:21-jre` (utilisateur non-root `spring`).

### Construire l’image

```bash
docker build -t kata-car:local .
```

### Lancer le conteneur

```bash
docker run --rm -p 8080:8080 --name kata-car kata-car:local
```

Accès application: `http://localhost:8080`

Console H2: `http://localhost:8080/h2-console`

### Variables d’environnement utiles

- `SPRING_PROFILES_ACTIVE` (défaut: `default`)
- `JAVA_OPTS` pour options JVM (ex: mémoire)
- `SERVER_PORT` pour changer le port d’écoute Spring Boot

Exemple:

```bash
docker run --rm -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=default \
  -e SERVER_PORT=8081 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  --name kata-car kata-car:local
```

Notes:

- L’image est multi-arch (basée sur Temurin 21). Sur Apple Silicon, pas d’option spéciale requise.
- Le stockage H2 est en mémoire; les données sont perdues à l’arrêt du conteneur.

## Architecture et organisation du code

- **Approche**: architecture hexagonale (ports/adaptateurs) inspirée DDD.
- **Couches et responsabilités**
  - **domain/**
    - Modèles métiers immuables ou contrôlés: `Car`, `Customer`, `Lease`, enums `CarStatus`, `LeaseStatus`.
    - Ports (interfaces) de persistance: `CarRepository`, `CustomerRepository`, `LeaseRepository`.
    - Invariants métier et règles localisées dans le domaine (ex: transitions de statut, validations de dates).
  - **application/**
    - Cas d’usage transactionnels: `LeaseCarUseCase`, `ReturnCarUseCase`.
    - Modèles d’E/S pour l’application: `LeaseCarCommand`, `LeaseResponse`, `ReturnCarCommand`, `ReturnLeaseResponse`.
    - Exceptions applicatives: `CarNotFoundException`, `CustomerNotFoundException`, `LeaseNotFoundException`, `LeaseNotActiveException`, `CarAlreadyLeasedException`.
  - **api/**
    - Contrôleur REST: `LeaseController`.
    - DTO d’API: `LeaseCarRequest`, `LeaseCarResponse`, `ReturnCarRequest`, `ReturnCarResponse`.
    - Gestion globale des erreurs: `GlobalExceptionHandler` → codes HTTP cohérents + payload d’erreur uniforme (`ApiErrorResponse`).
  - **infrastructure/**
    - JPA Entities: `CarEntity`, `CustomerEntity`, `LeaseEntity`.
    - Spring Data repositories: `SpringDataCarRepository`, `SpringDataCustomerRepository`, `SpringDataLeaseRepository` (avec verrous pessimistes sur les lectures critiques).
    - Adapters des ports vers JPA: `CarRepositoryAdapter`, `CustomerRepositoryAdapter`, `LeaseRepositoryAdapter`.
    - Mappers domaine ↔ JPA: `CarMapper`, `CustomerMapper`, `LeaseMapper`.
    - `DataInitializer`: jeu de données minimal (3 voitures disponibles, 2 clients) au démarrage.

### Pourquoi ces choix ?

- **Hexagonal**: sépare le cœur métier des détails techniques (facilite tests, évolutivité, remplacements d’infra).
- **Invariants dans le domaine**: garantit la cohérence métier indépendamment des contrôleurs et de l’ORM.
- **Verrouillage pessimiste** (`@Lock(PESSIMISTIC_WRITE)`): évite les conflits de concurrence sur la location/retour.
- **DTO dédiés**: contrat d’API clair et stable, découplé du domaine et de la persistance.

## Règles métier (MVP)

- **Louer une voiture**
  - **Pré-conditions**
    - La voiture existe.
    - Le client existe.
    - Aucune location active pour cette voiture (contrôle via `LeaseRepository.findActiveByCarId`).
    - La voiture a le statut `AVAILABLE`.
    - `startDate` est optionnelle → défaut: aujourd’hui.
    - `endDatePlanned` est optionnelle mais ne peut pas être antérieure à `startDate`.
  - **Actions**
    - Création d’un `Lease` (statut `ACTIVE`).
    - Passage de la `Car` au statut `LEASED`.
    - Persistance de la location et de la voiture.
  - **Résultat**
    - HTTP 201 Created, header `Location: /api/leases/{leaseId}`.

- **Retourner une voiture**
  - **Pré-conditions**
    - La location existe.
    - La location est `ACTIVE`.
    - `returnDate` est optionnelle → défaut: aujourd’hui.
    - `returnDate` ne peut pas être antérieure à `startDate` de la location.
  - **Actions**
    - Clôture de la location (`RETURNED`, `returnDate` renseignée).
    - Passage de la `Car` au statut `AVAILABLE`.
    - Persistance de la location et de la voiture.
  - **Résultat**
    - HTTP 200 OK.

## Critères d’acceptation (extraits)

- **Louer une voiture disponible**
  - Given une voiture au statut `AVAILABLE` et un client existant
  - When j’appelle `POST /api/leases`
  - Then je reçois 201 + un `leaseId`, la voiture passe à `LEASED`.
- **Conflit sur seconde location**
  - Given une voiture déjà louée
  - When j’appelle `POST /api/leases` à nouveau
  - Then je reçois 409 `CAR_ALREADY_LEASED`.
- **Retourner une voiture**
  - Given une location `ACTIVE`
  - When j’appelle `POST /api/leases/{leaseId}/return`
  - Then je reçois 200 et la location passe à `RETURNED`, la voiture à `AVAILABLE`.
- **Retour d’une location inconnue** → 404 `LEASE_NOT_FOUND`.

Ces critères sont couverts par des tests unitaires et d’intégration (voir section Tests).

## Contrat d’API

- **Base URL**: `http://localhost:8080`

### 1) Louer une voiture

- **POST** `/api/leases`
- **Request body (JSON)**
```json
{
  "carId": "2f0e7c2a-...-...-...",
  "customerId": "7a3f65f1-...-...-...",
  "startDate": "2025-11-17",          
  "endDatePlanned": "2025-11-30"      
}
```
- `carId` et `customerId` obligatoires. `startDate` et `endDatePlanned` optionnels.
- **Réponses**
  - 201 Created
```json
{
  "leaseId": "d0b6f2a1-...",
  "carId": "2f0e7c2a-...",
  "customerId": "7a3f65f1-...",
  "status": "ACTIVE",
  "startDate": "2025-11-17"
}
```
  - 400 `VALIDATION_ERROR` (ex. corps invalide)
  - 404 `CAR_NOT_FOUND` | `CUSTOMER_NOT_FOUND`
  - 409 `CAR_ALREADY_LEASED`

### 2) Retourner une voiture

- **POST** `/api/leases/{leaseId}/return`
- **Request body (JSON)**
```json
{
  "returnDate": "2025-11-20"          
}
```
- `returnDate` optionnel (défaut = aujourd’hui).
- **Réponses**
  - 200 OK
```json
{
  "leaseId": "d0b6f2a1-...",
  "carId": "2f0e7c2a-...",
  "customerId": "7a3f65f1-...",
  "status": "RETURNED",
  "startDate": "2025-11-17",
  "returnDate": "2025-11-20"
}
```
  - 400 `BAD_REQUEST` | `VALIDATION_ERROR`
  - 404 `LEASE_NOT_FOUND`
  - 409 `LEASE_NOT_ACTIVE`

### Format d’erreur uniforme

```json
{
  "error": "CAR_ALREADY_LEASED",
  "message": "Car {carId} is already leased"
}
```

## Données, persistance et initialisation

- **H2 en mémoire** (réinitialisé à chaque run)
  - URL JDBC: `jdbc:h2:mem:carrental;DB_CLOSE_DELAY=-1`
  - Utilisateur: `sa` / mot de passe: vide
- **Console H2**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:carrental`
  - Requêtes utiles:
    - `SELECT * FROM CAR;`
    - `SELECT * FROM CUSTOMER;`
    - `SELECT * FROM LEASE;`
- **Jeu de données** (au premier démarrage et si vide)
  - 3 voitures: `AA-123-AA`, `BB-456-BB`, `CC-789-CC` (statut `AVAILABLE`).
  - 2 clients: `Alice`, `Bob`.

## Exemples d’utilisation (cURL)

1) Récupérer des identifiants via la console H2
- Ouvrir `http://localhost:8080/h2-console` → se connecter.
- Exécuter:
  - `SELECT id, plate_number, status FROM CAR;`
  - `SELECT id, name FROM CUSTOMER;`
- Copier un `carId` (statut `AVAILABLE`) et un `customerId`.

2) Louer la voiture
```bash
curl -i -X POST "http://localhost:8080/api/leases" \
  -H "Content-Type: application/json" \
  -d '{
    "carId": "<UUID-CAR>",
    "customerId": "<UUID-CUSTOMER>",
    "startDate": "2025-11-17"
  }'
```
- Attendu: `201 Created` + JSON contenant `leaseId`.

3) Rejouer une location sur la même voiture (conflit attendu)
```bash
curl -i -X POST "http://localhost:8080/api/leases" \
  -H "Content-Type: application/json" \
  -d '{
    "carId": "<UUID-CAR>",
    "customerId": "<UUID-CUSTOMER>"
  }'
```
- Attendu: `409 Conflict` avec `error: CAR_ALREADY_LEASED`.

4) Retourner la voiture
```bash
curl -i -X POST "http://localhost:8080/api/leases/<UUID-LEASE>/return" \
  -H "Content-Type: application/json" \
  -d '{
    "returnDate": "2025-11-20"
  }'
```
- Attendu: `200 OK` + JSON avec `status: RETURNED`.

## Tests

- **Unitaires (domaine et application)**
  - `LeaseTest`: vérifie la création de location, les transitions de statut, et les validations de dates.
  - `LeaseCarUseCaseTest`: succès de location, conflits (voiture déjà louée, location active existante), changements d’état de la voiture.
  - `ReturnCarUseCaseTest`: succès de retour, location inconnue, location non active.
- **Intégration (API)**
  - `LeaseControllerIT`: scénario bout-en-bout location → conflit → retour, et 404 sur retour d’une location inconnue.
- **Commande**
  - `mvnw.cmd test`

## Décisions, compromis et limites

- **Verrouillage pessimiste** sur la voiture et la location lors des opérations critiques pour éviter les doubles locations concurrentes.
- **Validation des invariants** dans le domaine (ex: `endDatePlanned >= startDate`, `returnDate >= startDate`).
- **H2 en mémoire** pour la simplicité (données volatiles à chaque run).
- **Limites actuelles**
  - Pas d’API de lecture (liste des voitures/clients/locations)
  - Pas de tarification, ni de pénalités, ni de prolongation.
  - Pas d’authentification/autorisation.
  - Gestion de fuseaux horaires simplifiée (dates en `LocalDate`).

## Pistes d’amélioration (bonus)

- **API de consultation**: lister voitures disponibles, détails d’une location, historique client.
- **Règles avancées**: calcul de prix, pénalités de retard, prolongations, cancellations, réservations.
- **Robustesse**: gestion plus fine des erreurs et des validations (Bean Validation sur davantage de DTOs).
- **Observabilité**: logs structurés, métriques, traces.
- **Persistance**: bascule facile vers PostgreSQL/MySQL via Spring Data (profil `prod`).
- **API Contract First**: description OpenAPI/Swagger + génération client.

## Structure des principaux fichiers

```
src/main/java/com/kata/app/car/
├─ api/
│  ├─ LeaseController.java
│  ├─ dto/ (LeaseCarRequest, LeaseCarResponse, ReturnCarRequest, ReturnCarResponse)
│  └─ error/ (GlobalExceptionHandler, ApiErrorResponse)
├─ application/
│  ├─ usecase/ (LeaseCarUseCase, ReturnCarUseCase)
│  ├─ model/ (LeaseCarCommand, LeaseResponse, ReturnCarCommand, ReturnLeaseResponse)
│  └─ exception/ (...)
├─ domain/
│  ├─ model/ (Car, Customer, Lease, CarStatus, LeaseStatus)
│  └─ repository/ (CarRepository, CustomerRepository, LeaseRepository)
└─ infrastructure/
   ├─ jpa/entity/ (CarEntity, CustomerEntity, LeaseEntity)
   ├─ jpa/repository/ (SpringDataCarRepository, SpringDataCustomerRepository, SpringDataLeaseRepository)
   ├─ adapter/ (CarRepositoryAdapter, CustomerRepositoryAdapter, LeaseRepositoryAdapter)
   ├─ mapper/ (CarMapper, CustomerMapper, LeaseMapper)
   └─ config/ (DataInitializer)
```

## Conseils d’utilisation et de debug

- **Console H2** pour inspecter les données et récupérer les UUID.
- **Logs SQL** activés (`spring.jpa.show-sql=true`).
- **Statuts HTTP** significatifs et payloads d’erreur explicites.

## Licence

Projet fourni dans le cadre d’un exercice technique. Usage pédagogique/d’évaluation.
