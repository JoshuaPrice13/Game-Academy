import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface User {
  id: string;
  username: string;
  email: string;
  role: 'STUDENT' | 'TEACHER';
}

interface NavLink {
  label: string;
  path: string;
  icon: string;
  roles: string[];
}

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  currentUser: User | null = null;
  isSidebarCollapsed = false;
  showUserMenu = false;

  navLinks: NavLink[] = [
    {
      label: 'Dashboard',
      path: '/student/dashboard',
      icon: '🏠',
      roles: ['STUDENT']
    },
    {
      label: 'Play Games',
      path: '/student/games',
      icon: '🎮',
      roles: ['STUDENT']
    },
    {
      label: 'Leaderboard',
      path: '/student/leaderboard',
      icon: '🏆',
      roles: ['STUDENT']
    },
    {
      label: 'Achievements',
      path: '/student/achievements',
      icon: '⭐',
      roles: ['STUDENT']
    },
    {
      label: 'Profile',
      path: '/student/profile',
      icon: '👤',
      roles: ['STUDENT']
    },
    {
      label: 'Dashboard',
      path: '/teacher/dashboard',
      icon: '📊',
      roles: ['TEACHER']
    },
    {
      label: 'My Classes',
      path: '/teacher/classes',
      icon: '👥',
      roles: ['TEACHER']
    },
    {
      label: 'Reports',
      path: '/teacher/reports',
      icon: '📈',
      roles: ['TEACHER']
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  getFilteredNavLinks(): NavLink[] {
    if (!this.currentUser) return [];
    return this.navLinks.filter(link =>
      link.roles.includes(this.currentUser!.role)
    );
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getRoleBadgeClass(): string {
    return this.currentUser?.role === 'TEACHER' ? 'role-teacher' : 'role-student';
  }

  getRoleDisplayName(): string {
    return this.currentUser?.role === 'TEACHER' ? 'Teacher' : 'Student';
  }
}
