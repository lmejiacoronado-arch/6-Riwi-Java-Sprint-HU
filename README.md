# Eventify - Persistent and Scalable Catalog API

Eventify is a Spring Boot project created as the architectural foundation for an event management platform.

This project started with an in-memory catalog and was evolved into a persistent and scalable REST API using Spring Data JPA, Hibernate, PostgreSQL, Flyway, pagination, sorting, Swagger documentation, and automated tests.

---

## User Story M6.1S1

### Architectural Foundation of Eventify - Base Catalog and Stereotypes

As a system administrator, I want to register and view events and venues in an organized way, so I can validate the initial catalog logic and ensure the stability of the base architecture.

### Completed Work

The first user story implemented the initial layered architecture using Spring MVC.

Implemented:

- Spring Boot project setup
- Spring Web MVC
- Lombok
- Constructor-based dependency injection
- Model classes for Event and Venue
- Repository layer
- Service layer with business validations
- REST controllers
- Initial seed data using `@Configuration` and `@Bean`
- Swagger/OpenAPI documentation
- Unit tests with JUnit 5 and Mockito

---

## User Story M6.1S2

### Strategic Persistence of Eventify - From Memory to Database

As a catalog administrator, I want to manage the full lifecycle of records in a real database, so the information is persistent, editable, and easy to query at scale.

### Completed Work

The second user story evolved Eventify from temporary in-memory persistence to a real database-backed application.

Implemented:

- Spring Data JPA
- Hibernate
- PostgreSQL database
- Docker Compose for local database setup
- Flyway database migrations
- JPA entities using `@Entity` and `@Table`
- Primary keys with `@Id` and `@GeneratedValue`
- Column restrictions using `@Column`
- Repositories extending `JpaRepository`
- Derived queries using `findByNameContainingIgnoreCase`
- Full CRUD operations
- `404 Not Found` handling for missing resources
- `204 No Content` response for successful deletes
- Pagination and sorting using `Pageable` and `Sort`
- Swagger documentation for pagination and error responses
- Repository integration tests using `@DataJpaTest`

---

## Main Technologies

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Docker Compose
- Lombok
- Springdoc OpenAPI / Swagger
- JUnit 5
- Mockito
- H2 for repository tests
- Maven

---

## Project Architecture

The project follows a layered Spring MVC architecture:

```text
Controller → Service → Repository → Database
```

### Layers

#### Model Layer

Contains the JPA entities used by the application:

- `Event`
- `Venue`

#### Repository Layer

Uses Spring Data JPA repositories to communicate with the database.

Implemented repositories:

- `EventRepository`
- `VenueRepository`

These repositories extend `JpaRepository` and include derived queries such as:

```java
findByNameContainingIgnoreCase(String name)
```

#### Service Layer

Contains business logic and validation rules.

Implemented services:

- `EventService`
- `VenueService`

Business validations include:

- Event name cannot be empty.
- Event date cannot be null.
- Venue name cannot be empty.
- Venue address cannot be empty.
- Venue capacity must be greater than zero.
- Existing resources must be validated before update or delete operations.

#### Controller Layer

Exposes REST API endpoints using `@RestController`.

Implemented controllers:

- `EventController`
- `VenueController`

#### Configuration Layer

Contains application configuration classes.

Implemented configuration:

- `DataSeederConfig`

This configuration loads initial data only when needed.

#### Exception Layer

Handles controlled API errors.

Implemented exceptions and handlers:

- `ResourceNotFoundException`
- `GlobalExceptionHandler`

---

## Database Persistence

The application uses PostgreSQL as the main database.

Flyway is responsible for creating and managing the database schema through migration files.

The initial migration creates the following tables:

- `events`
- `venues`

The database is started locally using Docker Compose.

---

## API Endpoints

### Events

| Method | Endpoint | Description | Expected Status |
|--------|----------|-------------|-----------------|
| POST | `/api/events` | Create a new event | `201 Created` |
| GET | `/api/events` | Get paginated events | `200 OK` |
| GET | `/api/events/{id}` | Get event by ID | `200 OK` or `404 Not Found` |
| PUT | `/api/events/{id}` | Update event by ID | `200 OK` or `404 Not Found` |
| DELETE | `/api/events/{id}` | Delete event by ID | `204 No Content` or `404 Not Found` |

### Venues

| Method | Endpoint | Description | Expected Status |
|--------|----------|-------------|-----------------|
| POST | `/api/venues` | Create a new venue | `201 Created` |
| GET | `/api/venues` | Get paginated venues | `200 OK` |
| GET | `/api/venues/{id}` | Get venue by ID | `200 OK` or `404 Not Found` |
| PUT | `/api/venues/{id}` | Update venue by ID | `200 OK` or `404 Not Found` |
| DELETE | `/api/venues/{id}` | Delete venue by ID | `204 No Content` or `404 Not Found` |

---

## Pagination and Sorting

The list endpoints support pagination and sorting using query parameters.

Example:

```text
GET /api/events?page=0&size=5&sort=name,asc
```

This means:

```text
page=0          first page
size=5          five records per page
sort=name,asc   sort by name in ascending order
```

Another example:

```text
GET /api/events?page=1&size=10&sort=date,desc
```

The paginated response includes metadata such as:

- `content`
- `totalElements`
- `totalPages`
- `size`
- `number`
- `first`
- `last`
- `empty`

---

## HTTP Status Codes

| Status Code | Meaning | When it happens |
|-------------|---------|-----------------|
| `200 OK` | Successful request | When listing, finding, or updating resources |
| `201 Created` | Resource created successfully | When creating a valid event or venue |
| `204 No Content` | Resource deleted successfully | When deleting an existing event or venue |
| `400 Bad Request` | Invalid request data | When business validation fails |
| `404 Not Found` | Resource does not exist | When an ID is not found |

---

## Example Requests

### Create an Event

```json
{
  "name": "Java Conference",
  "date": "2026-06-10",
  "description": "Technology event focused on Java and Spring Boot"
}
```

Expected response:

```text
201 Created
```

---

### Update an Event

```json
{
  "name": "Updated Java Conference",
  "date": "2026-06-15",
  "description": "Updated technology event"
}
```

Endpoint:

```text
PUT /api/events/{id}
```

Expected response:

```text
200 OK
```

---

### Delete an Event

Endpoint:

```text
DELETE /api/events/{id}
```

Expected response:

```text
204 No Content
```

---

### Create a Venue

```json
{
  "name": "Main Auditorium",
  "address": "123 Main Street",
  "capacity": 500
}
```

Expected response:

```text
201 Created
```

---

### Invalid Venue Example

```json
{
  "name": "Small Room",
  "address": "123 Main Street",
  "capacity": 0
}
```

Expected response:

```text
400 Bad Request
```

---

### Resource Not Found Example

Endpoint:

```text
GET /api/events/9999
```

Expected response:

```text
404 Not Found
```

---

## Swagger Documentation

Swagger/OpenAPI is available when the application is running.

Open one of these URLs:

```text
http://localhost:8080/swagger-ui.html
```

or:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger documents:

- CRUD endpoints
- Pagination parameters
- Sorting parameters
- Success responses
- Error responses such as `400`, `404`, and `204`

---

## How to Run the Application

### Requirements

Make sure you have installed:

- Java 21
- Maven or Maven Wrapper
- Docker
- Docker Compose
- Git

---

### 1. Clone the Repository

```bash
git clone <repository-url>
```

Enter the project folder:

```bash
cd eventify
```

---

### 2. Start PostgreSQL with Docker Compose

From the project root, run:

```bash
docker compose up -d
```

Check that the container is running:

```bash
docker ps
```

---

### 3. Run the Application

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application will start by default at:

```text
http://localhost:8080
```

When the application starts, Flyway runs the database migrations automatically.

---

### 4. Stop the Database

When you finish working, you can stop the PostgreSQL container with:

```bash
docker compose down
```

If you want to remove the database volume and delete the stored data, run:

```bash
docker compose down -v
```

Use `-v` carefully because it removes the persisted database data.

---

## How to Run the Tests

The project includes:

- Service unit tests with JUnit 5 and Mockito
- Repository integration tests with `@DataJpaTest`
- H2 in-memory database for repository tests

---

### Run All Tests

On Linux or macOS:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

Example:

```text
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
```

---

### Run Only Service Tests

Example:

```bash
./mvnw -Dtest=EventServiceTest,VenueServiceTest test
```

---

### Run Only Repository Tests

Example:

```bash
./mvnw -Dtest=EventRepositoryTest,VenueRepositoryTest test
```

---

## Testing Strategy

### Service Tests

Service tests use Mockito to isolate the service layer from the repository layer.

They validate:

- Creating valid events and venues
- Rejecting invalid data
- Preventing invalid data from reaching the repository
- Finding resources by ID
- Updating existing resources
- Deleting existing resources
- Handling missing resources

### Repository Tests

Repository tests use `@DataJpaTest`.

They validate:

- Entities are saved correctly.
- IDs are generated automatically.
- Records can be found by ID.
- Derived queries work correctly.
- Pagination works correctly.
- Records can be deleted.

The test profile uses H2 and disables Flyway for faster isolated testing.

---

## Acceptance Criteria Covered

### M6.1S1

#### Scenario 1: Successful Registration

A valid event can be created through:

```text
POST /api/events
```

The system validates the data, stores it, and returns:

```text
201 Created
```

#### Scenario 2: Invalid Registration

An event with an empty name is rejected.

The system returns:

```text
400 Bad Request
```

#### Scenario 3: Empty Catalog Query

If no data is loaded, the list endpoint can return an empty list or empty page with:

```text
200 OK
```

#### Scenario 4: Documentation Verification

Swagger displays all available endpoints.

---

### M6.1S2

#### Scenario 1: Persistence After Restart

Data is stored in PostgreSQL and remains available after restarting the application, as long as the Docker volume is not removed.

#### Scenario 2: Access to Non-Existing Resource

Requests using a non-existing ID return:

```text
404 Not Found
```

This applies to:

```text
GET /api/events/{id}
PUT /api/events/{id}
DELETE /api/events/{id}
```

and the equivalent venue endpoints.

#### Scenario 3: Pagination of Results

The list endpoints support:

```text
page
size
sort
```

Example:

```text
GET /api/events?page=0&size=5&sort=name,asc
```

The response includes five records and pagination metadata.

#### Scenario 4: Successful Deletion

Deleting an existing event or venue returns:

```text
204 No Content
```

---

## Current Project Status

Eventify now includes:

- Persistent PostgreSQL database
- Flyway database migrations
- JPA entities
- JpaRepository interfaces
- Full CRUD operations
- Pagination and sorting
- Swagger documentation
- Controlled error handling
- Service unit tests
- Repository integration tests

The project has evolved from a temporary in-memory catalog into a persistent and scalable REST API.
