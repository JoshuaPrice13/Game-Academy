# Game Academy Frontend

Frontend application for Game Academy - An educational gaming platform that helps students learn through interactive games.

## Overview

The Game Academy frontend is built with Angular and provides a user-friendly interface for:
- Student and teacher authentication
- Student dashboard with progress tracking
- Game catalog and gameplay interface
- Class management for teachers
- Leaderboards and achievements

## Technology Stack

- **Framework**: Angular 20.3
- **Language**: TypeScript
- **Styling**: SCSS
- **UI Components**: Angular Material
- **Charts**: Chart.js with ng2-charts
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router

## Prerequisites

Before you begin, ensure you have the following installed:
- Node.js 22.21+
- npm 10.9+
- Angular CLI 20+

## Installation

1. Navigate to the frontend directory:
```bash
cd game-academy-frontend
```

2. Install dependencies:
```bash
npm install
```

## Running the Application

### Development Server

```bash
npm start
```

or

```bash
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any source files.

### Production Build

```bash
npm run build
```

or

```bash
ng build --configuration production
```

The build artifacts will be stored in the `dist/` directory.

## Project Structure

```
src/
├── app/
│   ├── core/                 # Core functionality
│   │   ├── services/         # Singleton services
│   │   ├── guards/           # Route guards
│   │   └── interceptors/     # HTTP interceptors
│   ├── shared/               # Shared resources
│   │   ├── components/       # Reusable components
│   │   ├── pipes/            # Custom pipes
│   │   └── directives/       # Custom directives
│   ├── features/             # Feature modules
│   │   ├── auth/             # Authentication
│   │   ├── dashboard/        # Student/Teacher dashboard
│   │   ├── games/            # Game catalog and gameplay
│   │   └── leaderboard/      # Leaderboards
│   ├── models/               # TypeScript interfaces
│   ├── app.ts                # Root component
│   ├── app.config.ts         # App configuration
│   └── app.routes.ts         # Route definitions
├── environments/             # Environment configurations
│   ├── environment.ts        # Development environment
│   └── environment.prod.ts   # Production environment
└── styles.scss               # Global styles

```

## Configuration

### Environment Variables

**Development** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  apiTimeout: 30000
};
```

**Production** (`src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: '/api',
  apiTimeout: 30000
};
```

## Features

### Planned Features (To Be Implemented)

1. **Authentication Module**
   - Login/Registration forms
   - JWT token management
   - Role-based routing (Student/Teacher)

2. **Student Dashboard**
   - Progress overview
   - Recent games played
   - Points and level display
   - Achievements showcase

3. **Teacher Dashboard**
   - Class management
   - Student progress monitoring
   - Goal setting interface

4. **Game Catalog**
   - Browse available games
   - Filter by subject/grade level
   - Game details view

5. **Leaderboard**
   - Class rankings
   - Daily/Weekly/All-time views
   - Achievement highlights

## Styling

The application uses:
- **SCSS** for styling with variables and mixins
- **Angular Material** for UI components with custom theme
- **Responsive Design** for mobile and tablet support

### Theme Colors

- Primary: Indigo (#3F51B5)
- Accent: Amber (#FFC107)
- Warn: Red (#F44336)

## Development

### Code Scaffolding

Generate a new component:
```bash
ng generate component features/component-name
```

Generate a new service:
```bash
ng generate service core/services/service-name
```

Generate a new guard:
```bash
ng generate guard core/guards/guard-name
```

### Running Tests

Execute unit tests via Karma:
```bash
npm test
```

or

```bash
ng test
```

### Code Linting

```bash
ng lint
```

## Building for Production

1. Build the application:
```bash
ng build --configuration production
```

2. The output will be in `dist/game-academy-frontend/`

3. Deploy the contents to your web server

## API Integration

The frontend communicates with the backend API. Ensure the backend is running on `http://localhost:8080` during development.

### HTTP Interceptors

- **Auth Interceptor**: Automatically adds JWT token to requests
- **Error Interceptor**: Handles API errors globally

### Services Architecture

- **AuthService**: User authentication and token management
- **StudentService**: Student data and progress
- **GameService**: Game catalog and details
- **ClassService**: Class management
- **LeaderboardService**: Leaderboard data

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Troubleshooting

### Port Already in Use
Change the port in `angular.json`:
```json
"serve": {
  "options": {
    "port": 4300
  }
}
```

### API Connection Issues
- Verify backend is running on port 8080
- Check CORS configuration in backend
- Verify `apiUrl` in environment files

### Build Errors
- Clear node_modules: `rm -rf node_modules && npm install`
- Clear Angular cache: `ng cache clean`

## Contributing

1. Create a feature branch
2. Make your changes
3. Ensure tests pass
4. Submit a pull request

## Future Enhancements

- Progressive Web App (PWA) support
- Offline gameplay capability
- Real-time notifications
- Advanced analytics dashboard
- Mobile app using Ionic

## License

MIT License
