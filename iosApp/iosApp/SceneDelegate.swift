import UIKit
import SwiftUI
import Foundation
import ComposeApp


class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene,
               openURLContexts URLContexts: Set<UIOpenURLContext>) {
        if let url = URLContexts.first?.url {
            SpotifyRedirectHandlerKt.handleSpotifyRedirect(url: url.absoluteString)
        }
    }

    func scene(_ scene: UIScene,
               willConnectTo session: UISceneSession,
               options connectionOptions: UIScene.ConnectionOptions) {

        if let urlContext = connectionOptions.urlContexts.first {
            let url = urlContext.url
            SpotifyRedirectHandlerKt.handleSpotifyRedirect(url: url.absoluteString)
        }
    }
}
