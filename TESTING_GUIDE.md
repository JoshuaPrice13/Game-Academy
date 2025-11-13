# Game Academy - Testing Guide

**Last Updated**: January 2025
**Branch**: `claude/game-academy-phase-one-setup-011CUnFcyvmBBxnYZN3YEwNW`

---

## 📋 Prerequisites Checklist

Before starting, verify you have these installed:

### 1. Java 21
```bash
java -version
```
**Expected output**: `openjdk version "21.0.x"` or `java version "21.0.x"`

**If not installed (Windows)**:
- Download from: https://adoptium.net/temurin/releases/?version=21
- Choose: Windows x64 JDK .msi installer
- Run installer and follow prompts
- Add to PATH when prompted

### 2. Node.js 20+ and npm
```bash
node -v
npm -v
```
**Expected output**:
- Node: `v20.x.x` or higher
- npm: `10.x.x` or higher

**If not installed (Windows)**:
- Download from: https://nodejs.org/en/download
- Choose: Windows Installer (.msi) - LTS version
- Run installer and follow prompts

### 3. MongoDB 7.0+
```bash
mongod --version
```
**Expected output**: `db version v7.0.x` or higher

**If not installed (Windows)**:
- Download from: https://www.mongodb.com/try/download/community
- Choose: Windows x64, MSI package
- Run installer
- Choose "Complete" installation
- Install MongoDB as a Windows Service (recommended)

### 4. Angular CLI 20.x
```bash
ng version
```
**Expected output**: `Angular CLI: 20.x.x`

**If not installed**:
```bash
npm install -g @angular/cli@20
```

---

## 🚀 Step-by-Step Testing Process

### Step 1: Verify Prerequisites

Let's check all prerequisites are properly installed:

```bash
# Check Java
java -version

# Check Node.js
node -v

# Check npm
npm -v

# Check MongoDB
mongod --version

# Check Angular CLI
ng version
```

**✅ Expected**: All commands should return version numbers without errors.

---

### Step 2: Start MongoDB

#### Option A: MongoDB as Windows Service (Recommended)
If you installed MongoDB as a service, it should already be running.

**Verify it's running**:
```bash
# Check if MongoDB service is running
sc query MongoDB
```

**If not running, start it**:
```bash
net start MongoDB
```

#### Option B: Manual MongoDB Start
If not installed as a service:

```bash
# Navigate to MongoDB bin directory (adjust path if needed)
cd "C:\Program Files\MongoDB\Server\7.0\bin"

# Start MongoDB
mongod --dbpath="C:\data\db"
```

**Note**: Keep this terminal window open while testing.

**✅ Expected**: You should see log messages ending with "Waiting for connections" on port 27017.

---

### Step 3: Install Backend Dependencies

Open a new terminal and navigate to the backend directory:

```bash
cd /home/user/Game-Academy/game-academy-backend
```

**Check if dependencies need to be downloaded**:
The first build will download dependencies automatically. This happens when you run the backend.

---

### Step 4: Start Backend Application

From the backend directory:

```bash
# On Windows, use the Maven wrapper
mvnw.cmd spring-boot:run

# Or if you have Maven installed globally
mvn spring-boot:run
```

**What to watch for**:
1. Dependencies downloading (first time only - may take 5-10 minutes)
2. Spring Boot banner
3. "Started GameAcademyApplication in X seconds"
4. "Tomcat started on port(s): 8080"

**✅ Expected output**:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

...
2025-01-XX XX:XX:XX.XXX  INFO XXXXX --- [           main] c.g.GameAcademyApplication               : Started GameAcademyApplication in 8.543 seconds
```

**Common Issues**:
- **Port 8080 already in use**: Another app is using port 8080. Stop it or change backend port in `application.properties`
- **Cannot connect to MongoDB**: Make sure MongoDB is running (Step 2)

**Keep this terminal open!**

---

### Step 5: Verify Backend is Running

Open a new terminal or browser and test the backend:

```bash
# Test backend health (if you have health endpoint)
curl http://localhost:8080/actuator/health

# Or just visit in browser
# http://localhost:8080
```

**✅ Expected**: Should not get "Connection refused" error. May get 404 or a response.

---

### Step 6: Install Frontend Dependencies

Open a **new terminal** window and navigate to frontend:

```bash
cd /home/user/Game-Academy/game-academy-frontend

# Install dependencies (first time only)
npm install
```

**What to watch for**:
- Package installation progress
- May take 2-5 minutes
- Should complete without errors

**✅ Expected output**:
```
added XXX packages in XXs
```

---

### Step 7: Start Frontend Application

From the frontend directory:

```bash
# Start Angular dev server
ng serve
```

**Alternative** (if ng serve has issues):
```bash
npm start
```

**What to watch for**:
1. Angular CLI compiling the application
2. Webpack bundling
3. "Compiled successfully"
4. "Angular Live Development Server is listening on localhost:4200"

**✅ Expected output**:
```
Initial chunk files | Names         |  Raw size
polyfills.js        | polyfills     |  XX.XX kB |
main.js             | main          | XXX.XX kB |
styles.css          | styles        |  XX.XX kB |

                    | Initial total | XXX.XX kB

Application bundle generation complete. [X.XXX seconds]
Watch mode enabled. Watching for file changes...
Re-optimizing dependencies because lockfile has changed
  ➜  Local:   http://localhost:4200/
```

**Common Issues**:
- **Port 4200 already in use**: Add `--port 4201` to use a different port
- **Module not found errors**: Run `npm install` again
- **Angular CLI not found**: Install with `npm install -g @angular/cli@20`

**Keep this terminal open!**

---

## 🧪 Testing the Application

Now you should have:
- ✅ MongoDB running (Terminal 1)
- ✅ Backend running on port 8080 (Terminal 2)
- ✅ Frontend running on port 4200 (Terminal 3)

### Test 1: Open the Application

Open your browser and navigate to:
```
http://localhost:4200
```

**✅ Expected**: You should see the **Login page** with:
- Game Academy logo (🎓)
- Email and password fields
- "Remember me" checkbox
- "Sign In" button
- Link to register page
- Demo credentials section

---

### Test 2: Registration Flow

1. **Click "Create one"** (or "Register" link)

**✅ Expected**: Should navigate to `/register` and show:
- Role selection cards (Student/Teacher)
- Username field
- Email field
- Password fields (password + confirm)
- Terms agreement checkbox
- "Create Account" button

2. **Fill out the form**:
   - Select role: **Student**
   - Username: `teststudent`
   - Email: `test@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
   - Check "I agree to Terms"

3. **Click "Create Account"**

**✅ Expected behavior**:
- **If backend is running**: Should create account and redirect to student dashboard
- **If backend is NOT running**: Will show error "Failed to connect to backend"

---

### Test 3: Login Flow

1. **Navigate back to Login** (`http://localhost:4200/login`)

2. **Fill in credentials**:
   - Email: `test@example.com` (or use demo credentials from the page)
   - Password: `password123`

3. **Click "Sign In"**

**✅ Expected behavior**:
- **Success**: Should redirect to `/student/dashboard`
- **Invalid credentials**: Should show error message
- **Backend not running**: Should show connection error

---

### Test 4: Navigation System

Once logged in, verify the navigation:

**Top Navbar**:
- ✅ Game Academy logo on the left
- ✅ Hamburger menu button (☰)
- ✅ Notification bell (🔔) with badge showing "3"
- ✅ User menu on the right with:
  - Avatar with first letter of username
  - Username display
  - "Student" role badge

**Sidebar** (click hamburger menu if collapsed):
- ✅ Dashboard (🏠)
- ✅ Play Games (🎮)
- ✅ Leaderboard (🏆)
- ✅ Achievements (⭐)
- ✅ Profile (👤)
- ✅ Collapse toggle button at bottom

**User Dropdown** (click user menu):
- ✅ User info header
- ✅ My Profile link
- ✅ Settings link
- ✅ Help & Support link
- ✅ Logout button

---

### Test 5: Student Dashboard

You should be on `/student/dashboard`. Verify:

**Welcome Section**:
- ✅ Personalized greeting ("Good Morning/Afternoon/Evening, [username]")
- ✅ Level badge showing "Level 12" (mock data)

**Stats Cards** (4 cards in a row):
1. ✅ **Total Points**: Shows 2450 with progress bar
2. ✅ **Class Rank**: Shows #5 / 45
3. ✅ **Games Played**: Shows 38 with 87% accuracy
4. ✅ **Daily Streak**: Shows 7 days with 🔥

**Quick Play Section**:
- ✅ Title "Quick Play" with "View All Games →" link
- ✅ 3 game cards (Speed Math, Cell Explorer, Vocabulary Builder)
- ✅ Each card shows play count and best score

**Recent Activity**:
- ✅ 4 activity items with icons, descriptions, and time
- ✅ Points earned shown on right (+100, +50, etc.)

**Action Cards** (3 cards at bottom):
- ✅ Leaderboard card (🏆)
- ✅ Achievements card (⭐) showing "15 badges earned"
- ✅ My Profile card (👤)

**Click "View All Games →"** to proceed to next test.

---

### Test 6: Game List Page

Should navigate to `/student/games`. Verify:

**Page Header**:
- ✅ Title "🎮 Educational Games"
- ✅ Badge showing "9 Games Available"

**Search and Filters**:
- ✅ Search box with 🔍 icon
- ✅ Subject dropdown (All Subjects, Mathematics, Science, etc.)
- ✅ Difficulty dropdown (All Levels, Easy, Medium, Hard)
- ✅ Sort By dropdown (Most Popular, Name, Difficulty)
- ✅ "Clear Filters" button

**Test Filtering**:
1. **Select "Mathematics"** from Subject dropdown
   - ✅ Should show only 2 games (Speed Math, Fraction Master)

2. **Select "Medium"** from Difficulty
   - ✅ Should show only 1 game (Fraction Master)

3. **Type "vocab"** in search box
   - ✅ Should show Vocabulary Builder game

4. **Click "Clear Filters"**
   - ✅ Should show all 9 games again

**Game Cards**:
Each game card should show:
- ✅ Large icon (🔢, 🔬, 📚, etc.)
- ✅ Difficulty badge (Easy/Medium/Hard) with color
- ✅ Game title and subject
- ✅ Description
- ✅ Tags (up to 3)
- ✅ Stats row (play count, time, average score)
- ✅ "▶️ Play Now" button

**Click on a "Play Now" button** - it will navigate to `/student/games/[game-id]` (this route doesn't have a component yet, so you'll see a blank page - that's expected).

**Browse by Subject** (at bottom):
- ✅ 4 category cards showing subject icons and game counts

---

### Test 7: Leaderboard Page

Click **Leaderboard** from the sidebar. Should navigate to `/student/leaderboard`. Verify:

**Page Header**:
- ✅ Title "🏆 Leaderboard"
- ✅ Subtitle about competing with classmates

**Period Selector**:
- ✅ 3 buttons: Today (📅), This Week (📊), All Time (⭐)
- ✅ "All Time" is active by default (highlighted in blue)

**Top 3 Podium** (if viewing all-time):
- ✅ 1st place in center (larger card with crown 👑)
  - Shows MathWizard2024, 8450 points
- ✅ 2nd place on left
  - Shows ScienceGuru, 7920 points
- ✅ 3rd place on right
  - Shows BookLover123, 7350 points
- ✅ Each shows games played and accuracy

**Full Rankings Table**:
- ✅ Header: "Full Rankings" with "📍 Find Me" button
- ✅ Table with 8 columns (Rank, Player, Points, Games, Accuracy, Level, Streak, Trend)
- ✅ 10 players listed
- ✅ Rank 5 (You) is highlighted in blue
- ✅ Rank change indicators (📈/📉)
- ✅ Accuracy shown as progress bar

**Test Period Switching**:
1. **Click "This Week"**
   - ✅ Should load weekly data (You should be #2)
   - ✅ Podium updates
   - ✅ Table updates

2. **Click "Today"**
   - ✅ Should load daily data (You should be #1! 🎉)
   - ✅ Podium shows you in center with crown

3. **Click "Find Me" button**
   - ✅ Should scroll to your row (highlighted in blue)

---

### Test 8: Achievements Page

Click **Achievements** from the sidebar. Should navigate to `/student/achievements`. Verify:

**Page Header**:
- ✅ Title "⭐ Achievements"
- ✅ Subtitle about tracking progress

**Stats Overview** (3 cards at top):
- ✅ Achievements Unlocked: 5 / 15 (🏆)
- ✅ Points Earned: 450 (💎)
- ✅ Completion Rate: 33% (📊)

**Filters Section**:
- ✅ Category filter buttons (All, 🎮 Games, 💰 Points, 🔥 Streak, 🎯 Accuracy, ⚡ Speed, 📖 Learning)
- ✅ Status filter buttons (All, ✅ Unlocked, 🔒 Locked)
- ✅ "All" categories and "All" status selected by default

**Achievements Grid**:
- ✅ Shows 15 achievement cards in grid
- ✅ Cards have tier badges (bronze/silver/gold/platinum) in top right

**Unlocked Achievements** (5 unlocked):
1. ✅ First Steps (bronze, 🎮) - Full color
2. ✅ Point Collector (bronze, 💯) - Full color
3. ✅ Consistency King (silver, 🔥) - Full color
4. ✅ Perfectionist (silver, 🎯) - Full color
5. ✅ Speed Demon (silver, ⚡) - Full color

Each unlocked achievement shows:
- ✅ Colored icon
- ✅ Title and description
- ✅ Requirement badge
- ✅ "Unlocked [date]" in green
- ✅ Points reward (💎 +XX)

**Locked Achievements** (10 locked):
Each locked achievement shows:
- ✅ Grayscale icon with lock overlay (🔒)
- ✅ Slightly faded appearance
- ✅ Title and description
- ✅ Requirement badge
- ✅ Progress bar (if applicable)
  - Example: Gaming Enthusiast shows 7/10 progress
- ✅ Points reward

**Test Filtering**:
1. **Click "🔥 Streak"** category
   - ✅ Should show only 2 achievements (Consistency King, Dedication Master)

2. **Click "✅ Unlocked"** status
   - ✅ Should show only 1 achievement (Consistency King - unlocked)

3. **Click "🔒 Locked"** status
   - ✅ Should show only 1 achievement (Dedication Master - locked)

4. **Click "All"** category and "All"** status
   - ✅ Should show all 15 achievements again

---

### Test 9: Mobile Responsiveness

Let's test the mobile view:

1. **Open browser DevTools**:
   - Press `F12` or `Ctrl+Shift+I`
   - Click the device toolbar icon (or press `Ctrl+Shift+M`)

2. **Select a mobile device** (e.g., iPhone 12 Pro)

**✅ Expected**:
- Sidebar should be hidden by default
- Hamburger menu works to show/hide sidebar
- Sidebar overlays content when open
- Clicking overlay closes sidebar
- All components should be readable and functional
- Stats cards stack vertically
- Tables remain scrollable

---

### Test 10: Logout Flow

1. **Click on your user menu** (top right)
2. **Click "Logout"** button

**✅ Expected**:
- Should navigate back to `/login`
- Sidebar should disappear
- Should not be able to navigate to `/student/dashboard` directly
- If you try to visit `/student/dashboard`, should redirect to login

---

### Test 11: Teacher View (If Backend Supports)

1. **Login as teacher** (if you created a teacher account) or **Register as teacher**
2. **After login**, should redirect to `/teacher/dashboard`

**✅ Expected**:
- Sidebar shows different links (Dashboard, My Classes, Reports)
- Navigation works for teacher routes
- **Note**: Teacher dashboard component doesn't exist yet, so you'll see a blank page

---

## 🐛 Common Issues & Solutions

### Issue 1: "Cannot GET /" or White Screen
**Cause**: Frontend not running or wrong URL
**Solution**:
- Make sure `ng serve` is running
- Check you're visiting `http://localhost:4200` (not 8080)

### Issue 2: "Failed to connect" on Login
**Cause**: Backend not running or MongoDB not running
**Solution**:
- Check backend terminal for errors
- Verify MongoDB is running: `sc query MongoDB`
- Check backend is on port 8080: `curl http://localhost:8080`

### Issue 3: Components Show Empty/Blank
**Cause**: Route exists but component not loaded
**Solution**: This is expected for routes we haven't implemented yet:
- `/student/games/:id` (game detail)
- `/student/profile` (profile page)
- `/teacher/dashboard` (teacher dashboard)

### Issue 4: "Module not found" Errors
**Cause**: Missing dependencies
**Solution**:
```bash
cd game-academy-frontend
npm install
```

### Issue 5: Styling Looks Broken
**Cause**: CSS not loading
**Solution**:
- Hard refresh browser: `Ctrl+F5`
- Clear browser cache
- Restart `ng serve`

### Issue 6: Changes Not Reflecting
**Cause**: Browser cache or Angular not recompiling
**Solution**:
- Check terminal for compilation errors
- Hard refresh: `Ctrl+F5`
- Restart `ng serve` if needed

---

## ✅ Testing Checklist

Use this checklist to verify everything works:

### Prerequisites
- [ ] Java 21 installed and working
- [ ] Node.js 20+ installed and working
- [ ] MongoDB 7.0+ installed and working
- [ ] Angular CLI 20.x installed

### Services Running
- [ ] MongoDB service running
- [ ] Backend running on port 8080
- [ ] Frontend running on port 4200

### Authentication
- [ ] Can view login page
- [ ] Can view register page
- [ ] Can create student account (if backend ready)
- [ ] Can login with credentials (if backend ready)
- [ ] Invalid login shows error
- [ ] Can logout

### Navigation
- [ ] Top navbar displays correctly
- [ ] Sidebar shows/hides with hamburger
- [ ] User menu dropdown works
- [ ] All navigation links work
- [ ] Logout button works
- [ ] Role-based menu shows correct items

### Student Dashboard
- [ ] Welcome section displays
- [ ] All 4 stat cards display
- [ ] Quick play games show
- [ ] Recent activity shows
- [ ] Action cards work
- [ ] "View All Games" link works

### Game List
- [ ] All 9 games display
- [ ] Search works
- [ ] Subject filter works
- [ ] Difficulty filter works
- [ ] Sort by works
- [ ] Clear filters works
- [ ] Category quick filters work
- [ ] Game cards look good

### Leaderboard
- [ ] Period selector works
- [ ] Top 3 podium displays
- [ ] Full rankings table displays
- [ ] "Find Me" button works
- [ ] Rank changes show
- [ ] Data updates when switching periods

### Achievements
- [ ] Stats overview shows
- [ ] All 15 achievements display
- [ ] Unlocked achievements show in color
- [ ] Locked achievements show greyed out
- [ ] Progress bars show on locked items
- [ ] Category filter works
- [ ] Status filter works
- [ ] Points rewards display

### Mobile Responsiveness
- [ ] Works on mobile viewport
- [ ] Sidebar overlays correctly
- [ ] Components stack properly
- [ ] Everything readable and clickable

---

## 🎯 What to Test Next

After verifying the UI works:

1. **Backend Integration** - When backend is fully ready:
   - Test real registration
   - Test real login
   - Test real game data loading
   - Test real leaderboard updates
   - Test WebSocket real-time features

2. **Game Playing** - When game components are hooked up:
   - Click "Play Now" should launch game
   - Games should save scores
   - Leaderboard should update after playing

3. **Teacher Features** - When teacher components are built:
   - Test teacher login
   - Test class management
   - Test student roster

---

## 📝 Test Results Template

Document your test results:

```
Date: ___________
Tester: ___________
Browser: ___________

Prerequisites: ✅ / ❌
Backend Running: ✅ / ❌
Frontend Running: ✅ / ❌

Login Page: ✅ / ❌
Register Page: ✅ / ❌
Navigation: ✅ / ❌
Dashboard: ✅ / ❌
Game List: ✅ / ❌
Leaderboard: ✅ / ❌
Achievements: ✅ / ❌

Issues Found:
1.
2.
3.

Notes:

```

---

## 🚀 Quick Start Commands

**Terminal 1 - MongoDB**:
```bash
net start MongoDB
```

**Terminal 2 - Backend**:
```bash
cd game-academy-backend
mvnw.cmd spring-boot:run
```

**Terminal 3 - Frontend**:
```bash
cd game-academy-frontend
npm install  # First time only
ng serve
```

**Browser**:
```
http://localhost:4200
```

---

**Happy Testing! 🎉**

If you encounter any issues, check the "Common Issues & Solutions" section above or refer to the error messages in the terminal windows.
