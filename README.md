# Eventify - Architectural Foundation

Eventify is a Spring Boot project created as the architectural foundation for an event management platform.

This first user story implements the base catalog for managing events and venues using Spring MVC, layered architecture, in-memory persistence, Swagger documentation, and unit testing.

---

## User Story M6.1S1

### Architectural Foundation of Eventify - Base Catalog and Stereotypes

As a system administrator, I want to register and view events and venues in an organized way, so I can validate the initial catalog logic and ensure the stability of the base architecture.

---

## Project Goals

This project implements the first technical foundation of Eventify using:

- Spring Boot
- Spring MVC
- Constructor-based dependency injection
- Spring stereotypes
- In-memory repositories
- Swagger/OpenAPI documentation
- JUnit 5 and Mockito unit tests

---

## Implemented Features

### Events

The application allows managing events with the following attributes:

- id
- name
- date
- description

Available operations:

- Create an event
- List all events

### Venues

The application allows managing venues with the following attributes:

- id
- name
- address
- capacity

Available operations:

- Create a venue
- List all venues

---

## Architecture

The project follows a layered Spring MVC architecture:

```text
Controller -> Service -> Repository -> In-memory collection
```

### Layers

#### Model Layer

Contains the base POJOs used by the application:

- Event
- Venue

#### Repository Layer

Uses `@Repository` classes to simulate persistence with in-memory collections.

Implemented repositories:

- EventRepository
- VenueRepository

#### Service Layer

Uses `@Service` classes to manage business logic and validation rules.

Implemented services:

- EventService
- VenueService

Business validations include:

- Event name cannot be empty.
- Event date cannot be null.
- Venue name cannot be empty.
- Venue address cannot be empty.
- Venue capacity must be greater than zero.

#### Controller Layer

Uses `@RestController` classes to expose REST API endpoints.

Implemented controllers:

- EventController
- VenueController

#### Configuration Layer

Uses `@Configuration` and `@Bean` to load initial seed data when the application starts.

Implemented configuration:

- DataSeederConfig

#### Exception Handling

A global exception handler was implemented to return controlled error responses when business validations fail.

Implemented class:

- GlobalExceptionHandler

---

## API Endpoints

### Events

| Method | Endpoint | Description | Expected Status |
|--------|----------|-------------|-----------------|
| GET | `/api/events` | Get all events | `200 OK` |
| POST | `/api/events` | Create a new event | `201 Created` |

### Venues

| Method | Endpoint | Description | Expected Status |
|--------|----------|-------------|-----------------|
| GET | `/api/venues` | Get all venues | `200 OK` |
| POST | `/api/venues` | Create a new venue | `201 Created` |

---

## HTTP Status Codes

The application handles the following HTTP status codes:

| Status Code | Meaning | When it happens |
|-------------|---------|-----------------|
| `200 OK` | Successful request | When listing events or venues |
| `201 Created` | Resource created successfully | When creating a valid event or venue |
| `400 Bad Request` | Invalid request data | When business validation fails |

---

## Example Requests

### Create an Event

```json
{
  "id": 1,
  "name": "Java Conference",
  "date": "2026-06-10",
  "description": "Technology event focused on Java and Spring Boot"
}
```

Expected response:

```text
201 Created
```

### Create an Invalid Event

```json
{
  "id": 2,
  "name": "",
  "date": "2026-06-10",
  "description": "Invalid event"
}
```

Expected response:

```text
400 Bad Request
```

### Create a Venue

```json
{
  "id": 1,
  "name": "Main Auditorium",
  "address": "123 Main Street",
  "capacity": 500
}
```

Expected response:

```text
201 Created
```

### Create an Invalid Venue

```json
{
  "id": 2,
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

## Swagger Documentation

Swagger/OpenAPI was configured using Springdoc.

After running the application, the API documentation can be accessed at:

```text
http://localhost:8080/swagger-ui.html
```

or:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger allows testing the available `GET` and `POST` endpoints directly from the browser.

---

## How to Run the Application

### Requirements

Make sure you have installed:

- Java 17 or higher
- Maven
- Git

### Clone the Repository

```bash
git clone <repository-url>
```

Enter the project folder:

```bash
cd eventify
```

### Run the Application

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application will start by default on:

```text
http://localhost:8080
```

---

## How to Run the Tests

The project includes unit tests for the service layer using JUnit 5 and Mockito.

Run the tests with:

### Linux or macOS

```bash
./mvnw test
```

### Windows

```bash
mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

Example output:

```text
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
```

---

## Unit Testing

The following service classes were tested:

- EventService
- VenueService

The tests validate:

- Creating a valid event.
- Preventing creation of an event with an empty name.
- Preventing creation of an event with a null date.
- Listing all events.
- Creating a valid venue.
- Preventing creation of a venue with an empty name.
- Preventing creation of a venue with an empty address.
- Preventing creation of a venue with invalid capacity.
- Listing all venues.

Mockito was used to mock the repositories and test the services in isolation without starting the full Spring Boot context.

---

## Acceptance Criteria Covered

### Scenario 1: Successful Registration

A valid event can be sent through `POST /api/events`.

The system:

- Validates the data in the service layer.
- Stores the event in the repository collection.
- Returns the created object with status `201 Created`.

### Scenario 2: Invalid Registration

An event with an empty name is rejected.

The system:

- Detects the error in the service layer.
- Prevents the invalid object from reaching the repository.
- Returns a controlled error response with status `400 Bad Request`.

### Scenario 3: Empty Catalog Query

If no data is loaded, the system can return an empty list with status `200 OK`.

This demonstrates that the in-memory persistence layer works correctly even without initial data.

### Scenario 4: Documentation Verification

When the application is running, the API documentation is available through Swagger.

The developer can access:

```text
http://localhost:8080/swagger-ui.html
```

and see all documented `GET` and `POST` endpoints.

---

## Main Technologies

- Java
- Spring Boot
- Spring Web
- Spring MVC
- Lombok
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Maven

---

## Current Project Status

The first architectural foundation of Eventify is completed.

Implemented deliverables:

- Functional REST API with `GET` and `POST` endpoints.
- Layered architecture using Spring MVC.
- In-memory repositories.
- Initial data loading with `@Configuration` and `@Bean`.
- Swagger interactive documentation.
- Unit test suite for service logic.
