package task_10

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.*

class BrightDownFilter() {

    private val TAG_BRIGHT_DOWN_FILTER: String = "TagBrightDownFilter"
    private val COLOR_COEFFICIENT: Float = 0.9f //-10% brightness

    //launch - билдер корутины
    // job - результат запуска билдера

    suspend fun setImageBitmap(bitmap: Bitmap, imageView: ImageView) {
        CoroutineScope(Dispatchers.Default).launch {
            val bitmapResult = changeBrightnessBitmap(bitmap)
            //с помощью withContext можно переключаться между потоками в контексте корутины
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmapResult)
            }
        }
    }

    private fun changeBrightnessBitmap(bitmap: Bitmap): Bitmap {
        Log.d(TAG_BRIGHT_DOWN_FILTER, "Method changeBrightnessBitmap - " + Thread.currentThread().name)

        val changeBrightnessMatrix = floatArrayOf(COLOR_COEFFICIENT, 0f, 0f, 0f, 0f,
                0f, COLOR_COEFFICIENT, 0f, 0f, 0f,
                0f, 0f, COLOR_COEFFICIENT, 0f, 0f,
                0f, 0f, 0f, 1f, 0f)

        val colorMatrix = ColorMatrix(changeBrightnessMatrix)
        val width = bitmap.width
        val height = bitmap.height
        val bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapResult)
        val paint = Paint()
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmapResult
    }
}
