package lt.university.diabetescalculator

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Papildomas (neprivalomas) navigacijos patikrinimas: paspaudus apačios
 * juostos mygtukus "Istorija" ir "Info", atitinkamas ekranas turi tapti
 * matomas. Tai patvirtina, kad visi trys ekranai realiai pasiekiami per
 * navigaciją, o ne tik apibrėžti kode.
 */
class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNav_navigatesToHistoryScreen() {
        composeRule.onNodeWithText("Istorija").performClick()
        composeRule.onNodeWithText("Skaičiavimų istorija").assertExists()
    }

    @Test
    fun bottomNav_navigatesToInfoScreen() {
        composeRule.onNodeWithText("Info").performClick()
        composeRule.onNodeWithText("Apie skaičiavimus").assertExists()
    }
}
