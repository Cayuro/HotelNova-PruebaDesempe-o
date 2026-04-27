## Project Overview

Huellas Sanas is a Java 17+ desktop/console veterinary management system built with strict MVC and JDBC/DAO architecture.  
The core feature Registro Integral persists Client + Pet + Appointment in a single transactional unit to guarantee data consistency and enforce hard business rules.

## Architectural Justification: MVC Decoupling

This project enforces complete layer decoupling to maximize maintainability, testability, and security:

1. View Layer (SwingView and ConsoleView) only captures input/output and delegates to Controller.
2. Controller Layer orchestrates use cases and never accesses JDBC directly.
3. Service Layer contains all business rules and exclusive transaction management using setAutoCommit(false).
4. DAO Layer performs pure JDBC operations with PreparedStatement and try-with-resources.
5. Model Layer is framework-agnostic and reusable across View types.

Benefits:

1. Same business logic for Swing and Console channels.
2. Deterministic testing with clear seams for mocks.
3. Safer evolution of UI without touching transactional logic.
4. Controlled security policy enforcement (SHA-256, SQL injection prevention).

## SQL DDL (Core Schema with Constraints)

```sql
CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    national_id VARCHAR(30) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL UNIQUE,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','RECEPTIONIST','VET')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_user_person FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL UNIQUE,
    address VARCHAR(200),
    CONSTRAINT fk_client_person FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE vet (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL UNIQUE,
    license_number VARCHAR(40) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_vet_person FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE vet_species_qualification (
    vet_id BIGINT NOT NULL,
    species VARCHAR(20) NOT NULL CHECK (species IN ('CANINE','FELINE','AVIAN','OTHER')),
    PRIMARY KEY (vet_id, species),
    CONSTRAINT fk_vsq_vet FOREIGN KEY (vet_id) REFERENCES vet(id)
);

CREATE TABLE pet (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    species VARCHAR(20) NOT NULL CHECK (species IN ('CANINE','FELINE','AVIAN','OTHER')),
    birth_date DATE,
    sex VARCHAR(10) CHECK (sex IN ('MALE','FEMALE','UNKNOWN')),
    CONSTRAINT fk_pet_client FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    vet_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED','COMPLETED','CANCELLED')),
    clinical_note_hash CHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_pet FOREIGN KEY (pet_id) REFERENCES pet(id),
    CONSTRAINT fk_app_vet FOREIGN KEY (vet_id) REFERENCES vet(id),
    CONSTRAINT chk_time_range CHECK (end_time > start_time),
    CONSTRAINT uq_pet_start UNIQUE (pet_id, start_time),
    CONSTRAINT uq_vet_start UNIQUE (vet_id, start_time)
);
```

## Transaction Contract for Registro Integral

1. Open connection in Service.
2. setAutoCommit(false).
3. Validate all BR rules before write operations.
4. Insert/update records through DAO interfaces only.
5. Commit on success.
6. Rollback on any exception.
7. Restore auto-commit and close connection in finally.
