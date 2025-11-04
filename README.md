# Game Academy

An educational gaming platform that helps students learn through interactive games while allowing teachers to track progress and manage their classes.

## Overview

Game Academy is a full-stack application consisting of:
- **Frontend**: Angular-based web application for students and teachers
- **Backend**: Spring Boot REST API with MongoDB database
- **Features**: Authentication, progress tracking, gamification, leaderboards, and class management

## Project Structure

```
Game-Academy/
├── game-academy-frontend/    # Angular frontend application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/        # Core services and utilities
│   │   │   ├── shared/      # Shared components
│   │   │   ├── features/    # Feature modules
│   │   │   └── models/      # TypeScript interfaces
│   │   └── environments/    # Environment configs
│   └── README.md
│
├── game-academy-backend/     # Spring Boot backend API
│   ├── src/main/java/com/gameacademy/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # MongoDB repositories
│   │   ├── model/           # Entity models
│   │   ├── dto/             # Data transfer objects
│   │   ├── security/        # Security configuration
│   │   └── config/          # Application configuration
│   └── README.md
│
└── README.md                 # This file
```

## Technology Stack

### Frontend
- Angular 20.3
- TypeScript
- SCSS
- Angular Material
- Chart.js
- RxJS

### Backend
- Java 21
- Spring Boot 3.2.0
- Spring Security
- Spring Data MongoDB
- JWT Authentication
- Maven

### Database
- MongoDB 4.4+

## Quick Start

### Prerequisites

- Node.js 22.21+
- Java 21+
- Maven 3.9+
- MongoDB 4.4+
- npm 10.9+

### 1. Start MongoDB

```bash
# On Linux/Mac
sudo systemctl start mongod

# Verify it's running
mongo --eval 'db.runCommand({ ping: 1 })'
```

### 2. Start Backend

```bash
cd game-academy-backend
mvn spring-boot:run
```

Backend will be available at `http://localhost:8080`

### 3. Start Frontend

```bash
cd game-academy-frontend
npm install
npm start
```

Frontend will be available at `http://localhost:4200`

## Features

### Phase 1 (Current) - Core Infrastructure

- [x] User authentication (JWT-based)
- [x] User registration for students and teachers
- [x] Student profile and progress tracking
- [x] Class management
- [x] Game catalog
- [x] Game progress tracking
- [x] API documentation (Swagger)

### Phase 2 (Planned)

- [ ] Interactive game implementations
- [ ] Real-time leaderboards
- [ ] Achievement system
- [ ] Teacher analytics dashboard
- [ ] Class goals and challenges
- [ ] Notifications system

### Phase 3 (Future)

- [ ] Parent portal
- [ ] Mobile application
- [ ] Advanced analytics and reporting
- [ ] Content creation tools for teachers
- [ ] Integration with external learning platforms

## API Documentation

Once the backend is running, access the interactive API documentation at:

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Key Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token
- `GET /api/auth/me` - Get current user info

#### Students
- `GET /api/students/{userId}/profile` - Get student profile
- `GET /api/students/{studentId}/games` - Get game history
- `PUT /api/students/{studentId}/points` - Update points

#### Games
- `GET /api/games` - List all games
- `GET /api/games/subject/{subject}` - Filter by subject
- `POST /api/progress/record` - Record game session

#### Classes
- `POST /api/classes` - Create new class
- `GET /api/classes/teacher/{teacherId}` - Get teacher's classes
- `POST /api/classes/{classId}/goals` - Set class goal

## Database Schema

### Collections

1. **users** - User accounts (students and teachers)
2. **students** - Student profiles with progress data
3. **classes** - Class information and rosters
4. **games** - Game catalog
5. **gameProgress** - Individual game session records
6. **leaderboards** - Class ranking data

See `game-academy-backend/MONGODB_SCHEMA.md` for detailed schema documentation.

## Development

### Backend Development

```bash
cd game-academy-backend

# Run tests
mvn test

# Build
mvn clean package

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Development

```bash
cd game-academy-frontend

# Run tests
npm test

# Build for production
npm run build

# Lint code
ng lint
```

## Testing

### Backend Tests

```bash
cd game-academy-backend
mvn test
```

Test coverage includes:
- Controller integration tests
- Service unit tests
- Repository tests

### Frontend Tests

```bash
cd game-academy-frontend
npm test
```

## Deployment

### Backend Deployment

1. Build the JAR:
```bash
mvn clean package
```

2. Run the JAR:
```bash
java -jar target/game-academy-backend-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment

1. Build for production:
```bash
ng build --configuration production
```

2. Deploy the `dist/` folder to your web server (Nginx, Apache, etc.)

## Environment Variables

### Backend

- `JWT_SECRET` - Secret key for JWT token generation
- `MONGODB_URI` - MongoDB connection string
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins

### Frontend

Configure in `src/environments/`:
- `apiUrl` - Backend API URL
- `production` - Production mode flag

## Security

- JWT-based authentication
- BCrypt password hashing
- CORS protection
- Role-based access control (RBAC)
- Input validation
- XSS protection

## Performance Considerations

- MongoDB indexes on frequently queried fields
- JWT token expiration (24 hours)
- Connection pooling
- Lazy loading for Angular modules (planned)
- CDN for static assets (planned)

## Troubleshooting

### Backend won't start
- Check MongoDB is running
- Verify Java version (21+)
- Check port 8080 is available

### Frontend won't connect to backend
- Verify backend is running
- Check CORS configuration
- Verify API URL in environment files

### Database connection issues
- Ensure MongoDB service is running
- Check MongoDB connection string
- Verify database name matches configuration

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

MIT License - see LICENSE file for details

## Contact

For questions or support, please contact:
- Email: support@gameacademy.com
- Website: https://gameacademy.com

## Acknowledgments

- Spring Boot community
- Angular team
- MongoDB documentation
- All contributors

---

**Built with ❤️ for education**
