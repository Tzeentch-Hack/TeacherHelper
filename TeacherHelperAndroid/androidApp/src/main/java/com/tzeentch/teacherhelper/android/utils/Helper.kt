package com.tzeentch.teacherhelper.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt

object Helper {
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromResource(
        imageData: InputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            val decoder: BitmapRegionDecoder? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(imageData)
                } else {
                    BitmapRegionDecoder.newInstance(imageData, false)
                }
            inJustDecodeBounds = true
            decoder?.decodeRegion(Rect(0, 0, decoder.width, decoder.height), this)
            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false
            decoder?.decodeRegion(Rect(0, 0, decoder.width, decoder.height), this);
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun convertUriToBitmap(imageUri: Uri, context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        var roundedBitmap: Bitmap? = null
        try {
            val ins = context.contentResolver.openInputStream(imageUri)

            ins?.let {
                decodeSampledBitmapFromResource(ins, 2025, 2700)?.let { decodedBitmap ->
                    roundedBitmap = decodedBitmap
                }
                ins.close()
            }

            if (roundedBitmap == null) return null
            val newIns = context.contentResolver.openInputStream(imageUri)
            newIns?.let {
                val ei = ExifInterface(newIns)
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->
                        rotateImage(roundedBitmap!!, 90f)

                    ExifInterface.ORIENTATION_ROTATE_180 ->
                        rotateImage(roundedBitmap!!, 180f)

                    ExifInterface.ORIENTATION_ROTATE_270 ->
                        rotateImage(roundedBitmap!!, 270f)

                    ExifInterface.ORIENTATION_NORMAL -> roundedBitmap
                    else -> roundedBitmap
                }
                newIns.close()
            }
        } catch (err: IOException) {
            err.printStackTrace()
        }
        return bitmap
    }

    fun convertBitmapToFile(imageBitmap: Bitmap, context: Context): File? {
        val f = File(context.cacheDir, "file.jpg")
        return try {
            val os = FileOutputStream(f);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
            f
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun convertUriToFile(uri: Uri?, context: Context): File? {
        val bitmap = uri?.let { convertUriToBitmap(it, context) }
        return bitmap?.let { convertBitmapToFile(it, context) }
    }
}