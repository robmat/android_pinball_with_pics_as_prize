package com.batodev.pinball

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.core.content.edit


private const val WHEN_TO_SHOW_APP_RATE_POPUP = "WHEN_TO_SHOW_APP_RATE_POPUP"
private const val ACTION_COUNT = "ACTION_COUNT"
private const val DO_NOT_SHOW_ADS_EVER = Integer.MIN_VALUE

object RateAppHelper {
    fun increaseRateAppCounterAndShowDialogIfApplicable(activity: Activity) {
        Log.d(RateAppHelper.javaClass.simpleName, "increaseRateAppCounterAndShowDialogIfApplicable")
        val prefs = activity.getSharedPreferences(RateAppHelper.javaClass.simpleName, Context.MODE_PRIVATE)
        val whenToShow = prefs.getInt(WHEN_TO_SHOW_APP_RATE_POPUP, 3)
        var actionCount = prefs.getInt(ACTION_COUNT, 0)
        if (actionCount == DO_NOT_SHOW_ADS_EVER) {
            Log.d(RateAppHelper.javaClass.simpleName, "doing nothing since actionCount: $actionCount")
            return
        }
        ++actionCount
        Log.d(RateAppHelper.javaClass.simpleName, "whenToShow: $whenToShow, actionCount: ${actionCount}")
        if (actionCount >= whenToShow) {
            showRateAppPopup(activity, prefs)
        }
        prefs.edit {
            putInt(ACTION_COUNT, actionCount)
            apply()
            Log.d(RateAppHelper.javaClass.simpleName, "saved actionCount: $actionCount")
        }
    }

    private fun showRateAppPopup(activity: Activity, prefs: SharedPreferences) {
        val inflater = activity.layoutInflater
        val popupView = inflater.inflate(R.layout.popup_rate_app, activity.findViewById(R.id.main), false)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val btnRateNow = popupView.findViewById<Button>(R.id.btnRateNow)
        btnRateNow.setOnClickListener {
            onNeverPressed(prefs)
            val appPackageName = activity.packageName
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            popupWindow.dismiss()
            prefs.edit {
                putInt(AD_COUNTER, -5)
                apply()
                Log.d(RateAppHelper.javaClass.simpleName, "saved $AD_COUNTER: -5")
            }
        }
        val btnRateLater = popupView.findViewById<Button>(R.id.btnRateLater)
        btnRateLater.setOnClickListener {
            onLaterPressed(prefs)
            popupWindow.dismiss()
        }
        val btnNeverRate = popupView.findViewById<Button>(R.id.btnNeverRate)
        btnNeverRate.setOnClickListener {
            onNeverPressed(prefs)
            popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            if (prefs.getInt(ACTION_COUNT, 0) != DO_NOT_SHOW_ADS_EVER) {
                onLaterPressed(prefs)
            }
        }
    }

    private fun onNeverPressed(prefs: SharedPreferences) {
        prefs.edit {
            putInt(ACTION_COUNT, DO_NOT_SHOW_ADS_EVER)
            apply()
            Log.d(RateAppHelper.javaClass.simpleName, "saved actionCount: $DO_NOT_SHOW_ADS_EVER")
        }
    }

    private fun onLaterPressed(prefs: SharedPreferences) {
        prefs.edit {
            putInt(ACTION_COUNT, 0)
            apply()
            Log.d(RateAppHelper.javaClass.simpleName, "saved actionCount: 0")
        }
    }
}