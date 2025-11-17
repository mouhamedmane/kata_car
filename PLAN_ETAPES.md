# Plan d’étapes (MVP) — Carrefour Java Kata

Objectif: plan d’exécution complet et actionnable, focalisé sur le MVP (sans suivi du temps).

---

## Étape 1 — Créer l’arborescence des packages (DDD)

Actions :
- Créer sous `com.kata.app.car` :
  - `api` (controllers + DTO API)
  - `application` (use cases, DTO de commande/réponse applicatifs)
  - `domain` (modèle métier + ports)
  - `infrastructure` (JPA, mappers, config)

DoD :
- Packages créés et vus par l’IDE (pas de rouge).
- Un ou deux `package-info` ou commentaires pour expliquer la responsabilité des couches (bonus).

---

## Étape 2 — Configurer H2 et JPA

Actions :
- Dans `application.properties` (ou `application.yml`) :
  - URL H2 in-memory, ex.:
    - `spring.datasource.url=jdbc:h2:mem:carrental;DB_CLOSE_DELAY=-1`
    - `spring.datasource.driverClassName=org.h2.Driver`
    - `spring.datasource.username=sa`
    - `spring.datasource.password=`
  - JPA/Hibernate :
    - `spring.jpa.hibernate.ddl-auto=update` (ou `create-drop`)
    - `spring.jpa.show-sql=true` (facultatif)
  - Console H2 (si besoin) :
    - `spring.h2.console.enabled=true`
    - `spring.h2.console.path=/h2-console`

DoD :
- L’application démarre sans erreur.
- La console H2 est accessible (si activée).
- Aucune stacktrace liée à la base.

---

## Étape 3 — Modéliser le domaine (core DDD)

Actions :
- Dans `domain` :
  - Entités métier :
    - `Car` (id, plateNumber, status: `CarStatus`)
    - `Lease` (id, carId, customerId, startDate, endDatePlanned?, returnDate?, status: `LeaseStatus`)
    - `Customer` (simple, même minimal)
  - Enums :
    - `CarStatus { AVAILABLE, LEASED }`
    - `LeaseStatus { ACTIVE, RETURNED }`
  - Ports (interfaces) :
    - `CarRepository`
    - `LeaseRepository`
  - Ajouter quelques méthodes métier dans `Lease` :
    - `start(...)`, `returnLease(returnDate)`, etc.

DoD :
- Le domaine compile sans aucune dépendance à Spring.
- Les interfaces de repository sont définies côté `domain`.
- Les règles métier de base sont exprimées dans les entités (pas dans les controllers).

---

## Étape 4 — (Optionnel mais classe) Tests unitaires du domaine

Actions :
- Écrire 1 ou 2 tests unitaires simples sur `Lease` :
  - créer une lease active,
  - retour de lease → changement de status, `returnDate`.
- Utiliser JUnit 5 + AssertJ.

DoD :
- Tests de domaine verts.
- Tu peux dire en entretien : « J’ai d’abord sécurisé le cœur métier avec des tests unitaires simples ».

Note : si short en temps, cette étape peut être compressée/déplacée plus tard.

---

## Étape 5 — Infrastructure JPA

Actions :
- Dans `infrastructure.jpa` :
  - Créer `CarEntity`, `LeaseEntity` avec annotations `@Entity`.
  - Créer les interfaces Spring Data :
    - `SpringDataCarRepository extends JpaRepository<CarEntity, UUID>`
    - `SpringDataLeaseRepository extends JpaRepository<LeaseEntity, UUID>`
  - Créer des mappers Domain ⇄ JPA (`CarMapper`, `LeaseMapper`).
  - Créer des adaptateurs implémentant les ports `CarRepository`, `LeaseRepository` en s’appuyant sur les `SpringData*`.

DoD :
- L’application démarre toujours.
- Une insertion simple testée via `CommandLineRunner` ou test rapide vérifie la persistance.

---

## Étape 6 — Bootstrap de données (seed minimal)

Actions :
- Dans `infrastructure` ou `config` :
  - via `data.sql`, ou
  - via un `CommandLineRunner`.
- Insérer :
  - 2–3 voitures (`AVAILABLE`),
  - 1 ou 2 clients.

DoD :
- À l’exécution, il y a au moins quelques voitures et clients en base pour pouvoir tester l’API sans POSTs préalables.
- Vérifier dans H2 que les lignes existent.

---

## Étape 7 — Cas d’usage (Application layer)

Actions :
- Dans `application` :
  - DTO de commande :
    - `LeaseCarCommand` (carId, customerId, startDate?, endDatePlanned?)
    - `ReturnCarCommand` (leaseId ou carId, returnDate?)
  - DTO de réponse :
    - `LeaseResponse`, `ReturnLeaseResponse`
  - Use cases :
    - `LeaseCarUseCase` :
      - vérifier existence voiture + client,
      - vérifier `CarStatus == AVAILABLE`,
      - créer `Lease` active,
      - changer la voiture en `LEASED`,
      - persister via ports.
    - `ReturnCarUseCase` :
      - trouver lease active pour la voiture ou par `leaseId`,
      - passer la lease en `RETURNED`,
      - voiture en `AVAILABLE`.

DoD :
- Les règles métier principales sont codées dans `application` + `domain`, pas dans le controller.
- Le code compile.

---

## Étape 8 — API REST (Controller)

Actions :
- Dans `api` :
  - Créer les DTO API :
    - `LeaseCarRequest`, `LeaseCarResponse`
    - `ReturnCarRequest`, `ReturnCarResponse`
  - Créer `LeaseController` avec :
    - `POST /api/leases` → louer une voiture,
    - `POST /api/leases/{leaseId}/return` → rendre une voiture.
  - Mapper :
    - `LeaseCarRequest` → `LeaseCarCommand` → `LeaseResponse` → `LeaseCarResponse`,
    - `ReturnCarRequest` → `ReturnCarCommand` → `ReturnLeaseResponse`.

DoD :
- Les endpoints sont exposés (`/api/leases`, `/api/leases/{id}/return`).
- Un appel minimal via Postman/Curl retourne une réponse JSON conforme (au moins en « happy path »).

---

## Étape 9 — Validation d’entrée (Bean Validation)

Actions :
- Ajouter des annotations sur les DTO API : `@NotNull`, `@NotBlank` sur `carId`, `customerId`, etc.
- Ajouter `@Valid` sur les paramètres de méthodes dans `LeaseController`.
- Vérifier que les erreurs de validation génèrent bien un HTTP 400.

DoD :
- Une requête invalide (id manquant, champs vides) renvoie un 400 avec un message lisible.
- Pas d’exception brute type `ConstraintViolationException` non gérée dans la console.

---

## Étape 10 — Gestion d’erreurs globale

Actions :
- Créer un `@ControllerAdvice` :
  - mapper les exceptions métier (`CarAlreadyLeasedException`, `LeaseNotFoundException`, etc.) vers :
    - 400 / 404 / 409 selon le cas,
    - format de réponse d’erreur standard :
      ```json
      { "error": "CAR_ALREADY_LEASED", "message": "Car XXX is already leased" }
      ```
  - Gérer aussi les erreurs de validation si besoin.

DoD :
- Pour chaque situation d’erreur métier :
  - code HTTP cohérent,
  - corps JSON conforme au format défini,
  - aligné avec la doc dans le README.

---

## Étape 11 — Tests unitaires (use cases)

Actions :
- Tests sur `LeaseCarUseCase` :
  - cas succès : voiture dispo → lease créée,
  - cas erreur : voiture déjà louée → exception métier.
- Tests sur `ReturnCarUseCase` :
  - cas succès : lease active → retour OK,
  - cas erreur : aucune lease active → exception métier.
- Mock des repositories (Mockito ou équivalent).

DoD :
- Tous les tests use cases sont verts.
- Les règles métier principales sont couvertes par les tests.

---

## Étape 12 — Test d’intégration REST

Actions :
- Utiliser MockMvc ou WebTestClient :
  - tester `POST /api/leases` (succès, conflit),
  - tester `POST /api/leases/{leaseId}/return` (succès, pas de lease active).
- Lancer avec contexte Spring complet + H2.

DoD :
- Au moins 1–2 tests d’intégration verts pour chaque endpoint.
- Tu peux dire en entretien : « J’ai vérifié les flux de bout en bout ».

---

## Étape 13 — Build et vérification manuelle

Actions :
- Build complet :
  - Windows : `mvnw.cmd clean verify`
  - Unix/macOS : `./mvnw clean verify`
- Lancer l’app :
  - Windows : `mvnw.cmd spring-boot:run`
  - Unix/macOS : `./mvnw spring-boot:run`
- Tester « à la main » :
  - louer une voiture via Postman,
  - la rendre,
  - tester un cas d’erreur (ex. : relouer une voiture déjà louée).

DoD :
- Build OK, tests verts.
- Scénarios manuels conformes aux critères d’acceptation.

---

## Étape 14 — Mise à jour README

Actions :
- Décrire :
  - les user stories,
  - les hypothèses métier,
  - l’architecture (api / application / domain / infra),
  - les endpoints, exemples de requêtes/réponses,
  - comment lancer l’appli et les tests.
- Mentionner les bonus implémentés le cas échéant.

DoD :
- Un lecteur externe peut :
  - comprendre le modèle,
  - lancer l’application,
  - tester les fonctionnalités,
  - voir les décisions métier.
- Le README correspond réellement au code.

---

## Étape 15 — Nettoyage final

Actions :
- Renommer les éléments douteux, clarifier les noms.
- Supprimer le code mort/unused.
- Vérifier :
  - imports inutiles,
  - logs de debug excessifs.
- Reformater le code (auto-format IDE).

DoD :
- Code propre, lisible, warnings minimes.
- Projet prêt à être lu et challengé en entretien.

---

## Récapitulatif rapide (checklist)

- [ ] 1: Packages DDD créés
- [ ] 2: H2/JPA configurés
- [ ] 3: Domaine (entités, enums, ports) en place
- [ ] 4: Tests domaine (optionnel) verts
- [ ] 5: Infra JPA + mappers + adaptateurs
- [ ] 6: Seed minimal (cars, customers)
- [ ] 7: Use cases (lease/return)
- [ ] 8: API REST + DTO API + mapping
- [ ] 9: Validation d’entrée
- [ ] 10: Gestion d’erreurs globale
- [ ] 11: Tests unitaires use cases
- [ ] 12: Tests d’intégration REST
- [ ] 13: Build + vérif manuelle
- [ ] 14: README à jour
- [ ] 15: Nettoyage final
