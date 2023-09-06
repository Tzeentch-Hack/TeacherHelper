package com.tzeentch.teacherhelper.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import java.io.IOException
import kotlin.math.roundToInt

object Helper {
    private fun resize(imaged: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        var image = imaged
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).roundToInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).roundToInt()
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, false).also {
                if (it != null) {
                    image = it
                }
            }
        }
        return image
    }

    fun convertUriToBitmap(imageUri: Uri, context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val ins = context.contentResolver.openInputStream(imageUri)
            val decoder: BitmapRegionDecoder? =
                ins?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        BitmapRegionDecoder.newInstance(it)
                    } else {
                        BitmapRegionDecoder.newInstance(it, false)
                    }
                }
            bitmap =
                decoder?.decodeRegion(Rect(0, 0, decoder.width, decoder.height), null)
                    ?.let { resize(it, 2000, 2000) }
            ins?.close()
        } catch (err: IOException) {
            err.printStackTrace()
        }
        return bitmap
    }
}