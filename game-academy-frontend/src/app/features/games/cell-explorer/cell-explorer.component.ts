import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseGameComponent } from '../../../shared/base-game.component';
import { GameInfo, DifficultyLevel } from '../../../models/game.interface';

interface CellPart {
  id: string;
  name: string;
  description: string;
  function: string;
  x: number; // Position percentage (0-100)
  y: number; // Position percentage (0-100)
  found: boolean;
}

interface Question {
  cellPart: CellPart;
  type: 'identify' | 'function';
  question: string;
  options: string[];
  correctAnswer: string;
}

@Component({
  selector: 'app-cell-explorer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cell-explorer.component.html',
  styleUrls: ['./cell-explorer.component.scss']
})
export class CellExplorerComponent extends BaseGameComponent {
  cellParts: CellPart[] = [
    {
      id: 'nucleus',
      name: 'Nucleus',
      description: 'The control center of the cell',
      function: 'Contains DNA and controls cell activities',
      x: 50,
      y: 45,
      found: false
    },
    {
      id: 'mitochondria',
      name: 'Mitochondria',
      description: 'The powerhouse of the cell',
      function: 'Produces energy (ATP) through cellular respiration',
      x: 35,
      y: 60,
      found: false
    },
    {
      id: 'cell_membrane',
      name: 'Cell Membrane',
      description: 'The outer boundary of the cell',
      function: 'Controls what enters and exits the cell',
      x: 85,
      y: 50,
      found: false
    },
    {
      id: 'cytoplasm',
      name: 'Cytoplasm',
      description: 'The jelly-like substance filling the cell',
      function: 'Holds organelles in place and facilitates cellular processes',
      x: 60,
      y: 70,
      found: false
    },
    {
      id: 'ribosome',
      name: 'Ribosome',
      description: 'Tiny structures that build proteins',
      function: 'Synthesizes proteins from amino acids',
      x: 40,
      y: 35,
      found: false
    },
    {
      id: 'endoplasmic_reticulum',
      name: 'Endoplasmic Reticulum',
      description: 'A network of membranes',
      function: 'Transports materials and synthesizes proteins and lipids',
      x: 65,
      y: 40,
      found: false
    },
    {
      id: 'golgi_apparatus',
      name: 'Golgi Apparatus',
      description: 'The packaging center',
      function: 'Modifies, sorts, and packages proteins for transport',
      x: 70,
      y: 60,
      found: false
    },
    {
      id: 'vacuole',
      name: 'Vacuole',
      description: 'Storage compartment',
      function: 'Stores water, nutrients, and waste products',
      x: 25,
      y: 50,
      found: false
    }
  ];

  currentQuestion: Question | null = null;
  selectedAnswer: string = '';
  showFeedback: boolean = false;
  feedback: string = '';
  correctFeedback: boolean = false;
  totalQuestionsGoal: number = 15;
  foundPartsCount: number = 0;
  highlightedPart: string | null = null;

  private feedbackTimeout: any = null;

  getGameInfo(): GameInfo {
    return {
      title: 'Cell Structure Explorer',
      description: 'Learn about cell parts by identifying them and their functions!',
      subject: 'Science',
      instructions: 'Answer questions about different parts of a cell. Each correct answer reveals more about cell biology!',
      totalQuestions: this.totalQuestionsGoal
    };
  }

  protected override onStart(): void {
    this.generateQuestion();
  }

  generateQuestion(): void {
    // Randomly choose between identify and function question types
    const questionType = Math.random() < 0.5 ? 'identify' : 'function';

    // Select a random cell part
    const cellPart = this.cellParts[Math.floor(Math.random() * this.cellParts.length)];

    let question: string;
    let options: string[];
    let correctAnswer: string;

    if (questionType === 'identify') {
      question = `What is the name of the cell part that: ${cellPart.function}`;
      correctAnswer = cellPart.name;

      // Create options with 3 incorrect answers
      options = this.getRandomOptions(cellPart.name, 4);
    } else {
      question = `What is the function of the ${cellPart.name}?`;
      correctAnswer = cellPart.function;

      // Get functions from other cell parts as incorrect options
      const otherFunctions = this.cellParts
        .filter(p => p.id !== cellPart.id)
        .map(p => p.function);

      options = this.shuffleArray([
        correctAnswer,
        ...this.getRandomElements(otherFunctions, 3)
      ]);
    }

    this.currentQuestion = {
      cellPart,
      type: questionType,
      question,
      options,
      correctAnswer
    };

    this.selectedAnswer = '';
    this.showFeedback = false;
    this.highlightedPart = null;

    // Update game state
    this.updateGameState({
      currentQuestion: this.currentQuestion,
      foundPartsCount: this.foundPartsCount
    });
  }

  private getRandomOptions(correctAnswer: string, count: number): string[] {
    const allNames = this.cellParts.map(p => p.name);
    const options = [correctAnswer];

    const otherNames = allNames.filter(name => name !== correctAnswer);
    const shuffled = this.shuffleArray(otherNames);

    for (let i = 0; i < count - 1 && i < shuffled.length; i++) {
      options.push(shuffled[i]);
    }

    return this.shuffleArray(options);
  }

  private getRandomElements<T>(array: T[], count: number): T[] {
    const shuffled = this.shuffleArray([...array]);
    return shuffled.slice(0, Math.min(count, shuffled.length));
  }

  private shuffleArray<T>(array: T[]): T[] {
    const result = [...array];
    for (let i = result.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [result[i], result[j]] = [result[j], result[i]];
    }
    return result;
  }

  selectAnswer(answer: string): void {
    if (this.showFeedback) return;
    this.selectedAnswer = answer;
  }

  submitAnswer(): void {
    if (!this.currentQuestion || !this.selectedAnswer) {
      return;
    }

    const isCorrect = this.checkAnswer(this.selectedAnswer);

    if (isCorrect) {
      this.recordAnswer(true, 10);
      this.feedback = 'Correct! Well done!';
      this.correctFeedback = true;

      // Mark cell part as found
      const part = this.cellParts.find(p => p.id === this.currentQuestion!.cellPart.id);
      if (part && !part.found) {
        part.found = true;
        this.foundPartsCount++;
      }

      this.highlightedPart = this.currentQuestion.cellPart.id;
    } else {
      this.recordAnswer(false, 0);
      this.feedback = `Incorrect. The correct answer is: ${this.currentQuestion.correctAnswer}`;
      this.correctFeedback = false;
    }

    this.showFeedback = true;

    // Clear previous timeout
    if (this.feedbackTimeout) {
      clearTimeout(this.feedbackTimeout);
    }

    // Auto-advance to next question
    this.feedbackTimeout = setTimeout(() => {
      this.showFeedback = false;
      this.highlightedPart = null;

      if (this.questionsAnswered >= this.totalQuestionsGoal) {
        this.complete();
      } else {
        this.nextQuestion();
      }
    }, 2500);
  }

  checkAnswer(answer: any): boolean {
    if (!this.currentQuestion) return false;
    return answer === this.currentQuestion.correctAnswer;
  }

  getCellPartStyle(part: CellPart): any {
    return {
      left: `${part.x}%`,
      top: `${part.y}%`,
      opacity: part.found ? 1 : 0.3
    };
  }

  isCellPartHighlighted(partId: string): boolean {
    return this.highlightedPart === partId;
  }

  getProgressPercentage(): number {
    return (this.questionsAnswered / this.totalQuestionsGoal) * 100;
  }

  getFoundPercentage(): number {
    return (this.foundPartsCount / this.cellParts.length) * 100;
  }

  protected override onComplete(): void {
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
