package com.onza.barcode.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException


/**
 * Created by Ilia Polozov on 18/February/2020
 */

class Utils {

    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    fun saveBitmapToFile(file: File, scaleTo: Int = 1000): File? {
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / scaleTo, photoH / scaleTo)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        val resized = BitmapFactory.decodeFile(file.absolutePath, bmOptions)
        file.outputStream().use {
            resized.compress(Bitmap.CompressFormat.JPEG, 75, it)
            resized.recycle()
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