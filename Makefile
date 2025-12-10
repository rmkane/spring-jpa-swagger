.DEFAULT_GOAL := help

.PHONY: db-up db-down db-reset db-logs \
	app-run app-build app-clean \
	format lint help

db-up:
	docker compose up -d postgres

db-down:
	docker compose down

db-reset:
	docker compose down -v
	docker compose up -d postgres

db-logs:
	docker compose logs -f postgres

app-run:
	mvn clean compile spring-boot:run

app-build:
	mvn clean package

app-clean:
	mvn clean

format:
	mvn spotless:apply

lint:
	mvn spotless:check

help:
	@echo "Available targets:"
	@echo "  db-up      - Start PostgreSQL database"
	@echo "  db-down    - Stop PostgreSQL database"
	@echo "  db-reset   - Reset database (stop, remove volumes, restart)"
	@echo "  db-logs    - Show database logs"
	@echo "  app-run    - Run the Spring Boot application"
	@echo "  app-build  - Build the application"
	@echo "  app-clean  - Clean build artifacts"
	@echo "  format     - Format code using Spotless"
	@echo "  lint       - Check code formatting (Spotless check)"
