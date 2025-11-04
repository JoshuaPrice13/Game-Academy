import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseGameComponent } from '../../../shared/base-game.component';
import { GameInfo, DifficultyLevel, QuestionType } from '../../../models/game.interface';

interface MathQuestion {
  num1: number;
  num2: number;
  operator: string;
  correctAnswer: number;
  displayText: string;
}

@Component({
  selector: 'app-speed-math',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './speed-math.component.html',
  styleUrls: ['./speed-math.component.scss']
})
export class SpeedMathComponent extends BaseGameComponent {
  currentQuestion: MathQuestion | null = null;
  userAnswer: string = '';
  feedback: string = '';
  showFeedback: boolean = false;
  streak: number = 0;
  bestStreak: number = 0;
  difficulty: DifficultyLevel = DifficultyLevel.EASY;
  totalQuestionsGoal: number = 20;
  operators: string[] = ['+', '-', '×'];

  private feedbackTimeout: any = null;

  getGameInfo(): GameInfo {
    return {
      title: 'Speed Math',
      description: 'Solve math problems as quickly as possible!',
      subject: 'Math',
      instructions: 'Answer each math question correctly to earn points. Higher streaks earn bonus points!',
      totalQuestions: this.totalQuestionsGoal
    };
  }

  protected override onInitialize(): void {
    // Set difficulty based on grade level or config
    this.difficulty = this.config.difficulty || DifficultyLevel.EASY;
    this.updateOperators();
  }

  protected override onStart(): void {
    this.generateQuestion();
  }

  private updateOperators(): void {
    switch (this.difficulty) {
      case DifficultyLevel.EASY:
        this.operators = ['+', '-'];
        break;
      case DifficultyLevel.MEDIUM:
        this.operators = ['+', '-', '×'];
        break;
      case DifficultyLevel.HARD:
        this.operators = ['+', '-', '×', '÷'];
        break;
    }
  }

  generateQuestion(): void {
    const operator = this.operators[Math.floor(Math.random() * this.operators.length)];
    let num1: number, num2: number, correctAnswer: number;

    switch (this.difficulty) {
      case DifficultyLevel.EASY:
        num1 = Math.floor(Math.random() * 10) + 1;
        num2 = Math.floor(Math.random() * 10) + 1;
        break;
      case DifficultyLevel.MEDIUM:
        num1 = Math.floor(Math.random() * 50) + 1;
        num2 = Math.floor(Math.random() * 20) + 1;
        break;
      case DifficultyLevel.HARD:
        num1 = Math.floor(Math.random() * 100) + 1;
        num2 = Math.floor(Math.random() * 50) + 1;
        break;
      default:
        num1 = 1;
        num2 = 1;
    }

    // Calculate answer based on operator
    switch (operator) {
      case '+':
        correctAnswer = num1 + num2;
        break;
      case '-':
        // Ensure positive results for easy mode
        if (this.difficulty === DifficultyLevel.EASY && num1 < num2) {
          [num1, num2] = [num2, num1];
        }
        correctAnswer = num1 - num2;
        break;
      case '×':
        correctAnswer = num1 * num2;
        break;
      case '÷':
        // Ensure clean division
        correctAnswer = num2;
        num1 = num2 * (Math.floor(Math.random() * 10) + 1);
        correctAnswer = num1 / num2;
        break;
      default:
        correctAnswer = 0;
    }

    this.currentQuestion = {
      num1,
      num2,
      operator,
      correctAnswer,
      displayText: `${num1} ${operator} ${num2}`
    };

    this.userAnswer = '';
    this.showFeedback = false;

    // Update game state
    this.updateGameState({
      currentQuestion: this.currentQuestion,
      streak: this.streak,
      bestStreak: this.bestStreak
    });
  }

  checkAnswer(answer: any): boolean {
    if (!this.currentQuestion) {
      return false;
    }

    const userAnswerNum = parseFloat(answer);
    return Math.abs(userAnswerNum - this.currentQuestion.correctAnswer) < 0.01;
  }

  submitAnswer(): void {
    if (!this.currentQuestion || this.userAnswer.trim() === '') {
      return;
    }

    const isCorrect = this.checkAnswer(this.userAnswer);

    if (isCorrect) {
      this.streak++;
      if (this.streak > this.bestStreak) {
        this.bestStreak = this.streak;
      }

      // Calculate points with streak bonus
      let points = 10;
      if (this.streak >= 5) points += 5;
      if (this.streak >= 10) points += 10;
      if (this.streak >= 15) points += 15;

      this.recordAnswer(true, points);
      this.feedback = `Correct! +${points} points`;

      if (this.streak >= 5) {
        this.feedback += ` 🔥 ${this.streak} streak!`;
      }
    } else {
      this.streak = 0;
      this.recordAnswer(false, 0);
      this.feedback = `Incorrect. The answer was ${this.currentQuestion.correctAnswer}`;
    }

    this.showFeedback = true;

    // Clear previous timeout
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }

    // Auto-advance to next question after showing feedback
    this.feedbackTimeout = setTimeout(() => {
      this.showFeedback = false;

      if (this.questionsAnswered >= this.totalQuestionsGoal) {
        this.complete();
      } else {
        this.nextQuestion();
      }
    }, 1500);
  }

  handleKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !this.showFeedback) {
      this.submitAnswer();
    }
  }

  setDifficulty(level: DifficultyLevel): void {
    this.difficulty = level;
    this.updateOperators();
    this.generateQuestion();
  }

  getProgressPercentage(): number {
    return (this.questionsAnswered / this.totalQuestionsGoal) * 100;
  }

  protected override onComplete(): void {
    // Clear any pending feedback timeout
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }
  }
}
