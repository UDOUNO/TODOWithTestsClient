import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.todo.Screen
import com.example.todo.presentation.MainActivity
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule

abstract class BaseTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    protected val screen = Screen(composeTestRule)
}