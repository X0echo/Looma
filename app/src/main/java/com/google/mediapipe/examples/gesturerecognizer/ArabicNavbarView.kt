package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArabicNavbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {


    private lateinit var recyclerView: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var adapter: ArabicLetterAdapter

    // Updated list with Arabic letter names (Alef, Beh, etc.)
    private val arabicLetterNames = listOf(
        "Alef", "Beh", "Teh", "Theh", "Jeem", "Hah", "Khah",
        "Dal", "Thal", "Ra", "Zay", "Seen", "Sheen", "Sad",
        "Dad", "Tah", "Zah", "Ain", "Ghain", "Fah", "Qaf",
        "Kaf", "Lam", "Meem", "Noon", "Heh", "Waw", "Ya"
    )

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.arabic_navbar, this, true)
        recyclerView = findViewById(R.id.letterRecyclerView)
        skipButton = findViewById(R.id.skipButton)



        // Initialize adapter with the Arabic letter names
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        adapter = ArabicLetterAdapter(arabicLetterNames, 0)
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(SpacingItemDecoration(16))

        skipButton.setOnClickListener {
            adapter.skipToNext()

        }
    }

    // Function to get the current letter name
    fun getCurrentLetter(): String {
        return adapter.getCurrentLetter()
    }

    // Function to handle letter recognition
    fun onLetterRecognized(letter: String, confidence: Float) {
        val currentLetter = adapter.getCurrentLetter()
        if (letter == currentLetter && confidence >= 0.9f) {
            adapter.markCurrentLetterSuccess()

        }
    }
}
