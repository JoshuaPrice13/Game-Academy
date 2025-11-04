import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Subject, takeUntil } from 'rxjs';
import { environment } from '../../../../environments/environment';

interface ClassOverview {
  classId: string;
  className: string;
  studentCount: number;
  averageScore: number;
  totalGamesPlayed: number;
  activeStudents: number;
}

interface StudentPerformance {
  studentId: string;
  studentName: string;
  totalPoints: number;
  level: number;
  gamesCompleted: number;
  averageAccuracy: number;
  lastActive: Date;
}

interface GameStatistics {
  gameId: string;
  gameName: string;
  timesPlayed: number;
  averageScore: number;
  completionRate: number;
}

@Component({
  selector: 'app-teacher-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './teacher-dashboard.component.html',
  styleUrls: ['./teacher-dashboard.component.scss']
})
export class TeacherDashboardComponent implements OnInit, OnDestroy {
  teacherId: string = 'demo-teacher-id'; // In real app, get from auth service
  classes: ClassOverview[] = [];
  selectedClass: ClassOverview | null = null;
  students: StudentPerformance[] = [];
  gameStats: GameStatistics[] = [];

  isLoading: boolean = true;
  private destroy$ = new Subject<void>();

  // Summary stats
  totalStudents: number = 0;
  totalGamesPlayed: number = 0;
  averageClassScore: number = 0;
  mostPlayedGame: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadTeacherClasses();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadTeacherClasses(): void {
    this.isLoading = true;

    // In real implementation, this would fetch from the backend
    // For now, we'll use mock data
    this.loadMockData();
  }

  private loadMockData(): void {
    // Mock classes
    this.classes = [
      {
        classId: '1',
        className: 'Math - Grade 5A',
        studentCount: 25,
        averageScore: 856,
        totalGamesPlayed: 342,
        activeStudents: 23
      },
      {
        classId: '2',
        className: 'Science - Grade 5B',
        studentCount: 22,
        averageScore: 742,
        totalGamesPlayed: 298,
        activeStudents: 20
      },
      {
        classId: '3',
        className: 'Language Arts - Grade 6',
        studentCount: 28,
        averageScore: 923,
        totalGamesPlayed: 421,
        activeStudents: 26
      }
    ];

    // Mock students
    this.students = [
      {
        studentId: '1',
        studentName: 'Alice Johnson',
        totalPoints: 1250,
        level: 8,
        gamesCompleted: 45,
        averageAccuracy: 92.5,
        lastActive: new Date('2025-01-15')
      },
      {
        studentId: '2',
        studentName: 'Bob Smith',
        totalPoints: 980,
        level: 6,
        gamesCompleted: 38,
        averageAccuracy: 87.3,
        lastActive: new Date('2025-01-14')
      },
      {
        studentId: '3',
        studentName: 'Carol Williams',
        totalPoints: 1420,
        level: 9,
        gamesCompleted: 52,
        averageAccuracy: 94.8,
        lastActive: new Date('2025-01-15')
      },
      {
        studentId: '4',
        studentName: 'David Brown',
        totalPoints: 756,
        level: 5,
        gamesCompleted: 31,
        averageAccuracy: 82.1,
        lastActive: new Date('2025-01-13')
      },
      {
        studentId: '5',
        studentName: 'Emma Davis',
        totalPoints: 1105,
        level: 7,
        gamesCompleted: 42,
        averageAccuracy: 89.7,
        lastActive: new Date('2025-01-15')
      }
    ];

    // Mock game statistics
    this.gameStats = [
      {
        gameId: '1',
        gameName: 'Speed Math',
        timesPlayed: 156,
        averageScore: 245,
        completionRate: 89.5
      },
      {
        gameId: '2',
        gameName: 'Cell Structure Explorer',
        timesPlayed: 134,
        averageScore: 182,
        completionRate: 92.3
      },
      {
        gameId: '3',
        gameName: 'Vocabulary Builder',
        timesPlayed: 142,
        averageScore: 167,
        completionRate: 87.1
      }
    ];

    // Calculate summary stats
    this.calculateSummaryStats();

    this.isLoading = false;
  }

  private calculateSummaryStats(): void {
    this.totalStudents = this.classes.reduce((sum, c) => sum + c.studentCount, 0);
    this.totalGamesPlayed = this.classes.reduce((sum, c) => sum + c.totalGamesPlayed, 0);
    this.averageClassScore = this.classes.length > 0
      ? Math.round(this.classes.reduce((sum, c) => sum + c.averageScore, 0) / this.classes.length)
      : 0;

    if (this.gameStats.length > 0) {
      const mostPlayed = this.gameStats.reduce((max, game) =>
        game.timesPlayed > max.timesPlayed ? game : max
      );
      this.mostPlayedGame = mostPlayed.gameName;
    }
  }

  selectClass(classData: ClassOverview): void {
    this.selectedClass = classData;
    // In real implementation, load class-specific data
  }

  getStudentRank(index: number): string {
    return (index + 1).toString();
  }

  formatDate(date: Date): string {
    const now = new Date();
    const diff = now.getTime() - new Date(date).getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days === 0) return 'Today';
    if (days === 1) return 'Yesterday';
    if (days < 7) return `${days} days ago`;
    return new Date(date).toLocaleDateString();
  }

  getPerformanceColor(accuracy: number): string {
    if (accuracy >= 90) return 'excellent';
    if (accuracy >= 80) return 'good';
    if (accuracy >= 70) return 'average';
    return 'needs-improvement';
  }

  exportReport(): void {
    // In real implementation, generate and download a report
    alert('Report export feature would be implemented here');
  }

  viewStudentDetails(studentId: string): void {
    // In real implementation, navigate to student detail page
    alert(`View details for student: ${studentId}`);
  }
}
