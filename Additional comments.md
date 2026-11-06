# Prescriptions: solution notes

## Run
```bash
docker compose up -d        # Postgres
./mvnw spring-boot:run      # app on :8080
./mvnw test                 # tests (in-memory H2)
```
`requests.http` contains example calls (VS Code REST Client), including every error case.

## What was built
| Endpoint | Result |
|---|---|
| `POST /prescriptions` | `201` + `Location` header |
| `GET /prescriptions` | `200`, sorted by `timeOfDay` |
| `GET /prescriptions/{id}` | `200`, or `404` |

```json
{ "id": 1, "medication": { "id": 1, "name": "Aspirin", "dosage": "100mg" }, "timeOfDay": "08:00", "label": "with breakfast" }
```


## Decisions and assumptions
- **Unknown `medicationId` → 422**: the body is well-formed and the URL exists, but the reference is invalid.
- **Missing or invalid fields → 400**, listing each invalid field.
- **`timeOfDay` is in `HH:mm` format** in local wall-clock time, with no timezone.
- **`label` is optional**: trimmed, blank stored as `null`, max 100 characters.
- **Same medication at the same time → 409.** The service checks first, and a database unique constraint covers concurrent requests.
- **Flat URLs** (`/prescriptions`): a prescription is its own resource, and a medication is a catalogue entry.
- **Schema via Hibernate `ddl-auto`**, as in the template.

## Trade-offs
- `ddl-auto` is quick but not production-safe. 
- Tests are `@Transactional` for isolation. 
- `MedicationController` still calls its repository directly, since it has no business rules.
- The existing `MedicationControllerTest` no longer hard-codes ids 1 and 2. The H2 database is shared between test classes, so ids depend on the order tests run in.

## With more time
- **Update and delete endpoints** (`PUT`/`DELETE /prescriptions/{id}`), so a prescription can be changed or removed instead of only adding new ones.
- **Proper schema management**: replace Hibernate generating tables from the Java entities (`ddl-auto`) with versioned SQL migrations (Flyway), and set Hibernate to `validate`.
- **More edge-case tests**: label limits, other time formats, concurrent duplicate requests, and tests against real Postgres (Testcontainers).
- **A simple UI** for creating and viewing prescriptions, or generated Swagger/OpenAPI docs as a quick way to try the API.


## AI usage
I used Claude Code for explaining the codebase and pair programmer, since Java, Spring, JPA and Docker within the databases were new to me. I made the design decisions above after discussing options with it. I wrote the entity, repository, DTOs, service and controller with its explanations. It generated the exception classes, error handler, tests and `requests.http`, which I reviewed, trimmed and checked by running the tests and calling the API manually.
