package ru.vvdev.yamap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

class ImageCacheManager {
    companion object {
        private val imageCache = mutableMapOf<String, Bitmap?>()

        @Throws(IOException::class)
        private fun getBitmap(context: Context, url: String): Bitmap {
            if (url.contains("http://") || url.contains("https://")) {
                val aURL = URL(url)
                val conn = aURL.openConnection()
                conn.connect()
                val `is` = conn.getInputStream()
                val bis = BufferedInputStream(`is`)
                val bitmap = BitmapFactory.decodeStream(bis)
                bis.close()
                `is`.close()
                return bitmap
            }
            val id = context.resources.getIdentifier(url, "drawable", context.packageName)

            return BitmapFactory.decodeResource(
                context.resources,
                id
            )
        }

        private fun downloadImageBitmap(context: Context, url: String, cb: Callback<Bitmap?>) {
            object : Thread() {
                override fun run() {
                    try {
                        val bitmap = getBitmap(context, url)
                        Handler(Looper.getMainLooper()).post { cb.invoke(bitmap) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

        fun getImage(context: Context, source: String, setImage: (image: Bitmap?) -> Unit) {
            imageCache[source]?.let {
                setImage(it)
                return
            }

            downloadImageBitmap(context, source, object : Callback<Bitmap?> {
                override fun invoke(arg: Bitmap?) {
                    try {
                        setImage(arg)
                        imageCache[source] = arg
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }
}
