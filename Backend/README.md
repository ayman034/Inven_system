# eGAZ Inventory Management - Backend

Backend ya Spring Boot iliyotengenezwa maalum kwa mfumo wako wa eGAZ Inventory Management, ikibadilisha localStorage na database halisi ya MySQL, yenye JWT authentication na role-based permissions.

## Roles

- **ADMIN**: Anasimamia users (create/edit/enable/disable). Anaona (view-only) items, rooms, reports.
- **STOREKEEPER**: Anaweza ku-create/edit/delete items, rooms, na kutenga (allocate) stock kwenye rooms.
- **SUPERVISOR**: Anaona kila kitu (view-only) na anaweza kuprint reports.

## Hatua za Kuanza

### 1. Unda Database
```sql
CREATE DATABASE egaz_db;
```
(Au acha tu — imewekwa `createDatabaseIfNotExist=true`)

### 2. Badilisha Credentials
Kwenye `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 3. Endesha
```bash
mvn spring-boot:run
```
Server itaanza kwenye **http://localhost:8080**

### 4. Akaunti ya Kwanza (Admin)
Mfumo utaunda kiotomatiki akaunti ya kwanza ya ADMIN mara ya kwanza unapoendesha:
- **Username:** `admin`
- **Password:** `admin1234`

Ingia na hii, kisha unda users wengine (STOREKEEPER, SUPERVISOR) kupitia `/api/users`.

---

## API Endpoints

### Authentication (Public)
| Method | Endpoint | Maelezo |
|---|---|---|
| POST | `/api/auth/login` | Login, inarudisha JWT token |

**Request:**
```json
{ "username": "admin", "password": "admin1234" }
```

**Response:**
```json
{
  "token": "eyJ...",
  "username": "admin",
  "fullName": "System Administrator",
  "role": "ADMIN"
}
```

### Users (ADMIN pekee)
| Method | Endpoint | Maelezo |
|---|---|---|
| GET | `/api/users` | Orodha ya users |
| POST | `/api/users` | Unda user mpya |
| PUT | `/api/users/{id}` | Badilisha user |
| PATCH | `/api/users/{id}/toggle` | Enable/Disable user |

### Items (Roles zote - GET; STOREKEEPER - badiliko)
| Method | Endpoint |
|---|---|
| GET | `/api/items` |
| POST | `/api/items` |
| PUT | `/api/items/{id}` |
| DELETE | `/api/items/{id}` |

### Rooms (Roles zote - GET; STOREKEEPER - badiliko)
| Method | Endpoint |
|---|---|
| GET | `/api/rooms` |
| POST | `/api/rooms` |
| PUT | `/api/rooms/{id}` |
| DELETE | `/api/rooms/{id}` |

### Allocations (Roles zote - GET; STOREKEEPER - badiliko)
| Method | Endpoint | Maelezo |
|---|---|---|
| GET | `/api/allocations` | Orodha ya allocations zote |
| POST | `/api/allocations` | Tenga stock (itemId, roomId, quantity) |
| PUT | `/api/allocations/{id}` | Weka kiasi mahususi (quantity) |
| PATCH | `/api/allocations/{id}/add` | Ongeza kiasi |
| PATCH | `/api/allocations/{id}/remove` | Punguza kiasi |
| DELETE | `/api/allocations/{id}` | Futa allocation |

Kwa endpoints zote (isipokuwa `/api/auth/login`), ongeza header:
```
Authorization: Bearer <token>
```

---

## Muundo wa Project

```
src/main/java/com/egaz/backend/
├── config/       -> SecurityConfig, DataInitializer (admin ya kwanza)
├── security/      -> JWT utils, filters
├── model/         -> User, Item, Room, Allocation, Role
├── repository/     -> JPA Repositories
├── service/        -> Business logic (allocated/remaining calculations)
├── controller/      -> REST Controllers (role-based @PreAuthorize)
├── dto/            -> Request/Response objects
└── exception/       -> Custom exceptions + Global handler
```
