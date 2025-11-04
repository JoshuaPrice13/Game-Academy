import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import {
  Game,
  GameSession,
  GameProgress,
  GameCompletionResult
} from '../models/game.interface';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = `${environment.apiUrl}/games`;
  private sessionApiUrl = `${environment.apiUrl}/game-sessions`;

  // Current active session
  private activeSessionSubject = new BehaviorSubject<GameSession | null>(null);
  public activeSession$ = this.activeSessionSubject.asObservable();

  constructor(private http: HttpClient) {}

  // ==================== Game Management ====================

  /**
   * Get all available games
   */
  getAllGames(): Observable<Game[]> {
    return this.http.get<Game[]>(this.apiUrl);
  }

  /**
   * Get games by subject
   */
  getGamesBySubject(subject: string): Observable<Game[]> {
    const params = new HttpParams().set('subject', subject);
    return this.http.get<Game[]>(`${this.apiUrl}/by-subject`, { params });
  }

  /**
   * Get games by grade level
   */
  getGamesByGradeLevel(gradeLevel: string): Observable<Game[]> {
    const params = new HttpParams().set('gradeLevel', gradeLevel);
    return this.http.get<Game[]>(`${this.apiUrl}/by-grade`, { params });
  }

  /**
   * Get specific game details
   */
  getGameDetails(gameId: string): Observable<Game> {
    return this.http.get<Game>(`${this.apiUrl}/${gameId}`);
  }

  // ==================== Session Management ====================

  /**
   * Start a new game session
   */
  startSession(studentId: string, gameId: string): Observable<GameSession> {
    const params = new HttpParams()
      .set('studentId', studentId)
      .set('gameId', gameId);

    return this.http.post<GameSession>(`${this.sessionApiUrl}/start`, null, { params })
      .pipe(
        tap(session => this.activeSessionSubject.next(session))
      );
  }

  /**
   * Get active session for a student and game
   */
  getActiveSession(studentId: string, gameId: string): Observable<GameSession | null> {
    const params = new HttpParams()
      .set('studentId', studentId)
      .set('gameId', gameId);

    return this.http.get<GameSession>(`${this.sessionApiUrl}/active`, { params })
      .pipe(
        tap(session => this.activeSessionSubject.next(session))
      );
  }

  /**
   * Update game session progress
   */
  updateSessionProgress(sessionId: string, progress: GameProgress, gameState?: Record<string, any>): Observable<GameSession> {
    const progressData = {
      currentScore: progress.currentScore,
      questionsAnswered: progress.questionsAnswered,
      correctAnswers: progress.correctAnswers,
      incorrectAnswers: progress.incorrectAnswers,
      timeSpentSeconds: progress.timeSpentSeconds,
      gameState: gameState || {}
    };

    return this.http.put<GameSession>(
      `${this.sessionApiUrl}/${sessionId}/progress`,
      progressData
    ).pipe(
      tap(session => this.activeSessionSubject.next(session))
    );
  }

  /**
   * Pause game session
   */
  pauseSession(sessionId: string): Observable<GameSession> {
    return this.http.put<GameSession>(
      `${this.sessionApiUrl}/${sessionId}/pause`,
      {}
    ).pipe(
      tap(session => this.activeSessionSubject.next(session))
    );
  }

  /**
   * Resume game session
   */
  resumeSession(sessionId: string): Observable<GameSession> {
    return this.http.put<GameSession>(
      `${this.sessionApiUrl}/${sessionId}/resume`,
      {}
    ).pipe(
      tap(session => this.activeSessionSubject.next(session))
    );
  }

  /**
   * Complete game session
   */
  completeSession(sessionId: string, finalProgress: GameProgress, gameState?: Record<string, any>): Observable<GameCompletionResult> {
    const finalData = {
      currentScore: finalProgress.currentScore,
      questionsAnswered: finalProgress.questionsAnswered,
      correctAnswers: finalProgress.correctAnswers,
      incorrectAnswers: finalProgress.incorrectAnswers,
      timeSpentSeconds: finalProgress.timeSpentSeconds,
      gameState: gameState || {}
    };

    return this.http.post<GameCompletionResult>(
      `${this.sessionApiUrl}/${sessionId}/complete`,
      finalData
    ).pipe(
      tap(() => this.activeSessionSubject.next(null))
    );
  }

  /**
   * Abandon game session
   */
  abandonSession(sessionId: string): Observable<GameSession> {
    return this.http.put<GameSession>(
      `${this.sessionApiUrl}/${sessionId}/abandon`,
      {}
    ).pipe(
      tap(() => this.activeSessionSubject.next(null))
    );
  }

  /**
   * Get session details
   */
  getSessionDetails(sessionId: string): Observable<GameSession> {
    return this.http.get<GameSession>(`${this.sessionApiUrl}/${sessionId}`);
  }

  /**
   * Get all sessions for a student
   */
  getStudentSessions(studentId: string): Observable<GameSession[]> {
    return this.http.get<GameSession[]>(`${this.sessionApiUrl}/student/${studentId}`);
  }

  /**
   * Clear active session
   */
  clearActiveSession(): void {
    this.activeSessionSubject.next(null);
  }

  /**
   * Get current active session value
   */
  getCurrentSession(): GameSession | null {
    return this.activeSessionSubject.value;
  }
}
