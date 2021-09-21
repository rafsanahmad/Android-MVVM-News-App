package com.rafsan.newsapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rafsan.newsapp.R
import com.rafsan.newsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import setupWithNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.feed,
            R.navigation.favorite
        )

        binding.bottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
    }
}