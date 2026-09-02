# Simple Log-in

Focused Spring Boot login project: sign up, form-based login, logout, and one protected menu titled **“Simple Log-in Page.”**

## Included

- Spring Boot 3.5 / Java 17 / Maven
- Thymeleaf pages with background
- Registration form with Bean Validation and password confirmation
- Spring Security form login/logout, BCrypt password hashing, `USER` and `ADMIN` roles
- H2 in-memory database with JPA/Hibernate
- Admin-only JSON account CRUD at `/api/users`
- JSON API validation and error responses, friendly HTML error pages
- Unit, repository, and MVC/security tests

## To Run:

```bash
./mvnw spring-boot:run
```

Open at [http://localhost:8080/login](http://localhost:8080/login). The H2 console is at `/h2-console` while the app runs.

Development administrator: `admin` / `change-me`.
