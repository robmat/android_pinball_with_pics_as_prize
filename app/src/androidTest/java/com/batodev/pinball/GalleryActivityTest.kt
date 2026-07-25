package com.batodev.pinball

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Images are organized into three prize tiers (prize-images-10k/100k/500k,
// unlocked by real score thresholds reached through real play - not
// practical to simulate deterministically via Espresso, see
// BouncyActivityTest). GalleryActivity itself doesn't care which tier a
// picture came from (ImageHelper.findPathForImage searches all three), so
// seeding uncoveredPics straight from the 10k tier is enough to exercise
// this Activity for real. leftClicked()/rightClicked() self-correct at the
// boundaries (index > 0 / index < size - 1 guards) rather than toggling
// button visibility, unlike some other apps in this workspace.
@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {

    private lateinit var images: List<String>

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        images = context.assets.list(ImageHelper.PRIZE_IMAGES_10_K)!!.take(3)
        resetSettings { uncoveredPics = images.toMutableList() }
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    private fun launch(): ActivityScenario<GalleryActivity> =
        ActivityScenario.launch(GalleryActivity::class.java)

    @Test
    fun launchesShowingFirstImage() {
        val scenario = launch()

        onView(withId(R.id.gallery_activity_background)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun leftClickAtFirstImageNoOpsWithoutCrashing() {
        val scenario = launch()

        onView(withId(R.id.gallery_left)).perform(click())

        onView(withId(R.id.gallery_activity_background)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun rightClickPastLastImageSelfCorrectsWithoutCrashing() {
        val scenario = launch()

        repeat(images.size + 1) {
            onView(withId(R.id.gallery_right)).perform(click())
        }

        onView(withId(R.id.gallery_activity_background)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun shareButtonSharesCurrentImage() {
        val scenario = launch()

        onView(withId(R.id.gallery_share_btn)).perform(click())

        intended(hasAction(Intent.ACTION_SEND))
        intended(hasType("image/*"))
        scenario.close()
    }

    @Test
    fun wallpaperButtonSetsWallpaperWithoutCrashing() {
        val scenario = launch()

        onView(withId(R.id.gallery_wallpaper_btn)).perform(click())

        onView(withId(R.id.gallery_activity_background)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun backButtonFinishesActivity() {
        val scenario = launch()

        onView(withId(R.id.gallery_back_btn)).perform(click())

        assertEventuallyDestroyed(scenario)
    }

    @Test
    fun systemBackPressFinishesActivity() {
        val scenario = launch()

        assertBackPressFinishesScenario(scenario)
    }
}
