# Carrefour Java Kata — Location de voitures (MVP)

## 1) Objectif

Ce projet est un kata dont le but est d’implémenter un mini-système de location de voitures en Java 21. Le périmètre visé ici est le MVP strict, sans fonctionnalités bonus.

- En tant que client, je peux louer une voiture.
- En tant que client, je peux rendre une voiture louée.

Ce dépôt fournit un squelette Spring Boot. Le code métier et les endpoints REST du MVP sont à implémenter selon la conception décrite ci-dessous.


## 2) Prérequis

- Java 21 (JDK 21)
- Maven 3.9+
- Git (pour la gestion de versions)


## 3) Pile technique

- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Validation Jakarta (Bean Validation)
- H2 (base de données en mémoire pour le développement)
- Tests: JUnit 5, Spring Boot Test

Extrait pertinent du `pom.xml`:

- `java.version=21`
- Dépendances: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `h2`, `spring-boot-starter-test` (test)


## 4) Décisions et hypothèses métier

- Une voiture a 2 états: `AVAILABLE` ou `LEASED`.
- Une voiture ne peut être louée que si elle est `AVAILABLE`.
- Une voiture ne peut être rendue que si une location active existe pour elle.
- Une location (`Lease`) référence un client et une voiture, avec une date de début, un statut `ACTIVE` puis `RETURNED` lors du retour.
- `startDate` par défaut = date du jour si non fournie.
- Simplifications: gestion client minimale (identifiant), pas de paiement, pas d’authentification.


## 5) Critères d’acceptation (MVP)

- Louer une voiture
  - Si la voiture est `AVAILABLE`, création d’une `Lease` active et passage de la voiture à `LEASED`.
  - Si la voiture est déjà `LEASED`, la demande est refusée (HTTP 409 ou 400 avec message clair).
  - Données minimales: `carId`, `customerId`, `startDate` (facultatif).
- Rendre une voiture
  - Si une `Lease` active est trouvée, elle passe à `RETURNED` et la voiture redevient `AVAILABLE`.
  - Sinon, réponse HTTP 404/400 avec message explicite.
- Validation des entrées et messages d’erreur clairs pour les cas invalides.


## 6) Architecture (DDD light / hexagonale)

Organisation logique recommandée (à créer/compléter dans `src/main/java`):

```
com.kata.app.car
 ├─ api              # Adaptateurs d’entrée (REST) : controllers, DTO HTTP
 ├─ application      # Cas d’usage (use cases), DTO applicatifs, services applicatifs
 ├─ domain           # Modèle métier pur : entités, value objects, services de domaine, ports
 └─ infrastructure   # Adaptateurs techniques : JPA (entities, repos Spring Data), mappers, config
```

- Couche domain (métier, sans dépendances Spring):
  - `Car` (id, plateNumber, status: AVAILABLE/LEASED)
  - `Customer` (id, name)
  - `Lease` (id, carId, customerId, startDate, returnDate?, status: ACTIVE/RETURNED)
  - Ports (interfaces) de repository: `CarRepository`, `LeaseRepository`
- Couche application (orchestration des règles):
  - Use cases: `LeaseCarUseCase`, `ReturnCarUseCase`
  - Commands/Responses pour l’API interne applicative
- Couche api (REST):
  - `LeaseController` avec endpoints décrits ci-dessous
- Couche infrastructure (JPA/H2):
  - Entités JPA, interfaces Spring Data, mappers Domain ⇄ JPA


## 7) Contrat d’API REST (prévu)

Note: les endpoints ci-dessous constituent la spécification fonctionnelle du MVP. Ils doivent être implémentés.

- Créer une location (louer une voiture)
  - `POST /api/leases`
  - Request
    ```json
    {
      "carId": "car-123",
      "customerId": "customer-456",
      "startDate": "2025-11-17"
    }
    ```
  - Responses
    - 201 CREATED
      ```json
      {
        "leaseId": "lease-1",
        "carId": "car-123",
        "customerId": "customer-456",
        "status": "ACTIVE",
        "startDate": "2025-11-17"
      }
      ```
    - 409 CONFLICT (voiture déjà louée) ou 400 BAD_REQUEST (données invalides)
      ```json
      {
        "error": "CAR_ALREADY_LEASED",
        "message": "Car car-123 is already leased."
      }
      ```

- Retourner une voiture (clore la location)
  - `POST /api/leases/{leaseId}/return`
  - Request
    ```json
    {
      "returnDate": "2025-11-19"
    }
    ```
  - Responses
    - 200 OK
      ```json
      {
        "leaseId": "lease-1",
        "carId": "car-123",
        "customerId": "customer-456",
        "status": "RETURNED",
        "startDate": "2025-11-17",
        "returnDate": "2025-11-19"
      }
      ```
    - 404 NOT_FOUND / 400 BAD_REQUEST si aucune location active n’est trouvée

- Convention d’erreur (recommandée)
  ```json
  {
    "error": "CODE",
    "message": "Description lisible"
  }
  ```


## 8) Persistance et configuration

Base de données recommandée pour le développement: H2 en mémoire.

Ajouter au besoin dans `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:h2:mem:carrental;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Console H2 (facultatif)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Schéma JPA attendu (indicatif):
- `car` (id, plate_number, status)
- `lease` (id, car_id, customer_id, start_date, return_date, status)


## 9) Démarrage local

- Build + tests
  - `mvn clean verify`
- Exécuter l’application
  - `mvn spring-boot:run`
- URL par défaut
  - `http://localhost:8080`


## 10) Tests

- Lancer tous les tests: `mvn test`
- À prévoir (MVP):
  - Tests unitaires `LeaseCarUseCase` (succès, voiture déjà louée).
  - Tests unitaires `ReturnCarUseCase` (succès, aucune location active).
  - Éventuellement un test d’intégration REST minimal par endpoint.


## 11) Structure du dépôt

Structure actuelle (principaux éléments):
```
com.kata.app.car
 ├─ api
 │   ├─ LeaseController.java
 │   ├─ dto/{LeaseCarRequest, LeaseCarResponse, ReturnCarRequest, ReturnCarResponse}
 │   └─ error/{GlobalExceptionHandler, ApiErrorResponse}
 ├─ application
 │   ├─ model/{LeaseCarCommand, ReturnCarCommand, LeaseResponse, ReturnLeaseResponse}
 │   ├─ usecase/{LeaseCarUseCase, ReturnCarUseCase}
 │   └─ exception/{CarNotFoundException, CustomerNotFoundException, LeaseNotFoundException, CarAlreadyLeasedException}
 ├─ domain
 │   ├─ model/{Car, Customer, Lease, CarStatus, LeaseStatus}
 │   └─ repository/{CarRepository, LeaseRepository, CustomerRepository}
 └─ infrastructure
     ├─ jpa/entity/{CarEntity, LeaseEntity, CustomerEntity}
     ├─ jpa/repository/{SpringDataCarRepository, SpringDataLeaseRepository, SpringDataCustomerRepository}
     ├─ adapter/{CarRepositoryAdapter, LeaseRepositoryAdapter, CustomerRepositoryAdapter}
     ├─ mapper/{CarMapper, LeaseMapper, CustomerMapper}
     └─ config/{DataInitializer}

Tests principaux:
- `src/test/java/com/kata/app/car/domain/LeaseTest.java`
- `src/test/java/com/kata/app/car/application/{LeaseCarUseCaseTest, ReturnCarUseCaseTest}.java`
- `src/test/java/com/kata/app/car/api/LeaseControllerIT.java`
```


## 12) Conventions et bonnes pratiques

- Découpage par couches (api/application/domain/infrastructure) pour limiter les couplages.
- Messages d’erreur clairs et statuts HTTP appropriés (201, 200, 400, 404, 409).
- Validation d’entrée via Bean Validation.
- Petits commits atomiques avec message explicite (ex: Conventional Commits: `feat: ...`, `fix: ...`, `test: ...`, `docs: ...`).


## 13) État du dépôt

- MVP implémenté (domaine, use cases, API REST, validation, erreurs globales).
- H2/JPA configurés et seed de données minimal.


## 14) Comment prouver le respect du MVP

- Jeux de tests unitaires couvrant les règles métier (location/rendu et erreurs).
- Exemples de requêtes HTTP (via cURL/Postman) qui démontrent les cas de succès et d’échec, par exemple:
  - **Créer une location**
    ```
    curl -X POST http://localhost:8080/api/leases \
      -H "Content-Type: application/json" \
      -d '{"carId":"<uuid-car>", "customerId":"<uuid-client>"}'
    ```
  - **Rendre une voiture**
    ```
    curl -X POST http://localhost:8080/api/leases/<leaseId>/return \
      -H "Content-Type: application/json" \
      -d '{"returnDate":"2025-11-19"}'
    ```
- README à jour (présent) décrivant les hypothèses, l’API et la procédure de lancement.


---

Pour toute question ou précision, documenter les décisions directement dans ce README afin d’assurer la cohérence fonctionnelle et technique du projet.
