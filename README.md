# Herb Catalogue

A simple Spring Boot herb catalogue with public browsing and search, secure accounts, personal notes, and administrator management pages.

Included

- Spring Boot 3.5 / Java 17 / Maven
- Thymeleaf pages with a simple parchment-paper background
- Registration form with Bean Validation and password confirmation
- Spring Security form login/logout, BCrypt password hashing, `USER` and `ADMIN` roles
- H2 in-memory database with JPA/Hibernate
- Related JPA entities: `HerbCategory` → `Herb`, plus user-owned `HerbNote` records
- Public herb catalogue with name/botanical-name search and category filtering
- User CRUD for personal herb notes; users cannot change another user's notes
- Admin web pages for herbs, categories, and a user dashboard
- JSON CRUD APIs: public `GET /api/herbs` and `GET /api/categories`; admin catalogue management; authenticated note management; admin account management at `/api/users`
- JSON API validation and error responses, friendly HTML error pages
- Unit, repository, and MVC/security tests

## Run

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080/catalogue](http://localhost:8080/catalogue). The H2 console is at `/h2-console` while the app runs.

Development administrator: `admin` / `admin-access2222`.

## Roles

- Visitors can browse and search the catalogue.
- `USER` accounts can add, edit, and delete only their own herb notes.
- `ADMIN` accounts can manage herbs and categories, view the user dashboard, and use the full account API.
