import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
fun LyraApp() {
        Navigator(AuthorizationScreen) { navigator ->
                SlideTransition(navigator)
        }
}