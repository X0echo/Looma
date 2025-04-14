package com.google.mediapipe.examples.gesturerecognizer

import android.widget.ImageView
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
        val letterImage: ImageView = view.findViewById(R.id.letterImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_arabic_letter, parent, false)
        return LetterViewHolder(view)
    }

    override fun getItemCount(): Int = letterNames.size

    private val successfulLetters = mutableSetOf<Int>()

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        // Bring ImageView to the front by setting elevation
        holder.letterImage.apply {
            elevation = 10f  // Elevation to bring the image to the front
        }

        holder.letterBox.apply {
            val imageName = when (val name = letterNames[position]) {
                "ا" -> "alef"
                "ب" -> "baa"
                "ت" -> "teh"
                "ث" -> "theh"
                "ج" -> "jeem"
                "ح" -> "hah"
                "خ" -> "khah"
                "د" -> "dal"
                "ذ" -> "thal"
                "ر" -> "raa"
                "ز" -> "zain"
                "س" -> "seen"
                "ش" -> "sheen"
                "ص" -> "sad"
                "ض" -> "dad"
                "ط" -> "taa"
                "ظ" -> "thad"
                "ع" -> "ain"
                "غ" -> "ghain"
                "ف" -> "faa"
                "ق" -> "qaf"
                "ك" -> "kaf"
                "ل" -> "lam"
                "م" -> "meem"
                "ن" -> "noon"
                "ه" -> "heh"
                "و" -> "waw"
                "ي" -> "yaa"
                "لا" -> "laa"
                "ة" -> "teh_marbuta"
                else -> "placeholder" // Default case if something goes wrong
            }

            // Get the resource ID for the image
            val resId = holder.itemView.context.resources.getIdentifier(imageName, "drawable", holder.itemView.context.packageName)

            // Set the image resource if found
            if (resId != 0) {
                holder.letterImage.setImageResource(resId)
            } else {
                // If the resource is not found, set a placeholder image
                holder.letterImage.setImageResource(R.drawable.placeholder_image) // Set a placeholder image
            }

            // Display the letter name
            text = letterNames[position]

            // Change the background and text color based on the current state
            if (position == currentIndex) {
                setBackgroundResource(R.drawable.current_letter_bg) // Blue for current letter
                textSize = 20f
                setTextColor(Color.WHITE)
            } else if (position in successfulLetters) {
                setBackgroundResource(R.drawable.success_letter_bg)  // Green for successful recognition
                setTextColor(Color.WHITE)
            } else {
                setBackgroundResource(R.drawable.letter_box_bg)  // Default background
                setTextColor(Color.WHITE)
                textSize = 18f
            }
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
