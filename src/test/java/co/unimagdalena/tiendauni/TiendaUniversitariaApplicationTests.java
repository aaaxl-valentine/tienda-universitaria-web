package co.unimagdalena.tiendauni;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.DockerClientFactory;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TiendaUniversitariaApplicationTests {

    @BeforeAll
    static void requireDocker() {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker is required for context test");
    }

    @Test
    void contextLoads() {
    }

}
