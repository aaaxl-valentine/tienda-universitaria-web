package co.unimagdalena.tiendauni;

import org.springframework.boot.SpringApplication;

public class TestTiendaUniversitariaApplication {

    public static void main(String[] args) {
        SpringApplication.from(TiendaUniversitariaApplication::main).with(TestcontainersConfiguration.class).run(args);
        //llamar a todos los test

    }

}
