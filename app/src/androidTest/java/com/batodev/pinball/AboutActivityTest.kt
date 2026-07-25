package com.batodev.pinball

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dozingcatsoftware.bouncy.AboutActivity
import org.junit.Test
import org.junit.runner.RunWith

// AboutActivity.startForLevel() (used by BouncyActivity's real "about"
// button, covered in BouncyActivityTest) passes a "level" int extra -
// launched directly here the same way.
@RunWith(AndroidJUnit4::class)
class AboutActivityTest {

    private fun launch(): ActivityScenario<AboutActivity> {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AboutActivity::class.java
        ).apply { putExtra("level", 1) }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun launchesShowingAboutText() {
        val scenario = launch()

        onView(withId(R.id.aboutTextView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun systemBackPressFinishesActivity() {
        val scenario = launch()

        assertBackPressFinishesScenario(scenario)
    }
}
