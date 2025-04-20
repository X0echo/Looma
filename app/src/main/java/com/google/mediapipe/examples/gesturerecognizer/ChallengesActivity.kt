package com.google.mediapipe.examples.gesturerecognizer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityChallengesBinding

class ChallengesActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityChallengesBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityChallengesBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        // Set up navigation if needed
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.challenge_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}