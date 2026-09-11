# Scheduler testing

## iOS

`BGTaskScheduler` never runs on a wall-clock — `earliestBeginDate` is a lower bound and iOS decides when to actually launch. To exercise the handler on demand, use LLDB's private simulate selectors.

1. Run the app from Xcode (`⌘ R`) on a physical device — the debugger must be attached.
2. In the app, set an update period once so a request is submitted.
3. Send the app to background (swipe up to home; don't kill it).
4. Pause the debugger: **Debug → Pause** (`⌃ ⌘ Y`). Wait for the `(lldb)` prompt in the console.
5. Simulate a launch:

   ```
   e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"io.github.sophon.fightingnerd.refresh"]
   ```

6. **Debug → Continue** (`⌃ ⌘ Y`). Expect `Scheduler`-tagged Napier logs: `bgTask: <report>` on success, `bgTask: <error>` on failure. `submitBGRequest` runs from the `finally` and re-queues the next task.

Simulate the expiration path with the same flow, swapping the selector:

```
e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateExpirationForTaskWithIdentifier:@"io.github.sophon.fightingnerd.refresh"]
```

Expect the coroutine to cancel and the OS to receive `setTaskCompletedWithSuccess(false)`.

Both selectors are private; debug builds only. If pausing gives no `(lldb)` prompt, the debugger isn't attached — relaunch with `⌘R` instead of tapping the app icon.
