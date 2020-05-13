package task_10

import android.graphics.*
import kotlinx.coroutines.*

class BrightDownFilter {

    //launch — метод для асинхронного запуска корутины
    //Dispatchers.IO используется для фоновых задач, не блокирующих основной поток

//    CoroutineScope(Dispatchers.IO).launch {
//    }

    //для отложенной задачи
//    GlobalScope.launch { }

//    withContext(Dispatchers.IO) {}

        fun changeBrightnessBitmap(bitmap: Bitmap, colorCoefficient: Float): Bitmap {

            var changeBrightnessMatrix = floatArrayOf(colorCoefficient, 0f, 0f, 0f, 0f,
                    0f, colorCoefficient, 0f, 0f, 0f,
                    0f, 0f, colorCoefficient, 0f, 0f,
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
