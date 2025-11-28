import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object AuthorizationScreen : Screen {
        @Composable
        override fun Content() {
                AuthorizationScreenUi()
        }
}

@Composable
internal fun AuthorizationScreenUi() {
        Scaffold {
                Text("Authorization Screen")
        }
}
