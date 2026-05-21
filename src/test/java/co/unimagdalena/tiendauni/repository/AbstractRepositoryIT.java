package co.unimagdalena.tiendauni.repository;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractRepositoryIT {

    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @BeforeAll
    static void requireDocker() {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for Testcontainers tests");
        if (!postgres.isRunning()) {
            postgres.start();
        }
    }
}
