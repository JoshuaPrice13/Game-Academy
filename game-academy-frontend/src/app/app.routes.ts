import { Routes } from '@angular/router';
import { authGuard, teacherGuard, studentGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Public routes
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },

  // Student routes (protected)
  {
    path: 'student',
    canActivate: [authGuard, studentGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/student/dashboard/student-dashboard.component').then(m => m.StudentDashboardComponent)
      },
      {
        path: 'games',
        loadComponent: () => import('./features/games/game-list/game-list.component').then(m => m.GameListComponent)
      },
      {
        path: 'games/:gameId',
        loadComponent: () => import('./features/games/game-container/game-container.component').then(m => m.GameContainerComponent)
      },
      {
        path: 'leaderboard',
        loadComponent: () => import('./features/leaderboard/leaderboard.component').then(m => m.LeaderboardComponent)
      },
      {
        path: 'achievements',
        loadComponent: () => import('./features/achievements/achievements.component').then(m => m.AchievementsComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
      }
    ]
  },

  // Teacher routes (protected)
  {
    path: 'teacher',
    canActivate: [authGuard, teacherGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/teacher/dashboard/teacher-dashboard.component').then(m => m.TeacherDashboardComponent)
      },
      {
        path: 'classes',
        loadComponent: () => import('./features/teacher/classes/class-list.component').then(m => m.ClassListComponent)
      },
      {
        path: 'classes/:classId',
        loadComponent: () => import('./features/teacher/classes/class-detail.component').then(m => m.ClassDetailComponent)
      }
    ]
  },

  // Wildcard route
  {
    path: '**',
    redirectTo: '/login'
  }
];
