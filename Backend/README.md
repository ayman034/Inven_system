# eGAZ Inventory Management - Backend

This Spring Boot backend powers the eGAZ Inventory Management system. It replaces localStorage with a real MySQL database and provides JWT authentication with role-based permissions.

## Roles

- **ADMIN**: Manages users (create/edit/enable/disable). Can view items, rooms, and reports.
- **STOREKEEPER**: Can create/edit/delete items and rooms, and allocate stock to rooms.
- **SUPERVISOR**: Can view everything and print reports.
