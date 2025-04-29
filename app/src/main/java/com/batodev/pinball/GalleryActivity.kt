package com.batodev.pinball

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.net.toUri

class GalleryActivity : Activity() {
    val pics = mutableListOf<String>()
    var currentPic = ""
    var scrollCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val windowInsetsController = WindowCompat.getInsetsController(this.window, this.window.decorView)
        windowInsetsController.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContentView(R.layout.gallery_activity)
    }

    override fun onResume() {
        super.onResume()
        pics.clear()
        val settingsHelper = SettingsHelper(this)
        pics.addAll(settingsHelper.preferences.uncoveredPics)
        findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
            ImageHelper.findBitmap(pics[settingsHelper.preferences.lastSeenGalleryPic], this)
        )
        currentPic = pics[settingsHelper.preferences.lastSeenGalleryPic]
    }

    fun leftClicked(view: View) {
        val indexOf = pics.indexOf(currentPic)
        if (indexOf > 0) {
            findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
                ImageHelper.findBitmap(pics[indexOf - 1], this)
            )
            currentPic = pics[indexOf - 1]
            saveLastSeenPic(indexOf - 1)
        }
        showAD()
    }


    fun rightClicked(view: View) {
        val indexOf = pics.indexOf(currentPic)
        if (indexOf < pics.size - 1) {
            findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
                ImageHelper.findBitmap(pics[indexOf + 1], this)
            )
            currentPic = pics[indexOf + 1]
            saveLastSeenPic(indexOf + 1)
        }
        showAD()
    }

    private fun saveLastSeenPic(indexOf: Int) {
        val scope = CoroutineScope(Dispatchers.IO)
        val settingsHelper = SettingsHelper(this)
        scope.launch {
            val result = async {
                settingsHelper.preferences.lastSeenGalleryPic = indexOf
                settingsHelper.savePreferences()
            }
            val data = result.await()
            Log.d(GalleryActivity::class.java.simpleName, "$data")
        }
    }

    private fun showAD() {
        Log.d(GalleryActivity::class.java.simpleName, "Showing ad.")
        if (++scrollCount >= 3) {
            AdHelper.showAd(this)
            scrollCount = 0
        }
    }

    fun backClicked(view: View) {
        finish()
    }

    fun shareClicked(view: View) {
        val imgFolder = ImageHelper.findPathForImage(this.assets, currentPic)
        val inputStream = assets.open("${imgFolder}${File.separator}${currentPic}")
        val tmpImgPath = "tmp_shared/tmp.png"
        val file = File(filesDir, tmpImgPath)
        File(filesDir, "tmp_shared").mkdirs()
        file.delete()
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
        val shareIntent = Intent(Intent.ACTION_SEND)
        val uri = "content://com.batodev.pinball.ImagesProvider/$tmpImgPath".toUri()
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        startActivity(shareIntent)
    }

    fun wallpaperClicked(view: View) {

        try {
            val fileShared = copyToTempFile()
            val wallpaperManager = WallpaperManager.getInstance(this)
            val bitmap = BitmapFactory.decodeFile(fileShared.absolutePath)
            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(this, getString(R.string.wallpaper_ok), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(GalleryActivity::class.java.simpleName, "Błąd podczas ustawiania tapety", e)
            Toast.makeText(this, "Error: $e" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToTempFile(): File {
        val bitmap = ImageHelper.findBitmap(currentPic, this)
        val dirShared = File(filesDir, "shared")
        if (!dirShared.exists()) {
            dirShared.mkdir()
        }
        val fileShared = File(dirShared, "shared.jpg")
        if (fileShared.exists()) {
            fileShared.delete()
        }
        fileShared.createNewFile()
        FileOutputStream(fileShared).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
        return fileShared
    }
}
