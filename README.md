# Orion TT

Table tennis app for sports clubs - Spring Boot backend.

## Features

### Current (MVP)
- Players, matches and sets
- Match result API: `GET /api/matches/{id}`

### Planned
- Live scorekeeping
- Player match history
- Clubs
- Tournaments
- User accounts (email verification, password recovery)

## Tech stack

- Java 21
- Spring Boot 4
- Spring Data JPA (Hibernate)
- MySQL (XAMPP for local development)
- Maven

## How to run

### Prerequisites
- JDK 21
- MySQL running locally (e.g. XAMPP)

### Steps
1. Clone the repository
   ```bash
   git clone https://github.com/luca-ros/orion-tt.git
   ```
2. Create the database in MySQL
   ```sql
   CREATE DATABASE orion_tt;
   ```
3. Check the database connection in `src/main/resources/application.properties`
4. Start the application
   ```bash
   ./mvnw spring-boot:run
   ```
5. Open the API in the browser
   ```
   http://localhost:8080/api/matches/1
   ```
