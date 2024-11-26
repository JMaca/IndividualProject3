
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItems(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object Welcome : BottomNavigationItems("welcomeScreen")

    object LogScreen : BottomNavigationItems(
        route = "loginScreen",
        title = "Log",
        icon = Icons.Outlined.Home
    )
    object SignupScreen : BottomNavigationItems(
        route = "signupScreen",
        title = "sign",
        icon = Icons.Default.CheckCircle
    )
    object GameScreen : BottomNavigationItems(
        route = "gameScreen",
        title = "game",
        icon = Icons.Outlined.AccountCircle
    )
//    object QuestionsScreen : BottomNavigationItems(
//        route = "questionScreen",
//        title = "questions",
//        icon = Icons.Outlined.Info
//    )
}