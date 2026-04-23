# bookstore-api

API REST para gestión de una librería en línea, construida con **Spring Boot 3**, **Spring Security + JWT** y **JPA/H2**.

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| Java        | 17+            |
| Maven       | 3.8+           |
| Git         | 2.x            |

---

## Ejecutar localmente

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd bookstore-api

# 2. (Opcional) Configurar variable de entorno para JWT
export JWT_SECRET=mi-clave-secreta-muy-larga-de-al-menos-32-chars

# 3. Compilar y ejecutar
mvn spring-boot:run
```

La API queda disponible en: `http://localhost:8080/api/v1`

> Sin la variable `JWT_SECRET`, usa la clave por defecto del `application.yml` (solo para desarrollo).

---

## Documentación interactiva (Swagger)

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

Para endpoints protegidos: clic en **Authorize** → ingresar `Bearer <token>`.

## Consola H2 (base de datos en memoria)

```
http://localhost:8080/api/v1/h2-console
JDBC URL: jdbc:h2:mem:bookstoredb
User: sa  |  Password: (vacío)
```

---

## Endpoints principales

### Autenticación (público)
| Método | Ruta               | Descripción              |
|--------|--------------------|--------------------------|
| POST   | `/auth/register`   | Registrar nuevo usuario  |
| POST   | `/auth/login`      | Iniciar sesión → JWT     |

### Libros
| Método | Ruta            | Acceso  | Descripción                         |
|--------|-----------------|---------|-------------------------------------|
| GET    | `/books`        | Público | Listar con paginación y filtros     |
| GET    | `/books/{id}`   | Público | Detalle de un libro                 |
| POST   | `/books`        | ADMIN   | Crear libro                         |
| PUT    | `/books/{id}`   | ADMIN   | Actualizar libro                    |
| DELETE | `/books/{id}`   | ADMIN   | Eliminar libro                      |

#### Filtros disponibles en GET /books
```
GET /books?categoryId=1&page=0&size=10
GET /books?authorId=2&page=0&size=5
```

### Autores
| Método | Ruta                    | Acceso  | Descripción              |
|--------|-------------------------|---------|--------------------------|
| GET    | `/authors`              | Público | Listar autores           |
| GET    | `/authors/{id}`         | Público | Detalle de autor         |
| GET    | `/authors/{id}/books`   | Público | Libros de un autor       |
| POST   | `/authors`              | ADMIN   | Crear autor              |
| PUT    | `/authors/{id}`         | ADMIN   | Actualizar autor         |
| DELETE | `/authors/{id}`         | ADMIN   | Eliminar autor           |

### Categorías
| Método | Ruta                       | Acceso  | Descripción                   |
|--------|----------------------------|---------|-------------------------------|
| GET    | `/categories`              | Público | Listar categorías             |
| GET    | `/categories/{id}`         | Público | Detalle de categoría          |
| GET    | `/categories/{id}/books`   | Público | Libros de una categoría       |
| POST   | `/categories`              | ADMIN   | Crear categoría               |
| PUT    | `/categories/{id}`         | ADMIN   | Actualizar categoría          |
| DELETE | `/categories/{id}`         | ADMIN   | Eliminar categoría            |

### Pedidos
| Método | Ruta                | Acceso       | Descripción                    |
|--------|---------------------|--------------|--------------------------------|
| POST   | `/orders`           | USER/ADMIN   | Crear pedido                   |
| GET    | `/orders/my`        | USER/ADMIN   | Mis pedidos                    |
| GET    | `/orders`           | ADMIN        | Todos los pedidos              |
| PATCH  | `/orders/{id}/cancel` | USER/ADMIN | Cancelar un pedido             |

---

## Contratos de respuesta

### Éxito
```json
{
  "status": "success",
  "code": 200,
  "message": "Operación completada",
  "data": {},
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error
```json
{
  "status": "error",
  "code": 404,
  "message": "Libro con id 99 no fue encontrado",
  "errors": ["Libro con id 99 no fue encontrado"],
  "path": "/api/v1/books/99",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## Flujo de uso rápido con Postman

```
1. POST /auth/register  → { "name": "Admin", "email": "admin@test.com", "password": "admin123" }
2. POST /auth/login     → { "email": "admin@test.com", "password": "admin123" }
   ← Recibe token JWT

3. POST /authors  (Bearer token)  → { "name": "Gabriel García Márquez" }
4. POST /categories (Bearer token) → { "name": "Novela" }
5. POST /books    (Bearer token)  → { "title": "Cien años de soledad", "isbn": "978-0307474728",
                                       "price": 29.99, "stock": 50, "authorId": 1, "categoryIds": [1] }

6. POST /orders   (Bearer token)  → { "items": [{ "bookId": 1, "quantity": 2 }] }
```

---

## Estructura del proyecto

```
com.taller.bookstore
├── config/          → Beans globales (Security, CORS, OpenAPI, PasswordEncoder)
├── controller/      → Endpoints HTTP
├── dto/
│   ├── request/     → DTOs de entrada con validaciones @Valid
│   └── response/    → DTOs de salida (ApiResponse, ApiErrorResponse)
├── entity/          → Entidades JPA
├── exception/
│   ├── custom/      → Excepciones de dominio
│   └── handler/     → @RestControllerAdvice global
├── mapper/          → Conversión Entity ↔ DTO (sin librerías externas)
├── repository/      → Spring Data JPA
├── security/        → JWT (JwtService, JwtAuthFilter, UserDetailsServiceImpl)
└── service/
    └── impl/        → Lógica de negocio
```

---

## Estrategia Git

```
main
└── develop
    ├── feature/auth-module
    ├── feature/book-catalog
    ├── feature/order-management
    └── feature/author-category
```

**Convención de commits:**
```
feat: add JWT authentication filter
fix: correct price validation in BookRequest
refactor: extract order total calculation to service
docs: add endpoint documentation in AuthController
```

---

## Configuración para producción (PostgreSQL)

Crear `application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookstore
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

Ejecutar con: `mvn spring-boot:run -Dspring.profiles.active=prod`
