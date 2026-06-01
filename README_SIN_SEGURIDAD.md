# Cambios aplicados: backend sin inicio de sesión

Este paquete deja el backend listo para funcionar como panel administrativo local sin login ni JWT.

## Cambios realizados

- Se eliminaron las propiedades `security.jwt.*`/`jwt.*` de `src/main/resources/application.properties`.
- Se agregó `src/main/java/co/unimagdalena/tiendauni/config/CorsConfig.java` para permitir peticiones del frontend Vite desde:
  - `http://localhost:5173`
  - `http://127.0.0.1:5173`
- No se incluye carpeta `target/` para evitar clases compiladas antiguas como `JwtAuthenticationFilter` o `JwtService`.
- El `pom.xml` incluido no contiene dependencias de Spring Security ni JJWT.

## Ejecución

```bash
mvn clean spring-boot:run
```

Si usas Maven Wrapper en Windows:

```powershell
.\mvnw.cmd clean spring-boot:run
```

## Endpoints esperados desde el frontend

El frontend puede consumir directamente endpoints como:

- `GET http://localhost:8080/api/products`
- `GET http://localhost:8080/api/categories`
- `GET http://localhost:8080/api/customers`
- `GET http://localhost:8080/api/orders`
- `GET http://localhost:8080/api/inventory/low-stock`
- `GET http://localhost:8080/api/reports/...`

## Nota importante

Si en tu copia local anterior existen carpetas o clases dentro de `src/main/java/.../security`, bórralas antes de compilar. También ejecuta siempre `mvn clean` para eliminar clases antiguas de `target/`.
