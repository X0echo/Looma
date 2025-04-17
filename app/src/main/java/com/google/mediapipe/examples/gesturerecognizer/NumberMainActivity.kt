package com.google.mediapipe.examples.gesturerecognizer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityMainBinding

class NumberMainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: NumberMainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout with DataBinding
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Set the start destination to number_permissions_fragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment

        navController = navHostFragment.navController
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph) as NavGraph

        navGraph.setStartDestination(R.id.number_permissions_fragment)
        navController.graph = navGraph
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
