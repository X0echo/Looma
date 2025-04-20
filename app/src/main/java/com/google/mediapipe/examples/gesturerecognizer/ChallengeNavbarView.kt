package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChallengesNavbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var challengeRecycler: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var adapter: ChallengeLetterAdapter
    private var pendingAdvancement: Runnable? = null
    private var isProcessingSuccess = false // New flag added

    private val challengeLetters = listOf(
        "ا", "ب", "ت", "ث", "ج", "ح", "خ",
        "د", "ذ", "ر", "ز", "س", "ش", "ص",
        "ض", "ط", "ظ", "ع", "غ", "ف", "ق",
        "ك", "ل", "م", "ن", "ه", "و", "ي",
        "لا", "ة", "إ", "ئ", "ال"
    )

    init {
        initChallengeView()
    }

    private fun initChallengeView() {
        LayoutInflater.from(context).inflate(R.layout.challenge_navbar, this, true)
        challengeRecycler = findViewById(R.id.challengeRecycler)
        skipButton = findViewById(R.id.btnSkipChallenge)

        val shuffledLetters = challengeLetters.shuffled()
        challengeRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        adapter = ChallengeLetterAdapter(shuffledLetters, 0)
        challengeRecycler.adapter = adapter

        skipButton.setOnClickListener {
            if (adapter.hasMoreLetters()) {
                adapter.advanceToNext()
                challengeRecycler.smoothScrollToPosition(adapter.currentPosition)
            }
        }
    }

    fun handleSuccessfulRecognition(recognizedLetter: String) {
        if (isProcessingSuccess) return // Block subsequent recognitions

        if (recognizedLetter == adapter.getCurrentLetter()) {
            pendingAdvancement?.let { removeCallbacks(it) }

            adapter.markSuccess()
            isProcessingSuccess = true // Set processing flag

            pendingAdvancement = Runnable {
                if (adapter.hasMoreLetters()) {
                    adapter.advanceToNext()
                    challengeRecycler.smoothScrollToPosition(adapter.currentPosition)
                }
                isProcessingSuccess = false // Reset flag after advancement
            }
            postDelayed(pendingAdvancement!!, 1000)
        }
    }

    fun getCurrentChallengeLetter(): String = adapter.getCurrentLetter()
}