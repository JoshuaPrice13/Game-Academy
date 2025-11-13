import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseGameComponent } from '../../../shared/base-game.component';
import { GameInfo, DifficultyLevel, QuestionType } from '../../../models/game.interface';

interface VocabularyWord {
  word: string;
  definition: string;
  synonyms: string[];
  antonyms: string[];
  exampleSentence: string;
  partOfSpeech: string;
  difficulty: DifficultyLevel;
}

interface Question {
  type: 'definition' | 'synonym' | 'antonym' | 'sentence';
  word: VocabularyWord;
  question: string;
  options: string[];
  correctAnswer: string;
}

@Component({
  selector: 'app-vocabulary-builder',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vocabulary-builder.component.html',
  styleUrls: ['./vocabulary-builder.component.scss']
})
export class VocabularyBuilderComponent extends BaseGameComponent {
  vocabularyWords: VocabularyWord[] = [
    {
      word: 'Benevolent',
      definition: 'Well-meaning and kindly',
      synonyms: ['kind', 'generous', 'charitable', 'compassionate'],
      antonyms: ['malevolent', 'cruel', 'unkind', 'mean'],
      exampleSentence: 'The benevolent teacher always helped students after class.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.MEDIUM
    },
    {
      word: 'Diligent',
      definition: 'Showing care and effort in one\'s work',
      synonyms: ['hardworking', 'industrious', 'conscientious', 'dedicated'],
      antonyms: ['lazy', 'careless', 'negligent', 'idle'],
      exampleSentence: 'She was a diligent student who never missed homework.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.EASY
    },
    {
      word: 'Eloquent',
      definition: 'Fluent and persuasive in speaking or writing',
      synonyms: ['articulate', 'expressive', 'fluent', 'persuasive'],
      antonyms: ['inarticulate', 'unclear', 'incoherent', 'clumsy'],
      exampleSentence: 'The eloquent speaker captivated the entire audience.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.HARD
    },
    {
      word: 'Resilient',
      definition: 'Able to recover quickly from difficulties',
      synonyms: ['strong', 'tough', 'adaptable', 'flexible'],
      antonyms: ['weak', 'fragile', 'brittle', 'delicate'],
      exampleSentence: 'Children are remarkably resilient and bounce back from setbacks.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.MEDIUM
    },
    {
      word: 'Ambitious',
      definition: 'Having strong desire to achieve something',
      synonyms: ['driven', 'determined', 'aspiring', 'motivated'],
      antonyms: ['unmotivated', 'apathetic', 'lazy', 'indifferent'],
      exampleSentence: 'Her ambitious goals pushed her to work harder every day.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.EASY
    },
    {
      word: 'Persevere',
      definition: 'To continue despite difficulties',
      synonyms: ['persist', 'endure', 'continue', 'persist'],
      antonyms: ['quit', 'surrender', 'abandon', 'give up'],
      exampleSentence: 'You must persevere through challenges to reach your goals.',
      partOfSpeech: 'verb',
      difficulty: DifficultyLevel.MEDIUM
    },
    {
      word: 'Meticulous',
      definition: 'Showing great attention to detail',
      synonyms: ['careful', 'thorough', 'precise', 'detailed'],
      antonyms: ['careless', 'sloppy', 'hasty', 'negligent'],
      exampleSentence: 'The meticulous artist spent hours perfecting each brushstroke.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.HARD
    },
    {
      word: 'Innovative',
      definition: 'Introducing new ideas or methods',
      synonyms: ['creative', 'original', 'inventive', 'novel'],
      antonyms: ['conventional', 'traditional', 'ordinary', 'typical'],
      exampleSentence: 'The innovative design won first prize at the competition.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.MEDIUM
    },
    {
      word: 'Collaborate',
      definition: 'To work jointly with others',
      synonyms: ['cooperate', 'work together', 'partner', 'team up'],
      antonyms: ['compete', 'oppose', 'work alone', 'conflict'],
      exampleSentence: 'Scientists from different countries collaborate on research projects.',
      partOfSpeech: 'verb',
      difficulty: DifficultyLevel.EASY
    },
    {
      word: 'Analytical',
      definition: 'Using logical reasoning to examine something',
      synonyms: ['logical', 'systematic', 'methodical', 'rational'],
      antonyms: ['illogical', 'unsystematic', 'random', 'emotional'],
      exampleSentence: 'Her analytical mind helped solve the complex math problem.',
      partOfSpeech: 'adjective',
      difficulty: DifficultyLevel.MEDIUM
    }
  ];

  currentQuestion: Question | null = null;
  selectedAnswer: string = '';
  showFeedback: boolean = false;
  feedback: string = '';
  correctFeedback: boolean = false;
  totalQuestionsGoal: number = 15;
  wordsLearned: Set<string> = new Set();
  currentWordDisplay: VocabularyWord | null = null;

  // Make String accessible in template
  String = String;

  private feedbackTimeout: any = null;
  private usedWords: Set<string> = new Set();

  getGameInfo(): GameInfo {
    return {
      title: 'Vocabulary Builder',
      description: 'Expand your vocabulary by learning new words and their meanings!',
      subject: 'Language Arts',
      instructions: 'Answer questions about word definitions, synonyms, antonyms, and usage to master new vocabulary!',
      totalQuestions: this.totalQuestionsGoal
    };
  }

  protected override onStart(): void {
    this.generateQuestion();
  }

  generateQuestion(): void {
    // Select a word (prefer words not yet used)
    const availableWords = this.vocabularyWords.filter(w => !this.usedWords.has(w.word));
    const wordPool = availableWords.length > 0 ? availableWords : this.vocabularyWords;
    const word = wordPool[Math.floor(Math.random() * wordPool.length)];

    this.usedWords.add(word.word);
    if (this.usedWords.size >= this.vocabularyWords.length) {
      this.usedWords.clear(); // Reset if all words used
    }

    this.currentWordDisplay = word;

    // Randomly choose question type
    const questionTypes: Array<'definition' | 'synonym' | 'antonym' | 'sentence'> =
      ['definition', 'synonym', 'antonym', 'sentence'];
    const questionType = questionTypes[Math.floor(Math.random() * questionTypes.length)];

    let question: string;
    let options: string[];
    let correctAnswer: string;

    switch (questionType) {
      case 'definition':
        question = `What is the definition of "${word.word}"?`;
        correctAnswer = word.definition;
        options = this.getDefinitionOptions(word);
        break;

      case 'synonym':
        const synonym = word.synonyms[Math.floor(Math.random() * word.synonyms.length)];
        question = `Which word is a synonym (similar meaning) of "${word.word}"?`;
        correctAnswer = synonym;
        options = this.getSynonymOptions(word, synonym);
        break;

      case 'antonym':
        const antonym = word.antonyms[Math.floor(Math.random() * word.antonyms.length)];
        question = `Which word is an antonym (opposite meaning) of "${word.word}"?`;
        correctAnswer = antonym;
        options = this.getAntonymOptions(word, antonym);
        break;

      case 'sentence':
        question = `Which sentence correctly uses the word "${word.word}"?`;
        correctAnswer = word.exampleSentence;
        options = this.getSentenceOptions(word);
        break;

      default:
        question = '';
        correctAnswer = '';
        options = [];
    }

    this.currentQuestion = {
      type: questionType,
      word,
      question,
      options,
      correctAnswer
    };

    this.selectedAnswer = '';
    this.showFeedback = false;

    // Update game state
    this.updateGameState({
      currentQuestion: this.currentQuestion,
      wordsLearned: Array.from(this.wordsLearned)
    });
  }

  private getDefinitionOptions(word: VocabularyWord): string[] {
    const otherDefinitions = this.vocabularyWords
      .filter(w => w.word !== word.word)
      .map(w => w.definition);

    return this.shuffleArray([
      word.definition,
      ...this.getRandomElements(otherDefinitions, 3)
    ]);
  }

  private getSynonymOptions(word: VocabularyWord, correctSynonym: string): string[] {
    const otherWords: string[] = [];

    // Add some antonyms as distractors
    otherWords.push(...word.antonyms.slice(0, 2));

    // Add synonyms from other words
    this.vocabularyWords
      .filter(w => w.word !== word.word)
      .forEach(w => otherWords.push(...w.synonyms.slice(0, 1)));

    return this.shuffleArray([
      correctSynonym,
      ...this.getRandomElements(otherWords, 3)
    ]);
  }

  private getAntonymOptions(word: VocabularyWord, correctAntonym: string): string[] {
    const otherWords: string[] = [];

    // Add some synonyms as distractors
    otherWords.push(...word.synonyms.slice(0, 2));

    // Add antonyms from other words
    this.vocabularyWords
      .filter(w => w.word !== word.word)
      .forEach(w => otherWords.push(...w.antonyms.slice(0, 1)));

    return this.shuffleArray([
      correctAntonym,
      ...this.getRandomElements(otherWords, 3)
    ]);
  }

  private getSentenceOptions(word: VocabularyWord): string[] {
    const incorrectSentences = [
      `The ${word.word.toLowerCase()} was very confusing to everyone.`,
      `I don't understand what ${word.word.toLowerCase()} means at all.`,
      `The ${word.word.toLowerCase()} happened yesterday afternoon.`
    ];

    return this.shuffleArray([
      word.exampleSentence,
      ...incorrectSentences
    ]);
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
      this.feedback = 'Excellent! You got it right!';
      this.correctFeedback = true;
      this.wordsLearned.add(this.currentQuestion.word.word);
    } else {
      this.recordAnswer(false, 0);
      this.feedback = `Not quite. The correct answer is: "${this.currentQuestion.correctAnswer}"`;
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

      if (this.questionsAnswered >= this.totalQuestionsGoal) {
        this.complete();
      } else {
        this.nextQuestion();
      }
    }, 3000);
  }

  checkAnswer(answer: any): boolean {
    if (!this.currentQuestion) return false;
    return answer === this.currentQuestion.correctAnswer;
  }

  getProgressPercentage(): number {
    return (this.questionsAnswered / this.totalQuestionsGoal) * 100;
  }

  getWordsLearnedPercentage(): number {
    return (this.wordsLearned.size / this.vocabularyWords.length) * 100;
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
