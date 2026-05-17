````markdown
# FocusNotify

FocusNotify is an Android-based adaptive notification management system designed to help students reduce distractions during study sessions without completely missing important notifications.

The app intercepts notifications during an active study session, scores each notification using an interpretable rule-based policy, and either delivers urgent notifications immediately or delays lower-priority notifications into a digest.

This project was developed as a Human-Computer Interaction / Digital Well-being research prototype focused on student attention, notification overload, and fear of missing out (FoMO).

---

## Project Overview

Smartphone notifications can repeatedly interrupt students during study sessions. However, completely blocking all notifications can create anxiety because students may worry about missing urgent academic, family, or project-related messages.

FocusNotify addresses this problem using adaptive notification triage. Instead of allowing every notification or blocking everything, the app evaluates each incoming notification based on factors such as app category, urgency keywords, user preference, and FoMO sensitivity.

Notifications that appear important are delivered immediately, while less important notifications are saved for a later digest review.

According to the project report, Adaptive Triage reduced delivered interruptions from 468 in Baseline mode to 33 in Adaptive mode, achieving a 92.95% reduction in delivered interruptions during study sessions. :contentReference[oaicite:0]{index=0}

---

## Features

- Start and stop study sessions
- Intercept incoming Android notifications
- Adaptive per-notification scoring
- Deliver urgent notifications immediately
- Batch lower-priority notifications into digest summaries
- Support for Baseline, Fixed Batching, and Adaptive Triage modes
- FoMO-aware user preference settings
- Notification logging for analysis
- View notification logs inside the app
- Export/share CSV log files
- Local Room database support
- Background digest scheduling using WorkManager
- Simple Android UI for session control, settings, and logs

---

## Study Modes

FocusNotify supports three study conditions:

| Mode | Behavior |
|---|---|
| Baseline | All notifications are delivered while the system logs silently |
| Fixed Batching | Notifications are held and released through digests |
| Adaptive Triage | Each notification is scored and either delivered or batched |

The final report evaluated the system using a seven-day field deployment with 15 Android users, 1,766 notification events, and 73 post-session survey records. :contentReference[oaicite:1]{index=1}

---

## Adaptive Scoring Policy

Each notification receives a score based on multiple factors:

- App category
- Urgency keywords
- Time context
- FoMO-aware user preference

If the score crosses the delivery threshold, the notification is delivered immediately. Otherwise, it is delayed into the digest.

The report describes this as an interpretable scoring policy:

```text
S = w1(AppCategory) + w2(UrgencyKeyword) + w3(TimeContext) + w4(FoMOPreference)
````

This rule-based design makes the system easier to understand, debug, and adjust compared to black-box notification filtering. 

---

## Tech Stack

* Kotlin
* Android SDK
* Android NotificationListenerService
* Room Database
* WorkManager
* ViewBinding
* SharedPreferences
* CSV Logging
* Gradle

---

## Project Structure

```text
FocusNotify/
│
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/focusnotify/app/
│       │   ├── FocusNotifyApp.kt
│       │   │
│       │   ├── data/
│       │   │   ├── db/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   └── NotificationDao.kt
│       │   │   └── model/
│       │   │       └── NotificationEntity.kt
│       │   │
│       │   ├── engine/
│       │   │   └── ScoringEngine.kt
│       │   │
│       │   ├── service/
│       │   │   └── FocusNotificationService.kt
│       │   │
│       │   ├── ui/
│       │   │   ├── MainActivity.kt
│       │   │   ├── SettingsActivity.kt
│       │   │   └── LogActivity.kt
│       │   │
│       │   ├── util/
│       │   │   ├── CsvLogger.kt
│       │   │   └── PrefsHelper.kt
│       │   │
│       │   └── worker/
│       │       └── DigestWorker.kt
│       │
│       └── res/
│           ├── layout/
│           ├── values/
│           ├── drawable/
│           └── xml/
│
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## Main Components

### MainActivity

Controls the main app screen. It allows users to start or stop a study session, open settings, view logs, and share CSV log files.

### FocusNotificationService

Uses Android `NotificationListenerService` to listen for incoming notifications during active study sessions.

### ScoringEngine

Applies adaptive scoring logic to decide whether a notification should be delivered immediately or batched.

### DigestWorker

Uses Android WorkManager to schedule digest-based notification handling during study sessions.

### CsvLogger

Stores notification decisions and metadata in CSV format for later review and research analysis.

### Room Database

Stores notification-related data locally using Room.

---

## Requirements

Before running the project, make sure you have:

* Android Studio installed
* Android SDK installed
* Gradle configured
* Android device or emulator
* Android 8.0 or above recommended
* Notification Access permission enabled for the app

The project uses:

```text
compileSdk 34
minSdk 26
targetSdk 34
```

---

## How to Run the Project

### 1. Clone the Repository

```bash
git clone https://github.com/Harshith-123/FocusNotify.git
```

### 2. Open in Android Studio

Open Android Studio and select:

```text
File → Open → FocusNotify
```

### 3. Let Gradle Sync

Wait for Android Studio to download dependencies and sync the project.

### 4. Connect Device or Start Emulator

Use either:

* A physical Android device
* An Android emulator

A real Android device is recommended because notification access works better on actual devices.

### 5. Run the App

Click the green **Run** button in Android Studio.

---

## Permission Setup

FocusNotify requires notification access permission.

After installing the app:

1. Open FocusNotify.
2. Click **Start Session**.
3. If permission is not enabled, the app redirects to Android Notification Access settings.
4. Enable notification access for FocusNotify.
5. Return to the app.
6. Start the study session again.

---

## How to Use

1. Open the app.
2. Enable notification access permission.
3. Select or configure study mode from settings.
4. Click **Start Session**.
5. During the session, FocusNotify monitors incoming notifications.
6. Important notifications may be delivered immediately.
7. Lower-priority notifications are delayed into a digest.
8. Click **Stop Session** when the study session ends.
9. View logs using the log screen.
10. Export or share CSV logs if needed.

---

## Research Evaluation

The final report evaluated FocusNotify using:

* 15 Android participants
* 7-day field deployment
* 3 conditions: Baseline, Fixed Batching, Adaptive Triage
* 1,766 notification events
* 73 post-session survey records
* 15 qualitative feedback artifacts

The study found that Adaptive Triage reduced delivered interruptions by 92.95% compared with Baseline while maintaining better user comfort than rigid Fixed Batching. 

Qualitative feedback showed that users trusted the adaptive mode more when they could rely on digest visibility, sender-level controls, and keyword-based personalization. 

---

## Design Recommendations from the Study

The report suggests that future student-facing notification systems should:

* Score notifications individually instead of blocking by app alone
* Provide visible digests for held notifications
* Support academic keywords and favorite senders
* Distinguish direct messages from group chats
* Include correction controls for wrong decisions
* Explain adaptive triage clearly during onboarding



---

## Future Enhancements

* Add sender-level controls for messaging apps
* Add group-level filtering for WhatsApp or similar apps
* Add a “release digest now” option
* Add correction buttons such as “should have delivered” or “should have batched”
* Improve notification classification using local on-device models
* Add dashboard analytics for focus sessions
* Add better visualization for interruption trends
* Add user-defined focus profiles
* Improve UI design and onboarding flow

---

## Privacy Note

FocusNotify treats notification content as sensitive user data. The prototype uses lightweight, interpretable, on-device rules instead of sending notification content to external cloud services or large language models.

This design reduces privacy risk while still supporting adaptive notification filtering. 

---

## Project Purpose

This project was developed as a research-oriented Android prototype to explore how adaptive notification management can reduce student study interruptions while preserving trust, control, and psychological comfort.

---

## Author

**Harshith Basavaraju**

GitHub: [Harshith-123](https://github.com/Harshith-123)
