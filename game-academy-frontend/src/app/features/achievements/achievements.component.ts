import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationComponent } from '../../shared/navigation/navigation.component';

interface Achievement {
  id: string;
  title: string;
  description: string;
  icon: string;
  category: 'games' | 'points' | 'streak' | 'accuracy' | 'speed' | 'learning';
  tier: 'bronze' | 'silver' | 'gold' | 'platinum';
  pointsReward: number;
  requirement: string;
  progress?: number; // 0-100
  maxProgress?: number;
  isUnlocked: boolean;
  unlockedDate?: Date;
}

type FilterCategory = 'all' | 'games' | 'points' | 'streak' | 'accuracy' | 'speed' | 'learning';
type FilterStatus = 'all' | 'unlocked' | 'locked';

@Component({
  selector: 'app-achievements',
  standalone: true,
  imports: [CommonModule, NavigationComponent],
  templateUrl: './achievements.component.html',
  styleUrls: ['./achievements.component.scss']
})
export class AchievementsComponent implements OnInit {
  achievements: Achievement[] = [
    {
      id: '1',
      title: 'First Steps',
      description: 'Complete your first game',
      icon: '🎮',
      category: 'games',
      tier: 'bronze',
      pointsReward: 50,
      requirement: 'Play 1 game',
      isUnlocked: true,
      unlockedDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
    },
    {
      id: '2',
      title: 'Gaming Enthusiast',
      description: 'Play 10 different games',
      icon: '🎯',
      category: 'games',
      tier: 'silver',
      pointsReward: 100,
      requirement: 'Play 10 games',
      progress: 7,
      maxProgress: 10,
      isUnlocked: false
    },
    {
      id: '3',
      title: 'Game Master',
      description: 'Play 50 different games',
      icon: '👑',
      category: 'games',
      tier: 'gold',
      pointsReward: 250,
      requirement: 'Play 50 games',
      progress: 7,
      maxProgress: 50,
      isUnlocked: false
    },
    {
      id: '4',
      title: 'Point Collector',
      description: 'Earn your first 1000 points',
      icon: '💯',
      category: 'points',
      tier: 'bronze',
      pointsReward: 100,
      requirement: 'Earn 1000 points',
      isUnlocked: true,
      unlockedDate: new Date(Date.now() - 20 * 24 * 60 * 60 * 1000)
    },
    {
      id: '5',
      title: 'Point Hoarder',
      description: 'Accumulate 5000 total points',
      icon: '💰',
      category: 'points',
      tier: 'silver',
      pointsReward: 200,
      requirement: 'Earn 5000 points',
      progress: 3250,
      maxProgress: 5000,
      isUnlocked: false
    },
    {
      id: '6',
      title: 'Point Legend',
      description: 'Reach 10000 total points',
      icon: '💎',
      category: 'points',
      tier: 'gold',
      pointsReward: 500,
      requirement: 'Earn 10000 points',
      progress: 3250,
      maxProgress: 10000,
      isUnlocked: false
    },
    {
      id: '7',
      title: 'Consistency King',
      description: 'Maintain a 7-day streak',
      icon: '🔥',
      category: 'streak',
      tier: 'silver',
      pointsReward: 150,
      requirement: '7 day streak',
      isUnlocked: true,
      unlockedDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)
    },
    {
      id: '8',
      title: 'Dedication Master',
      description: 'Maintain a 30-day streak',
      icon: '🌟',
      category: 'streak',
      tier: 'gold',
      pointsReward: 400,
      requirement: '30 day streak',
      progress: 12,
      maxProgress: 30,
      isUnlocked: false
    },
    {
      id: '9',
      title: 'Perfectionist',
      description: 'Achieve 100% accuracy in a game',
      icon: '🎯',
      category: 'accuracy',
      tier: 'silver',
      pointsReward: 150,
      requirement: '100% accuracy',
      isUnlocked: true,
      unlockedDate: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000)
    },
    {
      id: '10',
      title: 'Accuracy Expert',
      description: 'Maintain 90%+ accuracy across 10 games',
      icon: '🎓',
      category: 'accuracy',
      tier: 'gold',
      pointsReward: 300,
      requirement: '90% avg in 10 games',
      progress: 6,
      maxProgress: 10,
      isUnlocked: false
    },
    {
      id: '11',
      title: 'Speed Demon',
      description: 'Complete a game in under 5 minutes',
      icon: '⚡',
      category: 'speed',
      tier: 'silver',
      pointsReward: 100,
      requirement: 'Complete in <5 min',
      isUnlocked: true,
      unlockedDate: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000)
    },
    {
      id: '12',
      title: 'Lightning Fast',
      description: 'Complete 20 games in under 5 minutes',
      icon: '⚡⚡',
      category: 'speed',
      tier: 'gold',
      pointsReward: 250,
      requirement: '20 games <5 min',
      progress: 8,
      maxProgress: 20,
      isUnlocked: false
    },
    {
      id: '13',
      title: 'Math Whiz',
      description: 'Complete 10 math games with 85%+ accuracy',
      icon: '🔢',
      category: 'learning',
      tier: 'gold',
      pointsReward: 200,
      requirement: '10 math games, 85%+',
      progress: 5,
      maxProgress: 10,
      isUnlocked: false
    },
    {
      id: '14',
      title: 'Science Scholar',
      description: 'Complete 10 science games with 85%+ accuracy',
      icon: '🔬',
      category: 'learning',
      tier: 'gold',
      pointsReward: 200,
      requirement: '10 science games, 85%+',
      progress: 4,
      maxProgress: 10,
      isUnlocked: false
    },
    {
      id: '15',
      title: 'Word Master',
      description: 'Complete 10 language arts games with 85%+ accuracy',
      icon: '📚',
      category: 'learning',
      tier: 'gold',
      pointsReward: 200,
      requirement: '10 language games, 85%+',
      progress: 3,
      maxProgress: 10,
      isUnlocked: false
    }
  ];

  filteredAchievements: Achievement[] = [];
  selectedCategory: FilterCategory = 'all';
  selectedStatus: FilterStatus = 'all';

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredAchievements = this.achievements.filter(achievement => {
      // Category filter
      const categoryMatch = this.selectedCategory === 'all' ||
                           achievement.category === this.selectedCategory;

      // Status filter
      let statusMatch = true;
      if (this.selectedStatus === 'unlocked') {
        statusMatch = achievement.isUnlocked;
      } else if (this.selectedStatus === 'locked') {
        statusMatch = !achievement.isUnlocked;
      }

      return categoryMatch && statusMatch;
    });
  }

  changeCategory(category: FilterCategory): void {
    this.selectedCategory = category;
    this.applyFilters();
  }

  changeStatus(status: FilterStatus): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  getTierClass(tier: string): string {
    return `tier-${tier}`;
  }

  getTierColor(tier: string): string {
    switch (tier) {
      case 'bronze': return '#cd7f32';
      case 'silver': return '#c0c0c0';
      case 'gold': return '#ffd700';
      case 'platinum': return '#e5e4e2';
      default: return '#6b7280';
    }
  }

  getCategoryIcon(category: string): string {
    switch (category) {
      case 'games': return '🎮';
      case 'points': return '💰';
      case 'streak': return '🔥';
      case 'accuracy': return '🎯';
      case 'speed': return '⚡';
      case 'learning': return '📖';
      default: return '🏆';
    }
  }

  getProgressPercentage(achievement: Achievement): number {
    if (!achievement.progress || !achievement.maxProgress) return 0;
    return (achievement.progress / achievement.maxProgress) * 100;
  }

  getUnlockedCount(): number {
    return this.achievements.filter(a => a.isUnlocked).length;
  }

  getTotalPoints(): number {
    return this.achievements
      .filter(a => a.isUnlocked)
      .reduce((sum, a) => sum + a.pointsReward, 0);
  }

  getCompletionPercentage(): number {
    return (this.getUnlockedCount() / this.achievements.length) * 100;
  }

  formatDate(date?: Date): string {
    if (!date) return '';
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }
}
