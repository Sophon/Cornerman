# FightingNerd auto-update scheduler — plan

## Scope

- Global settings: on/off + interval (hours/days/etc.)
- Background worker: iterates enabled wikis, refreshes stale ones
- Manual refresh button per game on games list — same refresh call path
- Timing is best-effort, not precise

## Components

### Common (shared)

- **DataStore** — global on/off flag, interval value
- **Existing per-wiki refresh** — already used by swipe-to-refresh, reused as-is
- **New shared use case** — iterate configured wikis, check each `last_synced_at`, call refresh on stale ones
- **`SchedulerPort`** — expect/actual, exposed from a new scheduler module's `integration/` package

### Android adapter

- **WorkManager** (`androidx.work:work-runtime-ktx`)
- **`PeriodicWorkRequest`** — the scheduled trigger; 15-min minimum interval (clamp in settings UI)
- **`CoroutineWorker`** — thin subclass in the Android source set, delegates to the common worker body
- Registered via WorkManager's default initializer (or disabled + manual init if we want control)

### iOS adapter

- **BackgroundTasks framework** — `BGTaskScheduler`
- **`BGProcessingTask`** — the right task class here. `BGAppRefreshTask` is capped at ~30s which won't survive a multi-wiki refresh. Processing tasks run longer and can require network.
- **Info.plist entries**:
    - `BGTaskSchedulerPermittedIdentifiers` — array with our task identifier (e.g. `io.github.sophon.fightingnerd.refresh`)
    - `UIBackgroundModes` — `processing`
- **Handler registration** — must happen at app launch in the iOS entry point (before `didFinishLaunching` returns). ~5 lines of Swift-side glue calling into the shared handler.
- iOS decides when tasks actually run based on user behavior — we can only request minimum interval, not guarantee it.


## Timestamp tracking

- Per-wiki `last_synced_at` lives in each wiki module's cache (SQLDelight)
- Written by the single refresh operation, so manual button / swipe-to-refresh / background worker all update it uniformly
- Background use case reads it to decide skip vs refresh

## Testing notes

- **Android**: WorkManager has a testing artifact (`work-testing`) with `TestDriver` to force periodic runs
- **iOS**: no simulator support for real background triggers. Manual trigger via lldb:
  `e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"io.github.sophon.fightingnerd.refresh"]`
  Real-world timing verification requires a physical device left idle.

## Constraints to price in

- Android minimum period: 15 minutes (WorkManager hard limit)
- iOS: no guaranteed cadence; system may skip runs entirely if app is rarely opened
- Both platforms: no exact timing. "Every N hours" is a lower bound at best.
- User setting should communicate this — label like "Check at least every…" rather than "Every…"

## Out of scope (for this iteration)

- Wi-Fi-only / charging-only constraints (WorkManager + BGProcessingTask both support this; add later if requested)
- Per-wiki intervals
- Notifications on refresh completion
- Retry/backoff tuning beyond WorkManager defaults
- 