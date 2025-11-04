# Game Academy Backend

Backend service for Game Academy - An educational gaming platform that helps students learn through interactive games.

## Overview

The Game Academy backend is built with Spring Boot 3.2.0 and provides RESTful APIs for:
- User authentication and authorization (JWT-based)
- Student progress tracking and management
- Class management for teachers
- Game catalog and game progress tracking
- Leaderboard functionality

## Technology Stack

- **Java**: 21
- **Framework**: Spring Boot 3.2.0
- **Database**: MongoDB
- **Security**: Spring Security with JWT
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven 3.9.11
- **Testing**: JUnit 5, Mockito

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 21 or later
- Maven 3.9+
- MongoDB 4.4+ (running on localhost:27017)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd game-academy-backend
```

2. Install dependencies:
```bash
mvn clean install
```

3. Configure MongoDB:
   - Ensure MongoDB is running on `localhost:27017`
   - The application will automatically create the `gameacademy` database

4. Configure environment variables (optional):
   - `JWT_SECRET`: Secret key for JWT token generation (defaults to dev key)

## Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Production Mode

```bash
mvn clean package
java -jar target/game-academy-backend-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
src/main/java/com/gameacademy/
├── config/              # Configuration classes (Security, CORS, OpenAPI)
├── controller/          # REST API endpoints
├── service/             # Business logic
├── repository/          # MongoDB repositories
├── model/               # Entity models
├── dto/                 # Data Transfer Objects
├── security/            # Security components (JWT filter, UserDetailsService)
├── exception/           # Custom exceptions and handlers
└── util/                # Utility classes (JWT)

src/main/resources/
├── application.yml      # Main application configuration
└── application-dev.yml  # Development-specific configuration
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## API Endpoints Overview

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/validate` - Validate JWT token
- `GET /api/auth/me` - Get current user details

### Students
- `GET /api/students/{userId}/profile` - Get student profile
- `GET /api/students/{studentId}/progress` - Get progress details
- `GET /api/students/{studentId}/games` - Get game history
- `PUT /api/students/{studentId}/points` - Update points
- `POST /api/students/{studentId}/enroll/{classId}` - Enroll in class

### Classes
- `POST /api/classes` - Create new class
- `GET /api/classes/{classId}` - Get class details
- `GET /api/classes/teacher/{teacherId}` - Get teacher's classes
- `POST /api/classes/{classId}/students/{studentId}` - Add student to class
- `POST /api/classes/{classId}/goals` - Set class goal

### Games
- `GET /api/games` - Get all games
- `GET /api/games/{gameId}` - Get game details
- `GET /api/games/subject/{subject}` - Filter by subject
- `POST /api/games` - Create game (admin)

### Game Progress
- `POST /api/progress/record` - Record game session
- `GET /api/progress/student/{studentId}` - Get student progress
- `GET /api/progress/student/{studentId}/game/{gameId}` - Get specific progress

## Configuration

### application.yml

Key configuration options:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/gameacademy

server:
  port: 8080

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours

cors:
  allowed-origins: http://localhost:4200
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=AuthControllerTest
```

## MongoDB Collections

The application uses the following collections:
- `users` - User accounts (students and teachers)
- `students` - Student profiles and progress
- `classes` - Class information
- `games` - Game catalog
- `gameProgress` - Student game progress tracking
- `leaderboards` - Class leaderboards

See [MONGODB_SCHEMA.md](MONGODB_SCHEMA.md) for detailed schema documentation.

## Security

- JWT-based authentication
- BCrypt password hashing
- CORS configured for frontend origin
- Role-based access control (STUDENT, TEACHER)

## Error Handling

The API returns standardized error responses:

```json
{
  "timestamp": "2025-11-04T10:30:00",
  "status": 400,
  "message": "Error message",
  "path": "/api/endpoint"
}
```

## Development

### Adding New Endpoints

1. Create model in `model/` package
2. Create repository interface in `repository/` package
3. Implement business logic in `service/` package
4. Create controller in `controller/` package
5. Add tests in `src/test/java/`

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow REST best practices
- Document all public APIs with Swagger annotations
- Write unit tests for services

## Troubleshooting

### MongoDB Connection Issues
- Ensure MongoDB is running: `systemctl status mongod`
- Check connection string in `application.yml`

### JWT Token Issues
- Verify JWT secret is properly configured
- Check token expiration time

### Port Already in Use
- Change port in `application.yml`: `server.port: 8081`

## Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Submit a pull request

## License

MIT License
