import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NavigationComponent } from '../../../shared/navigation/navigation.component';

interface Game {
  id: string;
  title: string;
  description: string;
  subject: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  icon: string;
  color: string;
  estimatedTime: number; // in minutes
  playCount: number;
  averageScore: number;
  tags: string[];
}

@Component({
  selector: 'app-game-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavigationComponent],
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.scss']
})
export class GameListComponent implements OnInit {
  games: Game[] = [
    {
      id: 'speed-math',
      title: 'Speed Math Challenge',
      description: 'Test your mental math skills with quick arithmetic problems. Race against the clock!',
      subject: 'Mathematics',
      difficulty: 'Easy',
      icon: '🔢',
      color: '#3b82f6',
      estimatedTime: 10,
      playCount: 1247,
      averageScore: 85,
      tags: ['arithmetic', 'timed', 'multiplication', 'addition']
    },
    {
      id: 'cell-explorer',
      title: 'Cell Structure Explorer',
      description: 'Learn about cell biology by identifying different parts of plant and animal cells.',
      subject: 'Science',
      difficulty: 'Medium',
      icon: '🔬',
      color: '#10b981',
      estimatedTime: 15,
      playCount: 892,
      averageScore: 78,
      tags: ['biology', 'cells', 'anatomy']
    },
    {
      id: 'vocabulary-builder',
      title: 'Vocabulary Builder',
      description: 'Expand your vocabulary by learning new words, their meanings, synonyms, and antonyms.',
      subject: 'Language Arts',
      difficulty: 'Medium',
      icon: '📚',
      color: '#8b5cf6',
      estimatedTime: 12,
      playCount: 1053,
      averageScore: 82,
      tags: ['vocabulary', 'words', 'definitions', 'synonyms']
    },
    {
      id: 'geography-quiz',
      title: 'Geography Explorer',
      description: 'Test your knowledge of world geography, countries, capitals, and landmarks.',
      subject: 'Social Studies',
      difficulty: 'Hard',
      icon: '🌍',
      color: '#f59e0b',
      estimatedTime: 20,
      playCount: 675,
      averageScore: 72,
      tags: ['geography', 'countries', 'maps', 'capitals']
    },
    {
      id: 'fraction-master',
      title: 'Fraction Master',
      description: 'Master fractions with interactive problems covering addition, subtraction, and comparison.',
      subject: 'Mathematics',
      difficulty: 'Medium',
      icon: '➗',
      color: '#3b82f6',
      estimatedTime: 15,
      playCount: 934,
      averageScore: 76,
      tags: ['fractions', 'math', 'division']
    },
    {
      id: 'spelling-bee',
      title: 'Spelling Bee Champion',
      description: 'Improve your spelling skills with challenging words from various difficulty levels.',
      subject: 'Language Arts',
      difficulty: 'Easy',
      icon: '✍️',
      color: '#8b5cf6',
      estimatedTime: 10,
      playCount: 1156,
      averageScore: 88,
      tags: ['spelling', 'words', 'writing']
    },
    {
      id: 'periodic-table',
      title: 'Periodic Table Quest',
      description: 'Learn chemical elements, their symbols, and properties in this interactive chemistry game.',
      subject: 'Science',
      difficulty: 'Hard',
      icon: '⚗️',
      color: '#10b981',
      estimatedTime: 18,
      playCount: 543,
      averageScore: 68,
      tags: ['chemistry', 'elements', 'periodic table']
    },
    {
      id: 'history-timeline',
      title: 'History Timeline',
      description: 'Place historical events in the correct chronological order and learn about world history.',
      subject: 'Social Studies',
      difficulty: 'Medium',
      icon: '📜',
      color: '#f59e0b',
      estimatedTime: 15,
      playCount: 789,
      averageScore: 74,
      tags: ['history', 'timeline', 'events']
    },
    {
      id: 'grammar-guru',
      title: 'Grammar Guru',
      description: 'Master English grammar rules including parts of speech, tenses, and sentence structure.',
      subject: 'Language Arts',
      difficulty: 'Hard',
      icon: '📖',
      color: '#8b5cf6',
      estimatedTime: 20,
      playCount: 621,
      averageScore: 71,
      tags: ['grammar', 'writing', 'language']
    }
  ];

  filteredGames: Game[] = [];
  subjects: string[] = ['All Subjects', 'Mathematics', 'Science', 'Language Arts', 'Social Studies'];
  difficulties: string[] = ['All Levels', 'Easy', 'Medium', 'Hard'];

  selectedSubject: string = 'All Subjects';
  selectedDifficulty: string = 'All Levels';
  searchQuery: string = '';
  sortBy: string = 'popular'; // popular, name, difficulty

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredGames = this.games.filter(game => {
      // Subject filter
      const subjectMatch = this.selectedSubject === 'All Subjects' ||
                          game.subject === this.selectedSubject;

      // Difficulty filter
      const difficultyMatch = this.selectedDifficulty === 'All Levels' ||
                             game.difficulty === this.selectedDifficulty;

      // Search query filter
      const searchMatch = this.searchQuery === '' ||
                         game.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                         game.description.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                         game.tags.some(tag => tag.toLowerCase().includes(this.searchQuery.toLowerCase()));

      return subjectMatch && difficultyMatch && searchMatch;
    });

    // Apply sorting
    this.sortGames();
  }

  sortGames(): void {
    switch (this.sortBy) {
      case 'popular':
        this.filteredGames.sort((a, b) => b.playCount - a.playCount);
        break;
      case 'name':
        this.filteredGames.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'difficulty':
        const difficultyOrder = { 'Easy': 1, 'Medium': 2, 'Hard': 3 };
        this.filteredGames.sort((a, b) => difficultyOrder[a.difficulty] - difficultyOrder[b.difficulty]);
        break;
    }
  }

  onSubjectChange(): void {
    this.applyFilters();
  }

  onDifficultyChange(): void {
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onSortChange(): void {
    this.sortGames();
  }

  clearFilters(): void {
    this.selectedSubject = 'All Subjects';
    this.selectedDifficulty = 'All Levels';
    this.searchQuery = '';
    this.applyFilters();
  }

  getDifficultyClass(difficulty: string): string {
    return `difficulty-${difficulty.toLowerCase()}`;
  }

  getDifficultyColor(difficulty: string): string {
    switch (difficulty) {
      case 'Easy': return '#10b981';
      case 'Medium': return '#f59e0b';
      case 'Hard': return '#ef4444';
      default: return '#6b7280';
    }
  }

  getGameCountBySubject(subject: string): number {
    return this.games.filter(g => g.subject === subject).length;
  }
}
