# Self service AI service

_The service orchestrates sessions and questions posted to an AI ​​assistant who answers questions about a customer's
invoices, consumption, etc._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **MariaDB**(if applicable)
- **Git**
- **[Dependent Microservices](#dependencies)** (if applicable)

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Sundsvallskommun/api-service-self-service-ai.git
   cd api-service-self-service-ai
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible.
   See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

     ```bash
     mvn spring-boot:run
     ```
   - Using Gradle:

     ```bash
     gradle bootRun
     ```

## Dependencies

This microservice depends on the following services:

- **Customer**
  - **Purpose:** Provides service with customer information.
  - **Repository:** [Link to the repository](https://github.com/Sundsvallskommun/api-service-customer)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **Agreement**
  - **Purpose:** Provides service with customer agreement information.
  - **Repository:** [Link to the repository](https://github.com/Sundsvallskommun/api-service-agreement)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **Installedbase**
  - **Purpose:** Provides service with customers installed base.
  - **Repository:** [Link to the repository](https://github.com/Sundsvallskommun/api-service-installedbase)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **MeasurementData**
  - **Purpose:** Provides service with measurement data for customer facilities.
  - **Repository:** [Link to the repository](https://github.com/Sundsvallskommun/api-service-measurement-data)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **Invoices**
  - **Purpose:** Provides service with customer invoice information.
  - **Repository:** [Link to the repository](https://github.com/Sundsvallskommun/api-service-invoices)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **Eneo**
  - **Purpose:** Handles logic for AI assistants.
  - **Repository:** External service.
- **Lime**
  - **Purpose:** Stores chat logs for historical purposes.
  - **Repository:** External service.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, refer to the `openapi.yml` file located in directory `/src/test/resources/api` for the OpenAPI
specification.

## Usage

### API Endpoints

Refer to the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X POST 'http://localhost:8080/2281/session
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in
`application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **Database Settings:**

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/your_database
      username: your_db_username
      password: your_db_password
  ```
- **External Service URLs:**

  ```yaml
  integration:
    customer:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    agreement:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    installedbase:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    measurement-data:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    invoices:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    eneo:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds
    lime:
      url: http://dependency_service_url
      connect-timeout: connect_timeout_in_seconds
      read-timeout: read_timeout_in_seconds

  spring:
    security:
      oauth2:
        client:
          provider:
            customer:
              token-uri: https://token_url
            agreement:
              token-uri: https://token_url
            installedbase:
              token-uri: https://token_url
            measurement-data:
              token-uri: https://token_url
            invoices:
              token-uri: https://token_url
            eneo:
              token-uri: https://token_url
            lime:
              token-uri: https://token_url
          registration:
            customer:
              client-id: client_id
              client-secret: client_secret
            agreement:
              client-id: client_id
              client-secret: client_secret
            installedbase:
              client-id: client_id
              client-secret: client_secret
            measurement-data:
              client-id: client_id
              client-secret: client_secret
            invoices:
              client-id: client_id
              client-secret: client_secret
            eneo:
              client-id: client_id
              client-secret: client_secret
            lime:
              client-id: client_id
              client-secret: client_secret
  ```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by
default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are
  correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please
see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-self-service-ai&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-self-service-ai)

---

&copy; 2024 Sundsvalls kommun
