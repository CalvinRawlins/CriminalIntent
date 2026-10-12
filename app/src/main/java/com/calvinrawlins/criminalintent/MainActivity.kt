package com.calvinrawlins.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.calvinrawlins.criminalintent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        supportFragmentManager
            .addOnBackStackChangedListener {
                updateUpButton()
            }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    CrimeListFragment()
                )
                .commit()
        }

        updateUpButton()
    }

    private fun updateUpButton() {

        val showUpButton =
            supportFragmentManager
                .backStackEntryCount > 0

        supportActionBar
            ?.setDisplayHomeAsUpEnabled(
                showUpButton
            )
    }

    override fun onSupportNavigateUp(): Boolean {

        return if (
            supportFragmentManager
                .backStackEntryCount > 0
        ) {

            supportFragmentManager
                .popBackStack()

            true

        } else {

            super.onSupportNavigateUp()
        }
    }
}