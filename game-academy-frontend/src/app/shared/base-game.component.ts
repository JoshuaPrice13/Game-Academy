import { Component, OnDestroy, OnInit, Output, EventEmitter } from '@angular/core';
import {
  IGame,
  GameProgress,
  GameInfo,
  GameConfig,
  GameEvent,
  GameEventType,
  SessionStatus
} from '../models/game.interface';

/**
 * Abstract base class for all game components
 * Provides common functionality like:
 * - Session management
 * - Timer tracking
 * - Score tracking
 * - Progress updates
 * - Pause/Resume functionality
 */
@Component({
  template: ''
})
export abstract class BaseGameComponent implements IGame, OnInit, OnDestroy {
  // Session tracking
  protected sessionId: string = '';
  protected gameId: string = '';
  protected sessionStatus: SessionStatus = SessionStatus.ACTIVE;

  // Score tracking
  protected currentScore: number = 0;
  protected questionsAnswered: number = 0;
  protected correctAnswers: number = 0;
  protected incorrectAnswers: number = 0;

  // Time tracking
  protected startTime: Date | null = null;
  protected timeSpentSeconds: number = 0;
  protected pausedTime: Date | null = null;
  protected pausedDurationSeconds: number = 0;
  private timerInterval: any = null;

  // Game state
  protected gameState: Record<string, any> = {};
  protected isPaused: boolean = false;
  protected isInitialized: boolean = false;

  // Configuration
  protected config: GameConfig = {
    enableSound: true,
    enableTimer: true,
    showHints: true
  };

  // Events
  @Output() gameEvent = new EventEmitter<GameEvent>();

  ngOnInit(): void {
    // Override in child classes if needed
  }

  ngOnDestroy(): void {
    this.stopTimer();
  }

  // ==================== Abstract Methods (must be implemented) ====================

  /**
   * Generate a new question/challenge for the game
   */
  abstract generateQuestion(): void;

  /**
   * Check if the provided answer is correct
   */
  abstract checkAnswer(answer: any): boolean;

  /**
   * Get game-specific information
   */
  abstract getGameInfo(): GameInfo;

  // ==================== Lifecycle Methods ====================

  initialize(sessionId: string, gameId: string): void {
    this.sessionId = sessionId;
    this.gameId = gameId;
    this.isInitialized = true;
    this.resetGameState();
    this.onInitialize();
  }

  start(): void {
    if (!this.isInitialized) {
      throw new Error('Game must be initialized before starting');
    }

    this.startTime = new Date();
    this.sessionStatus = SessionStatus.ACTIVE;
    this.startTimer();
    this.generateQuestion();
    this.onStart();

    this.emitEvent(GameEventType.GAME_RESUMED, {
      sessionId: this.sessionId
    });
  }

  pause(): void {
    if (this.sessionStatus !== SessionStatus.ACTIVE) {
      return;
    }

    this.isPaused = true;
    this.pausedTime = new Date();
    this.sessionStatus = SessionStatus.PAUSED;
    this.stopTimer();
    this.onPause();

    this.emitEvent(GameEventType.GAME_PAUSED, {
      sessionId: this.sessionId,
      currentProgress: this.getCurrentProgress()
    });
  }

  resume(): void {
    if (this.sessionStatus !== SessionStatus.PAUSED) {
      return;
    }

    if (this.pausedTime) {
      const pausedMs = new Date().getTime() - this.pausedTime.getTime();
      this.pausedDurationSeconds += Math.floor(pausedMs / 1000);
      this.pausedTime = null;
    }

    this.isPaused = false;
    this.sessionStatus = SessionStatus.ACTIVE;
    this.startTimer();
    this.onResume();

    this.emitEvent(GameEventType.GAME_RESUMED, {
      sessionId: this.sessionId
    });
  }

  complete(): void {
    this.sessionStatus = SessionStatus.COMPLETED;
    this.stopTimer();
    this.onComplete();

    this.emitEvent(GameEventType.GAME_COMPLETED, {
      sessionId: this.sessionId,
      finalProgress: this.getCurrentProgress(),
      finalState: this.gameState
    });
  }

  // ==================== Progress Methods ====================

  getCurrentProgress(): GameProgress {
    return {
      currentScore: this.currentScore,
      questionsAnswered: this.questionsAnswered,
      correctAnswers: this.correctAnswers,
      incorrectAnswers: this.incorrectAnswers,
      timeSpentSeconds: this.timeSpentSeconds
    };
  }

  getGameState(): Record<string, any> {
    return { ...this.gameState };
  }

  updateGameState(state: Record<string, any>): void {
    this.gameState = { ...this.gameState, ...state };
  }

  // ==================== Question Methods ====================

  nextQuestion(): void {
    this.generateQuestion();
  }

  protected recordAnswer(isCorrect: boolean, pointsEarned: number = 10): void {
    this.questionsAnswered++;

    if (isCorrect) {
      this.correctAnswers++;
      this.currentScore += pointsEarned;
    } else {
      this.incorrectAnswers++;
    }

    this.emitEvent(GameEventType.QUESTION_ANSWERED, {
      isCorrect,
      pointsEarned,
      totalScore: this.currentScore,
      questionsAnswered: this.questionsAnswered
    });

    this.emitEvent(GameEventType.SCORE_UPDATED, {
      currentScore: this.currentScore,
      questionsAnswered: this.questionsAnswered
    });
  }

  // ==================== Timer Methods ====================

  private startTimer(): void {
    if (!this.config.enableTimer) {
      return;
    }

    this.stopTimer(); // Clear any existing timer

    this.timerInterval = setInterval(() => {
      if (!this.isPaused && this.sessionStatus === SessionStatus.ACTIVE) {
        this.timeSpentSeconds++;
        this.onTimerTick();

        this.emitEvent(GameEventType.TIME_UPDATED, {
          timeSpent: this.timeSpentSeconds
        });
      }
    }, 1000);
  }

  private stopTimer(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  getRemainingTime(): number {
    // Override in child classes for timed games
    return 0;
  }

  // ==================== Utility Methods ====================

  protected getAccuracy(): number {
    if (this.questionsAnswered === 0) return 0;
    return (this.correctAnswers / this.questionsAnswered) * 100;
  }

  protected getAverageTimePerQuestion(): number {
    if (this.questionsAnswered === 0) return 0;
    return this.timeSpentSeconds / this.questionsAnswered;
  }

  protected resetGameState(): void {
    this.currentScore = 0;
    this.questionsAnswered = 0;
    this.correctAnswers = 0;
    this.incorrectAnswers = 0;
    this.timeSpentSeconds = 0;
    this.pausedDurationSeconds = 0;
    this.gameState = {};
  }

  protected emitEvent(type: GameEventType, data: any): void {
    this.gameEvent.emit({
      type,
      data,
      timestamp: new Date()
    });
  }

  // ==================== Hook Methods (can be overridden) ====================

  protected onInitialize(): void {
    // Override in child classes
  }

  protected onStart(): void {
    // Override in child classes
  }

  protected onPause(): void {
    // Override in child classes
  }

  protected onResume(): void {
    // Override in child classes
  }

  protected onComplete(): void {
    // Override in child classes
  }

  protected onTimerTick(): void {
    // Override in child classes
  }
}
