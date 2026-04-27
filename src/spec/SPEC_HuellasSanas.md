# 📋 SPECIFICATION: Registro Integral de Clínica Veterinaria Huellas Sanas

> Spec ID: SPEC-001  
> Author: Equipo de Arquitectura  
> Date: 2026-04-24  
> Status: [x] Draft | [ ] Approved | [ ] Implemented

## 1. Business Goal

**As a** recepcionista de la clínica,  
**I need** registrar de forma integral Cliente + Mascota + Cita en una sola operación transaccional,  
**So that** se garantice consistencia de datos, trazabilidad y cumplimiento de reglas clínicas sin conflictos de agenda.

---

## 2. Hard Business Rules

| Rule ID | Rule Description | Error Behavior |
|---------|------------------|----------------|
| BR-001 | El nombre de la mascota es obligatorio y no puede estar vacío. | Throw ValidationException("Pet name is required") |
| BR-002 | La especie de la mascota debe pertenecer al catálogo permitido (CANINE, FELINE, AVIAN, OTHER). | Throw ValidationException("Invalid pet species") |
| BR-003 | El cliente debe tener documento único (nationalId) no duplicado. | Throw DuplicateClientException("Client nationalId already exists") |
| BR-004 | El veterinario no puede tener dos citas solapadas en el mismo rango horario. | Throw ScheduleConflictException("Vet schedule conflict detected") |
| BR-005 | Una mascota no puede tener dos citas en la misma fecha y hora. | Throw ScheduleConflictException("Pet already has an appointment at the same time") |
| BR-006 | La fecha/hora de cita debe ser futura respecto al reloj del sistema. | Throw ValidationException("Appointment date must be in the future") |
| BR-007 | El estado inicial de una cita nueva debe ser SCHEDULED. | Throw BusinessRuleException("Invalid initial appointment status") |
| BR-008 | Las notas clínicas sensibles deben almacenarse cifradas con SHA-256 (hash one-way) antes de persistir. | Throw SecurityPolicyException("Clinical note hashing policy violation") |
| BR-009 | El email del cliente debe cumplir formato RFC básico. | Throw ValidationException("Invalid email format") |
| BR-010 | El teléfono del cliente debe contener solo dígitos y longitud 8-15. | Throw ValidationException("Invalid phone format") |
| BR-011 | El registro integral es atómico: si falla cualquier subproceso, no debe persistirse ningún dato parcial. | Throw TransactionRollbackException("Integral registration rolled back") |
| BR-012 | Un veterinario solo puede atender especies habilitadas en su perfil profesional. | Throw VetQualificationException("Vet is not qualified for pet species") |

### 2.1 Calculation Rules

| Calc ID | Description | Formula |
|---------|-------------|---------|
| CALC-001 | Duración estándar de cita | endTime - startTime = 30 minutes |
| CALC-002 | Hash de nota clínica | SHA-256(plainClinicalNote) |

---

## 3. Acceptance Criteria (BDD)

### Scenario 1 — Happy Path: Successful Integral Registration
Validates: BR-001, BR-002, BR-003, BR-004, BR-006, BR-007, BR-008, BR-011, CALC-002

```gherkin
Given a valid client, pet, and appointment request
And the vet is available in the requested timeslot
And all required fields are complete
When registerIntegral() is executed
Then the client is persisted
And the pet is persisted and linked to the client
And the appointment is persisted and linked to the pet and vet
And clinical notes are stored as SHA-256 hash
And the transaction is committed successfully
```

### Scenario 2 — Mandatory Pet Name Validation
Validates: BR-001

```gherkin
Given a registration request with petName as null
When registerIntegral() is executed
Then a ValidationException is thrown with message "Pet name is required"
And no record is persisted
```

### Scenario 3 — Duplicate Client Document
Validates: BR-003, BR-011

```gherkin
Given a registration request with an existing client nationalId
When registerIntegral() is executed
Then a DuplicateClientException is thrown
And transaction is rolled back
```

### Scenario 4 — Vet Schedule Conflict
Validates: BR-004, BR-011

```gherkin
Given an appointment already exists for the same vet in the same timeslot
When registerIntegral() is executed
Then a ScheduleConflictException is thrown
And transaction is rolled back
```

### Scenario 5 — Pet Appointment Conflict
Validates: BR-005, BR-011

```gherkin
Given an appointment already exists for the same pet in the same timeslot
When registerIntegral() is executed
Then a ScheduleConflictException is thrown
And transaction is rolled back
```

### Scenario 6 — Past Date Validation
Validates: BR-006

```gherkin
Given an appointment date-time in the past
When registerIntegral() is executed
Then a ValidationException is thrown with message "Appointment date must be in the future"
```

### Scenario 7 — Initial Status Enforcement
Validates: BR-007

```gherkin
Given an appointment request with initial status different from SCHEDULED
When registerIntegral() is executed
Then a BusinessRuleException is thrown
```

### Scenario 8 — SHA-256 Enforcement
Validates: BR-008, CALC-002

```gherkin
Given a plain clinical note "Paciente con dolor abdominal"
When registerIntegral() is executed
Then the stored clinical note value is not plain text
And the stored value equals SHA-256 hash output format
```

### Scenario 9 — Vet Qualification by Species
Validates: BR-012

```gherkin
Given a vet without qualification for AVIAN species
And a pet of AVIAN species
When registerIntegral() is executed
Then a VetQualificationException is thrown
And no records are persisted
```

### Scenario 10 — Boundary Validation for Phone
Validates: BR-010

```gherkin
Given a client phone with 7 digits
When registerIntegral() is executed
Then a ValidationException is thrown with message "Invalid phone format"
```

Test pattern note: test method structure must follow BusinessRuleSpecTemplateTest.java with one test per scenario and explicit assertion of exception messages.

---

## 4. UI Requirements (Swing + Console)

| Element Type | Name/Label | Bound to Field | Action/Validation |
|--------------|------------|----------------|-------------------|
| Swing Input Text | Client Name | client.fullName | Required |
| Swing Input Text | Client National ID | client.nationalId | Unique BR-003 |
| Swing Input Text | Client Email | client.email | Format BR-009 |
| Swing Input Text | Client Phone | client.phone | Format BR-010 |
| Swing Input Text | Pet Name | pet.name | Required BR-001 |
| Swing ComboBox | Pet Species | pet.species | Enum BR-002 |
| Swing ComboBox | Vet | appointment.vetId | Qualification BR-012 |
| Swing DateTime Picker | Appointment DateTime | appointment.startTime | Future BR-006 |
| Swing TextArea | Clinical Notes | appointment.clinicalNote | Hash policy BR-008 |
| Swing Button | Register Integral | N/A | Calls controller.registerIntegral() |
| Console Command | register-integral | all fields via prompts | Same validations as Swing |
| Console Output | success/error | N/A | Show transaction result only |

---

## 5. Technical Notes

### 5.1 Artifacts to Generate

| Layer | Class Name | Key Responsibility |
|-------|------------|--------------------|
| Exception | ValidationException.java | Generic input validation violations |
| Exception | DuplicateClientException.java | Unique nationalId violation |
| Exception | ScheduleConflictException.java | Vet/Pet schedule collision |
| Exception | SecurityPolicyException.java | SHA-256 enforcement |
| Exception | VetQualificationException.java | Vet species qualification violation |
| Exception | TransactionRollbackException.java | Transaction-level rollback signal |
| Model | Person.java | Base class: id, fullName, nationalId, phone, email |
| Model | User.java | Extends Person: username, passwordHash, role |
| Model | Client.java | Extends Person: address, createdAt |
| Model | Vet.java | Extends Person: licenseNumber, active, qualifiedSpecies |
| Model | Pet.java | id, clientId, name, species, birthDate, sex |
| Model | Appointment.java | id, petId, vetId, startTime, endTime, status, clinicalNoteHash |
| DAO Interface | PersonDao.java | Read/write person-related records |
| DAO Interface | ClientDao.java | Client persistence and unique checks |
| DAO Interface | VetDao.java | Vet availability and qualifications |
| DAO Interface | PetDao.java | Pet persistence and ownership checks |
| DAO Interface | AppointmentDao.java | Appointment persistence and collision checks |
| Service | IntegralRegistrationService.java | Enforce all BR rules and transaction boundaries |
| Controller | RegistroIntegralController.java | Input orchestration from Swing/Console |
| Test | IntegralRegistrationServiceSpecTest.java | One scenario = one test case |

### 5.2 Domain Model Inheritance

```text
Person
├── User
├── Client
└── Vet
```

### 5.3 DAO Interface Signatures (Strict JDBC)

```java
public interface ClientDao {
    boolean existsByNationalId(java.sql.Connection conn, String nationalId) throws java.sql.SQLException;
    long insert(java.sql.Connection conn, Client client) throws java.sql.SQLException;
    java.util.Optional<Client> findById(java.sql.Connection conn, long clientId) throws java.sql.SQLException;
}

public interface VetDao {
    boolean isAvailable(java.sql.Connection conn, long vetId, java.time.LocalDateTime start, java.time.LocalDateTime end) throws java.sql.SQLException;
    boolean isQualifiedForSpecies(java.sql.Connection conn, long vetId, String species) throws java.sql.SQLException;
    java.util.Optional<Vet> findById(java.sql.Connection conn, long vetId) throws java.sql.SQLException;
}

public interface PetDao {
    long insert(java.sql.Connection conn, Pet pet) throws java.sql.SQLException;
    boolean existsAppointmentConflict(java.sql.Connection conn, long petId, java.time.LocalDateTime start, java.time.LocalDateTime end) throws java.sql.SQLException;
    java.util.Optional<Pet> findById(java.sql.Connection conn, long petId) throws java.sql.SQLException;
}

public interface AppointmentDao {
    long insert(java.sql.Connection conn, Appointment appointment) throws java.sql.SQLException;
    java.util.Optional<Appointment> findById(java.sql.Connection conn, long appointmentId) throws java.sql.SQLException;
}
```

### 5.4 Transactional Flow — Registro Integral (setAutoCommit(false))

```text
1. Service receives IntegralRegistrationRequest.
2. Acquire Connection from ConnectionManager.
3. conn.setAutoCommit(false).
4. Validate BR-001..BR-012 in deterministic order.
5. Execute DAO operations in this order:
   5.1 insert client (or reuse existing when allowed by business mode)
   5.2 insert pet linked to client
   5.3 insert appointment linked to pet and vet
6. If all DAO operations succeed, conn.commit().
7. On any business/SQL exception:
   7.1 conn.rollback()
   7.2 throw mapped custom exception
8. In finally:
   8.1 conn.setAutoCommit(true)
   8.2 conn.close()
```

### 5.5 Security Requirements

| Security ID | Requirement |
|-------------|-------------|
| SEC-001 | Never store plain clinical notes in DB; only SHA-256 hash. |
| SEC-002 | Passwords for User model must be stored as salted hash (future auth scope). |
| SEC-003 | Use PreparedStatement only; no dynamic SQL concatenation. |
| SEC-004 | Do not expose internal SQL errors directly to View layer. |

---

## 6. Out of Scope

- Facturación y pagos.
- Gestión de inventario farmacológico.
- Reportes BI avanzados.
- Integración con APIs externas.

---

## 7. Approval

| Role | Name | Date | ✓ |
|------|------|------|---|
| Tech Lead |  |  | [ ] |
| Product Owner |  |  | [ ] |

No code generation until this spec is approved.
