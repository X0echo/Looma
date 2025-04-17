package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PracticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)

        val lettersButton = findViewById<Button>(R.id.lettersButton)
        val numbersButton = findViewById<Button>(R.id.numbersButton)
        val challengesButton = findViewById<Button>(R.id.challengesButton)

        // Letters button: Start the MainActivity for letter recognition
        lettersButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Numbers button: Start the NumbersActivity for number recognition
        numbersButton.setOnClickListener {
            val intent = Intent(this, NumberMainActivity::class.java)
            startActivity(intent)

        }

        // Challenges button: Start the ChallengesActivity (implement similarly if needed)
        challengesButton.setOnClickListener {
            // TODO: Launch ChallengesActivity or similar
        }
    }
}
