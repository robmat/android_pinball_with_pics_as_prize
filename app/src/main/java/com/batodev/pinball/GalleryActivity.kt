package com.batodev.pinball

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class GalleryActivity : Activity() {
    val pics = mutableListOf<String>()
    var currentPic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.gallery_activity)
    }

    override fun onResume() {
        super.onResume()
        pics.clear()
        pics.addAll(SettingsHelper(this).preferences.uncoveredPics)
        findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
            ImageHelper.findBitmap(pics[0], this)
        );
        currentPic = pics[0]
    }

    fun leftClicked(view: View) {
        val indexOf = pics.indexOf(currentPic)
        if (indexOf > 0) {
            findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
                ImageHelper.findBitmap(pics[indexOf - 1], this)
            )
            currentPic = pics[indexOf - 1]
        }
    }

    fun rightClicked(view: View) {
        val indexOf = pics.indexOf(currentPic)
        if (indexOf < pics.size - 1) {
            findViewById<PhotoView>(R.id.gallery_activity_background).setImageBitmap(
                ImageHelper.findBitmap(pics[indexOf + 1], this)
            )
            currentPic = pics[indexOf + 1]
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
        val uri =
            Uri.parse("content://com.batodev.pinball.ImagesProvider/$tmpImgPath")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        startActivity(shareIntent)
    }
}