import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object ShowListeningHistoryScreen : Screen {
        @Composable
        override fun Content() {
                ShowListeningHistoryUi()
        }
}

@Composable
internal fun ShowListeningHistoryUi() {
        Scaffold {
                Text("Listening history Screen")
        }
}
