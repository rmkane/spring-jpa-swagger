---
name: Spring Library App with Swagger
overview: Create a basic Spring Boot library application with Swagger, featuring Book and Author entities with many-to-many relationship, User entity for audit fields, full MVC layers (Controller, Service/Impl, Repository, JPA entities), Docker Compose for PostgreSQL, and environment configuration.
todos: []
---

# Spring Library Application with Swagger

## Overview

Create a basic Spring Boot library application with Swagger documentation, featuring books and authors with audit fields, full MVC architecture, and PostgreSQL database setup.

## Dependencies

Update `pom.xml` to include:

- Change parent to Spring Boot 3.5.8
- Change groupId to `org.acme.web` and artifactId to `web-api`
- Set Java version to 17 in properties
- Add version properties:
  - MapStruct 1.6.3
  - Lombok MapStruct Binding 0.2.0
  - SpringDoc OpenAPI MVC 2.8.14
  - Spotless 2.43.0
- Dependencies:
  - Spring Data JPA
  - Flyway Core and Flyway PostgreSQL (for database migrations)
  - PostgreSQL driver
  - SpringDoc OpenAPI MVC (2.8.14)
  - Validation
  - Lombok
  - MapStruct (1.6.3) with annotation processor
  - Lombok MapStruct Binding (0.2.0)
  - Spotless Maven Plugin (2.43.0) for code formatting and linting
- Build plugins:
  - Maven Compiler Plugin with annotation processors (Lombok, MapStruct, lombok-mapstruct-binding)
  - Build Helper Maven Plugin to include generated sources
  - Spotless Maven Plugin with Eclipse formatter (version 4.29)

## Project Structure

Create standard Maven directory structure:

- `src/main/java/org/acme/web/`
- `src/main/resources/`
- `.vscode/` - VS Code configuration for Java development

## Entities

1. **User** (`entity/User.java`)

   - Fields: id, username, email, firstName, lastName
   - Audit: createdAt, updatedAt (LocalDateTime)
   - Order: createdAt, updatedAt

2. **Author** (`entity/Author.java`)

   - Fields: id, firstName, lastName, bio
   - Audit: createdBy (User), updatedBy (User), createdAt, updatedAt
   - Relationship: Many-to-Many with Book
   - Order: createdBy, createdAt, updatedBy, updatedAt
   - Uses `@EqualsAndHashCode(exclude = {"books", "createdBy", "updatedBy"})` to prevent recursion

3. **Book** (`entity/Book.java`)

   - Fields: id, title, isbn, publicationYear
   - Audit: createdBy (User), updatedBy (User), createdAt, updatedAt
   - Relationship: Many-to-Many with Author
   - Order: createdBy, createdAt, updatedBy, updatedAt
   - Uses `@EqualsAndHashCode(exclude = {"authors", "createdBy", "updatedBy"})` to prevent recursion

4. **BookAuthor** (`entity/BookAuthor.java`)

   - Join entity for Book-Author many-to-many relationship
   - Fields: book, author, createdAt

## Repositories

- `repository/UserRepository.java` - extends JpaRepository
- `repository/AuthorRepository.java` - extends JpaRepository with `@Query` using `LEFT JOIN FETCH` to eagerly load books and audit fields
- `repository/BookRepository.java` - extends JpaRepository with `@Query` using `LEFT JOIN FETCH` to eagerly load authors and audit fields

## DTOs

For each entity (User, Author, Book), create separate DTOs:

- `dto/request/CreateUserRequest.java`, `dto/request/UpdateUserRequest.java`, `dto/response/UserResponse.java`
- `dto/request/CreateAuthorRequest.java`, `dto/request/UpdateAuthorRequest.java`, `dto/response/AuthorResponse.java`
- `dto/request/CreateBookRequest.java`, `dto/request/UpdateBookRequest.java`, `dto/response/BookResponse.java`

All request DTOs include `@Schema` annotations with example JSON for consistent Swagger documentation.

## Mappers

- `mapper/UserMapper.java` - MapStruct mapper interface for User entity ↔ DTOs (with @NonNull annotations)
- `mapper/AuthorMapper.java` - MapStruct mapper interface for Author entity ↔ DTOs (with @NonNull annotations)
- `mapper/BookMapper.java` - MapStruct mapper interface for Book entity ↔ DTOs (with @NonNull annotations)

All mappers use `componentModel = "spring"` for dependency injection.

## Services

- `service/UserService.java` (interface) + `service/impl/UserServiceImpl.java` (implementation)
- `service/AuthorService.java` (interface) + `service/impl/AuthorServiceImpl.java` (implementation)
- `service/BookService.java` (interface) + `service/impl/BookServiceImpl.java` (implementation)

All service methods include `@NonNull` annotations on parameters and return types.

## Controllers

- `controller/HomeController.java` - Root home controller with basic endpoint (package: `org.acme.web.controller`)
- `api/UserController.java` - REST endpoints with Swagger annotations, @NonNull annotations, explicit @PathVariable names (package: `org.acme.web.api`)
- `api/AuthorController.java` - REST endpoints with Swagger annotations, @NonNull annotations, explicit @PathVariable names (package: `org.acme.web.api`)
- `api/BookController.java` - REST endpoints with Swagger annotations, @NonNull annotations, explicit @PathVariable names (package: `org.acme.web.api`)

All controllers:

- Use `produces = "application/json"` at class level
- Use `consumes = "application/json"` only on POST and PUT methods (not GET/DELETE)
- Include `@Tag`, `@Operation`, and `@ApiResponse` annotations for Swagger documentation

## Configuration

- `config/SwaggerConfig.java` - SpringDoc OpenAPI configuration
- `config/JpaAuditingConfig.java` - Enable JPA auditing for timestamps
- `config/AuditorAwareImpl.java` - Simple AuditorAware implementation returning default user for createdBy/updatedBy
- `config/JacksonConfig.java` - Custom Jackson configuration for LocalDateTime formatting (format: "yyyy-MM-dd HH:mm:ss")
- `exception/ResourceNotFoundException.java` - Generic exception for entity not found scenarios
- `exception/GlobalExceptionHandler.java` - @ControllerAdvice for consistent error responses (handles ResourceNotFoundException, validation errors, etc.)
- `exception/ErrorResponse.java` - DTO for error responses
- `listener/StartupListener.java` - Application startup listener to log Swagger UI URL
- `application.yml` - YAML configuration file for database and app settings (references env vars), Flyway configuration, disable Spring Security
- `logback-spring.xml` - Logback configuration with rolling file appenders (logs to `logs/` directory)
  - Console output at INFO level
  - File logs at DEBUG/TRACE levels for detailed debugging
  - Hibernate logs configured to only write to files, not console
- `.env` - Environment variables for database connection
- `docker-compose.yml` - PostgreSQL service using `postgres:17` image tag, no `version:` field at top
- `Makefile` - Targets for:
  - `db-up` - Start PostgreSQL database
  - `db-down` - Stop PostgreSQL database
  - `db-reset` - Reset database (stop, remove volumes, restart)
  - `db-logs` - Show database logs
  - `app-run` - Run the Spring Boot application (includes clean compile)
  - `app-build` - Build the application
  - `app-clean` - Clean build artifacts
  - `format` - Format code using Spotless
  - `lint` - Check code formatting (Spotless check)
- `formatter.xml` - Eclipse formatter configuration for Spotless (version 4.29)
- `.vscode/settings.json` - VS Code Java configuration for annotation processors
- `.vscode/extensions.json` - Recommended VS Code extensions

## Database Migrations

- `db/migration/V1__Initial_schema.sql` - Flyway migration for initial database schema
  - Creates users, authors, books, book_authors tables
  - Includes foreign keys, indexes, and constraints
  - Audit fields ordered: created_by, created_at, updated_by, updated_at
- `db/migration/V2__Seed_data.sql` - Seed data migration with sample users, authors, books, and relationships

## Main Application

- `LibraryApplication.java` - Spring Boot main class

## Implementation Details

- **Database Migrations**: Use Flyway for database schema management (replaces Hibernate ddl-auto)
  - Initial migration: `V1__Initial_schema.sql` creates all tables with proper constraints and indexes
  - Seed data migration: `V2__Seed_data.sql` provides sample data
  - JPA ddl-auto set to `validate` to ensure entity matches schema
- **Null Safety**: Comprehensive use of `@NonNull` annotations throughout:
  - All service method parameters and return types
  - All mapper method parameters and return types
  - All controller method parameters (@PathVariable, @RequestBody)
  - Explicit path variable names: `@PathVariable("id")`
  - Use `@SuppressWarnings("null")` for known false positives (e.g., `orElseThrow()` guarantees non-null)
- **Logging**: Logback configuration with rolling file appenders:
  - Main log: `logs/library-api.log` (10MB max, 30 day retention, 1GB total cap)
  - Error log: `logs/library-api-error.log` (ERROR level only, 10MB max, 30 day retention, 500MB total cap)
  - Console output at INFO level for development
  - File logs at DEBUG/TRACE levels for detailed debugging
  - Hibernate SQL logs configured to only write to files, not console
- **Code Quality**: Spotless Maven Plugin for formatting and linting:
  - Eclipse formatter configuration (version 4.29)
  - Import ordering and unused import removal
  - Runs during validate phase
  - Makefile targets: `make format` and `make lint`
- **Date Formatting**: Custom Jackson configuration for LocalDateTime:
  - Format: "yyyy-MM-dd HH:mm:ss" (e.g., "2025-12-09 20:35:44")
  - Configured via `JacksonConfig.java` with `Jackson2ObjectMapperBuilderCustomizer`
- **Entity Relationships**:
  - Many-to-Many relationship between Book and Author (using join table `book_authors`)
  - `@EqualsAndHashCode` excludes bidirectional collections to prevent recursion
  - Eager loading in repositories using `LEFT JOIN FETCH` to prevent LazyInitializationException
- **Swagger Documentation**:
  - All request DTOs include `@Schema` annotations with example JSON
  - Consistent example values across all endpoints
  - Field-level descriptions for better API documentation
- **VS Code Configuration**:
  - `.vscode/settings.json` configures Java to work with annotation processors
  - Disables automatic build to prevent conflicts with Maven
  - Includes generated sources in project source paths
- **Build Configuration**:
  - Build Helper Maven Plugin adds generated sources (MapStruct implementations) to classpath
  - Annotation processors configured for Lombok, MapStruct, and lombok-mapstruct-binding
  - Makefile `app-run` target includes `clean compile` to ensure generated sources are available
- Disable Spring Security (exclude from dependencies or configure to permit all) - localhost HTTP dev example
- Use `@EntityListeners(AuditingEntityListener.class)` for automatic timestamp management
- Use `@CreatedBy` and `@LastModifiedBy` with `AuditorAware` implementation
- Use `@ManyToMany` with `@JoinTable` for Book-Author relationship
- Implement basic CRUD operations for all entities
- Services map between JPA entities and DTOs using MapStruct mappers
- Controllers use request/response DTOs, not entities directly
- Add Swagger annotations (`@Operation`, `@ApiResponse`) to controllers
- Use Lombok for entity and DTO classes to reduce boilerplate
