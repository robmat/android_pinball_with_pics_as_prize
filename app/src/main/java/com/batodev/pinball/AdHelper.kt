package com.batodev.pinball

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdHelper {
    fun showAd(activity: Activity) {
        Log.d(AdHelper::class.simpleName, "showAd(activity: $activity)")
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity, BuildConfig.AD_HELPER_AD_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.i(AdHelper::class.simpleName, "onAdLoaded: $interstitialAd")
                    interstitialAd.fullScreenContentCallback = object :
                        FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(AdHelper::class.simpleName, "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(
                                AdHelper::class.simpleName,
                                "Ad dismissed fullscreen content."
                            )
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            Log.e(
                                AdHelper::class.simpleName,
                                "Ad failed to show fullscreen content."
                            )
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(AdHelper::class.simpleName, "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(AdHelper::class.simpleName, "Ad showed fullscreen content.")
                        }
                    }
                    interstitialAd.show(activity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.w(AdHelper::class.simpleName, "onAdLoaded: $loadAdError")
                }
            })
    }
}