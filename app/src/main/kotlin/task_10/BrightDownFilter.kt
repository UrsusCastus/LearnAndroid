package task_10

import android.graphics.*
import android.util.Log
import kotlinx.coroutines.*

class BrightDownFilter() {

    private val TAG_BRIGHT_DOWN_FILTER: String = "TagBrightDownFilter"
    private val COLOR_COEFFICIENT: Float = 0.9f //-10% brightness

    lateinit var mBitmap: Bitmap

    suspend fun applyBrightnessFilter(bitmap: Bitmap): Bitmap {
        mBitmap = Bitmap.createBitmap(bitmap)
        Log.d(TAG_BRIGHT_DOWN_FILTER, "Method applyBrightnessFilter before withContext - " + Thread.currentThread().name)
        withContext(Dispatchers.Default) {
            Log.d(TAG_BRIGHT_DOWN_FILTER, "Method applyBrightnessFilter Context - " + Thread.currentThread().name)
            Log.d(TAG_BRIGHT_DOWN_FILTER, "applyBrightnessFilter BEFORE - " + mBitmap)
            mBitmap = changeBrightnessBitmap(mBitmap)
            Log.d(TAG_BRIGHT_DOWN_FILTER, "applyBrightnessFilter AFTER - " + mBitmap)
        }
        return mBitmap
    }

    fun changeBrightnessBitmap(bitmap: Bitmap): Bitmap {
        Log.d(TAG_BRIGHT_DOWN_FILTER, "Method changeBrightnessBitmap - " + Thread.currentThread().name)

        var changeBrightnessMatrix = floatArrayOf(COLOR_COEFFICIENT, 0f, 0f, 0f, 0f,
                0f, COLOR_COEFFICIENT, 0f, 0f, 0f,
                0f, 0f, COLOR_COEFFICIENT, 0f, 0f,
                0f, 0f, 0f, 1f, 0f)

        var colorMatrix = ColorMatrix(changeBrightnessMatrix)
        var width = bitmap.width
        var height = bitmap.height
        var bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmapResult)
        var paint = Paint()
        var colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmapResult
    }
}