# Blumenladen – REST-Microservice mit Spring Boot, Kubernetes und Terraform

![Java](https://img.shields.io/badge/Java-26-b07219)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Helm-326CE5)
![Terraform](https://img.shields.io/badge/Terraform-1.15-7B42BC)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)

Ein Microservice zur Verwaltung von Blumenläden mit Filialleitung und Sortiment. Entstanden im Modul
**Softwarearchitektur** an der Hochschule Karlsruhe (Sommersemester 2026). Er läuft lokal mit Docker Compose
und auf einem Kubernetes-Cluster, ausgerollt mit Helm und Terraform.

## Entstehung

Grundlage ist das Beispielprojekt „Kunde“ von Prof. Jürgen Zimmermann (HKA), lizenziert unter GPL-3.0.
Architektur, Build-Konfiguration und die Deployment-Vorlagen stammen aus diesem Beispiel.

Selbst entwickelt bzw. angepasst habe ich:

- **Domäne und Code**: eigenes Datenmodell `Blumenladen` → `Leiter` (1:1) und `Blume` (1:N) mit Entities, DTOs,
  Mapper, Repository, Read-/Write-Services und Controllern
- **Datenbank**: Flyway-Migrationen für Schema, Indizes und Testdaten
- **Build**: Umstellung von Gradle auf Maven
- **Deployment**: Helm-Chart und Terraform-Konfiguration für den Blumenladen angepasst (u. a. Rolling Update mit
  `maxUnavailable: 0`, angepasste `progressDeadlineSeconds`, schlankere ConfigMap); wiederholtes Ausrollen und
  Abbauen auf einem lokalen Kubernetes-Cluster per Terraform
- **Qualitätssicherung**: SonarQube als Docker-Compose-Stack ergänzt; Prüfung des Helm-Charts mit kube-linter,
  kubescape, Polaris und Pluto

## Funktionen

- CRUD für Blumenläden mit Filialleitung und Sortiment
- Suche nach Namensbestandteil
- Bedingtes GET über `ETag` / `If-None-Match` → `304 Not Modified`
- Optimistische Sperre über `If-Match` → `412 Precondition Failed` bei parallelen Änderungen
- Fehlerantworten als ProblemDetail nach RFC 9457
- API-Versionierung über den Header `X-Version`

## Technologiestack

| Bereich     | Eingesetzt                                                                   |
|-------------|------------------------------------------------------------------------------|
| Sprache     | Java 26 (Preview-Features aktiviert)                                         |
| Framework   | Spring Boot 4.1, Spring MVC, Spring Data JPA                                 |
| Persistenz  | PostgreSQL 18, Hibernate, Flyway                                             |
| Build       | Maven (Wrapper enthalten), Container-Image mit Cloud Native Buildpacks       |
| Tests       | JUnit 5, AssertJ, JaCoCo                                                     |
| Codeanalyse | Checkstyle, PMD, SpotBugs, NullAway/JSpecify, SonarQube                      |
| Betrieb     | Docker Compose, Kubernetes, Helm, Terraform, Gateway (kgateway / Traefik)    |
| Doku        | PlantUML (Klassen-, Komponenten-, Sequenzdiagramme, Use Cases), AsciiDoc     |

## Projektstruktur

```text
src/main/java/com/acme/blumenladen
├── controller   REST-Controller, DTOs, Mapper
├── service      Geschäftslogik (Lesen / Schreiben getrennt)
├── repository   Spring Data JPA
├── entity       Blumenladen, Leiter, Blume
└── config       Spring-Konfiguration
src/main/resources/db/migration   Flyway-Skripte
extras/
├── compose      Docker Compose: PostgreSQL, Service, SonarQube
├── helm         Helm-Chart des Service
├── terraform    Deployment des Helm-Charts per Terraform
├── kubernetes   Manifeste und Prüfskripte (kube-linter, kubescape, Polaris, Pluto)
├── gateway      Routing über kgateway bzw. Traefik
├── bruno        API-Requests für v1 und v2
└── doc          Projekthandbuch und PlantUML-Diagramme
```

## Lokal starten

Vorausgesetzt sind ein JDK 26, Docker und OpenSSL. Schlüssel, Zertifikate und Passwörter liegen bewusst nicht im
Repository und werden einmalig lokal erzeugt.

**1. Zertifikat für den Service erzeugen** (der Service läuft über HTTPS):

```bash
openssl req -x509 -newkey rsa:2048 -nodes -days 365 \
  -keyout src/main/resources/private-key.pem \
  -out src/main/resources/certificate.crt \
  -subj "/CN=localhost" -addext "subjectAltName=DNS:localhost"
```

**2. TLS-Dateien und Passwort für PostgreSQL anlegen:**

```bash
openssl req -x509 -newkey rsa:2048 -nodes -days 365 \
  -keyout extras/compose/postgres/init/tls/server.key \
  -out extras/compose/postgres/init/tls/server.crt \
  -subj "/CN=postgres"
echo "p" > extras/compose/postgres/password.txt
```

**3. PostgreSQL starten:**

```bash
cd extras/compose/postgres
docker compose up
```

**4. Service starten** – Flyway legt Schema und Testdaten an:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

Die API ist anschließend unter `https://localhost:8443/api` erreichbar. Beispiel-Requests liegen in
`extras/bruno`.

## Deployment auf Kubernetes

Voraussetzung ist ein lokaler Cluster (z. B. Docker Desktop, kind oder minikube) mit dem Namespace `acme`.

```bash
# Container-Image bauen
./mvnw spring-boot:build-image -Dmaven.test.skip=true

# Helm-Chart per Terraform ausrollen
cd extras/terraform
terraform init
terraform plan
terraform apply -auto-approve

# wieder abbauen
terraform destroy -auto-approve
```

Alternativ direkt mit Helm aus `extras/helm/blumenladen`. Details stehen in den ReadMe-Dateien unter `extras/`.

## Lizenz

GPL-3.0, siehe [LICENSE](LICENSE). Das zugrundeliegende Beispielprojekt stammt von Jürgen Zimmermann, siehe
[AUTHORS](AUTHORS).
