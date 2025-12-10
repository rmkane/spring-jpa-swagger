# Library API

A Spring Boot REST API for managing a library with books, authors, and users. Features include JPA entities with audit fields, Swagger/OpenAPI documentation, and PostgreSQL database with Flyway migrations.

## Features

- **RESTful API** with full CRUD operations for Books, Authors, and Users
- **Swagger/OpenAPI Documentation** with example JSON for all endpoints
- **JPA Auditing** with automatic timestamp and user tracking
- **Database Migrations** using Flyway
- **DTO Pattern** with MapStruct for entity-to-DTO mapping
- **Comprehensive Error Handling** with global exception handler
- **Code Quality** with Spotless for formatting and linting
- **Logging** with Logback (rolling file logs and console output)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Make (optional, for convenience commands)

## Quick Start

### 1. Set up environment variables

Create a `.env` file in the project root:

```bash
DB_URL=jdbc:postgresql://localhost:5432/library
DB_USERNAME=library
DB_PASSWORD=library
```

### 2. Start the database

```bash
make db-up
```

Or manually:

```bash
docker compose up -d postgres
```

### 3. Run the application

```bash
make app-run
```

Or manually:

```bash
mvn clean compile spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access Swagger UI

Once the application is running, open:

> <http://localhost:8080/swagger-ui.html>

The startup listener will also log the Swagger UI URL to the console.

## Project Structure

```none
src/main/java/org/acme/web/
├── api/                    # REST controllers
├── config/                 # Configuration classes (Swagger, JPA, Jackson)
├── controller/             # Home controller
├── dto/
│   ├── request/           # Request DTOs (Create/Update)
│   └── response/          # Response DTOs
├── entity/                 # JPA entities
├── exception/              # Custom exceptions and handlers
├── listener/               # Application listeners
├── mapper/                 # MapStruct mappers
├── repository/             # JPA repositories
└── service/
    └── impl/               # Service implementations

src/main/resources/
├── application.yml         # Application configuration
├── logback-spring.xml      # Logging configuration
└── db/migration/           # Flyway migrations
    ├── V1__Initial_schema.sql
    └── V2__Seed_data.sql
```

## API Endpoints

### Users

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Authors

- `GET /api/authors` - Get all authors
- `GET /api/authors/{id}` - Get author by ID
- `POST /api/authors` - Create a new author
- `PUT /api/authors/{id}` - Update author
- `DELETE /api/authors/{id}` - Delete author

### Books

- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create a new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

All endpoints return JSON and use standard HTTP status codes.

## Makefile Commands

The project includes a `Makefile` with convenient commands:

```bash
# Database management
make db-up      # Start PostgreSQL database
make db-down    # Stop PostgreSQL database
make db-reset   # Reset database (stop, remove volumes, restart)
make db-logs    # Show database logs

# Application
make app-run    # Run the Spring Boot application
make app-build  # Build the application (creates JAR)
make app-clean  # Clean build artifacts

# Code quality
make format     # Format code using Spotless
make lint       # Check code formatting (Spotless check)
```

## Database

The application uses PostgreSQL 17 running in Docker. The database schema is managed by Flyway migrations:

- `V1__Initial_schema.sql` - Creates all tables, constraints, and indexes
- `V2__Seed_data.sql` - Inserts sample data for development

### Audit Fields

All entities (except User) include audit fields:

- `createdBy` - User who created the record
- `createdAt` - Timestamp when record was created
- `updatedBy` - User who last updated the record
- `updatedAt` - Timestamp when record was last updated

The User entity only has `createdAt` and `updatedAt` (no user references to avoid circular dependencies).

## Development

### VS Code Setup

The project includes VS Code configuration (`.vscode/settings.json`) to work properly with annotation processors (Lombok, MapStruct).

**Important**: After opening the project in VS Code:

1. Reload the window (`Cmd+Shift+P` → "Reload Window")
2. Run `mvn clean compile` to generate MapStruct implementations
3. Use Maven commands (not VS Code's build) to ensure annotation processors run correctly

### Code Formatting

The project uses Spotless with Eclipse formatter. Format code before committing:

```bash
make format
```

Check formatting:

```bash
make lint
```

### Logging

Logs are written to:

- Console: INFO level (for development)
- `logs/library-api.log`: DEBUG/TRACE levels (rolling, 10MB max, 30 day retention)
- `logs/library-api-error.log`: ERROR level only (rolling, 10MB max, 30 day retention)

Hibernate SQL logs are configured to only write to files, not the console.

### Date Formatting

LocalDateTime fields are formatted as `"yyyy-MM-dd HH:mm:ss"` (e.g., `"2025-12-09 20:35:44"`). This is configured in `JacksonConfig.java`. To use ISO-8601 format instead, remove or modify that configuration class.

## Testing the API

### Using Swagger UI

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Expand any endpoint
3. Click "Try it out"
4. Use the example JSON provided (or modify it)
5. Click "Execute"

### Using curl

```bash
# Get all authors
curl -X GET 'http://localhost:8080/api/authors' \
  -H 'accept: application/json'

# Create an author
curl -X POST 'http://localhost:8080/api/authors' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "firstName": "Jane",
  "lastName": "Doe",
  "bio": "Award-winning author"
}'

# Create a book
curl -X POST 'http://localhost:8080/api/books' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "The Great Novel",
  "isbn": "978-0-123456-78-9",
  "publicationYear": 2023,
  "authorIds": [1]
}'
```

## Technologies

- **Spring Boot 3.5.8** - Application framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL 17** - Database
- **Flyway** - Database migrations
- **MapStruct 1.6.3** - DTO mapping
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI 2.8.14** - Swagger/OpenAPI documentation
- **Spotless** - Code formatting
- **Logback** - Logging

## License

This is a sample project for educational purposes.
