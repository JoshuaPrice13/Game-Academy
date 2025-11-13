import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NavigationComponent } from '../../../shared/navigation/navigation.component';

interface User {
  id: string;
  username: string;
  email: string;
  role: 'STUDENT' | 'TEACHER';
}

interface StudentStats {
  totalPoints: number;
  level: number;
  rank: number;
  totalStudents: number;
  gamesPlayed: number;
  accuracy: number;
  streak: number;
  achievements: number;
}

interface RecentActivity {
  id: string;
  type: 'game' | 'achievement' | 'level_up';
  title: string;
  description: string;
  points: number;
  timestamp: Date;
  icon: string;
}

interface QuickGame {
  id: string;
  title: string;
  subject: string;
  icon: string;
  color: string;
  playCount: number;
  bestScore: number;
}

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavigationComponent],
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.scss']
})
export class StudentDashboardComponent implements OnInit {
  currentUser: User | null = null;
  isLoading = true;

  stats: StudentStats = {
    totalPoints: 2450,
    level: 12,
    rank: 5,
    totalStudents: 45,
    gamesPlayed: 38,
    accuracy: 87,
    streak: 7,
    achievements: 15
  };

  recentActivities: RecentActivity[] = [
    {
      id: '1',
      type: 'achievement',
      title: 'Speed Demon',
      description: 'Completed 10 Speed Math games',
      points: 100,
      timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2 hours ago
      icon: '🏆'
    },
    {
      id: '2',
      type: 'game',
      title: 'Cell Structure Explorer',
      description: 'Scored 95% accuracy',
      points: 50,
      timestamp: new Date(Date.now() - 5 * 60 * 60 * 1000), // 5 hours ago
      icon: '🔬'
    },
    {
      id: '3',
      type: 'level_up',
      title: 'Level Up!',
      description: 'Reached Level 12',
      points: 200,
      timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000), // 1 day ago
      icon: '⬆️'
    },
    {
      id: '4',
      type: 'game',
      title: 'Vocabulary Builder',
      description: 'Learned 8 new words',
      points: 40,
      timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), // 2 days ago
      icon: '📚'
    }
  ];

  quickGames: QuickGame[] = [
    {
      id: 'speed-math',
      title: 'Speed Math',
      subject: 'Mathematics',
      icon: '🔢',
      color: '#3b82f6',
      playCount: 15,
      bestScore: 980
    },
    {
      id: 'cell-explorer',
      title: 'Cell Explorer',
      subject: 'Science',
      icon: '🔬',
      color: '#10b981',
      playCount: 8,
      bestScore: 850
    },
    {
      id: 'vocabulary-builder',
      title: 'Vocabulary Builder',
      subject: 'Language Arts',
      icon: '📚',
      color: '#8b5cf6',
      playCount: 12,
      bestScore: 920
    }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoading = false;
    });

    // TODO: Load actual student stats from backend
    // this.loadStudentStats();
  }

  getGreeting(): string {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
  }

  getTimeAgo(timestamp: Date): string {
    const seconds = Math.floor((new Date().getTime() - timestamp.getTime()) / 1000);

    if (seconds < 60) return 'Just now';
    if (seconds < 3600) return `${Math.floor(seconds / 60)} minutes ago`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)} hours ago`;
    if (seconds < 604800) return `${Math.floor(seconds / 86400)} days ago`;
    return timestamp.toLocaleDateString();
  }

  getLevelProgress(): number {
    // Points needed for next level: level * 250
    const pointsForNextLevel = (this.stats.level + 1) * 250;
    const pointsInCurrentLevel = this.stats.totalPoints % 250;
    return (pointsInCurrentLevel / 250) * 100;
  }

  getPointsToNextLevel(): number {
    const pointsForNextLevel = (this.stats.level + 1) * 250;
    const pointsInCurrentLevel = this.stats.totalPoints % 250;
    return 250 - pointsInCurrentLevel;
  }

  getRankPercentile(): number {
    return Math.round((1 - (this.stats.rank / this.stats.totalStudents)) * 100);
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'game': return '🎮';
      case 'achievement': return '🏆';
      case 'level_up': return '⬆️';
      default: return '📌';
    }
  }

  getActivityClass(type: string): string {
    switch (type) {
      case 'game': return 'activity-game';
      case 'achievement': return 'activity-achievement';
      case 'level_up': return 'activity-levelup';
      default: return '';
    }
  }
}
