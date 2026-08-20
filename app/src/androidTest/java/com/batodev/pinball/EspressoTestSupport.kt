package com.batodev.pinball

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals

// Shared across BouncyActivityTest/GalleryActivityTest/AboutActivityTest.
// No Activity here overrides back-press (doQuit() just calls finish()), so
// the standard press-back-and-expect-DESTROYED pattern applies everywhere.

fun assertEventuallyDestroyed(
    scenario: ActivityScenario<*>,
    timeoutMs: Long = 8_000,
) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (scenario.state != Lifecycle.State.DESTROYED && System.currentTimeMillis() < deadline) {
        Thread.sleep(50)
    }
    assertEquals(Lifecycle.State.DESTROYED, scenario.state)
}

fun assertBackPressFinishesScenario(scenario: ActivityScenario<*>) {
    try {
        pressBack()
    } catch (expected: NoActivityResumedException) {
    }
    assertEventuallyDestroyed(scenario)
}

/**
 * Unlike every other app in this workspace, SettingsHelper here is a plain
 * per-instance class (SettingsHelper(context).preferences, .savePreferences())
 * rather than a load()/save() singleton object - still just plain
 * SharedPreferences under the hood ("preferences" prefs file), so seeding
 * works the same way: construct one, mutate .preferences, save.
 */
fun resetSettings(configure: Preferences.() -> Unit = {}) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val settingsHelper = SettingsHelper(context)
    settingsHelper.preferences.uncoveredPics.clear()
    settingsHelper.preferences.lastSeenGalleryPic = 0
    settingsHelper.preferences.apply(configure)
    settingsHelper.savePreferences()
}

fun waitFor(millis: Long): ViewAction =
    object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()

        override fun getDescription(): String = "wait for ${millis}ms while pumping the main looper"

        override fun perform(
            uiController: UiController,
            view: View,
        ) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    }
