package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArabicNavbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var adapter: ArabicLetterAdapter

    // Arabic letter names list
    private val arabicLetterNames = listOf(
        "أ", "ب", "ت", "ث", "ج", "ح", "خ",
        "د", "ذ", "ر", "ز", "س", "ش", "ص",
        "ض", "ط", "ظ", "ع", "غ", "ف", "ق",
        "ك", "ل", "م", "ن", "هـ", "و", "ي"
    )

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.arabic_navbar, this, true)
        recyclerView = findViewById(R.id.letterRecyclerView)
        skipButton = findViewById(R.id.skipButton)

        // Setup the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        adapter = ArabicLetterAdapter(arabicLetterNames, 0)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpacingItemDecoration(16))

        // When skip is tapped, skip to the next letter and scroll the RecyclerView accordingly
        skipButton.setOnClickListener {
            adapter.skipToNext()
            scrollToCurrent()
        }
    }

    // Function to scroll the RecyclerView to the current letter position
    private fun scrollToCurrent() {
        recyclerView.smoothScrollToPosition(adapter.getCurrentIndex())
    }

    // Get the current letter name from the adapter
    fun getCurrentLetter(): String = adapter.getCurrentLetter()

    // Handle recognized letters: if recognized correctly, mark as success and scroll to next letter.
    fun onLetterRecognized(letter: String, confidence: Float) {
        val currentLetter = adapter.getCurrentLetter()
        if (letter == currentLetter && confidence >= 0.9f) {
            adapter.markCurrentLetterSuccess()
            scrollToCurrent()
        }
    }
}
