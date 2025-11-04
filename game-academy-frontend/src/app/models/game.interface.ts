// Core game interfaces and types for the Game Academy platform

export interface Game {
  id: string;
  gameTitle: string;
  subject: string;
  gradeLevel: string;
  description: string;
  maxPoints: number;
  difficultyLevel: string;
}

export interface GameSession {
  id: string;
  studentId: string;
  gameId: string;
  status: SessionStatus;
  startTime: Date;
  endTime?: Date;
  lastPausedTime?: Date;
  currentScore: number;
  questionsAnswered: number;
  correctAnswers: number;
  incorrectAnswers: number;
  timeSpentSeconds: number;
  pausedDurationSeconds: number;
  gameState: Record<string, any>;
  performanceMetrics: PerformanceMetrics;
  bonusPointsAwarded: boolean;
  bonusPointsEarned: number;
}

export enum SessionStatus {
  ACTIVE = 'ACTIVE',
  PAUSED = 'PAUSED',
  COMPLETED = 'COMPLETED',
  ABANDONED = 'ABANDONED'
}

export interface PerformanceMetrics {
  accuracy: number;
  averageTimePerQuestion: number;
  totalTime: number;
}

export interface GameCompletionResult {
  session: GameSession;
  bonusPoints: number;
  newAchievements: string[];
  performanceMetrics: PerformanceMetrics;
}

export interface GameProgress {
  currentScore: number;
  questionsAnswered: number;
  correctAnswers: number;
  incorrectAnswers: number;
  timeSpentSeconds: number;
}

// Base interface that all games must implement
export interface IGame {
  // Game lifecycle methods
  initialize(sessionId: string, gameId: string): void;
  start(): void;
  pause(): void;
  resume(): void;
  complete(): void;

  // Game state methods
  getCurrentProgress(): GameProgress;
  getGameState(): Record<string, any>;
  updateGameState(state: Record<string, any>): void;

  // Question/challenge methods
  generateQuestion(): void;
  checkAnswer(answer: any): boolean;
  nextQuestion(): void;

  // Utility methods
  getGameInfo(): GameInfo;
  getRemainingTime?(): number; // Optional for timed games
}

export interface GameInfo {
  title: string;
  description: string;
  subject: string;
  instructions: string;
  totalQuestions?: number;
  timeLimit?: number; // in seconds, optional
}

export interface GameQuestion {
  id: string;
  question: string;
  type: QuestionType;
  options?: string[];
  correctAnswer: any;
  points: number;
  difficulty: DifficultyLevel;
  explanation?: string;
}

export enum QuestionType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  TRUE_FALSE = 'TRUE_FALSE',
  SHORT_ANSWER = 'SHORT_ANSWER',
  MATCHING = 'MATCHING',
  DRAG_AND_DROP = 'DRAG_AND_DROP',
  INTERACTIVE = 'INTERACTIVE'
}

export enum DifficultyLevel {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD'
}

export interface GameConfig {
  enableSound?: boolean;
  enableTimer?: boolean;
  showHints?: boolean;
  difficulty?: DifficultyLevel;
  questionCount?: number;
}

// Events emitted by games
export interface GameEvent {
  type: GameEventType;
  data: any;
  timestamp: Date;
}

export enum GameEventType {
  QUESTION_ANSWERED = 'QUESTION_ANSWERED',
  SCORE_UPDATED = 'SCORE_UPDATED',
  TIME_UPDATED = 'TIME_UPDATED',
  GAME_PAUSED = 'GAME_PAUSED',
  GAME_RESUMED = 'GAME_RESUMED',
  GAME_COMPLETED = 'GAME_COMPLETED',
  ERROR = 'ERROR'
}
