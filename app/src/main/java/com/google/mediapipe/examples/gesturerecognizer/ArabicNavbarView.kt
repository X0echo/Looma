package com.google.mediapipe.examples.gesturerecognizer

import android.media.MediaPlayer
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper

class ArabicNavbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var adapter: ArabicLetterAdapter

    private val arabicLetterNames = listOf(
        "ا", "ب", "ت", "ث", "ج", "ح", "خ",
        "د", "ذ", "ر", "ز", "س", "ش", "ص",
        "ض", "ط", "ظ", "ع", "غ", "ف", "ق",
        "ك", "ل", "م", "ن", "ه", "و", "ي",
        "لا", "ة", "إ", "ئ", "ال", "none"
    )

    private val handler = Handler(Looper.getMainLooper())
    private var skipRunnable: Runnable? = null
    private var isSuccessHandled = false // Block duplicate recognitions

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.arabic_navbar, this, true)
        recyclerView = findViewById(R.id.letterRecyclerView)
        skipButton = findViewById(R.id.skipButton)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        adapter = ArabicLetterAdapter(arabicLetterNames, 0)
        recyclerView.adapter = adapter

        skipButton.setOnClickListener {
            cancelPendingSkip() // Cancel any scheduled skips
            adapter.skipToNext()
            isSuccessHandled = false // Reset for new letter
            scrollToCurrent()
        }
    }

    private fun scrollToCurrent() {
        recyclerView.smoothScrollToPosition(adapter.getCurrentIndex())
    }

    fun getCurrentLetter(): String = adapter.getCurrentLetter()

    fun onLetterRecognized(letter: String, confidence: Float) {
        val currentLetter = adapter.getCurrentLetter()

        // Only process if not already handling a success
        if (!isSuccessHandled && letter == currentLetter && confidence >= 0.8f) {
            isSuccessHandled = true
            cancelPendingSkip()
            adapter.markCurrentLetterSuccess()
            playSuccessSound()
            scheduleSkipToNext()
        }
    }

    private fun scheduleSkipToNext() {
        skipRunnable = Runnable {
            adapter.skipToNext()
            isSuccessHandled = false // Allow new recognitions
            scrollToCurrent()
        }
        handler.postDelayed(skipRunnable!!, 1000)
    }

    private fun cancelPendingSkip() {
        skipRunnable?.let {
            handler.removeCallbacks(it)
            skipRunnable = null
        }
    }

    private fun playSuccessSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.success)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }
}