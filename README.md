# MASTER DEVELOPMENT PROMPT — CORECHIPERASSISTANT

## 1. PROJECT IDENTITY

Build a complete, production-quality native Android application named **CoreChiperAssistant**.

Developer / Brand: CoreChiperLabs

Application Name: CoreChiperAssistant

Package Name: com.corechiper.assistant

Tagline: One App. Your Entire Life.

Application Type: All-in-One Personal Life Management System.

Primary Platform: Android.

Development Language: Kotlin.

UI Framework: Jetpack Compose with Material 3.

Build System: Gradle Kotlin DSL.

Distribution: Android APK through GitHub Actions.

Minimum Android Version: Android 8.0 (API 26).

Default Language: Indonesian.

Additional Language: English.

The application must be a real, functional application, not a visual prototype.

## 2. MAIN OBJECTIVE

Create an elegant, reliable, secure, and easy-to-use Android application that combines six essential aspects of everyday life:

1. Health Management.
2. Finance Management.
3. Productivity Management.
4. Document Management.
5. Learning Management.
6. Emergency Assistant.

All modules must work together through one unified dashboard.

The application must prioritize:
- Practical functionality.
- Excellent user experience.
- Offline-first architecture.
- Data persistence.
- Privacy and security.
- Performance.
- Maintainable code.
- Compatibility with different Android devices.

Do not create unnecessary complexity.

## 3. DESIGN SYSTEM AND VISUAL IDENTITY

Create a premium, minimalist, modern, and elegant interface.

Design direction:
- Professional.
- Clean.
- Sophisticated.
- Minimalist.
- Comfortable for extended use.
- Consistent across all modules.

Avoid:
- Excessive gradients.
- Neon colors.
- Excessive animations.
- Overloaded dashboards.
- Excessive glassmorphism.
- Oversized decorative elements.
- Unnecessary visual effects.
- Childish illustrations.

Color palette:

Light Theme:
- Background: #F6F7F9
- Surface: #FFFFFF
- Primary: #304F73
- Secondary: #6F91B5
- Text Primary: #17212F
- Text Secondary: #758092
- Border: #E7EAF0

Dark Theme:
- Background: #10151D
- Surface: #1A2431
- Primary: #89A9CC
- Secondary: #607F9F
- Text Primary: #F1F4F8
- Text Secondary: #A5B0BE
- Border: #303B49

Design requirements:
- Use Material 3.
- Use consistent spacing.
- Use rounded cards between 14 and 20 dp.
- Use readable typography.
- Use consistent iconography.
- Use subtle transitions.
- Support edge-to-edge layouts.
- Support different screen sizes.
- Support system light/dark theme.
- Include proper loading, empty, success, and error states.

Create a custom application logo and launcher icon consistent with CoreChiperLabs branding.

## 4. MAIN NAVIGATION

Implement five bottom navigation destinations:

1. Home
2. Explore
3. Quick Add
4. Insights
5. Profile

HOME:
Display a personalized dashboard containing:
- Greeting and current date.
- Financial overview.
- Today's tasks.
- Upcoming reminders.
- Health summary.
- Learning progress.
- Quick access to six main modules.
- Recent activities.

EXPLORE:
Display six main modules in an organized grid.

QUICK ADD:
Create a functional quick-action menu:
- Add transaction.
- Add task.
- Add health record.
- Add document.
- Add learning note.
- Add reminder.

INSIGHTS:
Display useful statistics and charts:
- Financial statistics.
- Task completion.
- Health trends.
- Learning progress.
- Monthly activity summary.

PROFILE:
Include:
- Personal information.
- Preferences.
- Language settings.
- Theme settings.
- Biometric security.
- Backup and restore.
- Data management.
- About CoreChiperAssistant.
- Application version.

## 5. MODULE 01 — HEALTH MANAGEMENT

Create a complete personal health tracking module.

Features:

A. Health Dashboard
- Current weight.
- Height.
- BMI calculation.
- Weight history.
- Health activity summary.

B. Health Records
Allow users to record:
- Weight.
- Height.
- Blood pressure.
- Blood glucose.
- Body measurements.
- Personal health notes.

C. Exercise Tracking
- Activity type.
- Duration.
- Exercise date.
- Calories estimate when supported by a documented calculation.
- Exercise history.

D. Medication Reminders
- Medication name.
- Dosage information.
- Schedule.
- Reminder frequency.
- Reminder notifications.
- Medication history.

E. Health Statistics
- Weight progress chart.
- Blood pressure history.
- Activity statistics.
- Health record history.

Requirements:
- Complete CRUD operations.
- Data stored in Room.
- Input validation.
- Date filtering.
- Search functionality.
- Clear visual charts.

Important:
Do not provide medical diagnoses.
Clearly identify estimated values.
Do not present BMI as a medical diagnosis.
Medication reminders must not claim that medication was taken automatically.

## 6. MODULE 02 — FINANCE MANAGEMENT

Create a fully functional personal finance manager.

Features:

A. Finance Dashboard
- Total income.
- Total expenses.
- Current balance.
- Monthly budget.
- Savings progress.

B. Transactions
Users can:
- Add income.
- Add expenses.
- Edit transactions.
- Delete transactions.
- Select transaction categories.
- Add descriptions.
- Select dates.
- Select payment methods.

C. Categories

Income:
- Salary.
- Business.
- Allowance.
- Other income.

Expenses:
- Food.
- Transportation.
- Education.
- Shopping.
- Health.
- Entertainment.
- Bills.
- Other expenses.

D. Wallet Management
- Create wallets.
- Edit wallets.
- Delete wallets when safe.
- Set initial balance.
- Track wallet balances.

E. Budget Management
- Monthly budget.
- Category budgets.
- Spending limits.
- Budget progress.
- Budget warnings.

F. Savings Goals
- Goal name.
- Target amount.
- Current amount.
- Target date.
- Progress percentage.

G. Debt Management
- Borrowed money.
- Lent money.
- Due dates.
- Payment history.
- Outstanding balance.

H. Financial Reports
- Daily reports.
- Weekly reports.
- Monthly reports.
- Category breakdown.
- Income versus expenses.
- PDF export.
- CSV export.

Requirements:
- Use precise decimal arithmetic for monetary values.
- Use Indonesian Rupiah (IDR) by default.
- Format currency correctly.
- All financial calculations must be accurate.
- Prevent duplicate transactions caused by repeated submissions.
- Store all data locally.

Do not implement fake bank integration.

## 7. MODULE 03 — PRODUCTIVITY MANAGEMENT

Create a comprehensive productivity system.

Features:

A. Task Manager
- Create tasks.
- Edit tasks.
- Delete tasks.
- Complete tasks.
- Task descriptions.
- Due dates.
- Priorities.
- Categories.
- Task status.

B. Calendar
- Monthly calendar.
- Weekly calendar.
- Daily schedule.
- Upcoming events.
- Task indicators.

C. Reminders
- Custom reminders.
- Repeating reminders.
- Reminder notifications.
- Reminder management.

D. Habit Tracker
- Create habits.
- Daily tracking.
- Weekly progress.
- Completion statistics.
- Habit history.

E. Pomodoro Timer
- Focus session.
- Short break.
- Long break.
- Session counter.
- Optional notification and sound.
- Session history.

F. Activity History
- Completed tasks.
- Missed deadlines.
- Productivity statistics.

Requirements:
- Persistent database storage.
- Background reminder scheduling.
- Handle Android notification permissions.
- Handle device restart and reschedule eligible reminders.
- Explain limitations of exact and inexact alarm scheduling.
- Avoid unnecessary battery consumption.

## 8. MODULE 04 — DOCUMENT MANAGEMENT

Create a functional document manager.

Features:

A. Document Library
- Import PDF.
- Import images.
- Import supported document formats.
- Create folders.
- Rename documents.
- Delete documents.
- Search documents.
- Sort documents.
- Filter by category.

B. Document Scanner
- CameraX integration.
- Capture document images.
- Document cropping.
- Image enhancement.
- Save scanned documents as PDF.

C. OCR
- Recognize text from document images.
- Extract searchable text.
- Allow users to copy recognized text.

D. PDF Tools
- PDF preview.
- PDF page navigation.
- Image to PDF.
- PDF compression where supported.
- Share PDF.
- Export PDF.

E. Security
- Optional biometric application lock.
- Private application storage for imported copies.
- Secure backup.
- Clear explanation of storage permissions.

Use Android Storage Access Framework for user-selected files.

Do not claim a document is encrypted unless actual encryption is implemented and verified.

## 9. MODULE 05 — LEARNING MANAGEMENT

Create a personal learning platform.

Features:

A. Learning Dashboard
- Active learning courses.
- Study progress.
- Daily learning goals.
- Learning statistics.

B. Study Materials
- Create subjects.
- Create learning topics.
- Add descriptions.
- Save personal notes.
- Organize materials.

C. Flashcards
- Create flashcards.
- Front and back content.
- Flip animation.
- Mark as learned.
- Review history.

D. Quiz System
- Multiple-choice questions.
- Four answer options.
- Score calculation.
- Correct answer explanation.
- Quiz history.
- Performance statistics.

E. Study Goals
- Daily targets.
- Weekly targets.
- Study session tracking.
- Learning streak.

F. Initial Content
Provide useful sample learning materials and sample quizzes in Indonesian.

Do not leave the learning module empty on first installation.

All learning content must work offline.

## 10. MODULE 06 — EMERGENCY ASSISTANT

Create a practical emergency assistance module.

Features:

A. Emergency Dashboard
- Prominent SOS button.
- Emergency contacts.
- Emergency shortcuts.
- Personal emergency information.

B. Emergency Contacts
- Add contacts.
- Edit contacts.
- Delete contacts.
- Set primary contact.
- Store contact names and phone numbers.

C. SOS Workflow
When the user activates SOS:

1. Show a confirmation screen.
2. Request location permission if needed.
3. Retrieve current location only after user action.
4. Generate a Google Maps location link when available.
5. Prepare an emergency message.
6. Allow the user to send it through an SMS application or Android sharing interface.
7. Show the actual result of each action.

Do not falsely report that an emergency message was sent.

Do not silently send SMS or initiate phone calls.

Use Android ACTION_DIAL for phone shortcuts so the user confirms the call.

D. Emergency Information
- Full name.
- Blood type (optional).
- Allergies (optional).
- Important medical notes (optional).
- Emergency contacts.

E. First Aid Guide

Include offline educational guides for:
- Minor bleeding.
- Burns.
- Fainting.
- Choking.
- Basic CPR emergency guidance.
- Road accidents.
- Natural disasters.

Provide clear safety disclaimers and direct users to professional emergency assistance.

F. Emergency Numbers
Allow emergency numbers to be configured according to the user's location.

Provide Indonesian defaults where appropriate, but clearly explain that availability can vary by region.

## 11. GLOBAL APPLICATION FEATURES

Implement the following features across the entire application:

A. Global Search
Search:
- Tasks.
- Transactions.
- Health records.
- Documents.
- Learning materials.

B. Notifications
- Task reminders.
- Medication reminders.
- Budget alerts.
- Study reminders.
- Habit reminders.

C. Backup and Restore
- Export user data.
- Create backup archive.
- Encrypt backup using a secure key-management approach.
- Restore backup.
- Validate backup integrity.
- Handle incompatible backup versions.

D. Application Security
- Optional biometric lock.
- Secure handling of sensitive information.
- No hardcoded secrets.
- Proper Android permissions.
- Privacy-friendly architecture.

E. Localization
- Indonesian.
- English.

F. Theme
- Light.
- Dark.
- Follow system.

G. Data Management
- Clear cache.
- Export data.
- Delete selected data.
- Delete all application data with confirmation.

## 12. TECHNICAL ARCHITECTURE

Use a clean, maintainable architecture.

Technology stack:

- Kotlin.
- Jetpack Compose.
- Material 3.
- Room Database.
- SQLite.
- DataStore.
- Navigation Compose.
- Kotlin Coroutines.
- Flow.
- WorkManager.
- CameraX.
- ML Kit OCR.
- AndroidX Biometric.
- Android PDF APIs.

Architecture:

Presentation Layer:
- Compose Screens.
- UI Components.
- ViewModels.
- UI State.

Domain Layer:
- Use Cases.
- Business Logic.
- Validation.

Data Layer:
- Room Database.
- DAO.
- Entities.
- Repositories.
- Data Models.

Core Layer:
- Navigation.
- Notifications.
- Security.
- Permissions.
- Backup.
- Utilities.

Organize each feature into its own package.

Use dependency injection if appropriate.

Avoid unnecessarily complicated abstractions.

## 13. DATABASE DESIGN

Create Room entities and DAO interfaces for:

- UserProfile.
- HealthRecord.
- ExerciseRecord.
- Medication.
- MedicationSchedule.
- Transaction.
- TransactionCategory.
- Wallet.
- Budget.
- SavingsGoal.
- DebtRecord.
- DebtPayment.
- Task.
- CalendarEvent.
- Habit.
- HabitLog.
- StudySubject.
- StudyNote.
- Flashcard.
- Quiz.
- QuizQuestion.
- QuizResult.
- Document.
- DocumentFolder.
- EmergencyContact.
- EmergencyProfile.
- Reminder.

Requirements:
- Define primary keys.
- Define foreign keys where necessary.
- Define database indices.
- Define relationships.
- Implement migrations.
- Use transactions for related financial operations.
- Prevent orphaned records.
- Preserve data during database upgrades.

## 14. PERFORMANCE AND RELIABILITY

The application must:

- Launch reliably.
- Avoid unnecessary network requests.
- Work offline for core features.
- Handle missing permissions gracefully.
- Avoid blocking the main thread.
- Use pagination or lazy lists for large collections.
- Handle empty databases.
- Handle invalid inputs.
- Handle corrupted backup files.
- Recover gracefully from errors.
- Avoid crashes when rotating the device or recreating activities.
- Support different screen densities.
- Support modern Android versions.

Do not use placeholder buttons.

Do not use fake charts with hardcoded values in actual user statistics.

Do not create nonfunctional menu items.

Do not display fake notifications.

Do not use sample data as real user data.

## 15. GITHUB AND APK BUILD SYSTEM

The complete project must be hosted in a GitHub repository.

Create:

- README.md.
- .gitignore.
- Gradle build files.
- Gradle version catalog.
- AndroidManifest.xml.
- GitHub Actions workflow.

Create:

.github/workflows/android-build.yml

GitHub Actions must:

1. Check out the repository.
2. Configure the correct JDK.
3. Set up Android SDK.
4. Configure Gradle.
5. Run unit tests.
6. Build the debug APK.
7. Verify the generated APK exists.
8. Upload the APK as a GitHub Actions artifact.

Use the build task:

./gradlew assembleDebug

Expected APK location:

app/build/outputs/apk/debug/app-debug.apk

Also provide an optional release workflow.

Release signing must use GitHub Secrets and must never include signing passwords or private keys in the repository.

Document how to download the APK from GitHub Actions.

## 16. TESTING REQUIREMENTS

Implement tests for:

- Database operations.
- Finance calculations.
- BMI calculations.
- Task management.
- Reminder scheduling.
- Backup and restore.
- Input validation.
- Navigation.
- Important module workflows.

Perform integration testing where appropriate.

Before declaring completion:

- Build the complete project.
- Resolve all compilation errors.
- Resolve dependency errors.
- Check Android permissions.
- Test database persistence.
- Test navigation.
- Test important actions.
- Check for crashes.
- Verify APK generation.

## 17. DEVELOPMENT EXECUTION RULES

Follow these instructions strictly:

1. Build the actual Android project, not a design mockup.
2. Generate complete source files.
3. Do not omit required files.
4. Do not provide pseudocode in place of implementations.
5. Do not use fake functionality.
6. Do not leave unfinished TODO implementations in required features.
7. Keep the project compilable throughout development.
8. Use stable and compatible dependencies.
9. Do not invent unsupported Android APIs.
10. Fix errors before continuing.
11. Make the interface consistent across every module.
12. Make all important buttons functional.
13. Implement proper error handling.
14. Test the application before claiming it is complete.
15. Provide the exact GitHub Actions workflow and APK output location.

If the complete implementation is too large for one development session, divide the work into clearly defined phases. Complete and verify each phase before proceeding.

Do not silently remove features to make the build succeed.

## 18. FINAL DELIVERABLES

The final project must contain:

1. Complete Android Studio project.
2. Complete Kotlin source code.
3. Functional Jetpack Compose interface.
4. Room database implementation.
5. All six functional modules.
6. Working navigation.
7. Working notifications.
8. Document and PDF functionality.
9. Emergency workflows.
10. Backup and restore.
11. Light and dark themes.
12. Indonesian and English localization.
13. GitHub Actions build workflow.
14. README with setup instructions.
15. Successful APK build.

The final result must feel like a polished, premium Android application developed by a professional software development team.

**PRODUCT VISION:**

CoreChiperAssistant is not just another productivity application.

It is a unified personal assistant designed to help people manage their health, money, time, documents, knowledge, and safety in one reliable place.

**CORECHIPERLABS — WHERE SECURITY MEETS INTELLIGENCE.**
