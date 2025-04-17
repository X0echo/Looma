package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class NumberRecognizerHelper(
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.IMAGE,
    val context: Context,
    val numberRecognizerListener: NumberRecognizerListener? = null
) {

    private var numberRecognizer: GestureRecognizer? = null

    init {
        setupNumberRecognizer()
    }

    fun clearNumberRecognizer() {
        numberRecognizer?.close()
        numberRecognizer = null
    }

    // Initialize the number recognizer
    fun setupNumberRecognizer() {
        val baseOptionBuilder = BaseOptions.builder()

        // Set the hardware delegate
        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                baseOptionBuilder.setDelegate(Delegate.GPU)
            }
        }

        // Set the path for the number recognizer model
        baseOptionBuilder.setModelAssetPath(MP_NUMBER_RECOGNIZER_TASK)

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                GestureRecognizer.GestureRecognizerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }
            val options = optionsBuilder.build()
            numberRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            numberRecognizerListener?.onError(
                "Number recognizer failed to initialize. See error logs for details"
            )
            Log.e(TAG, "MP Task Vision failed to load the task with error: ${e.message}")
        } catch (e: RuntimeException) {
            numberRecognizerListener?.onError(
                "Number recognizer failed to initialize. See error logs for details",
                GPU_ERROR
            )
            Log.e(TAG, "MP Task Vision failed to load the task with error: ${e.message}")
        }
    }

    // Convert the ImageProxy to MP Image and feed it to the number recognizer
    fun recognizeLiveStream(imageProxy: ImageProxy) {
        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        recognizeAsync(mpImage, frameTime)
    }

    // Recognize the number asynchronously
    @VisibleForTesting
    fun recognizeAsync(mpImage: MPImage, frameTime: Long) {
        numberRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    // Process the result for live stream
    private fun returnLivestreamResult(result: GestureRecognizerResult, input: MPImage) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        numberRecognizerListener?.onResults(
            ResultBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    // Handle errors for live stream
    private fun returnLivestreamError(error: RuntimeException) {
        numberRecognizerListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    // Return the recognition result to the caller
    fun recognizeImage(image: Bitmap): ResultBundle? {
        if (runningMode != RunningMode.IMAGE) {
            throw IllegalArgumentException(
                "Attempting to call recognizeImage while not using RunningMode.IMAGE"
            )
        }

        val startTime = SystemClock.uptimeMillis()

        val mpImage = BitmapImageBuilder(image).build()

        numberRecognizer?.recognize(mpImage)?.also { recognizerResult ->
            val inferenceTimeMs = SystemClock.uptimeMillis() - startTime
            return ResultBundle(
                listOf(recognizerResult),
                inferenceTimeMs,
                image.height,
                image.width
            )
        }

        numberRecognizerListener?.onError("Number Recognizer failed to recognize.")
        return null
    }

    // Check if the recognizer is closed
    fun isClosed(): Boolean {
        return numberRecognizer == null
    }

    // Set the minimum number recognition confidence
    fun setMinNumberRecognitionConfidence(confidence: Float) {
        this.minHandDetectionConfidence = confidence
    }

    companion object {
        private const val TAG = "NumberRecognizerHelper"
        private const val MP_NUMBER_RECOGNIZER_TASK = "number_recognizer.task"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<GestureRecognizerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface NumberRecognizerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}
