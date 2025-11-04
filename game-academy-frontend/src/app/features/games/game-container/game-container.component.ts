import { Component, OnInit, OnDestroy, ViewChild, ComponentRef, ViewContainerRef, Type } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { GameService } from '../../../services/game.service';
import {
  Game,
  GameSession,
  GameEvent,
  GameEventType,
  GameCompletionResult,
  SessionStatus
} from '../../../models/game.interface';
import { BaseGameComponent } from '../../../shared/base-game.component';

@Component({
  selector: 'app-game-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-container.component.html',
  styleUrls: ['./game-container.component.scss']
})
export class GameContainerComponent implements OnInit, OnDestroy {
  @ViewChild('gameHost', { read: ViewContainerRef }) gameHost!: ViewContainerRef;

  game: Game | null = null;
  session: GameSession | null = null;
  studentId: string = '';
  gameId: string = '';

  isLoading: boolean = true;
  isPaused: boolean = false;
  showCompletionDialog: boolean = false;
  completionResult: GameCompletionResult | null = null;

  currentScore: number = 0;
  questionsAnswered: number = 0;
  timeSpent: number = 0;
  accuracy: number = 0;

  private gameComponentRef: ComponentRef<BaseGameComponent> | null = null;
  private destroy$ = new Subject<void>();
  private progressUpdateInterval: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private gameService: GameService
  ) {}

  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.gameId = params['gameId'];
      // In real implementation, get studentId from auth service
      this.studentId = params['studentId'] || 'demo-student-id';
      this.loadGame();
    });
  }

  ngOnDestroy(): void {
    this.stopProgressUpdates();
    if (this.gameComponentRef) {
      this.gameComponentRef.destroy();
    }
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadGame(): void {
    this.isLoading = true;

    this.gameService.getGameDetails(this.gameId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (game) => {
          this.game = game;
          this.checkForActiveSession();
        },
        error: (error) => {
          console.error('Failed to load game:', error);
          this.isLoading = false;
        }
      });
  }

  private checkForActiveSession(): void {
    this.gameService.getActiveSession(this.studentId, this.gameId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (session) => {
          if (session) {
            this.session = session;
            this.loadGameComponent();
          } else {
            this.startNewSession();
          }
        },
        error: () => {
          this.startNewSession();
        }
      });
  }

  private startNewSession(): void {
    this.gameService.startSession(this.studentId, this.gameId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (session) => {
          this.session = session;
          this.loadGameComponent();
        },
        error: (error) => {
          console.error('Failed to start session:', error);
          this.isLoading = false;
        }
      });
  }

  private loadGameComponent(): void {
    if (!this.session || !this.game) {
      return;
    }

    // Here we would dynamically load the appropriate game component
    // based on the game type. For now, we'll set loading to false
    // The actual game components will be loaded in the next steps

    this.isLoading = false;
    this.startProgressUpdates();
  }

  private startProgressUpdates(): void {
    this.stopProgressUpdates();

    // Send progress updates to backend every 10 seconds
    this.progressUpdateInterval = setInterval(() => {
      if (this.gameComponentRef && this.session && !this.isPaused) {
        const progress = this.gameComponentRef.instance.getCurrentProgress();
        const gameState = this.gameComponentRef.instance.getGameState();

        this.gameService.updateSessionProgress(this.session.id, progress, gameState)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (updatedSession) => {
              this.session = updatedSession;
            },
            error: (error) => {
              console.error('Failed to update progress:', error);
            }
          });
      }
    }, 10000); // Update every 10 seconds
  }

  private stopProgressUpdates(): void {
    if (this.progressUpdateInterval) {
      clearInterval(this.progressUpdateInterval);
      this.progressUpdateInterval = null;
    }
  }

  onGameEvent(event: GameEvent): void {
    switch (event.type) {
      case GameEventType.SCORE_UPDATED:
        this.currentScore = event.data.currentScore;
        this.questionsAnswered = event.data.questionsAnswered;
        break;

      case GameEventType.TIME_UPDATED:
        this.timeSpent = event.data.timeSpent;
        break;

      case GameEventType.GAME_COMPLETED:
        this.handleGameCompletion(event.data);
        break;

      case GameEventType.ERROR:
        console.error('Game error:', event.data);
        break;
    }

    // Calculate accuracy
    if (this.gameComponentRef) {
      const progress = this.gameComponentRef.instance.getCurrentProgress();
      this.accuracy = progress.questionsAnswered > 0
        ? (progress.correctAnswers / progress.questionsAnswered) * 100
        : 0;
    }
  }

  pauseGame(): void {
    if (!this.session || this.isPaused) {
      return;
    }

    this.isPaused = true;

    if (this.gameComponentRef) {
      this.gameComponentRef.instance.pause();
    }

    this.gameService.pauseSession(this.session.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (session) => {
          this.session = session;
        },
        error: (error) => {
          console.error('Failed to pause session:', error);
          this.isPaused = false;
        }
      });
  }

  resumeGame(): void {
    if (!this.session || !this.isPaused) {
      return;
    }

    this.gameService.resumeSession(this.session.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (session) => {
          this.session = session;
          this.isPaused = false;

          if (this.gameComponentRef) {
            this.gameComponentRef.instance.resume();
          }
        },
        error: (error) => {
          console.error('Failed to resume session:', error);
        }
      });
  }

  private handleGameCompletion(data: any): void {
    if (!this.session || !this.gameComponentRef) {
      return;
    }

    this.stopProgressUpdates();

    const finalProgress = this.gameComponentRef.instance.getCurrentProgress();
    const finalState = this.gameComponentRef.instance.getGameState();

    this.gameService.completeSession(this.session.id, finalProgress, finalState)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.completionResult = result;
          this.showCompletionDialog = true;
        },
        error: (error) => {
          console.error('Failed to complete session:', error);
        }
      });
  }

  exitGame(): void {
    if (this.session && this.session.status === SessionStatus.ACTIVE) {
      // Abandon the session if exiting mid-game
      this.gameService.abandonSession(this.session.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          complete: () => {
            this.router.navigate(['/games']);
          }
        });
    } else {
      this.router.navigate(['/games']);
    }
  }

  playAgain(): void {
    this.showCompletionDialog = false;
    this.completionResult = null;
    this.startNewSession();
  }

  returnToGames(): void {
    this.router.navigate(['/games']);
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  }
}
