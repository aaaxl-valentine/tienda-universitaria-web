# Tienda Universitaria

## Overview
Tienda Universitaria is a Spring Boot application designed for managing a university store. It provides functionalities for handling products, categories, customers, inventory, and orders, along with reporting capabilities for sales and stock management.

## Technical Stack
- **Language:** Java 21
- **Framework:** Spring Boot 4.0.5
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Testing:** JUnit 5, Testcontainers (PostgreSQL)
- **Utilities:** Lombok

## Requirements
To run this project, you will need:
- **Java Development Kit (JDK) 21** or higher.
- **Maven 3.6+** (optional, as Maven Wrapper is included).
- **PostgreSQL** database (unless running tests with Testcontainers).
- **Docker** (optional, required for running integration tests with Testcontainers).

## Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd tienda-universitaria-taller
    ```

2.  **Database Configuration:**
    Ensure you have a PostgreSQL instance running. Update the `src/main/resources/application.properties` file with your database credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/tienda_db
    spring.datasource.username=postgres
    spring.datasource.password=1234
    ```
    Alternatively, create the `tienda_db` database in your PostgreSQL server.

3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```
    (On Windows, use `mvnw.cmd clean install`)

## Running the Application

You can run the application using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` (default port).

### Entry Point
The main entry point of the application is:
`co.unimagdalena.tiendauni.TiendaUniversitariaApplication`

## Scripts & Commands

| Command | Description |
| --- | --- |
| `./mvnw clean install` | Compiles the code, runs tests, and packages the application. |
| `./mvnw spring-boot:run` | Starts the Spring Boot application. |
| `./mvnw test` | Executes all unit and integration tests. |
| `./mvnw compile` | Compiles the source code. |

## Environment Variables / Configuration
Key configuration properties are located in `src/main/resources/application.properties`:

- `spring.application.name`: Name of the application (`tienda-universitaria`).
- `server.port`: Port on which the server runs (default: `8080`).
- `spring.datasource.url`: JDBC URL for the PostgreSQL database.
- `spring.datasource.username`: Database username.
- `spring.datasource.password`: Database password.
- `spring.jpa.hibernate.ddl-auto`: Hibernate DDL strategy (set to `update`).

## Tests
The project uses JUnit 5 for testing and Testcontainers for integration tests involving a real PostgreSQL instance.

To run tests:
```bash
./mvnw test
```

Test reports can be found in `target/surefire-reports/`.

## Project Structure
```text
src
├── main
│   ├── java
│   │   └── co.unimagdalena.tiendauni
│   │       ├── DTOs                # Data Transfer Objects
│   │       ├── NotFoundException   # Custom exceptions and Global Handler
│   │       ├── entity              # JPA Entities (Product, Order, Customer, etc.)
│   │       ├── repository          # Spring Data JPA Repositories
│   │       ├── service             # Business Logic Interfaces and Implementations
│   │       │   └── mappers         # Entity-DTO Mappers
│   │       └── TiendaUniversitariaApplication.java # Entry Point
│   └── resources
│       └── application.properties   # App Configuration
└── test                            # Unit and Integration Tests
```

## License
TODO: Add license information. (The `pom.xml` currently has an empty license section).
