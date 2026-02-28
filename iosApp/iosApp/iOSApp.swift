import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinInitializer.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    SpotifyRedirectHandlerKt.handleSpotifyRedirect(url: url.absoluteString)
                }
        }
    }
}