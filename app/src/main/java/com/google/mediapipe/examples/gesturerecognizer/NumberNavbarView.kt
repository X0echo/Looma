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

class NumberNavbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var adapter: NumberAdapter

    // Number list: 0–10, 20, 30, ..., 100
    private val numberList = listOf(
        "ا", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "20", "30", "40", "50", "60", "70", "80", "90", "100"
    )

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.number_navbar, this, true)
        recyclerView = findViewById(R.id.letterRecyclerView)
        skipButton = findViewById(R.id.skipButton)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        adapter = NumberAdapter(numberList, 0)
        recyclerView.adapter = adapter

        skipButton.setOnClickListener {
            adapter.skipToNext()
            scrollToCurrent()
        }
    }

    private fun scrollToCurrent() {
        recyclerView.smoothScrollToPosition(adapter.getCurrentIndex())
    }

    fun getCurrentNumber(): String = adapter.getCurrentNumber()

    fun onNumberRecognized(number: String, confidence: Float) {
        val current = adapter.getCurrentNumber()
        if (number == current && confidence >= 0.8f) {
            adapter.markCurrentNumberSuccess()
            scrollToCurrent()
            playSuccessSound()
            // Add this part after marking the current number as success and playing the sound
            Handler(Looper.getMainLooper()).postDelayed({
                adapter.skipToNext()
                scrollToCurrent()
            }, 1000)  // 1000 milliseconds = 1 second


        }
    }

    private fun playSuccessSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.success)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}
