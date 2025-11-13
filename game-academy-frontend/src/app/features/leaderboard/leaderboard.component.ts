import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationComponent } from '../../shared/navigation/navigation.component';
import { AuthService } from '../../services/auth.service';

interface LeaderboardEntry {
  rank: number;
  userId: string;
  username: string;
  totalPoints: number;
  gamesPlayed: number;
  accuracy: number;
  level: number;
  streak: number;
  avatar?: string;
  isCurrentUser?: boolean;
  rankChange?: number; // positive = moved up, negative = moved down
}

type Period = 'daily' | 'weekly' | 'all-time';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule, NavigationComponent],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss']
})
export class LeaderboardComponent implements OnInit, OnDestroy {
  leaderboardData: LeaderboardEntry[] = [];
  filteredData: LeaderboardEntry[] = [];
  selectedPeriod: Period = 'all-time';
  isLoading = true;
  currentUserId: string | null = null;
  Math = Math; // Make Math available in template

  // Mock data for demonstration
  mockData: Record<Period, LeaderboardEntry[]> = {
    'all-time': [
      {
        rank: 1,
        userId: '1',
        username: 'MathWizard2024',
        totalPoints: 8450,
        gamesPlayed: 125,
        accuracy: 94,
        level: 28,
        streak: 45,
        rankChange: 0
      },
      {
        rank: 2,
        userId: '2',
        username: 'ScienceGuru',
        totalPoints: 7920,
        gamesPlayed: 118,
        accuracy: 91,
        level: 26,
        streak: 38,
        rankChange: 1
      },
      {
        rank: 3,
        userId: '3',
        username: 'BookLover123',
        totalPoints: 7350,
        gamesPlayed: 102,
        accuracy: 89,
        level: 24,
        streak: 22,
        rankChange: -1
      },
      {
        rank: 4,
        userId: '4',
        username: 'HistoryBuff',
        totalPoints: 6840,
        gamesPlayed: 95,
        accuracy: 87,
        level: 22,
        streak: 15,
        rankChange: 2
      },
      {
        rank: 5,
        userId: 'current',
        username: 'You',
        totalPoints: 6320,
        gamesPlayed: 88,
        accuracy: 85,
        level: 21,
        streak: 12,
        isCurrentUser: true,
        rankChange: -1
      },
      {
        rank: 6,
        userId: '6',
        username: 'QuizMaster',
        totalPoints: 5890,
        gamesPlayed: 82,
        accuracy: 83,
        level: 19,
        streak: 8,
        rankChange: 0
      },
      {
        rank: 7,
        userId: '7',
        username: 'BrainAce',
        totalPoints: 5420,
        gamesPlayed: 76,
        accuracy: 81,
        level: 18,
        streak: 5,
        rankChange: 3
      },
      {
        rank: 8,
        userId: '8',
        username: 'SmartKid',
        totalPoints: 4960,
        gamesPlayed: 71,
        accuracy: 79,
        level: 17,
        streak: 3,
        rankChange: -2
      },
      {
        rank: 9,
        userId: '9',
        username: 'StudyBuddy',
        totalPoints: 4530,
        gamesPlayed: 65,
        accuracy: 77,
        level: 15,
        streak: 7,
        rankChange: 1
      },
      {
        rank: 10,
        userId: '10',
        username: 'GameChamp',
        totalPoints: 4120,
        gamesPlayed: 59,
        accuracy: 75,
        level: 14,
        streak: 2,
        rankChange: -3
      }
    ],
    'weekly': [
      {
        rank: 1,
        userId: '2',
        username: 'ScienceGuru',
        totalPoints: 1250,
        gamesPlayed: 28,
        accuracy: 93,
        level: 26,
        streak: 7,
        rankChange: 2
      },
      {
        rank: 2,
        userId: 'current',
        username: 'You',
        totalPoints: 1180,
        gamesPlayed: 25,
        accuracy: 89,
        level: 21,
        streak: 7,
        isCurrentUser: true,
        rankChange: 3
      },
      {
        rank: 3,
        userId: '1',
        username: 'MathWizard2024',
        totalPoints: 1120,
        gamesPlayed: 23,
        accuracy: 95,
        level: 28,
        streak: 7,
        rankChange: -1
      },
      {
        rank: 4,
        userId: '7',
        username: 'BrainAce',
        totalPoints: 980,
        gamesPlayed: 21,
        accuracy: 88,
        level: 18,
        streak: 5,
        rankChange: 5
      },
      {
        rank: 5,
        userId: '4',
        username: 'HistoryBuff',
        totalPoints: 890,
        gamesPlayed: 19,
        accuracy: 86,
        level: 22,
        streak: 6,
        rankChange: 1
      }
    ],
    'daily': [
      {
        rank: 1,
        userId: 'current',
        username: 'You',
        totalPoints: 320,
        gamesPlayed: 8,
        accuracy: 92,
        level: 21,
        streak: 1,
        isCurrentUser: true,
        rankChange: 0
      },
      {
        rank: 2,
        userId: '7',
        username: 'BrainAce',
        totalPoints: 285,
        gamesPlayed: 7,
        accuracy: 89,
        level: 18,
        streak: 1,
        rankChange: 3
      },
      {
        rank: 3,
        userId: '1',
        username: 'MathWizard2024',
        totalPoints: 260,
        gamesPlayed: 6,
        accuracy: 96,
        level: 28,
        streak: 1,
        rankChange: -1
      },
      {
        rank: 4,
        userId: '2',
        username: 'ScienceGuru',
        totalPoints: 240,
        gamesPlayed: 6,
        accuracy: 90,
        level: 26,
        streak: 1,
        rankChange: 1
      }
    ]
  };

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUserId = user.id;
      }
    });

    this.loadLeaderboard();

    // TODO: Set up WebSocket connection for real-time updates
    // this.setupWebSocket();
  }

  ngOnDestroy(): void {
    // TODO: Clean up WebSocket connection
    // this.closeWebSocket();
  }

  loadLeaderboard(): void {
    this.isLoading = true;

    // Simulate API call delay
    setTimeout(() => {
      this.leaderboardData = this.mockData[this.selectedPeriod];
      this.filteredData = [...this.leaderboardData];
      this.isLoading = false;
    }, 500);

    // TODO: Replace with actual API call
    // this.leaderboardService.getLeaderboard(this.selectedPeriod).subscribe(...)
  }

  changePeriod(period: Period): void {
    this.selectedPeriod = period;
    this.loadLeaderboard();
  }

  getRankIcon(rank: number): string {
    switch (rank) {
      case 1: return '🥇';
      case 2: return '🥈';
      case 3: return '🥉';
      default: return '';
    }
  }

  getRankClass(rank: number): string {
    if (rank <= 3) return `rank-${rank}`;
    return '';
  }

  getRankChangeIcon(change?: number): string {
    if (!change) return '';
    if (change > 0) return '📈';
    if (change < 0) return '📉';
    return '';
  }

  getRankChangeClass(change?: number): string {
    if (!change) return '';
    if (change > 0) return 'rank-up';
    if (change < 0) return 'rank-down';
    return '';
  }

  getAvatarLetter(username: string): string {
    return username.charAt(0).toUpperCase();
  }

  getAvatarColor(userId: string): string {
    // Generate consistent color based on userId
    const colors = [
      '#ef4444', '#f59e0b', '#10b981', '#3b82f6',
      '#8b5cf6', '#ec4899', '#06b6d4', '#84cc16'
    ];
    const hash = userId.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    return colors[hash % colors.length];
  }

  scrollToCurrentUser(): void {
    const currentUserElement = document.querySelector('.leaderboard-row.current-user');
    if (currentUserElement) {
      currentUserElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }
}
