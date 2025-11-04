# MongoDB Collection Schemas

## Database: gameacademy

### 1. users Collection

```json
{
  "_id": ObjectId,
  "username": String,
  "email": String,
  "passwordHash": String,
  "role": String, // "STUDENT" or "TEACHER"
  "createdAt": Date,
  "lastLogin": Date
}
```

**Indexes:**
- email (unique)
- username (unique)

---

### 2. classes Collection

```json
{
  "_id": ObjectId,
  "className": String,
  "teacherId": ObjectId,
  "studentIds": [ObjectId],
  "goals": [
    {
      "goalDescription": String,
      "targetPoints": Number,
      "deadline": Date
    }
  ],
  "createdAt": Date
}
```

**Indexes:**
- teacherId

---

### 3. students Collection

```json
{
  "_id": ObjectId,
  "userId": ObjectId,
  "classId": ObjectId,
  "totalPoints": Number,
  "level": Number,
  "achievements": [
    {
      "achievementName": String,
      "earnedAt": Date,
      "points": Number
    }
  ],
  "progressData": {
    "gamesCompleted": Number,
    "totalTimeSpent": Number,
    "averageScore": Number
  }
}
```

**Indexes:**
- userId
- classId
- (classId, totalPoints DESC) - compound index for leaderboards

---

### 4. games Collection

```json
{
  "_id": ObjectId,
  "gameTitle": String,
  "subject": String,
  "gradeLevel": String,
  "description": String,
  "maxPoints": Number,
  "difficultyLevel": String // "EASY", "MEDIUM", "HARD"
}
```

**Indexes:**
- subject
- gradeLevel

---

### 5. gameProgress Collection

```json
{
  "_id": ObjectId,
  "studentId": ObjectId,
  "gameId": ObjectId,
  "score": Number,
  "completionStatus": String, // "IN_PROGRESS", "COMPLETED"
  "attemptsCount": Number,
  "lastPlayed": Date,
  "timeSpent": Number, // in seconds
  "bestScore": Number
}
```

**Indexes:**
- studentId
- (studentId, gameId) - compound index
- lastPlayed

---

### 6. leaderboards Collection

```json
{
  "_id": ObjectId,
  "classId": ObjectId,
  "period": String, // "DAILY", "WEEKLY", "ALL_TIME"
  "rankings": [
    {
      "studentId": ObjectId,
      "studentName": String,
      "points": Number,
      "rank": Number
    }
  ],
  "lastUpdated": Date
}
```

**Indexes:**
- (classId, period) - compound index
- lastUpdated

---

## Notes

- All ObjectId references should maintain referential integrity in the application layer
- Dates are stored in UTC
- Points are positive integers
- TimeSpent is measured in seconds
- Level is calculated based on total points (formula: floor(sqrt(totalPoints/100)))
