package com.batodev.pinball

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val AD_ID = BuildConfig.AD_HELPER_AD_ID
var ad: InterstitialAd? = null
const val AD_COUNTER = "AD_COUNTER"
const val WHEN_TO_SHOW_ADS = "WHEN_TO_SHOW_ADS"

// An interstitial opened the moment the last ball drains lands under fingers still hammering the
// flippers - AdMob reported 16%+ CTR from exactly that. So a due ad waits until the screen has been
// left alone for this long, and every touch pushes it back.
private const val QUIET_PERIOD_MS = 1500L

object AdHelper {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingAd: Runnable? = null

    fun showAddIfNeeded(activity: Activity) {
        val prefs = activity.getSharedPreferences(RateAppHelper.javaClass.simpleName, Context.MODE_PRIVATE)
        var adCounter = prefs.getInt(AD_COUNTER, 0)
        val whenToShowAds = prefs.getInt(WHEN_TO_SHOW_ADS, 0)
        Log.d(AdHelper.javaClass.simpleName, "loaded AD_COUNTER: $adCounter")
        Log.d(AdHelper.javaClass.simpleName, "loaded WHEN_TO_SHOW_ADS: $whenToShowAds")
        if (adCounter++ >= whenToShowAds) {
            showWhenIdle(activity)
            adCounter = 0
        }
        prefs.edit {
            putInt(AD_COUNTER, adCounter)
            apply()
            Log.d(AdHelper.javaClass.simpleName, "saved adCounter: $adCounter")
        }
    }

    fun showAd(activity: Activity) {
        showWhenIdle(activity)
    }

    /** Pushes a queued ad back by another quiet period - call on every user input. */
    fun postponePendingAd() {
        val pending = pendingAd ?: return
        mainHandler.removeCallbacks(pending)
        mainHandler.postDelayed(pending, QUIET_PERIOD_MS)
    }

    /** Drops a queued ad, e.g. because a new game started before the player paused. */
    fun cancelPendingAd() {
        pendingAd?.let(mainHandler::removeCallbacks)
        pendingAd = null
    }

    private fun showWhenIdle(activity: Activity) {
        cancelPendingAd()
        val show =
            Runnable {
                pendingAd = null
                // Left the screen, or a dialog (the rate-app prompt) is over it: drop this one.
                if (!activity.isFinishing && activity.hasWindowFocus()) {
                    ad?.show(activity)
                    Log.d(AdHelper.javaClass.simpleName, "showing ad: $ad")
                    loadAd(activity)
                }
            }
        pendingAd = show
        mainHandler.postDelayed(show, QUIET_PERIOD_MS)
    }

    fun loadAd(activity: Activity) {
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.i(AdHelper::class.simpleName, "onAdLoaded: $interstitialAd")
                    ad = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.w(AdHelper::class.simpleName, "onAdFailedToLoad: $loadAdError")
                }
            },
        )
    }
}
