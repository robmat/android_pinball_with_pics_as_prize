package com.batodev.pinball

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val AD_ID = BuildConfig.AD_HELPER_AD_ID
var ad: InterstitialAd? = null
const val AD_COUNTER= "AD_COUNTER"
const val WHEN_TO_SHOW_ADS= "WHEN_TO_SHOW_ADS"

object AdHelper {
    fun showAddIfNeeded(activity: Activity) {
        val prefs = activity.getSharedPreferences(RateAppHelper.javaClass.simpleName, Context.MODE_PRIVATE)
        var adCounter = prefs.getInt(AD_COUNTER, 0)
        val whenToShowAds = prefs.getInt(WHEN_TO_SHOW_ADS, 0)
        Log.d(AdHelper.javaClass.simpleName, "loaded AD_COUNTER: $adCounter")
        Log.d(AdHelper.javaClass.simpleName, "loaded WHEN_TO_SHOW_ADS: $whenToShowAds")
        if (adCounter++ >= whenToShowAds) {
            ad?.show(activity)
            Log.d(AdHelper.javaClass.simpleName, "showing ad: $ad")
            loadAd(activity)
            adCounter = 0
        }
        prefs.edit {
            putInt(AD_COUNTER, adCounter)
            apply()
            Log.d(AdHelper.javaClass.simpleName, "saved adCounter: $adCounter")
        }
    }

    fun showAd(activity: Activity) {
        ad?.show(activity)
        Log.d(AdHelper.javaClass.simpleName, "showing ad: $ad")
        loadAd(activity)
    }

    fun loadAd(activity: Activity) {
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(activity, AD_ID, adRequest,
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
            })
    }
}