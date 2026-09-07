import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        IosAppInitKt.iosAppInit()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
