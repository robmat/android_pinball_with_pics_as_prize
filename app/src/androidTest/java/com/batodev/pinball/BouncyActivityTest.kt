package com.batodev.pinball

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dozingcatsoftware.bouncy.BouncyActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// BouncyActivity is a real, open-source (Vector Pinball / "Bouncy") physics
// pinball game - one Activity manages both the main menu and the actual
// real-time gameplay (Box2D physics via a background FieldDriver thread,
// rendered through a Canvas or OpenGL view). Actually playing/scoring a
// pinball game isn't practical to simulate deterministically via Espresso
// (same "don't simulate real-time skill-based gameplay" judgment as
// android_tetris's Tetris coverage), so startGameButtonStartsTheGame only
// verifies the real state transition fires (menu hides), not gameplay
// itself. The picture-unlock mechanic is tied to score thresholds reached
// through real play, so GalleryActivity coverage seeds uncoveredPics
// directly instead (see GalleryActivityTest).
@RunWith(AndroidJUnit4::class)
class BouncyActivityTest {

    @Before
    fun setUp() {
        resetSettings()
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun launchesShowingMainMenu() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.buttonPanel)).check(matches(isDisplayed()))
        onView(withId(R.id.startGameButton)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun startGameButtonStartsTheGameAndHidesTheMenu() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.startGameButton)).perform(click())

        onView(withId(R.id.buttonPanel)).check(matches(withEffectiveVisibility(GONE)))
        scenario.close()
    }

    @Test
    fun galleryButtonNoOpsWithNoUncoveredPics() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.galleryButton)).perform(click())

        onView(withId(R.id.startGameButton)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun galleryButtonOpensGalleryActivityWithUncoveredPics() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val realPic = context.assets.list(ImageHelper.PRIZE_IMAGES_10_K)!!.first()
        resetSettings { uncoveredPics = mutableListOf(realPic) }
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.galleryButton)).perform(click())

        onView(withId(R.id.gallery_activity_background)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun moreGamesButtonOpensDeveloperPlayStorePage() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.moreGamesButton)).perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse("https://play.google.com/store/apps/dev?id=8228670503574649511")))
        scenario.close()
    }

    @Test
    fun aboutButtonOpensAboutActivity() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.aboutButton)).perform(click())

        onView(withId(R.id.aboutTextView)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun preferencesButtonOpensPreferencesScreen() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.preferencesButton)).perform(click())

        onView(withId(android.R.id.list)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun quitButtonFinishesActivity() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        onView(withId(R.id.quitButton)).perform(click())

        assertEventuallyDestroyed(scenario)
    }

    @Test
    fun systemBackPressFinishesActivity() {
        val scenario = ActivityScenario.launch(BouncyActivity::class.java)

        assertBackPressFinishesScenario(scenario)
    }
}
