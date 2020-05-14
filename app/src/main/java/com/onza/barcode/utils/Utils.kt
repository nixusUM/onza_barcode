package com.onza.barcode.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by Ilia Polozov on 18/February/2020
 */

class Utils {

    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    fun saveBitmapToFile(file: File): Bitmap? {
        var source = BitmapFactory.decodeFile(file.absolutePath)
        if (source.height == 1000 && source.width == 1000) return source
        val maxLength: Int = Math.min(800, 800)
        return try {
            source = source.copy(source.getConfig(), true)
            if (source.getHeight() <= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio =
                    source.getWidth() as Double / source.getHeight() as Double
                val targetWidth = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio =
                    source.getHeight() as Double / source.getWidth() as Double
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: Exception) {
            return source
        }
    }

    fun saveBitmap(bitmap: Bitmap?, path: String): File? {
        var file: File? = null
        if (bitmap != null) {
            file = File(path)
            try {
                var outputStream: FileOutputStream? = null
                try {
                    outputStream =
                        FileOutputStream(path) //here is set your file path where you want to save or also here you can set file object directly
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        75,
                        outputStream
                    ) // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        outputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp*density)
    }

    companion object {

        private var scannedItemList = mutableListOf<Any>()

        fun setSccanedList(list: List<Any>) {
            this.scannedItemList = list as MutableList<Any>
        }

        fun getScannedList(): List<Any> {
            return this.scannedItemList
        }

    }

    fun isInternetAvailable(): Boolean {
        try {
            var command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: IOException) { // Log error
        }
        return false
    }
}