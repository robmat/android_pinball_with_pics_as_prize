package com.batodev.pinball

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Keeps a bottom-pinned control clear of the strip along the bottom of the screen where
 * the system takes touches itself, or it renders there and becomes untappable.
 *
 * Which inset describes that strip depends on the screen: with the system bars visible the
 * nav bar window sits over the app (navigationBars()); with them hidden, navigationBars()
 * reports 0 but the same band stays reserved for system gestures (systemGestures()).
 * Padding by the larger of the two covers both cases.
 */
internal fun avoidNavigationBarOverlap(view: View) {
    val baseBottomMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val navBarBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        val gestureBottom = insets.getInsets(WindowInsetsCompat.Type.systemGestures()).bottom
        (v.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
            baseBottomMargin + maxOf(navBarBottom, gestureBottom)
        v.requestLayout()
        insets
    }
}
