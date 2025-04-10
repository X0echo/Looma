package com.google.mediapipe.examples.gesturerecognizer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArabicLetterAdapter(
    private val letterNames: List<String>,  // This list now holds the letter names (Alef, Beh, etc.)
    private var currentIndex: Int
) : RecyclerView.Adapter<ArabicLetterAdapter.LetterViewHolder>() {

    inner class LetterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val letterBox: TextView = view.findViewById(R.id.letterBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_arabic_letter, parent, false)
        return LetterViewHolder(view)
    }

    override fun getItemCount(): Int = letterNames.size

    private val successfulLetters = mutableSetOf<Int>()

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.letterBox.apply {
            text = letterNames[position]  // Display the letter name (Alef, Beh, etc.)
            if (position == currentIndex) {
                setBackgroundResource(R.drawable.current_letter_bg) // Blue for current letter
                textSize = 20f
                setTextColor(Color.WHITE)
            } else if (position in successfulLetters) {

                setBackgroundResource(R.drawable.success_letter_bg)  // Green for successful
                setTextColor(Color.WHITE)
            } else {
                setBackgroundResource(R.drawable.letter_box_bg)  // Default background
                setTextColor(Color.WHITE)
                textSize = 18f
            }
        }
    }

    // Method to update the current gesture and confidence
    fun updateCurrentGesture(currentLetter: String, confidence: Float) {
        val letterIndex = letterNames.indexOf(currentLetter)
        if (letterIndex != -1) {
            currentIndex = letterIndex
            // If the confidence is above a certain threshold, mark the letter as successful
            if (confidence >= 0.7) {  // Example confidence threshold
                markCurrentLetterSuccess()
            }
            notifyDataSetChanged()  // Refresh the RecyclerView
        }
    }

    // Method to mark the current letter as successfully recognized
    fun markCurrentLetterSuccess() {
        successfulLetters.add(currentIndex)
        skipToNext()  // Move to the next letter
    }

    // Skip to the next letter after success
    fun skipToNext() {
        if (currentIndex < letterNames.size - 1) {
            currentIndex++
            notifyDataSetChanged()
        }
    }

    // Get the current letter based on the index
    fun getCurrentLetter(): String = letterNames[currentIndex]

    // Get the current index of the letter being shown
    fun getCurrentIndex(): Int = currentIndex
}
